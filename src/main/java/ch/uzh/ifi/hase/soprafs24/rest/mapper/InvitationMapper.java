package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.rest.dto.InvitationDTO;
import ch.uzh.ifi.hase.soprafs24.entity.Invitation;
import org.springframework.stereotype.Component;

@Component
public class InvitationMapper {
    public InvitationDTO convertEntityToDTO(Invitation invitation) {
        InvitationDTO dto = new InvitationDTO();
        dto.setId(invitation.getId());
        dto.setInviterId(invitation.getInviter().getId());
        dto.setInviteeId(invitation.getInvitee().getId());
        dto.setQuizId(invitation.getQuiz().getId());
        dto.setStatus(invitation.getStatus().toString());
        dto.setCreatedAt(invitation.getCreatedAt());
        dto.setRespondedAt(invitation.getRespondedAt());
        return dto;
    }
}
