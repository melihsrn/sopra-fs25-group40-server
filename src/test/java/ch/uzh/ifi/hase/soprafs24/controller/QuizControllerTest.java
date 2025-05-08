package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Invitation;
import ch.uzh.ifi.hase.soprafs24.entity.Quiz;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.InvitationDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.QuizDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.InvitationMapper;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.QuizMapper;
import ch.uzh.ifi.hase.soprafs24.service.QuizService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(QuizController.class)
class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizService quizService;
    
    @MockBean
    private QuizMapper quizMapper;
    
    @MockBean
    private InvitationMapper invitationMapper;

    @InjectMocks
    private QuizController quizController;

    private User fromUser;
    private User toUser;

    @BeforeEach
    public void setup() {
        // Create a test user
        fromUser = new User();
        fromUser.setId(1L);
        fromUser.setUsername("fromUser");
        fromUser.setPassword("testPassword");
        fromUser.setCreationDate(new Date());
        fromUser.setStatus(UserStatus.ONLINE);

        toUser = new User();
        toUser.setId(2L);
        toUser.setUsername("toUser");
        toUser.setPassword("testPassword");
        toUser.setCreationDate(new Date());
        toUser.setStatus(UserStatus.ONLINE);

    }

    @Test
    void testSendQuizInvitation() throws Exception {
        // Arrange
        InvitationDTO invitationDTO = new InvitationDTO();
        invitationDTO.setFromUserId(1L);
        invitationDTO.setToUserId(2L);
        invitationDTO.setTimeLimit(5);

        QuizDTO quizDTO = new QuizDTO();
        quizDTO.setId(1L);

        Invitation invitation = new Invitation();
        invitation.setId(1L);

        Quiz quiz = new Quiz();
        quiz.setId(1L);

        when(quizService.createInvitation(any(InvitationDTO.class))).thenReturn(invitation);
        when(quizService.createQuiz(anyLong())).thenReturn(quiz);
        when(quizMapper.toDTO(any(Quiz.class))).thenReturn(quizDTO);

        // Act & Assert
        mockMvc.perform(post("/quiz/invitation")
                .contentType("application/json")
                .content("{\"fromUserId\": 1, \"toUserId\": 2, \"timeLimit\": 5}")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(quizService).createInvitation(any(InvitationDTO.class));
        verify(quizService).createQuiz(anyLong());
    }

    @Test
    void testGetQuizInvitation() throws Exception {
        // Arrange
        Long invitationId = 1L;
        Invitation invitation = new Invitation();
        invitation.setId(invitationId);

        InvitationDTO invitationDTO = new InvitationDTO();
        invitationDTO.setId(invitationId);

        when(quizService.getInvitationById(invitationId)).thenReturn(invitation);
        when(invitationMapper.toDTO(any(Invitation.class))).thenReturn(invitationDTO);

        // Act & Assert
        mockMvc.perform(get("/quiz/invitation/{invitationId}", invitationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(invitationId));

        verify(quizService).getInvitationById(invitationId);
    }


    @Test
    void testConfirmedQuizInvitation() throws Exception {
        // Arrange
        Long invitationId = 1L;

        // Act & Assert
        mockMvc.perform(get("/quiz/response/confirmation")
                .param("invitationId", invitationId.toString())
        )
                .andExpect(status().isOk());

        verify(quizService).confirmedInvitation(invitationId);
    }

    @Test
    void testRejectedQuizInvitation() throws Exception {
        // Arrange
        Long invitationId = 1L;

        // Act & Assert
        mockMvc.perform(delete("/quiz/response/rejection")
                .param("invitationId", invitationId.toString())
        )
                .andExpect(status().isOk());

        verify(quizService).rejectedInvitation(invitationId);
    }

    @Test
    void testDeleteQuizInvitation() throws Exception {
        // Arrange
        Long invitationId = 1L;

        // Act & Assert
        mockMvc.perform(delete("/quiz/invitation/delete/{invitationId}", invitationId))
                .andExpect(status().isOk());

        verify(quizService).deleteInvitationById(invitationId);
    }

    @Test
    void testGetQuizInvitationsBySender() throws Exception {
        // Arrange
        Invitation invitation = new Invitation();
        invitation.setId(1L);
        invitation.setFromUser(fromUser);

        InvitationDTO invitationDTO = new InvitationDTO();
        invitationDTO.setId(1L);
        invitationDTO.setFromUserId(fromUser.getId());

        List<Invitation> invitations = Arrays.asList(invitation);
        List<InvitationDTO> invitationDTOs = Arrays.asList(invitationDTO);

        when(quizService.getInvitationByFromUserId(fromUser.getId())).thenReturn(invitations);
        when(invitationMapper.toDTOList(invitations)).thenReturn(invitationDTOs);

        // Act & Assert
        mockMvc.perform(get("/quiz/invitation/senders")
                .param("fromUserId", String.valueOf(fromUser.getId()))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].fromUserId").value(fromUser.getId()));

        verify(quizService).getInvitationByFromUserId(fromUser.getId());
        verify(invitationMapper).toDTOList(invitations);
    }


    @Test
    void testGetQuizInvitationsByReceiver() throws Exception {
        // Arrange
        Invitation invitation = new Invitation();
        invitation.setId(1L);
        invitation.setToUser(toUser);

        InvitationDTO invitationDTO = new InvitationDTO();
        invitationDTO.setId(1L);
        invitationDTO.setToUserId(toUser.getId());

        List<Invitation> invitations = Arrays.asList(invitation);
        List<InvitationDTO> invitationDTOs = Arrays.asList(invitationDTO);

        when(quizService.getInvitationByToUserId(toUser.getId())).thenReturn(invitations);
        when(invitationMapper.toDTOList(invitations)).thenReturn(invitationDTOs);

        // Act & Assert
        mockMvc.perform(get("/quiz/invitation/receivers")
                .param("toUserId", String.valueOf(toUser.getId()))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].toUserId").value(toUser.getId()));

        verify(quizService).getInvitationByToUserId(toUser.getId());
        verify(invitationMapper).toDTOList(invitations);
    }



    @Test
    void testGetAcceptedQuizInvitationsForSender() throws Exception {
        // Arrange
        Invitation invitation = new Invitation();
        invitation.setId(1L);
        invitation.setFromUser(fromUser);
        invitation.setIsAccepted(true);
    
        InvitationDTO invitationDTO = new InvitationDTO();
        invitationDTO.setId(1L);
        invitationDTO.setFromUserId(fromUser.getId());
        invitationDTO.setIsAccepted(true);
    
        when(quizService.findInvitationByFromUserIdAndIsAcceptedTrue(fromUser.getId())).thenReturn(invitation);
        when(invitationMapper.toDTO(invitation)).thenReturn(invitationDTO);
    
        // Act & Assert
        mockMvc.perform(get("/quiz/invitation/accepted")
                .param("fromUserId", String.valueOf(fromUser.getId()))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fromUserId").value(fromUser.getId()))
                .andExpect(jsonPath("$.isAccepted").value(true));
    
        verify(quizService).findInvitationByFromUserIdAndIsAcceptedTrue(fromUser.getId());
        verify(invitationMapper).toDTO(invitation);
    }
    
}
