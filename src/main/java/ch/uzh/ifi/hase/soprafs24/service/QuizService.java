package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Deck;
import ch.uzh.ifi.hase.soprafs24.entity.Flashcard;
import ch.uzh.ifi.hase.soprafs24.entity.Quiz;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.constant.QuizStatus;
import ch.uzh.ifi.hase.soprafs24.repository.DeckRepository;
import ch.uzh.ifi.hase.soprafs24.repository.QuizRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizAnswerResponseDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizUpdateMessageDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizUpdateMessageDTO.PlayerProgressDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.FlashcardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private StatisticsService statisticsService;

    /**
     * Creates a new quiz.
     * Single-player: set status to IN_PROGRESS immediately.
     * Multi-player: set status to WAITING until second user accepts.
     * Also randomly picks 'numberOfQuestions' from the deck's flashcards.
     */
    public Quiz startQuiz(Long deckId, int numberOfQuestions, int timeLimit, Boolean isMultiple) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deck not found"));

        List<Flashcard> allFlashcards = deck.getFlashcards();
        if (allFlashcards.size() < numberOfQuestions) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Not enough flashcards in the deck to fulfill the requested number of questions");
        }

        Collections.shuffle(allFlashcards);
        List<Flashcard> selected = allFlashcards.subList(0, numberOfQuestions);

        Quiz quiz = new Quiz();
        quiz.setTimeLimit(timeLimit);
        quiz.setStartTime(new Date());
        quiz.setIsMultiple(isMultiple != null && isMultiple);

        // Single-player => start immediately
        // Multi-player => wait for second user
        if (Boolean.TRUE.equals(isMultiple)) {
            quiz.setQuizStatus(QuizStatus.WAITING);
        } else {
            quiz.setQuizStatus(QuizStatus.IN_PROGRESS);
        }

        // Link the deck to the quiz
        quiz.getDecks().add(deck);
        // Store the chosen flashcards in the quiz
        quiz.setSelectedFlashcards(new ArrayList<>(selected));
        deck.setQuiz(quiz);

        quizRepository.save(quiz);
        return quiz;
    }

    /**
     * Called after second user accepts invitation in a multi-player quiz.
     * If we detect enough participants, set status to IN_PROGRESS.
     */
    public Quiz startMultiplayerIfReady(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        // For a 2-player scenario, we check if there's at least 2 participants.
        // The simplest approach might be to check the quiz's decks, or check invitation logic.
        // Here we assume if there's at least 2 decks or we have a known logic that both users have joined.
        if (quiz.getIsMultiple() && QuizStatus.WAITING.equals(quiz.getQuizStatus())) {
            // Example: if quiz has 2 decks or we have a separate mechanism to ensure 2 user progress states
            if (quiz.getDecks() != null && quiz.getDecks().size() >= 2) {
                quiz.setQuizStatus(QuizStatus.IN_PROGRESS);
                quiz.setStartTime(new Date());
                quizRepository.save(quiz);
            }
        }
        return quiz;
    }

    /**
     * Return the current question for (quizId, userId) based on their progress index.
     * If single-player quiz is not IN_PROGRESS or multi-player quiz is WAITING, we throw an error.
     */
    public Flashcard getCurrentQuestion(Long quizId, Long userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        if (!QuizStatus.IN_PROGRESS.equals(quiz.getQuizStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quiz is not in progress yet.");
        }

        QuizProgressStore.ProgressState progress = QuizProgressStore.getProgress(quizId, userId);
        if (progress.isFinished()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already finished this quiz.");
        }

        List<Flashcard> flashcards = quiz.getSelectedFlashcards();
        if (flashcards.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No questions in this quiz.");
        }

        int currentIndex = progress.getCurrentIndex();
        if (currentIndex >= flashcards.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No more questions. Quiz is finished.");
        }

        return flashcards.get(currentIndex);
    }

    /**
     * Processes an answer, returns a response telling the front end if it was correct,
     * if user is finished, and the next question if not finished.
     */
    public QuizAnswerResponseDTO processAnswerWithFeedback(
            Long quizId,
            Long flashcardId,
            String selectedAnswer,
            Long userId
    ) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        // Must be IN_PROGRESS for user to answer
        if (!QuizStatus.IN_PROGRESS.equals(quiz.getQuizStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quiz not in progress.");
        }

        QuizProgressStore.ProgressState progress = QuizProgressStore.getProgress(quizId, userId);
        if (progress.isFinished()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You already finished this quiz.");
        }

        List<Flashcard> flashcards = quiz.getSelectedFlashcards();
        int currentIndex = progress.getCurrentIndex();
        if (currentIndex >= flashcards.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No more questions left.");
        }

        Flashcard currentCard = flashcards.get(currentIndex);
        if (!currentCard.getId().equals(flashcardId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Wrong flashcard ID for the current question.");
        }

        progress.setTotalAttempts(progress.getTotalAttempts() + 1);

        boolean wasCorrect = currentCard.getAnswer().equalsIgnoreCase(selectedAnswer.trim());
        if (wasCorrect) {
            // Correct => increment index or finish
            progress.setTotalCorrect(progress.getTotalCorrect() + 1);

            // If not last question, go to next
            if (currentIndex + 1 < flashcards.size()) {
                progress.setCurrentIndex(currentIndex + 1);
            } else {
                // user finishes
                progress.setFinished(true);
                long endTimeMillis = System.currentTimeMillis();
                long timeTakenMillis = endTimeMillis - progress.getStartTimeMillis();
                User user = findUserById(userId);
                statisticsService.recordQuizStats(user, quiz, progress.getTotalCorrect(), progress.getTotalAttempts(), timeTakenMillis);
            }
        }
        // if not correct, user stays on the same question

        // Check if all participants are finished. If so, set updateType=finished.
        boolean allFinished = checkAllPlayersDone(quiz);

        // broadcast scoreboard
        broadcastProgress(quiz.getId(), flashcards.size(), allFinished);

        // Build the response to front end
        QuizAnswerResponseDTO response = new QuizAnswerResponseDTO();
        response.setWasCorrect(wasCorrect);
        response.setFinished(progress.isFinished());

        if (!progress.isFinished()) {
            // fetch new question
            Flashcard nextCard = flashcards.get(progress.getCurrentIndex());
            response.setNextQuestion(FlashcardMapper.toDTO(nextCard));
        } else {
            response.setNextQuestion(null);
        }

        return response;
    }

    /**
     * If single-player, only check that user. If multi-player, we check that all users have isFinished = true.
     */
    private boolean checkAllPlayersDone(Quiz quiz) {
        // gather progress for all who have joined
        List<QuizProgressStore.UserProgressEntry> allProgress = QuizProgressStore.getProgressForQuiz(quiz.getId());
        if (quiz.getIsMultiple()) {
            // multi => we want to see if at least 2 user progress states exist, and all are finished
            if (allProgress.size() < 2) {
                return false;
            }
            return allProgress.stream().allMatch(entry -> entry.getProgress().isFinished());
        } else {
            // single => if the single user is finished
            // if there's a progress entry at all
            return !allProgress.isEmpty() && allProgress.get(0).getProgress().isFinished();
        }
    }

    /**
     * Broadcast scoreboard to /topic/quizUpdates/{quizId}.
     * If 'allFinished' is true => updateType=finished. Otherwise => progress.
     */
    private void broadcastProgress(Long quizId, int totalQuestions, boolean allFinished) {
        List<QuizProgressStore.UserProgressEntry> allProg = QuizProgressStore.getProgressForQuiz(quizId);

        List<PlayerProgressDTO> scoreboard = allProg.stream()
                .map(entry -> {
                    PlayerProgressDTO dto = new PlayerProgressDTO();
                    dto.setUserId(entry.getUserId());
                    dto.setScore(entry.getProgress().getTotalCorrect());
                    dto.setAnsweredQuestions(entry.getProgress().getCurrentIndex());
                    return dto;
                })
                .collect(Collectors.toList());

        QuizUpdateMessageDTO update = new QuizUpdateMessageDTO();
        update.setQuizId(quizId);
        update.setUpdateType(allFinished ? "finished" : "progress");
        update.setTotalQuestions((long) totalQuestions);
        update.setPlayerProgress(scoreboard);

        String destination = "/topic/quizUpdates/" + quizId;
        messagingTemplate.convertAndSend(destination, update);
    }

    // Overload if you want to broadcast without the "allFinished" param
    private void broadcastProgress(Long quizId, int totalQuestions) {
        broadcastProgress(quizId, totalQuestions, false);
    }

    /**
     * Summaries or other final calls
     */
    public Quiz getQuizStatus(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with id: " + userId));
    }
}
