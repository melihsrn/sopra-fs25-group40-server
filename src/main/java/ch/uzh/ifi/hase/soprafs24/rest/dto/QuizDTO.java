package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.Date;
import java.util.List;

import ch.uzh.ifi.hase.soprafs24.entity.Deck;
import ch.uzh.ifi.hase.soprafs24.entity.Invitation;
import ch.uzh.ifi.hase.soprafs24.entity.Score;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO representing a quiz instance, combining:
 *   • invitation-related data (decks, scores, invitation)
 *   • quiz-runtime data (deckId, timing, status, etc.)
 */
@Getter
@Setter
public class QuizDTO {

    /* ─────────────── Shared identifiers ─────────────── */
    private Long id;

    /* ─────────────── Invitation side (from main) ────── */
    private List<Deck>   decks;       // full decks for host/guest view
    private List<Score>  scores;      // per-user scores
    private Invitation   invitation;  // original invitation entity

    /* ─────────────── Quiz-runtime side (from shak_branch) ────── */
    private Long   deckId;       // single deck reference for quick lookup
    private Date   startTime;
    private Date   endTime;
    private int    timeLimit;    // seconds or minutes – service defines semantics
    private String quizStatus;   // e.g. “RUNNING”, “FINISHED”
    private Boolean isMultiple;  // multiple-choice mode flag
}
