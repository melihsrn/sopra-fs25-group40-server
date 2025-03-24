package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Quiz;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.service.QuizService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizInvitationDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.QuizMapper;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Quiz Controller
 * Handles quiz invitations between users.
 */
@RestController
@RequestMapping("/quiz")
public class QuizController {

    private final UserService userService;
    private final QuizService quizService;

    public QuizController(UserService userService, QuizService quizService) {
        this.userService = userService;
        this.quizService = quizService;
    }


    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Quiz createQuiz(@RequestBody @Valid QuizInvitationDTO quizInvitationDTO) {
        // Create the invitation
        Quiz quiz = quizService.createQuiz(quizInvitationDTO);

        return quiz;
    }

    @PostMapping("/invite")
    @ResponseStatus(HttpStatus.CREATED)
    public Quiz sendQuizInvitation(@RequestBody @Valid QuizDTO quizDTO) {
        // Create the invitation
        Quiz quiz = quizService.sendInvitation(QuizMapper.toEntity(quizDTO));

        return quiz;
    }
}
