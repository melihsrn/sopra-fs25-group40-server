package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Invitation;
import ch.uzh.ifi.hase.soprafs24.entity.Quiz;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.repository.InvitationRepository;
import ch.uzh.ifi.hase.soprafs24.repository.QuizRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;

@Service
public class InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizService quizService;

    /**
     * Create and send an invitation from one user to another for a quiz session.
     */
    public Invitation sendInvitation(Long inviterId, Long inviteeId, Long quizId) {
        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inviter not found"));
        User invitee = userRepository.findById(inviteeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitee not found"));
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        Invitation invitation = new Invitation();
        invitation.setInviter(inviter);
        invitation.setInvitee(invitee);
        invitation.setQuiz(quiz);
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setCreatedAt(new Date());
        return invitationRepository.save(invitation);
    }

    /**
     * Process the invitation response.
     * If accepted, check if the quiz is ready to start.
     */
    public Invitation respondToInvitation(Long invitationId, String response) {
        Optional<Invitation> invitationOpt = invitationRepository.findById(invitationId);
        if (invitationOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found");
        }
        Invitation invitation = invitationOpt.get();
        if ("ACCEPTED".equalsIgnoreCase(response)) {
            invitation.setStatus(InvitationStatus.ACCEPTED);
            invitation.setRespondedAt(new Date());
            invitationRepository.save(invitation);
            // For multiplayer quizzes, check if both players have joined.
            quizService.startMultiplayerIfReady(invitation.getQuiz().getId());
        } else if ("DECLINED".equalsIgnoreCase(response)) {
            invitation.setStatus(InvitationStatus.DECLINED);
            invitation.setRespondedAt(new Date());
            invitationRepository.save(invitation);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid response");
        }
        return invitation;
    }
}
