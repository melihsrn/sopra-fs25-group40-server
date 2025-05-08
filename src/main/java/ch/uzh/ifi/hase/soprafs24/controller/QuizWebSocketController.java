package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizUpdateMessageDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class QuizWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Receives messages sent to /app/quiz/update, then broadcasts them to a dynamic topic.
     * Clients should subscribe to /topic/quizUpdates/{quizId} to receive the correct updates.
     */
    @MessageMapping("/quiz/update")
    public void sendQuizUpdate(QuizUpdateMessageDTO message) {
        String destination = "/topic/quizUpdates/" + message.getQuizId();
        messagingTemplate.convertAndSend(destination, message);
        System.out.println("✅ WEBSOCKET is set.");
    }
}
