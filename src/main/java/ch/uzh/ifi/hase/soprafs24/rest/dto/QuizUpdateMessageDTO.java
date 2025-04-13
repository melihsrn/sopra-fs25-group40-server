package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

public class QuizUpdateMessageDTO {
    private Long quizId;
    private String updateType; // e.g., "progress", "scoreUpdate", etc.
    private Long totalQuestions;
    private List<PlayerProgressDTO> playerProgress;

    public Long getQuizId() {
        return quizId;
    }
    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }
    public String getUpdateType() {
        return updateType;
    }
    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }
    public Long getTotalQuestions() {
        return totalQuestions;
    }
    public void setTotalQuestions(Long totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
    public List<PlayerProgressDTO> getPlayerProgress() {
        return playerProgress;
    }
    public void setPlayerProgress(List<PlayerProgressDTO> playerProgress) {
        this.playerProgress = playerProgress;
    }

    // Inner DTO to represent each player's progress
    public static class PlayerProgressDTO {
        private Long userId;
        private int score;
        private int answeredQuestions;

        public Long getUserId() {
            return userId;
        }
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        public int getScore() {
            return score;
        }
        public void setScore(int score) {
            this.score = score;
        }
        public int getAnsweredQuestions() {
            return answeredQuestions;
        }
        public void setAnsweredQuestions(int answeredQuestions) {
            this.answeredQuestions = answeredQuestions;
        }
    }
}
