package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Quiz;
import ch.uzh.ifi.hase.soprafs24.service.QuizService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizInvitationDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizResponseDTO;
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
    private final QuizMapper quizMapper;
    private final QuizService quizService;

    public QuizController(UserService userService, QuizService quizService, QuizMapper quizMapper) {
        this.userService = userService;
        this.quizService = quizService;
        this.quizMapper = quizMapper;
    }

    @PostMapping("/invite")
    @ResponseStatus(HttpStatus.OK)
    public void sendQuizInvitation(@RequestBody @Valid QuizInvitationDTO quizInvitationDTO) {
        // Create the invitation
        Quiz quiz = quizMapper.fromInvitationToEntity(quizInvitationDTO);
        System.out.println(quiz.toString());
        quizService.sendInvitationNotification(quizInvitationDTO.getFromUserId(), quizInvitationDTO.getToUserId(), quiz);
    }

    @PutMapping("/respond")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void respondToQuizInvitation(@RequestBody QuizResponseDTO response) {
        System.out.println(response.getFromUserId().toString() + response.getToUserId().toString() + response.getQuizId().toString() + response.getResponse().toString());

        quizService.updateQuizAndUserStatus(response.getFromUserId(), response.getToUserId(), response.getQuizId(), response.getResponse());
    }
}
