package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

import ch.uzh.ifi.hase.soprafs24.entity.Deck;

public class InvitationDTO {

    private Long id;

    private List<Deck> decks;

    private Long fromUserId;

    private Long toUserId;

    private Long quizId;

    private int timeLimit;

    private Boolean isAccepted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public List<Deck> getDecks() {
        return decks;
      }
    
    public void setDecks(List<Deck> decks) {
        this.decks = decks;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Boolean getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(Boolean isAccepted) {
        this.isAccepted = isAccepted;
    }

}
