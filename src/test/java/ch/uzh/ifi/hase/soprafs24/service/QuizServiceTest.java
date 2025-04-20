package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.InvitationDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.QuizMapper;

import org.springframework.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class QuizServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private InvitationRepository invitationRepository;

    @Mock
    private QuizMapper quizMapper;

    @Mock
    private DeckRepository deckRepository;

    @InjectMocks
    private QuizService quizService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getInvitationByFromUserId_returnsInvitations() {
        User fromUser = new User();
        fromUser.setId(1L);

        List<Invitation> invitations = List.of(new Invitation());
        when(userService.getUserById(1L)).thenReturn(fromUser);
        when(invitationRepository.findByFromUser(fromUser)).thenReturn(invitations);

        List<Invitation> result = quizService.getInvitationByFromUserId(1L);

        assertEquals(1, result.size());
        verify(invitationRepository).findByFromUser(fromUser);
    }

    @Test
    void getInvitationByToUserId_returnsInvitations() {
        User toUser = new User();
        toUser.setId(2L);

        List<Invitation> invitations = List.of(new Invitation());
        when(userService.getUserById(2L)).thenReturn(toUser);
        when(invitationRepository.findByToUser(toUser)).thenReturn(invitations);

        List<Invitation> result = quizService.getInvitationByToUserId(2L);

        assertEquals(1, result.size());
        verify(invitationRepository).findByToUser(toUser);
    }

    @Test
    void createInvitation_success() {
        InvitationDTO dto = new InvitationDTO();
        dto.setFromUserId(1L);
        dto.setToUserId(2L);
        dto.setTimeLimit(2);
        dto.setDeckIds(List.of(100L));

        User from = new User();
        from.setStatus(UserStatus.ONLINE);
        User to = new User();
        to.setStatus(UserStatus.ONLINE);
        Deck deck = new Deck();

        when(userService.getUserById(1L)).thenReturn(from);
        when(userService.getUserById(2L)).thenReturn(to);
        when(deckRepository.findById(100L)).thenReturn(Optional.of(deck));

        Invitation saved = quizService.createInvitation(dto);

        assertEquals(from, saved.getFromUser());
        assertEquals(to, saved.getToUser());
        assertEquals(1, saved.getDecks().size());
        assertFalse(saved.getIsAccepted());
        verify(invitationRepository).save(any());
    }

    @Test
    void confirmedInvitation_success() {
        User sender = new User();
        sender.setStatus(UserStatus.ONLINE);
        User receiver = new User();
        receiver.setStatus(UserStatus.ONLINE);

        Quiz quiz = new Quiz();
        Invitation invitation = new Invitation();
        invitation.setFromUser(sender);
        invitation.setToUser(receiver);
        invitation.setQuiz(quiz);

        when(invitationRepository.findById(1L)).thenReturn(Optional.of(invitation));

        quizService.confirmedInvitation(1L);

        assertEquals(UserStatus.PLAYING, sender.getStatus());
        assertEquals(UserStatus.PLAYING, receiver.getStatus());
        assertTrue(invitation.getIsAccepted());
        assertNotNull(invitation.getIsAcceptedDate());

        verify(quizRepository).save(quiz);
        verify(userRepository).save(sender);
        verify(userRepository).save(receiver);
    }

    @Test
    void rejectedInvitation_deletesEntities() {
        Quiz quiz = new Quiz();
        Invitation invitation = new Invitation();
        invitation.setQuiz(quiz);

        when(invitationRepository.findById(1L)).thenReturn(Optional.of(invitation));

        quizService.rejectedInvitation(1L);

        verify(quizRepository).delete(quiz);
        verify(invitationRepository).delete(invitation);
    }

    @Test
    void findInvitationByFromUserIdAndIsAcceptedTrue_returnsEarliestAcceptedAndDeletesOthers() {
        User fromUser = new User();
        fromUser.setId(1L);

        User toUser1 = new User();
        User toUser2 = new User();

        Quiz quiz2 = new Quiz();

        Invitation accepted1 = new Invitation();
        accepted1.setIsAccepted(true);
        accepted1.setIsAcceptedDate(new Date(1000));
        accepted1.setToUser(toUser1);

        Invitation accepted2 = new Invitation();
        accepted2.setIsAccepted(true);
        accepted2.setIsAcceptedDate(new Date(2000));
        accepted2.setToUser(toUser2);
        accepted2.setQuiz(quiz2);

        when(userService.getUserById(1L)).thenReturn(fromUser);
        when(invitationRepository.findByFromUser(fromUser)).thenReturn(List.of(accepted1, accepted2));

        Invitation result = quizService.findInvitationByFromUserIdAndIsAcceptedTrue(1L);

        assertEquals(accepted1, result);
        assertEquals(UserStatus.ONLINE, toUser2.getStatus());
        verify(userRepository).save(toUser2);
        verify(quizRepository).delete(quiz2);
        verify(invitationRepository).delete(accepted2);
    }

    @Test
    void getQuizById_notFound_throwsException() {
        when(quizRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> quizService.getQuizById(999L));
    }

    @Test
    void deleteInvitationById_deletesSuccessfully() {
        Invitation invitation = new Invitation();
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(invitation));

        quizService.deleteInvitationById(1L);

        verify(invitationRepository).delete(invitation);
    }

    @Test
    void checkUserStatusForInvitation_throwsOnOffline() {
        User user = new User();
        user.setStatus(UserStatus.OFFLINE);
        assertThrows(ResponseStatusException.class, () -> quizService.checkUserStatusForInvitation(user));
    }

    @Test
    void checkUserStatusForInvitation_throwsOnPlaying() {
        User user = new User();
        user.setStatus(UserStatus.PLAYING);
        assertThrows(ResponseStatusException.class, () -> quizService.checkUserStatusForInvitation(user));
    }


    @Test
    void getInvitationById_validId_returnsInvitation() {
        Invitation mockInvitation = new Invitation();
        mockInvitation.setId(1L);

        when(invitationRepository.findById(1L)).thenReturn(Optional.of(mockInvitation));

        Invitation result = quizService.getInvitationById(1L);

        assertEquals(mockInvitation, result);
        verify(invitationRepository, times(1)).findById(1L);
    }

    @Test
    void getInvitationById_invalidId_throwsException() {
        when(invitationRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> quizService.getInvitationById(999L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("404 NOT_FOUND \"Invitation not found\"", exception.getMessage());
        verify(invitationRepository, times(1)).findById(999L);
    }

    @Test
    void createQuiz_validInvitationId_createsQuiz() {
        // Setup
        Invitation mockInvitation = new Invitation();
        mockInvitation.setId(1L);
        mockInvitation.setDecks(new ArrayList<>());

        Quiz mockQuiz = new Quiz();
        mockQuiz.setId(10L);
        mockQuiz.setDecks(new ArrayList<>());

        when(invitationRepository.findById(1L)).thenReturn(Optional.of(mockInvitation));
        when(quizMapper.fromInvitationToEntity(mockInvitation)).thenReturn(mockQuiz);
        when(quizRepository.save(any(Quiz.class))).thenReturn(mockQuiz);
        when(invitationRepository.save(any(Invitation.class))).thenReturn(mockInvitation);

        // Execute
        Quiz result = quizService.createQuiz(1L);

        // Verify
        assertEquals(mockQuiz, result);
        assertEquals(mockQuiz, mockInvitation.getQuiz());

        verify(invitationRepository, times(1)).findById(1L);
        verify(quizMapper, times(1)).fromInvitationToEntity(mockInvitation);
        verify(quizRepository, times(1)).save(mockQuiz);
        verify(invitationRepository, times(1)).save(mockInvitation);
    }

}
