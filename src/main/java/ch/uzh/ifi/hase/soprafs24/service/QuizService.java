// QuizService.java
package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.QuizStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.InvitationDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizAnswerResponseDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizUpdateMessageDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizUpdateMessageDTO.PlayerProgressDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.FlashcardMapper;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.QuizMapper;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    /* ──────────────── Dependencies ──────────────── */
    private final UserService           userService;
    private final QuizRepository        quizRepository;
    private final UserRepository        userRepository;
    private final InvitationRepository  invitationRepository;
    private final DeckRepository        deckRepository;
    private final QuizMapper            quizMapper;
    private final FlashcardMapper       flashcardMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final StatisticsService     statisticsService;
    private final ScoreRepository       scoreRepository;

    public QuizService(UserService            userService,
                       QuizRepository         quizRepository,
                       UserRepository         userRepository,
                       InvitationRepository   invitationRepository,
                       DeckRepository         deckRepository,
                       QuizMapper             quizMapper,
                       FlashcardMapper        flashcardMapper,
                       SimpMessagingTemplate  messagingTemplate,
                       StatisticsService      statisticsService,
                       ScoreRepository        scoreRepository) {
        this.userService          = userService;
        this.quizRepository       = quizRepository;
        this.userRepository       = userRepository;
        this.invitationRepository = invitationRepository;
        this.deckRepository       = deckRepository;
        this.quizMapper           = quizMapper;
        this.flashcardMapper      = flashcardMapper;
        this.messagingTemplate    = messagingTemplate;
        this.statisticsService    = statisticsService;
        this.scoreRepository      = scoreRepository;
    }

    /* ╔═════════════════ Invitation section ═══════════════╗ */

    public Invitation getInvitationById(Long id) {
        return invitationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found"));
    }

    public List<Invitation> getInvitationByFromUserId(Long uid) {
        return invitationRepository.findByFromUser(userService.getUserById(uid));
    }

    public List<Invitation> getInvitationByToUserId(Long uid) {
        return invitationRepository.findByToUser(userService.getUserById(uid));
    }

    @Transactional
    public void deleteInvitationById(Long invitationId) {
        Invitation inv = getInvitationById(invitationId);
        Quiz quiz = inv.getQuiz();                 // might be null
        if (quiz != null) {
            quizRepository.save(quiz);
        }
        inv.setQuiz(null);
        invitationRepository.save(inv);            // flush FK change
        invitationRepository.delete(inv);
    }


    private void ensureInvitable(User u) {
        if (u.getStatus() == UserStatus.OFFLINE || u.getStatus() == UserStatus.PLAYING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User cannot be invited while OFFLINE or PLAYING.");
        }
    }

    public Invitation createInvitation(InvitationDTO dto) {
        User from = userService.getUserById(dto.getFromUserId());
        User to   = userService.getUserById(dto.getToUserId());
        ensureInvitable(from);
        ensureInvitable(to);

        Invitation inv = new Invitation();
        inv.setFromUser(from);
        inv.setToUser(to);
        inv.setTimeLimit(dto.getTimeLimit());
        inv.setIsAccepted(false);

        /* attach decks */
        List<Deck> decks = dto.getDeckIds().stream()
                .map(id -> deckRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Deck not found: " + id)))
                .collect(Collectors.toList());
        if (decks.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one deck must be selected.");
        }
        inv.setDecks(decks);

        return invitationRepository.save(inv);
    }

    /** Factory: Invitation → Quiz. */
    @Transactional
    public Quiz createQuiz(Long invitationId) {
        Invitation inv = getInvitationById(invitationId);
        Quiz quiz = quizMapper.fromInvitationToEntity(inv);   // mapper saves once

        inv.setQuiz(quiz);                // set FK
        invitationRepository.save(inv);   // save owning side
//        quizRepository.flush();
        return quizRepository.saveAndFlush(quiz);  // already managed & saved
    }

    @Transactional
    public void confirmedInvitation(Long invitationId) {
        Invitation inv = getInvitationById(invitationId);
        Quiz quiz      = inv.getQuiz();

        quiz.setQuizStatus(QuizStatus.IN_PROGRESS);
        quiz.setStartTime(new Date());

        inv.setIsAccepted(true);
        inv.setIsAcceptedDate(new Date());

        inv.getFromUser().setStatus(UserStatus.PLAYING);
        inv.getToUser().setStatus(UserStatus.PLAYING);

        userRepository.save(inv.getFromUser());
        userRepository.save(inv.getToUser());
        quizRepository.save(quiz);
        invitationRepository.save(inv);
    }

    public void rejectedInvitation(Long id) {
        Invitation inv = getInvitationById(id);
        if (inv.getQuiz() != null) {
            quizRepository.delete(inv.getQuiz());
        }
        invitationRepository.delete(inv);
    }

    /** Return earliest accepted invite from sender and clean the rest. */
    public Invitation findInvitationByFromUserIdAndIsAcceptedTrue(Long fromUserId) {
        // Fetch all invitations sent by this user
        List<Invitation> invitations = getInvitationByFromUserId(fromUserId);

        // Filter to include only accepted invitations
        List<Invitation> acceptedInvitations = invitations.stream()
                .filter(Invitation::getIsAccepted)
                .sorted(Comparator.comparing(Invitation::getIsAcceptedDate)) // sort by accepted date ascending
                .collect(Collectors.toList());

        // If no accepted invitations found, return null
        if (acceptedInvitations.isEmpty()) {
            return null;
        }

        // The first one is the earliest accepted invitation
        Invitation earliestAccepted = acceptedInvitations.get(0);

        // All others are late accepted invitations – considered as rejected
        List<Invitation> lateAcceptedInvitations = acceptedInvitations.subList(1, acceptedInvitations.size());

        for (Invitation lateInvitation : lateAcceptedInvitations) {
            // Delete the corresponding quiz if it exists
            if (lateInvitation.getQuiz() != null) {
                quizRepository.delete(lateInvitation.getQuiz());
            }
            User toUser = lateInvitation.getToUser();
            toUser.setStatus(UserStatus.ONLINE);
            userRepository.save(toUser);

            // Delete the late invitation
            invitationRepository.delete(lateInvitation);
        }
        // Return the only accepted and kept invitation
        return earliestAccepted;
    }


    /* ╚═════════════════ Invitation section ═══════════════╝ */
    /* ╔═════════════════ Quiz-runtime section ══════════════╗ */

    /** Create a quiz directly from a single deck (solo or host). */
    public Quiz startQuiz(Long deckId,
                          Integer numberOfQuestions,
                          Integer timeLimit,
                          Boolean  isMultiple) {

        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deck not found"));

        List<Flashcard> all = deck.getFlashcards();
        int n = numberOfQuestions == null || numberOfQuestions <= 0
                ? all.size() : numberOfQuestions;

        if (all.size() < n) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Not enough flashcards in the deck for " + n + " questions");
        }

        Collections.shuffle(all);
        List<Flashcard> selected = new ArrayList<>(all.subList(0, n));

        Quiz quiz = new Quiz();
        quiz.setTimeLimit(timeLimit == null ? 0 : timeLimit);
        quiz.setStartTime(new Date());
        quiz.setIsMultiple(Boolean.TRUE.equals(isMultiple));
        quiz.setQuizStatus(quiz.getIsMultiple() ? QuizStatus.WAITING : QuizStatus.IN_PROGRESS);
        quiz.getDecks().add(deck);
        quiz.setSelectedFlashcards(selected);

        deck.setQuiz(quiz);
        return quizRepository.save(quiz);
    }

    /** For multi-player: flip WAITING → IN_PROGRESS when second player joins. */
    public Quiz startMultiplayerIfReady(Long quizId) {
        Quiz q = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        if (q.getIsMultiple()
                && QuizStatus.WAITING.equals(q.getQuizStatus())
                && q.getDecks().size() >= 2) {
            q.setQuizStatus(QuizStatus.IN_PROGRESS);
            q.setStartTime(new Date());
            quizRepository.save(q);
        }
        return q;
    }

    public Flashcard getCurrentQuestion(Long quizId, Long userId) {
        Quiz q = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        if (q.getQuizStatus() != QuizStatus.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quiz not in progress.");
        }

        QuizProgressStore.ProgressState prog = QuizProgressStore.getProgress(quizId, userId);
        if (prog.isFinished()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You already finished this quiz.");
        }

        List<Flashcard> cards = q.getSelectedFlashcards();
        if (cards.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No questions in this quiz.");
        }

        int idx = prog.getCurrentIndex();
        if (idx >= cards.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No more questions.");
        }
        return cards.get(idx);
    }

    @Transactional
    public QuizAnswerResponseDTO processAnswerWithFeedback(
            Long quizId, Long flashcardId, String answer, Long userId) {

        /* ───── validation ───── */
        Quiz q = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
        if (q.getQuizStatus() != QuizStatus.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quiz not in progress.");
        }

        QuizProgressStore.ProgressState prog = QuizProgressStore.getProgress(quizId, userId);
        if (prog.isFinished()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You already finished this quiz.");
        }

        List<Flashcard> cards = q.getSelectedFlashcards();
        int idx = prog.getCurrentIndex();
        if (idx >= cards.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No more questions.");
        }

        Flashcard cur = cards.get(idx);
        if (!cur.getId().equals(flashcardId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong flashcard ID for current question.");
        }

        /* ───── evaluate answer ───── */
        prog.setTotalAttempts(prog.getTotalAttempts() + 1);
        boolean correct = cur.getAnswer().equalsIgnoreCase(answer.trim());

        if (correct) {
            prog.setTotalCorrect(prog.getTotalCorrect() + 1);

            /* update persistent Score row */
            Score score = scoreRepository.findByQuizIdAndUserId(quizId, userId);
            if (score == null) {                    // safety: shouldn’t happen
                score = new Score();
                score.setQuiz(q);
                score.setUser(findUser(userId));
                score.setTotalQuestions(cards.size());
            }
            score.setCorrectQuestions(score.getCorrectQuestions() + 1);
            scoreRepository.save(score);
        }

        /* advance pointer or finish user */
        if (correct && idx + 1 < cards.size()) {
            prog.setCurrentIndex(idx + 1);
        } else if (correct && idx + 1 == cards.size()) {
            prog.setFinished(true);
        }

        long elapsed = System.currentTimeMillis() - prog.getStartTimeMillis();
        statisticsService.recordQuizStats(findUser(userId), q,
                prog.getTotalCorrect(), prog.getTotalAttempts(), elapsed);

        /* ───── end-of-quiz detection ───── */
        boolean allFinished = checkAllFinished(q);

        boolean timeExpired = q.getTimeLimit() > 0 &&
                (System.currentTimeMillis() - q.getStartTime().getTime() >= q.getTimeLimit() * 1000L);

        if (allFinished || timeExpired) {
            q.setQuizStatus(QuizStatus.COMPLETED);
            q.setEndTime(new Date());

            QuizProgressStore.getProgressForQuiz(quizId).forEach(entry -> {
                QuizProgressStore.ProgressState p = entry.getProgress();
                long el = System.currentTimeMillis() - p.getStartTimeMillis();
                statisticsService.recordQuizStats(findUser(entry.getUserId()), q,
                        p.getTotalCorrect(), p.getTotalAttempts(), el);
            });


            if (q.getInvitation() != null) {
                User u1 = q.getInvitation().getFromUser();
                User u2 = q.getInvitation().getToUser();
                u1.setStatus(UserStatus.ONLINE);
                u2.setStatus(UserStatus.ONLINE);
                userRepository.saveAll(List.of(u1, u2));
            }
            quizRepository.save(q);
        }

        /* ───── broadcast & response ───── */
        broadcastProgress(quizId, cards.size(), allFinished || timeExpired);

        QuizAnswerResponseDTO dto = new QuizAnswerResponseDTO();
        dto.setWasCorrect(correct);
        dto.setFinished(prog.isFinished());
        dto.setNextQuestion(prog.isFinished() ? null
                : flashcardMapper.toDTO(cards.get(prog.getCurrentIndex())));
        return dto;
    }


    /* helpers ------------------------------------------------------------ */

    private boolean checkAllFinished(Quiz q) {
        List<QuizProgressStore.UserProgressEntry> ps = QuizProgressStore.getProgressForQuiz(q.getId());
        return q.getIsMultiple()
                ? ps.size() >= 2 && ps.stream().allMatch(e -> e.getProgress().isFinished())
                : !ps.isEmpty() && ps.get(0).getProgress().isFinished();
    }

    private void broadcastProgress(Long quizId, int total, boolean finished) {
        List<PlayerProgressDTO> board = QuizProgressStore.getProgressForQuiz(quizId).stream()
                .map(e -> {
                    PlayerProgressDTO d = new PlayerProgressDTO();
                    d.setUserId(e.getUserId());
                    d.setScore(e.getProgress().getTotalCorrect());
                    d.setAnsweredQuestions(e.getProgress().getCurrentIndex());
                    return d;
                }).collect(Collectors.toList());

        QuizUpdateMessageDTO msg = new QuizUpdateMessageDTO();
        msg.setQuizId(quizId);
        msg.setUpdateType(finished ? "finished" : "progress");
        msg.setTotalQuestions((long) total);
        msg.setPlayerProgress(board);

        messagingTemplate.convertAndSend("/topic/quizUpdates/" + quizId, msg);
    }

    public Quiz getQuizStatus(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    /* ╚═════════════════ Quiz-runtime section ═════════════╝ */
}
