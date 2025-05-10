// Quiz.java
package ch.uzh.ifi.hase.soprafs24.entity;

import java.util.Date;
import java.io.Serializable;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ch.uzh.ifi.hase.soprafs24.constant.QuizStatus;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter // Generates getters, setters automatically
@Entity
@Table(name = "quiz")
public class Quiz  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // If multiple decks can be used, you already have getDecks().
    // To store the *actual questions*, let's define a ManyToMany of Flashcards.
    @ManyToMany
    @JoinTable(
            name = "quiz_flashcards",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "flashcard_id")
    )
    private List<Flashcard> selectedFlashcards = new ArrayList<>();

    // Associated decks (for example, if multiple decks/questions are used)
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Deck> decks = new ArrayList<>();

    // Scores for each participant (assuming Score is an existing entity)
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Score> scores = new ArrayList<>();

    @OneToOne(mappedBy = "quiz")
    @JsonIgnore
    private Invitation invitation;

    @Column(nullable = false)
    private Date startTime;

    // When the quiz session ended (if applicable)
    @Column(nullable = true)
    private Date endTime;

    // Time limit in seconds (or minutes) for the quiz session
    @Column(nullable = false)
    private int timeLimit;

    // Current status of the quiz (e.g., ONGOING, FINISHED). This should match your QuizStatus enum.
    @Column(nullable = false)
    private QuizStatus quizStatus;

    // ID of the winning user (if applicable)
    @Column(nullable = true)
    private Long winner;

    // Flag indicating whether the quiz is for multiple users (multiplayer) or single user.
    @Column(nullable = false)
    private Boolean isMultiple;

}
    // public Long getId() {
    //     return id;
    // }

    // public void setId(Long id) {
    //     this.id = id;
    // }

    // public List<Deck> getDecks() {
    //     return decks;
    //   }
    
    // public void setDecks(List<Deck> decks) {
    //     this.decks = decks;
    // }

    // public List<Score> getScores() {
    //     return scores;
    //   }
    
    // public void setScores(List<Score> scores) {
    //     this.scores = scores;
    // }

    // public Invitation getInvitation() {
    //     return invitation;
    //   }
    
    // public void setInvitation(Invitation invitation) {
    //     this.invitation = invitation;
    // }

    // public Date getStartTime() {
    //     return startTime;
    // }

    // public void setStartTime(Date startTime) {
    //     this.startTime = startTime;
    // }

    // public Date getEndTime() {
    //     return endTime;
    // }

    // public void setEndTime(Date endTime) {
    //     this.endTime = endTime;
    // }

    // public int getTimeLimit() {
    //     return timeLimit;
    // }

    // public void setTimeLimit(int timeLimit) {
    //     this.timeLimit = timeLimit;
    // }

    // public QuizStatus getQuizStatus() {
    //     return quizStatus;
    // }

    // public void setQuizStatus(QuizStatus quizStatus) {
    //     this.quizStatus = quizStatus;
    // }

    // public Long getWinner() {
    //     return winner;
    // }

    // public void setWinner(Long winner) {
    //     this.winner = winner;
    // }

    // public Boolean getIsMultiple() {
    //     return isMultiple;
    // }

    // public void setIsMultiple(Boolean isMultiple) {
    //     this.isMultiple = isMultiple;
    // }

// =======
//     // Getters and Setters
//     public Long getId() {
//         return id;
//     }
//     public void setId(Long id) {
//         this.id = id;
//     }

//     // getters and setters
//     public List<Flashcard> getSelectedFlashcards() {
//         return selectedFlashcards;
//     }

//     public void setSelectedFlashcards(List<Flashcard> selectedFlashcards) {
//         this.selectedFlashcards = selectedFlashcards;
//     }

//     public List<Deck> getDecks() {
//         return decks;
//     }
//     public void setDecks(List<Deck> decks) {
//         this.decks = decks;
//     }

//     public List<Score> getScores() {
//         return scores;
//     }
//     public void setScores(List<Score> scores) {
//         this.scores = scores;
//     }

//     public Date getStartTime() {
//         return startTime;
//     }
//     public void setStartTime(Date startTime) {
//         this.startTime = startTime;
//     }

//     public Date getEndTime() {
//         return endTime;
//     }
//     public void setEndTime(Date endTime) {
//         this.endTime = endTime;
//     }

//     public int getTimeLimit() {
//         return timeLimit;
//     }
//     public void setTimeLimit(int timeLimit) {
//         this.timeLimit = timeLimit;
//     }

//     public QuizStatus getQuizStatus() {
//         return quizStatus;
//     }
//     public void setQuizStatus(QuizStatus quizStatus) {
//         this.quizStatus = quizStatus;
//     }

//     public Long getWinner() {
//         return winner;
//     }
//     public void setWinner(Long winner) {
//         this.winner = winner;
//     }

//     public Boolean getIsMultiple() {
//         return isMultiple;
//     }
//     public void setIsMultiple(Boolean isMultiple) {
//         this.isMultiple = isMultiple;
//     }
