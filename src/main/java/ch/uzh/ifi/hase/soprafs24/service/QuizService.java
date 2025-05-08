package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.QuizStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

import ch.uzh.ifi.hase.soprafs24.entity.Deck;
import ch.uzh.ifi.hase.soprafs24.entity.Flashcard;
import ch.uzh.ifi.hase.soprafs24.entity.Invitation;
import ch.uzh.ifi.hase.soprafs24.entity.Quiz;
import ch.uzh.ifi.hase.soprafs24.entity.User;

import ch.uzh.ifi.hase.soprafs24.repository.DeckRepository;
import ch.uzh.ifi.hase.soprafs24.repository.InvitationRepository;
import ch.uzh.ifi.hase.soprafs24.repository.QuizRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizAnswerResponseDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizUpdateMessageDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizUpdateMessageDTO.PlayerProgressDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.InvitationDTO;

import ch.uzh.ifi.hase.soprafs24.rest.mapper.FlashcardMapper;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.QuizMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    /* ────────────────── Dependencies ────────────────── */
    private final UserService            userService;
    private final QuizRepository         quizRepository;
    private final UserRepository         userRepository;
    private final InvitationRepository   invitationRepository;
    private final DeckRepository         deckRepository;
    private final QuizMapper             quizMapper;
    private final SimpMessagingTemplate  messagingTemplate;
    private final StatisticsService      statisticsService;

    public QuizService(UserService userService,
                       QuizRepository quizRepository,
                       UserRepository userRepository,
                       InvitationRepository invitationRepository,
                       DeckRepository deckRepository,
                       QuizMapper quizMapper,
                       SimpMessagingTemplate messagingTemplate,
                       StatisticsService statisticsService) {
        this.userService          = userService;
        this.quizRepository       = quizRepository;
        this.userRepository       = userRepository;
        this.invitationRepository = invitationRepository;
        this.deckRepository       = deckRepository;
        this.quizMapper           = quizMapper;
        this.messagingTemplate    = messagingTemplate;
        this.statisticsService    = statisticsService;
    }

    /* ─────────────────────────── Invitation logic (from main) ─────────────────────────── */

    public Invitation getInvitationById(Long invitationId) {
        return invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found"));
    }

    public List<Invitation> getInvitationByFromUserId(Long fromUserId) {
        User fromUser = userService.getUserById(fromUserId);
        return invitationRepository.findByFromUser(fromUser);
    }

    public List<Invitation> getInvitationByToUserId(Long toUserId) {
        User toUser = userService.getUserById(toUserId);
        return invitationRepository.findByToUser(toUser);
    }

    public void deleteInvitationById(Long invitationId) {
        Invitation invitation = getInvitationById(invitationId);
        invitationRepository.delete(invitation);
    }

    /* Helper to ensure a user can be invited */
    private void checkUserStatusForInvitation(User user) {
        if (user.getStatus() == UserStatus.OFFLINE || user.getStatus() == UserStatus.PLAYING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User cannot be OFFLINE or PLAYING.");
        }
    }

    public Invitation createInvitation(InvitationDTO dto) {
        User fromUser = userService.getUserById(dto.getFromUserId());
        User toUser   = userService.getUserById(dto.getToUserId());
        checkUserStatusForInvitation(fromUser);
        checkUserStatusForInvitation(toUser);

        Invitation inv = new Invitation();
        inv.setFromUser(fromUser);
        inv.setToUser(toUser);
        inv.setTimeLimit(dto.getTimeLimit());
        inv.setIsAccepted(false);

        List<Deck> managedDecks = dto.getDeckIds().stream()
                .map(id -> deckRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Deck not found: " + id)))
                .collect(Collectors.toList());

        inv.setDecks(new ArrayList<>(managedDecks));
        invitationRepository.save(inv);
        return inv;
    }

    public Quiz createQuiz(Long invitationId) {
        Invitation inv = getInvitationById(invitationId);
        Quiz quiz = quizMapper.fromInvitationToEntity(inv);
        inv.setQuiz(quiz);

        invitationRepository.save(inv);
        quizRepository.save(quiz);
        return quiz;
    }

    public void confirmedInvitation(Long invitationId) {
        Invitation inv = getInvitationById(invitationId);
        Quiz quiz      = inv.getQuiz();

        quiz.setQuizStatus(QuizStatus.IN_PROGRESS);
        quiz.setStartTime(new Date());
        inv.setIsAccepted(true);
        inv.setIsAcceptedDate(new Date());

        User sender   = inv.getFromUser();
        User receiver = inv.getToUser();
        sender.setStatus(UserStatus.PLAYING);
        receiver.setStatus(UserStatus.PLAYING);

        userRepository.save(sender);
        userRepository.save(receiver);
        quizRepository.save(quiz);
        invitationRepository.save(inv);
    }

    public void rejectedInvitation(Long invitationId) {
        Invitation inv = getInvitationById(invitationId);
        if (inv.getQuiz() != null) {
            quizRepository.delete(inv.getQuiz());
        }
        invitationRepository.delete(inv);
    }

    /** Returns the *earliest* accepted invitation from a sender and cleans up the rest. */
    public Invitation findInvitationByFromUserIdAndIsAcceptedTrue(Long fromUserId) {
        List<Invitation> accepted = getInvitationByFromUserId(fromUserId).stream()
                .filter(Invitation::getIsAccepted)
                .sorted(Comparator.comparing(Invitation::getIsAcceptedDate))
                .collect(Collectors.toList());

        if (accepted.isEmpty()) return null;

        Invitation keep = accepted.get(0);
        accepted.subList(1, accepted.size()).forEach(late -> {
            if (late.getQuiz() != null) {
                quizRepository.delete(late.getQuiz());
            }
            User toUser = late.getToUser();
            toUser.setStatus(UserStatus.ONLINE);
            userRepository.save(toUser);

            invitationRepository.delete(late);
        });
        return keep;
    }

    /* ─────────────────────────── Quiz-runtime logic (from shak_branch) ─────────────────────────── */

    /** Creates a single- or multi-player quiz, selecting random questions from a deck. */
    public Quiz startQuiz(Long deckId, int numberOfQuestions, int timeLimit, Boolean isMultiple) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deck not found"));

        List<Flashcard> all = deck.getFlashcards();
        if (all.size() < numberOfQuestions) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Not enough flashcards in the deck for the requested number of questions");
        }

        Collections.shuffle(all);
        List<Flashcard> selected = all.subList(0, numberOfQuestions);

        Quiz quiz = new Quiz();
        quiz.setTimeLimit(timeLimit);
        quiz.setStartTime(new Date());
        quiz.setIsMultiple(Boolean.TRUE.equals(isMultiple));
        quiz.setQuizStatus(quiz.getIsMultiple() ? QuizStatus.WAITING : QuizStatus.IN_PROGRESS);
        quiz.getDecks().add(deck);
        quiz.setSelectedFlashcards(new ArrayList<>(selected));

        deck.setQuiz(quiz);
        quizRepository.save(quiz);
        return quiz;
    }

    /** In multi-player mode, start the quiz when enough participants joined. */
    public Quiz startMultiplayerIfReady(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        if (quiz.getIsMultiple() && QuizStatus.WAITING.equals(quiz.getQuizStatus())
                && quiz.getDecks() != null && quiz.getDecks().size() >= 2) {
            quiz.setQuizStatus(QuizStatus.IN_PROGRESS);
            quiz.setStartTime(new Date());
            quizRepository.save(quiz);
        }
        return quiz;
    }

    /** Returns the current question for a given user, enforcing quiz state & order. */
    public Flashcard getCurrentQuestion(Long quizId, Long userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        if (!QuizStatus.IN_PROGRESS.equals(quiz.getQuizStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quiz is not in progress.");
        }

        QuizProgressStore.ProgressState prog = QuizProgressStore.getProgress(quizId, userId);
        if (prog.isFinished()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already finished this quiz.");
        }

        List<Flashcard> cards = quiz.getSelectedFlashcards();
        if (cards.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No questions in this quiz.");
        }
        int idx = prog.getCurrentIndex();
        if (idx >= cards.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No more questions left.");
        }

        return cards.get(idx);
    }

    /** Handles an answer submission and returns feedback (correct?, finished?, next question?). */
    public QuizAnswerResponseDTO processAnswerWithFeedback(
            Long quizId, Long flashcardId, String selectedAnswer, Long userId) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
        if (!QuizStatus.IN_PROGRESS.equals(quiz.getQuizStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quiz not in progress.");
        }

        QuizProgressStore.ProgressState prog = QuizProgressStore.getProgress(quizId, userId);
        if (prog.isFinished()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You already finished this quiz.");
        }

        List<Flashcard> cards = quiz.getSelectedFlashcards();
        int idx = prog.getCurrentIndex();
        if (idx >= cards.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No more questions left.");
        }

        Flashcard current = cards.get(idx);
        if (!current.getId().equals(flashcardId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong flashcard ID for current question.");
        }

        prog.setTotalAttempts(prog.getTotalAttempts() + 1);

        boolean correct = current.getAnswer().equalsIgnoreCase(selectedAnswer.trim());
        if (correct) {
            prog.setTotalCorrect(prog.getTotalCorrect() + 1);
            if (idx + 1 < cards.size()) {
                prog.setCurrentIndex(idx + 1);
            } else {
                prog.setFinished(true);
                long elapsed = System.currentTimeMillis() - prog.getStartTimeMillis();
                statisticsService.recordQuizStats(findUserById(userId), quiz,
                        prog.getTotalCorrect(), prog.getTotalAttempts(), elapsed);
            }
        }

        boolean allFinished = checkAllPlayersDone(quiz);
        broadcastProgress(quizId, cards.size(), allFinished);

        QuizAnswerResponseDTO resp = new QuizAnswerResponseDTO();
        resp.setWasCorrect(correct);
        resp.setFinished(prog.isFinished());
        resp.setNextQuestion(prog.isFinished() ? null
                : FlashcardMapper.toDTO(cards.get(prog.getCurrentIndex())));
        return resp;
    }

    /* Check completion across participants. */
    private boolean checkAllPlayersDone(Quiz quiz) {
        List<QuizProgressStore.UserProgressEntry> all = QuizProgressStore.getProgressForQuiz(quiz.getId());
        if (quiz.getIsMultiple()) {
            return all.size() >= 2 && all.stream().allMatch(e -> e.getProgress().isFinished());
        }
        return !all.isEmpty() && all.get(0).getProgress().isFinished();
    }

    /* Web-Socket scoreboard broadcast */
    private void broadcastProgress(Long quizId, int totalQ, boolean finished) {
        List<PlayerProgressDTO> board = QuizProgressStore.getProgressForQuiz(quizId).stream()
                .map(e -> {
                    PlayerProgressDTO dto = new PlayerProgressDTO();
                    dto.setUserId(e.getUserId());
                    dto.setScore(e.getProgress().getTotalCorrect());
                    dto.setAnsweredQuestions(e.getProgress().getCurrentIndex());
                    return dto;
                }).collect(Collectors.toList());

        QuizUpdateMessageDTO msg = new QuizUpdateMessageDTO();
        msg.setQuizId(quizId);
        msg.setUpdateType(finished ? "finished" : "progress");
        msg.setTotalQuestions((long) totalQ);
        msg.setPlayerProgress(board);

        messagingTemplate.convertAndSend("/topic/quizUpdates/" + quizId, msg);
    }

    /* ────────────────── Misc helpers ────────────────── */

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
