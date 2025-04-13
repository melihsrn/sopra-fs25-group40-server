package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class InvitationRequestDTO {
    private Long inviterId;
    private Long inviteeId;
    private Long quizId;

    public Long getInviterId() {
        return inviterId;
    }
    public void setInviterId(Long inviterId) {
        this.inviterId = inviterId;
    }
    public Long getInviteeId() {
        return inviteeId;
    }
    public void setInviteeId(Long inviteeId) {
        this.inviteeId = inviteeId;
    }
    public Long getQuizId() {
        return quizId;
    }
    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }
}
