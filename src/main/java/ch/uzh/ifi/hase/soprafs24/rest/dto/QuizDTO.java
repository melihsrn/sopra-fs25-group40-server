package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.Date;

public class QuizDTO {
    private Long id;
    private Long deckId; // assume one deck per player, so you can expose one deck id
    private Date startTime;
    private Date endTime;
    private int timeLimit;
    private String quizStatus;
    private Boolean isMultiple;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getDeckId() {
        return deckId;
    }
    public void setDeckId(Long deckId) {
        this.deckId = deckId;
    }
    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public Date getEndTime() {
        return endTime;
    }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    public int getTimeLimit() {
        return timeLimit;
    }
    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }
    public String getQuizStatus() {
        return quizStatus;
    }
    public void setQuizStatus(String quizStatus) {
        this.quizStatus = quizStatus;
    }
    public Boolean getIsMultiple() {
        return isMultiple;
    }
    public void setIsMultiple(Boolean isMultiple) {
        this.isMultiple = isMultiple;
    }
}
