package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class QuizStartRequestDTO {
    private Long deckId;
    private int numberOfQuestions;
    private int timeLimit;
    private Boolean isMultiple;

    public Long getDeckId() {
        return deckId;
    }
    public void setDeckId(Long deckId) {
        this.deckId = deckId;
    }
    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }
    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }
    public int getTimeLimit() {
        return timeLimit;
    }
    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }
    public Boolean getIsMultiple() {
        return isMultiple;
    }
    public void setIsMultiple(Boolean isMultiple) {
        this.isMultiple = isMultiple;
    }
}
