package ch.uzh.ifi.hase.soprafs24.entity;

import java.util.Date;
import javax.persistence.*;

import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;

@Entity
@Table(name = "invitation")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user sending the invitation.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false)
    private User inviter;

    // The user being invited.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id", nullable = false)
    private User invitee;

    // The quiz session associated with this invitation.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    // Status of the invitation (e.g., PENDING, ACCEPTED, DECLINED)
    @Column(nullable = false)
    private InvitationStatus status;

    // Timestamp for when the invitation was created.
    @Column(nullable = false)
    private Date createdAt;

    // Timestamp for when the invitation was responded to (optional)
    @Column
    private Date respondedAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public User getInviter() {
        return inviter;
    }
    public void setInviter(User inviter) {
        this.inviter = inviter;
    }

    public User getInvitee() {
        return invitee;
    }
    public void setInvitee(User invitee) {
        this.invitee = invitee;
    }

    public Quiz getQuiz() {
        return quiz;
    }
    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public InvitationStatus getStatus() {
        return status;
    }
    public void setStatus(InvitationStatus status) {
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
