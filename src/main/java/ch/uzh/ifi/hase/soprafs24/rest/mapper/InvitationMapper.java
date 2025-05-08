package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ch.uzh.ifi.hase.soprafs24.entity.Invitation;
import ch.uzh.ifi.hase.soprafs24.rest.dto.InvitationDTO;

@Component
public class InvitationMapper {

    public InvitationDTO toDTO(Invitation invitation) {
        InvitationDTO dto = new InvitationDTO();
        dto.setId(invitation.getId());
        dto.setTimeLimit(invitation.getTimeLimit());
        dto.setIsAccepted(invitation.getIsAccepted());
        dto.setFromUserId(invitation.getFromUser().getId());
        dto.setToUserId(invitation.getToUser().getId());
        dto.setQuizId(invitation.getQuiz().getId());

        List<Long> managedDeckIds = invitation.getDecks().stream()
            .map(deck -> deck.getId())
            .collect(Collectors.toList());

        dto.setDeckIds(new ArrayList<>(managedDeckIds));
        return dto;
    }

    public List<InvitationDTO> toDTOList(List<Invitation> invitations) {
        return invitations.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // public Invitation toEntity(InvitationDTO dto) {
    //     Invitation invitation = new Invitation();
    //     invitation.setDecks(dto.getDecks());
    //     invitation.setTimeLimit(dto.getTimeLimit());
    //     invitation.setFromUser(dto.getFromUserId());
    //     invitation.setToUser(dto.getToUserId());
    //     return invitation;
    // }
}
