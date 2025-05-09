// QuizController.java
package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.*;
import ch.uzh.ifi.hase.soprafs24.service.QuizService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService      quizService;
    private final QuizMapper       quizMapper;
    private final InvitationMapper invitationMapper;
    private final FlashcardMapper  flashcardMapper;

    public QuizController(QuizService quizService,
                          QuizMapper quizMapper,
                          InvitationMapper invitationMapper,
                          FlashcardMapper flashcardMapper) {
        this.quizService      = quizService;
        this.quizMapper       = quizMapper;
        this.invitationMapper = invitationMapper;
        this.flashcardMapper  = flashcardMapper;
    }

    /* ───────────── Invitation endpoints ───────────── */

    @PostMapping("/invitation")
    @ResponseStatus(HttpStatus.OK)
    public QuizDTO sendQuizInvitation(@RequestBody @Valid InvitationDTO dto) {
        Invitation inv = quizService.createInvitation(dto);
        Quiz quiz     = quizService.createQuiz(inv.getId());
        return quizMapper.convertEntityToDTO(quiz);
    }

    @GetMapping("/invitation/{id}")
    public InvitationDTO getQuizInvitation(@PathVariable Long id) {
        return invitationMapper.toDTO(quizService.getInvitationById(id));
    }

    @GetMapping("/invitation/senders")
    public List<InvitationDTO> getInvitesBySender(@RequestParam Long fromUserId) {
        return invitationMapper.toDTOList(quizService.getInvitationByFromUserId(fromUserId));
    }

    @GetMapping("/invitation/receivers")
    public List<InvitationDTO> getInvitesByReceiver(@RequestParam Long toUserId) {
        return invitationMapper.toDTOList(quizService.getInvitationByToUserId(toUserId));
    }

    @GetMapping("/response/confirmation")
    @ResponseStatus(HttpStatus.OK)
    public void confirmInvite(@RequestParam Long invitationId) {
        quizService.confirmedInvitation(invitationId);
    }

    @DeleteMapping("/response/rejection")
    @ResponseStatus(HttpStatus.OK)
    public void rejectInvite(@RequestParam Long invitationId) {
        quizService.rejectedInvitation(invitationId);
    }

    @GetMapping("/invitation/accepted")
    public ResponseEntity<InvitationDTO> acceptedInvite(@RequestParam Long fromUserId) {
        Invitation inv = quizService.findInvitationByFromUserIdAndIsAcceptedTrue(fromUserId);
        return inv == null ? ResponseEntity.ok().body(null)
                : ResponseEntity.ok(invitationMapper.toDTO(inv));
    }

    @DeleteMapping("/invitation/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteInvitation(@PathVariable Long id) {
        quizService.deleteInvitationById(id);
    }

    /* ───────────── Quiz endpoints ───────────── */

    @PostMapping("/start")
    public QuizDTO startQuiz(@RequestBody QuizStartRequestDTO req) {
        if (req.getDeckId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deck ID is required");
        }
        Quiz quiz = quizService.startQuiz(
                req.getDeckId(),
                req.getNumberOfQuestions(),
                req.getTimeLimit(),
                req.getIsMultiple()
        );
        return quizMapper.convertEntityToDTO(quiz);
    }

    @PostMapping("/answer")
    public QuizAnswerResponseDTO answer(@RequestBody QuizAnswerRequestDTO req) {
        if (req.getQuizId() == null || req.getFlashcardId() == null
                || req.getSelectedAnswer() == null || req.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required fields");
        }
        return quizService.processAnswerWithFeedback(
                req.getQuizId(), req.getFlashcardId(), req.getSelectedAnswer(), req.getUserId());
    }

    @GetMapping("/{quizId}/currentQuestion")
    public FlashcardDTO current(@PathVariable Long quizId, @RequestParam Long userId) {
        return flashcardMapper.toDTO(quizService.getCurrentQuestion(quizId, userId));
    }

    @GetMapping("/status/{id}")
    public QuizDTO status(@PathVariable Long id) {
        return quizMapper.convertEntityToDTO(quizService.getQuizStatus(id));
    }
}
