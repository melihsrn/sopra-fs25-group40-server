package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.rest.dto.InvitationDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.InvitationRequestDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.InvitationResponseDTO;
import ch.uzh.ifi.hase.soprafs24.entity.Invitation;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.InvitationMapper;
import ch.uzh.ifi.hase.soprafs24.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/invitations")
public class InvitationController {

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private InvitationMapper invitationMapper;

    // Send an invitation for a multiplayer quiz
    @PostMapping("/send")
    public InvitationDTO sendInvitation(@RequestBody InvitationRequestDTO request) {
        if (request.getInviterId() == null || request.getInviteeId() == null || request.getQuizId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required fields");
        }
        Invitation invitation = invitationService.sendInvitation(request.getInviterId(), request.getInviteeId(), request.getQuizId());
        return invitationMapper.convertEntityToDTO(invitation);
    }

    // Respond to an invitation (ACCEPTED or DECLINED)
    @PostMapping("/respond")
    public InvitationDTO respondInvitation(@RequestBody InvitationResponseDTO request) {
        if (request.getInvitationId() == null || request.getResponse() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required fields");
        }
        Invitation invitation = invitationService.respondToInvitation(request.getInvitationId(), request.getResponse());
        return invitationMapper.convertEntityToDTO(invitation);
    }
}
