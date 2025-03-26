package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.QuizStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Quiz;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.QuizRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ScoreRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;


import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
@Transactional
public class QuizService {

    private final UserRepository userRepository;

    private final UserService userService;
    private final FirebaseService firebaseService;
    private final ScoreRepository scoreRepository;
    private final QuizRepository quizRepository;

    public QuizService(UserService userService,
                        FirebaseService firebaseService,
                        ScoreRepository scoreRepository,
                        QuizRepository quizRepository, UserRepository userRepository) {
        this.userService = userService;
        this.firebaseService = firebaseService;
        this.scoreRepository = scoreRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
    }


    public Quiz getQuizById(Long quizId) {
        Optional<Quiz> existingQuizOpt = quizRepository.findById(quizId);
        if (existingQuizOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found");
        }
        Quiz existingQuiz = existingQuizOpt.get();

        return existingQuiz;
    }

    public void sendInvitationNotification(Long senderId, Long receiverId, Quiz quiz) {

        User receiver = userService.getUserById(receiverId);

        // Retrieve the FCM token from the user
        String fcmToken = receiver.getFcmToken();
        if (fcmToken == null || fcmToken.isEmpty()) {
            System.out.println("No FCM token found for user.");
        }

        // Send notification
        firebaseService.sendInvitationNotification(senderId, receiverId, quiz.getId(), fcmToken);
    }

    public void updateQuizAndUserStatus(Long senderId, Long receiverId, Long quizId, Boolean response) {
            Quiz quiz = getQuizById(quizId);
            User sender = userService.getUserById(senderId);
            User receiver = userService.getUserById(receiverId);

            if (response) {
                quiz.setQuizStatus(QuizStatus.IN_PROGRESS); // Change quiz status to "in progress"
                sender.setStatus(UserStatus.PLAYING);
                receiver.setStatus(UserStatus.PLAYING);

                userRepository.save(sender);
                userRepository.save(receiver);

                quizRepository.save(quiz);

                // Send notification
                firebaseService.sendQuizResponseNotification(sender, quiz.getId(), true);
            } else {
                if (quizRepository.existsById(quizId)) {
                    quizRepository.delete(quiz);
                }

                // Send notification
                firebaseService.sendQuizResponseNotification(sender, quiz.getId(), false);
            }
    }
}
