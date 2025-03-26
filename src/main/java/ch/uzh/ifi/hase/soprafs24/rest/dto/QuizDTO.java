package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.Date;
import java.util.List;

import ch.uzh.ifi.hase.soprafs24.constant.QuizStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Deck;
import ch.uzh.ifi.hase.soprafs24.entity.Score;

public class QuizDTO {

    private Long id;

    private List<Deck> decks;

    private List<Score> scores;

    private Date startTime;
 
    private Date endTime;

    private int timeLimit;

    private QuizStatus quizStatus;

    private Long winner;

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
