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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.uzh.ifi.hase.soprafs24.constant.QuizStatus;

@Entity
@Table(name = "quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Deck> decks = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Score> scores = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "invitation_id")
    @JsonIgnore
    private Invitation invitation;

    @Column(nullable = false)
    private Date startTime;
 
    @Column(nullable = true)
    private Date endTime;

    @Column(nullable = false)
    private int timeLimit;

    @Column(nullable = false)
    private QuizStatus quizStatus;

    @Column(nullable = true)
    private Long winner;

    @Column(nullable = false)
    private Boolean isMultiple;
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Deck> getDecks() {
        return decks;
      }
    
    public void setDecks(List<Deck> decks) {
        this.decks = decks;
    }

    public List<Score> getScores() {
        return scores;
      }
    
    public void setScores(List<Score> scores) {
        this.scores = scores;
    }

    public Invitation getInvitation() {
        return invitation;
      }
    
    public void setInvitation(Invitation invitation) {
        this.invitation = invitation;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public QuizStatus getQuizStatus() {
        return quizStatus;
    }

    public void setQuizStatus(QuizStatus quizStatus) {
        this.quizStatus = quizStatus;
    }

    public Long getWinner() {
        return winner;
    }

    public void setWinner(Long winner) {
        this.winner = winner;
    }

    public Boolean getIsMultiple() {
        return isMultiple;
    }

    public void setIsMultiple(Boolean isMultiple) {
        this.isMultiple = isMultiple;
    }

}
