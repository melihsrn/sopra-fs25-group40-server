package ch.uzh.ifi.hase.soprafs24.entity;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "statistics",
        uniqueConstraints = @UniqueConstraint(columnNames = {"quiz_id", "user_id"}))
public class Statistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user for whom these statistics are recorded.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Optional link to the quiz session this record relates to.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    // Final score achieved
    @Column(nullable = false)
    private int score;

    // Total time taken for the quiz (e.g., in seconds)
    @Column(nullable = false)
    private Long timeTaken;

    // Total number of attempts across all questions during the quiz
    @Column(nullable = false)
    private int numberOfAttempts;

    // Date when the quiz was completed
    @Column(nullable = false)
    private Date quizDate;

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Quiz getQuiz() {
        return quiz;
    }
    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }

    public Long getTimeTaken() {
        return timeTaken;
    }
    public void setTimeTaken(Long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public int getNumberOfAttempts() {
        return numberOfAttempts;
    }
    public void setNumberOfAttempts(int numberOfAttempts) {
        this.numberOfAttempts = numberOfAttempts;
    }

    public Date getQuizDate() {
        return quizDate;
    }
    public void setQuizDate(Date quizDate) {
        this.quizDate = quizDate;
    }
}
