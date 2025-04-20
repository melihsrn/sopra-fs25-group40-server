package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.InvitationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class InvitationMapperTest {

    private InvitationMapper invitationMapper;

    @BeforeEach
    public void setup() {
        invitationMapper = new InvitationMapper();
    }

    @Test
    public void testToDTO_mapsAllFieldsCorrectly() {
        // Given
        User fromUser = new User();
        fromUser.setId(1L);

        User toUser = new User();
        toUser.setId(2L);

        Quiz quiz = new Quiz();
        quiz.setId(3L);

        Deck deck1 = new Deck();
        deck1.setId(101L);

        Deck deck2 = new Deck();
        deck2.setId(102L);

        Invitation invitation = new Invitation();
        invitation.setId(5L);
        invitation.setTimeLimit(300);
        invitation.setIsAccepted(true);
        invitation.setFromUser(fromUser);
        invitation.setToUser(toUser);
        invitation.setQuiz(quiz);
        invitation.setDecks(List.of(deck1, deck2));

        // When
        InvitationDTO dto = invitationMapper.toDTO(invitation);

        // Then
        assertEquals(invitation.getId(), dto.getId());
        assertEquals(invitation.getTimeLimit(), dto.getTimeLimit());
        assertEquals(invitation.getIsAccepted(), dto.getIsAccepted());
        assertEquals(fromUser.getId(), dto.getFromUserId());
        assertEquals(toUser.getId(), dto.getToUserId());
        assertEquals(quiz.getId(), dto.getQuizId());
        assertNotNull(dto.getDeckIds());
        assertEquals(2, dto.getDeckIds().size());
        assertTrue(dto.getDeckIds().contains(deck1.getId()));
        assertTrue(dto.getDeckIds().contains(deck2.getId()));
    }

    @Test
    public void testToDTOList_mapsListCorrectly() {
        // Given
        Invitation invitation = new Invitation();
        User fromUser = new User();
        fromUser.setId(1L);
        User toUser = new User();
        toUser.setId(2L);
        Quiz quiz = new Quiz();
        quiz.setId(3L);
        Deck deck = new Deck();
        deck.setId(101L);

        invitation.setId(99L);
        invitation.setTimeLimit(180);
        invitation.setIsAccepted(false);
        invitation.setFromUser(fromUser);
        invitation.setToUser(toUser);
        invitation.setQuiz(quiz);
        invitation.setDecks(List.of(deck));

        List<Invitation> invitationList = List.of(invitation);

        // When
        List<InvitationDTO> dtoList = invitationMapper.toDTOList(invitationList);

        // Then
        assertEquals(1, dtoList.size());
        InvitationDTO dto = dtoList.get(0);
        assertEquals(invitation.getId(), dto.getId());
        assertEquals(invitation.getFromUser().getId(), dto.getFromUserId());
    }
}
