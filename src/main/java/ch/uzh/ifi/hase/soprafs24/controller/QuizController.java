package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Invitation;
import ch.uzh.ifi.hase.soprafs24.entity.Quiz;
import ch.uzh.ifi.hase.soprafs24.entity.Flashcard;

import ch.uzh.ifi.hase.soprafs24.service.QuizService;

import ch.uzh.ifi.hase.soprafs24.rest.dto.InvitationDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizStartRequestDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizAnswerRequestDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizAnswerResponseDTO;

import ch.uzh.ifi.hase.soprafs24.rest.mapper.InvitationMapper;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.QuizMapper;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.FlashcardMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

 
/**
 * Quiz Controller
 * Combines invitation logic (from main) and quiz logic (from shak_branch).
 */
@RestController
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;
    private final QuizMapper quizMapper;
    private final InvitationMapper invitationMapper;

    public QuizController(QuizService quizService, QuizMapper quizMapper, InvitationMapper invitationMapper) {
        this.quizService = quizService;
        this.quizMapper = quizMapper;
        this.invitationMapper = invitationMapper;
    }

    // ---------------------------- Invitation endpoints ----------------------------

    @PostMapping("/invitation")
    @ResponseStatus(HttpStatus.OK)
    public QuizDTO sendQuizInvitation(@RequestBody @Valid InvitationDTO invitationDTO) {
        Invitation invitation = quizService.createInvitation(invitationDTO);
        Quiz quiz = quizService.createQuiz(invitation.getId());
        return quizMapper.convertEntityToDTO(quiz);
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

    // ------------------------------ Quiz endpoints -------------------------------

    @PostMapping("/start")
    public QuizDTO startQuiz(@RequestBody QuizStartRequestDTO request) {
        if (request.getDeckId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deck ID is required");
        }
        Quiz quiz = quizService.startQuiz(
                request.getDeckId(),
                request.getNumberOfQuestions(),
                request.getTimeLimit(),
                request.getIsMultiple()
        );
        return quizMapper.convertEntityToDTO(quiz);
    }

    @PostMapping("/answer")
    public QuizAnswerResponseDTO answerQuiz(@RequestBody QuizAnswerRequestDTO request) {
        if (request.getQuizId() == null || request.getFlashcardId() == null
                || request.getSelectedAnswer() == null || request.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required fields");
        }

        // Process the answer and return feedback
        return quizService.processAnswerWithFeedback(
                request.getQuizId(),
                request.getFlashcardId(),
                request.getSelectedAnswer(),
                request.getUserId()
        );
    }

    @GetMapping("/{quizId}/currentQuestion")
    public FlashcardDTO getCurrentQuestion(@PathVariable Long quizId, @RequestParam Long userId) {
        Flashcard question = quizService.getCurrentQuestion(quizId, userId);
        return FlashcardMapper.toDTO(question);
    }

    @GetMapping("/status/{quizId}")
    public QuizDTO getQuizStatus(@PathVariable Long quizId) {
        Quiz quiz = quizService.getQuizStatus(quizId);
        if (quiz == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found");
        }
        return quizMapper.convertEntityToDTO(quiz);
    }
}

