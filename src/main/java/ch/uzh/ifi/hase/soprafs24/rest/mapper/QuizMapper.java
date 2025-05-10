package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import ch.uzh.ifi.hase.soprafs24.entity.*;
import org.springframework.stereotype.Component;

import ch.uzh.ifi.hase.soprafs24.constant.QuizStatus;
import ch.uzh.ifi.hase.soprafs24.repository.DeckRepository;
import ch.uzh.ifi.hase.soprafs24.repository.QuizRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ScoreRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizDTO;

@Component
public class QuizMapper {

    /* ───────────────────── Dependencies (needed for invitation logic) ───────────────────── */
    private final ScoreRepository scoreRepository;
    private final QuizRepository  quizRepository;
    private final DeckRepository  deckRepository;

    public QuizMapper(ScoreRepository scoreRepository,
                      QuizRepository quizRepository,
                      DeckRepository deckRepository) {
        this.scoreRepository = scoreRepository;
        this.quizRepository  = quizRepository;
        this.deckRepository  = deckRepository;
    }

    /* ───────────────────── DTO ↔ Entity helpers ───────────────────── */

    /**
     * Generic mapper used by both branches.  
     * (Alias kept for backward-compatibility.)
     */
    public QuizDTO convertEntityToDTO(Quiz quiz) {
        QuizDTO dto = new QuizDTO();

        /* identifiers & invitation side (from main) */
        dto.setId(quiz.getId());
        dto.setDecks(quiz.getDecks());
        dto.setScores(quiz.getScores());

        /* single-deck shortcut (from shak_branch) */
        if (quiz.getDecks() != null && !quiz.getDecks().isEmpty()) {
            dto.setDeckId(quiz.getDecks().get(0).getId());
        }

        /* runtime metadata (shared) */
        dto.setStartTime(quiz.getStartTime());
        dto.setEndTime(quiz.getEndTime());
        dto.setTimeLimit(quiz.getTimeLimit());
        dto.setIsMultiple(quiz.getIsMultiple());
        dto.setQuizStatus(quiz.getQuizStatus() != null
                ? quiz.getQuizStatus().toString()
                : "UNKNOWN");

        return dto;
    }

    /** Handy alias for callers that still use the old name. */
    public QuizDTO toDTO(Quiz quiz) {
        return convertEntityToDTO(quiz);
    }

    public List<QuizDTO> toDTOList(List<Quiz> quizzes) {
        return quizzes.stream().map(this::convertEntityToDTO).collect(Collectors.toList());
    }

    /**
     * Only used in a few legacy tests – kept but trimmed to current DTO fields.
     */
    public Quiz toEntity(QuizDTO dto) {
        Quiz quiz = new Quiz();
        quiz.setId(dto.getId());
        quiz.setDecks(dto.getDecks());
        quiz.setScores(dto.getScores());
        quiz.setStartTime(dto.getStartTime());
        quiz.setEndTime(dto.getEndTime());
        quiz.setTimeLimit(dto.getTimeLimit());
        quiz.setIsMultiple(dto.getIsMultiple());
        if (dto.getQuizStatus() != null) {
            try {
                quiz.setQuizStatus(QuizStatus.valueOf(dto.getQuizStatus()));
            } catch (IllegalArgumentException e) {
                quiz.setQuizStatus(null);
            }
        }
        return quiz;
    }

    /* ───────────────────── Invitation → Quiz factory (from main) ───────────────────── */

    /**
     * Creates and persists a new {@link Quiz} based on an accepted {@link Invitation},
     * initialising both {@link Score} objects and attaching the sender/receiver decks.
     */
    public Quiz fromInvitationToEntity(Invitation invitation) {

        Quiz quiz = new Quiz();
        quiz.setStartTime(new Date());
        quiz.setTimeLimit(invitation.getTimeLimit());
        quiz.setQuizStatus(QuizStatus.WAITING);
        quiz.setIsMultiple(true);

        /* 1 make sure decks are managed entities */
        List<Deck> managedDecks = invitation.getDecks().stream()
            .map(deck -> deckRepository.findById(deck.getId())
                    .orElseThrow(() -> new RuntimeException("Deck not found: " + deck.getId())))
            .collect(Collectors.toList());
        quiz.setDecks(new ArrayList<>(managedDecks));

        /* ── 2. Use *all* flashcards from those decks ───────────────────────────── */
        List<Flashcard> cardPool = managedDecks.stream()
                .flatMap(d -> d.getFlashcards().stream())
                .collect(Collectors.toList());

        if (cardPool.isEmpty()) {
            throw new IllegalStateException("Invited decks contain no flashcards.");
        }

        Collections.shuffle(cardPool);                  // randomise order
        quiz.setSelectedFlashcards(new ArrayList<>(cardPool));   // ← keep them all

        /* 2️⃣  save quiz first (so scores have FK) */
        quiz = quizRepository.save(quiz);

        /* 3️⃣  create scores for both users */
        User sender   = invitation.getFromUser();
        User receiver = invitation.getToUser();

        int totalQ = quiz.getSelectedFlashcards().size();

        Score senderScore = new Score();
        senderScore.setUser(sender);
        senderScore.setQuiz(quiz);
        senderScore.setTotalQuestions(totalQ);
        scoreRepository.save(senderScore);

        Score receiverScore = new Score();
        receiverScore.setUser(receiver);
        receiverScore.setQuiz(quiz);
        receiverScore.setTotalQuestions(totalQ);
        scoreRepository.save(receiverScore);

        /* 4️⃣  attach scores to quiz and persist */
        quiz.getScores().add(senderScore);
        quiz.getScores().add(receiverScore);
        quizRepository.save(quiz);

        return quiz;
    }
}
