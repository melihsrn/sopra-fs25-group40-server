package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizStartRequestDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizAnswerRequestDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizAnswerResponseDTO;
import ch.uzh.ifi.hase.soprafs24.entity.Quiz;
import ch.uzh.ifi.hase.soprafs24.entity.Flashcard;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.QuizMapper;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.FlashcardMapper;
import ch.uzh.ifi.hase.soprafs24.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizMapper quizMapper;

    // Start a new quiz session
    @PostMapping("/start")
    public QuizDTO startQuiz(@RequestBody QuizStartRequestDTO request) {
        if (request.getDeckId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deck ID is required");
        }
        Quiz quiz = quizService.startQuiz(request.getDeckId(), request.getNumberOfQuestions(), request.getTimeLimit(), request.getIsMultiple());
        return quizMapper.convertEntityToDTO(quiz);
    }

    @PostMapping("/answer")
    public QuizAnswerResponseDTO answerQuiz(@RequestBody QuizAnswerRequestDTO request) {
        if (request.getQuizId() == null || request.getFlashcardId() == null
                || request.getSelectedAnswer() == null || request.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required fields");
        }

        // process the answer -> returns info about correctness and whether quiz ended
        QuizAnswerResponseDTO response = quizService.processAnswerWithFeedback(
                request.getQuizId(),
                request.getFlashcardId(),
                request.getSelectedAnswer(),
                request.getUserId()
        );

        return response;
    }


    // new: GET /quiz/{quizId}/currentQuestion?userId=X
    @GetMapping("/{quizId}/currentQuestion")
    public FlashcardDTO getCurrentQuestion(
            @PathVariable Long quizId,
            @RequestParam Long userId
    ) {
        Flashcard question = quizService.getCurrentQuestion(quizId, userId);
        return FlashcardMapper.toDTO(question); // or a custom mapper for partial data
    }

    // existing: GET /quiz/{quizId} (status)
    @GetMapping("/status/{quizId}")
    public QuizDTO getQuizStatus(@PathVariable Long quizId) {
        Quiz quiz = quizService.getQuizStatus(quizId);
        if (quiz == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found");
        }
        return quizMapper.convertEntityToDTO(quiz);
    }

}
