package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class QuizAnswerRequestDTO {
    private Long quizId;
    private Long flashcardId;
    private String selectedAnswer;
    private Long userId;

    public Long getQuizId() {
        return quizId;
    }
    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }
    public Long getFlashcardId() {
        return flashcardId;
    }
    public void setFlashcardId(Long flashcardId) {
        this.flashcardId = flashcardId;
    }
    public String getSelectedAnswer() {
        return selectedAnswer;
    }
    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
