package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.*;

import ch.uzh.ifi.hase.soprafs24.entity.Quiz;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizDTO;

@Mapper
public class QuizMapper {

    public static QuizDTO toDTO(Quiz quiz) {
        QuizDTO dto = new QuizDTO();
        dto.setId(quiz.getId());
        dto.setDecks(quiz.getDecks());
        dto.setScores(quiz.getScores());
        dto.setStartTime(quiz.getStartTime());
        dto.setEndTime(quiz.getEndTime());
        dto.setTimeLimit(quiz.getTimeLimit());
        dto.setQuizStatus(quiz.getQuizStatus());
        dto.setWinner(quiz.getWinner());
        dto.setIsMultiple(quiz.getIsMultiple());
        dto.setQuizInvitation(quiz.getQuizInvitation());
        return dto;
    }

    public static List<QuizDTO> toDTOList(List<Quiz> quizzes) {
        return quizzes.stream().map(QuizMapper::toDTO).collect(Collectors.toList());
    }

    public static Quiz toEntity(QuizDTO dto) {
        Quiz quiz = new Quiz();
        quiz.setId(dto.getId());
        quiz.setDecks(dto.getDecks());
        quiz.setScores(dto.getScores());
        quiz.setStartTime(dto.getStartTime());
        quiz.setEndTime(dto.getEndTime());
        quiz.setTimeLimit(dto.getTimeLimit());
        quiz.setQuizStatus(dto.getQuizStatus());
        quiz.setWinner(dto.getWinner());
        quiz.setIsMultiple(dto.getIsMultiple());
        quiz.setQuizInvitation(dto.getQuizInvitation());
        return quiz;
    }

}
