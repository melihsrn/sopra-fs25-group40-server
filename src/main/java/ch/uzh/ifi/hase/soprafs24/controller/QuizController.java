package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Invitation;
import ch.uzh.ifi.hase.soprafs24.entity.Quiz;
import ch.uzh.ifi.hase.soprafs24.service.QuizService;
import ch.uzh.ifi.hase.soprafs24.rest.dto.InvitationDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.InvitationMapper;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.QuizMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.validation.Valid;

/**
 * Quiz Controller
 * Handles quiz invitations between users.
 */
@RestController
@RequestMapping("/quiz")
public class QuizController {


    private final QuizMapper quizMapper;
    private final InvitationMapper invitationMapper;
    private final QuizService quizService;

    public QuizController(QuizService quizService, QuizMapper quizMapper, InvitationMapper invitationMapper) {
        this.quizService = quizService;
        this.quizMapper = quizMapper;
        this.invitationMapper = invitationMapper;
    }

    @PostMapping("/invitation")
    @ResponseStatus(HttpStatus.OK)
    public QuizDTO sendQuizInvitation(@RequestBody @Valid InvitationDTO invitationDTO) {
        // Invitation invitation = invitationMapper.toEntity(invitationDTO);
        Invitation invitation = quizService.createInvitation(invitationDTO);

        Quiz quiz = quizService.createQuiz(invitation.getId());

        return quizMapper.toDTO(quiz);
    }

    @GetMapping("/invitation/{invitationId}")
    @ResponseStatus(HttpStatus.OK)
    public InvitationDTO getQuizInvitation(@PathVariable Long invitationId) {

        Invitation invitation = quizService.getInvitationById(invitationId);

        return invitationMapper.toDTO(invitation);
    }

    @GetMapping("/invitation/senders")
    @ResponseStatus(HttpStatus.OK)
    public List<InvitationDTO> getQuizInvitationsBySender(@RequestParam Long fromUserId) {

        List<Invitation> invitations = quizService.getInvitationByFromUserId(fromUserId);

        return invitationMapper.toDTOList(invitations);
    }

    @GetMapping("/invitation/receivers")
    @ResponseStatus(HttpStatus.OK)
    public List<InvitationDTO> getQuizInvitationsByReceiver(@RequestParam Long toUserId) {

        List<Invitation> invitations = quizService.getInvitationByToUserId(toUserId);

        return invitationMapper.toDTOList(invitations);
    }

    @GetMapping("/response/confirmation")
    @ResponseStatus(HttpStatus.OK)
    public void confirmedQuizInvitation(@RequestParam Long invitationId) {

        quizService.confirmedInvitation(invitationId);

    }

    @DeleteMapping("/response/rejection")
    @ResponseStatus(HttpStatus.OK)
    public void rejectedQuizInvitation(@RequestParam Long invitationId) {

        quizService.rejectedInvitation(invitationId);

    }

    @GetMapping("/invitation/accepted")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<InvitationDTO> getAcceptedQuizInvitationsForSender(@RequestParam Long fromUserId) {
        Invitation invitation = quizService.findInvitationByFromUserIdAndIsAcceptedTrue(fromUserId);
        if (invitation == null) {
            return ResponseEntity.ok().body(null); // 200 OK with empty body
        }
        return ResponseEntity.ok(invitationMapper.toDTO(invitation));
    }

    @DeleteMapping("/invitation/delete/{invitationId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteInvitationById(@PathVariable Long invitationId) {

        quizService.deleteInvitationById(invitationId);

    }


}
