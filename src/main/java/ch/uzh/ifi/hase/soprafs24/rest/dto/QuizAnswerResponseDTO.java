package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class QuizAnswerResponseDTO {
    private boolean wasCorrect;
    private boolean isFinished;
    private FlashcardDTO nextQuestion; // can be null if finished

    public boolean isWasCorrect() {
        return wasCorrect;
    }
    public void setWasCorrect(boolean wasCorrect) {
        this.wasCorrect = wasCorrect;
    }

    public boolean isFinished() {
        return isFinished;
    }
    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public FlashcardDTO getNextQuestion() {
        return nextQuestion;
    }
    public void setNextQuestion(FlashcardDTO nextQuestion) {
        this.nextQuestion = nextQuestion;
    }

}
