package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizDTO;
import ch.uzh.ifi.hase.soprafs24.entity.Quiz;
import org.springframework.stereotype.Component;

@Component
public class QuizMapper {
    public QuizDTO convertEntityToDTO(Quiz quiz) {
        QuizDTO dto = new QuizDTO();
        dto.setId(quiz.getId());
        // Assume that for a single-player quiz you use the first deck’s id
        if (quiz.getDecks() != null && !quiz.getDecks().isEmpty()) {
            dto.setDeckId(quiz.getDecks().get(0).getId());
        }
        dto.setStartTime(quiz.getStartTime());
        dto.setEndTime(quiz.getEndTime());
        dto.setTimeLimit(quiz.getTimeLimit());
        dto.setIsMultiple(quiz.getIsMultiple());
        dto.setQuizStatus(quiz.getQuizStatus() != null ? quiz.getQuizStatus().toString() : "UNKNOWN");
        return dto;
    }
}
