package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ch.uzh.ifi.hase.soprafs24.constant.QuizStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Deck;
import ch.uzh.ifi.hase.soprafs24.entity.Quiz;
import ch.uzh.ifi.hase.soprafs24.entity.Score;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.DeckRepository;
import ch.uzh.ifi.hase.soprafs24.repository.QuizRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ScoreRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizInvitationDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

@Component
public class QuizMapper {

    private final UserService userService;
    private final ScoreRepository scoreRepository;
    private final QuizRepository quizRepository;
    private final DeckRepository deckRepository;

    public QuizMapper(UserService userService,
                        ScoreRepository scoreRepository,
                        QuizRepository quizRepository,
                        DeckRepository deckRepository) {
        this.userService = userService;
        this.scoreRepository = scoreRepository;
        this.quizRepository = quizRepository;
        this.deckRepository = deckRepository;
    }

    public QuizDTO toDTO(Quiz quiz) {
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
        return dto;
    }

    public List<QuizDTO> toDTOList(List<Quiz> quizzes) {
        return quizzes.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Quiz toEntity(QuizDTO dto) {
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
        return quiz;
    }

    public Quiz fromInvitationToEntity(QuizInvitationDTO dto) {
        Quiz quiz = new Quiz();

        quiz.setStartTime(new Date());
        quiz.setTimeLimit(dto.getTimeLimit());
        quiz.setQuizStatus(QuizStatus.WAITING);
        quiz.setIsMultiple(dto.getIsMultiple());

        // ✅ Step 1: Retrieve Decks from DB to ensure they are managed
        List<Deck> managedDecks = dto.getDecks().stream()
            .map(deck -> deckRepository.findById(deck.getId()).orElseThrow(
                () -> new RuntimeException("Deck not found: " + deck.getId())
            ))
            .collect(Collectors.toList());

        quiz.setDecks(new ArrayList<>(managedDecks));

        quiz = quizRepository.save(quiz);

        User senderUser = userService.getUserById(dto.getFromUserId());
        User invitedUser = userService.getUserById(dto.getToUserId());

        Score senderScore = new Score();
        senderScore.setUser(senderUser);
        senderScore.setQuiz(quiz);
        scoreRepository.save(senderScore);

        Score invitedScore = new Score();
        invitedScore.setUser(invitedUser);
        invitedScore.setQuiz(quiz);
        scoreRepository.save(invitedScore);

        quiz.getScores().add(senderScore);  // Adds senderScore to existing list
        quiz.getScores().add(invitedScore);
        quizRepository.save(quiz);

        return quiz;
    }

}
