package ch.uzh.ifi.hase.soprafs24.service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

public class QuizProgressStore {

    // Key format: "quizId:userId"
    private static final Map<String, ProgressState> progressMap = new HashMap<>();

    public static ProgressState getProgress(Long quizId, Long userId) {
        String key = quizId + ":" + userId;
        return progressMap.computeIfAbsent(key, k -> new ProgressState());
    }

    // New helper: retrieve progress for all users in a given quiz.
    public static List<UserProgressEntry> getProgressForQuiz(Long quizId) {
        String quizPrefix = quizId + ":";
        return progressMap.entrySet().stream()
                .filter(e -> e.getKey().startsWith(quizPrefix))
                .map(e -> {
                    String[] parts = e.getKey().split(":");
                    Long userId = Long.valueOf(parts[1]);
                    return new UserProgressEntry(userId, e.getValue());
                })
                .collect(Collectors.toList());
    }

    public static class ProgressState {
        private int currentIndex = 0;      // index of the current question
        private int totalCorrect = 0;      // number of correct answers
        private int totalAttempts = 0;     // number of attempts
        private boolean isFinished = false;
        // Record the start time (in millis) when the progress object is first created
        private long startTimeMillis = System.currentTimeMillis();

        // Getters and Setters
        public int getCurrentIndex() { return currentIndex; }
        public void setCurrentIndex(int currentIndex) { this.currentIndex = currentIndex; }

        public int getTotalCorrect() { return totalCorrect; }
        public void setTotalCorrect(int totalCorrect) { this.totalCorrect = totalCorrect; }

        public int getTotalAttempts() { return totalAttempts; }
        public void setTotalAttempts(int totalAttempts) { this.totalAttempts = totalAttempts; }

        public boolean isFinished() { return isFinished; }
        public void setFinished(boolean finished) { isFinished = finished; }

        public long getStartTimeMillis() { return startTimeMillis; }
        public void setStartTimeMillis(long startTimeMillis) { this.startTimeMillis = startTimeMillis; }
    }

    // Represents a user's progress, pairing userId with their progress state.
    public static class UserProgressEntry {
        private Long userId;
        private ProgressState progress;

        public UserProgressEntry(Long userId, ProgressState progress) {
            this.userId = userId;
            this.progress = progress;
        }

        public Long getUserId() { return userId; }
        public ProgressState getProgress() { return progress; }
    }
}
