package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ch.uzh.ifi.hase.soprafs24.entity.Score;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ScoreDTO;

@Component
public class ScoreMapper {

    public ScoreDTO toDTO(Score score) {
        ScoreDTO dto = new ScoreDTO();
        dto.setId(score.getId());
        dto.setQuiz(score.getQuiz());
        dto.setUser(score.getUser());
        dto.setCorrectQuestions(score.getCorrectQuestions());
        dto.setTotalQuestions(score.getTotalQuestions());
        return dto;
    }

    public List<ScoreDTO> toDTOList(List<Score> scores) {
        return scores.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Score toEntity(ScoreDTO dto) {
        Score score = new Score();
        score.setId(dto.getId());
        score.setQuiz(dto.getQuiz());
        score.setUser(dto.getUser());
        score.setCorrectQuestions(dto.getCorrectQuestions());
        score.setTotalQuestions(dto.getTotalQuestions());
        return score;
    }
}
