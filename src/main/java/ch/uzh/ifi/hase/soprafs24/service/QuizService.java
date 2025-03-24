package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.QuizStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Quiz;
import ch.uzh.ifi.hase.soprafs24.entity.Score;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.QuizRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ScoreRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizInvitationDTO;

import java.util.Arrays;
import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class QuizService {

    public UserService userService;
    public ScoreRepository scoreRepository;
    public QuizRepository quizRepository;

    
    public Quiz createQuiz(QuizInvitationDTO quizInvitationDTO){
        User senderUser = userService.getUserById(quizInvitationDTO.getFromUserId());
        User invitedUser = userService.getUserById(quizInvitationDTO.getToUserId());

        userService.isStatusAvailable(invitedUser.getStatus());

        Quiz quiz = new Quiz();
        quiz.setDecks(quizInvitationDTO.getDecks());
        quiz.setStartTime(new Date());
        quiz.setTimeLimit(quizInvitationDTO.getTimeLimit());
        quiz.setQuizStatus(QuizStatus.WAITING);
        quiz.setIsMultiple(quizInvitationDTO.getIsMultiple());
        quiz.setQuizInvitation(quizInvitationDTO);

        Score senderScore = new Score();
        senderScore.setUser(senderUser);
        senderScore.setQuiz(quiz);
        scoreRepository.save(senderScore);

        Score invitedScore = new Score();
        invitedScore.setUser(invitedUser);
        invitedScore.setQuiz(quiz);
        scoreRepository.save(invitedScore);

        quiz.setScores(Arrays.asList(senderScore, invitedScore));
        quizRepository.save(quiz);

        return quiz;

    }

    public Quiz sendInvitation(Quiz quiz) {
        Boolean response = sendInvitationNotification(quiz);

        if (response){
            quiz.setQuizStatus(QuizStatus.IN_PROGRESS);
        } else{
            quiz.setQuizStatus(QuizStatus.CANCELLED);
        }
        return quiz;
    }

    public Boolean sendInvitationNotification(Quiz quiz) {
        return false;
    }
}
