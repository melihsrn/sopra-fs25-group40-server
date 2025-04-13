package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class InvitationResponseDTO {
    private Long invitationId;
    private String response; // "ACCEPTED" or "DECLINED"

    public Long getInvitationId() {
        return invitationId;
    }
    public void setInvitationId(Long invitationId) {
        this.invitationId = invitationId;
    }
    public String getResponse() {
        return response;
    }
    public void setResponse(String response) {
        this.response = response;
    }
}
