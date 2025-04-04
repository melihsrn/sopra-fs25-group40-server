package ch.uzh.ifi.hase.soprafs24.entity;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "invitation")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "invitation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Quiz quiz;

    @OneToMany(mappedBy = "invitation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Deck> decks = new ArrayList<>();

    @Column(nullable = false)
    private int timeLimit;

    @ManyToOne
    @JoinColumn(name = "from_user_id", nullable = false)
    @JsonIgnore
    private User fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id", nullable = false)
    @JsonIgnore
    private User toUser;

    @Column(nullable = false)
    private Boolean isAccepted;

    @Column(nullable = true)
    private Date isAcceptedDate;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public List<Deck> getDecks() {
        return decks;
      }
    
    public void setDecks(List<Deck> decks) {
        this.decks = decks;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Boolean getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(Boolean isAccepted) {
        this.isAccepted = isAccepted;
    }

    public Date getIsAcceptedDate() {
        return isAcceptedDate;
    }

    public void setIsAcceptedDate(Date isAcceptedDate) {
        this.isAcceptedDate = isAcceptedDate;
    }

}
