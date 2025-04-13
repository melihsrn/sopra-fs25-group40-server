package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.Date;

public class InvitationDTO {
    private Long id;
    private Long inviterId;
    private Long inviteeId;
    private Long quizId;
    private String status;
    private Date createdAt;
    private Date respondedAt;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
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
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getRespondedAt() {
        return respondedAt;
    }
    public void setRespondedAt(Date respondedAt) {
        this.respondedAt = respondedAt;
    }
}
