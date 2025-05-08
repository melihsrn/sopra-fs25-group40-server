package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ch.uzh.ifi.hase.soprafs24.constant.FlashcardCategory;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter // Generates getters, setters automatically
@Entity
@Table(name = "flashcard")
public class Flashcard implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String imageUrl; // Can be a URL or base64 string

    @ManyToOne
    @JoinColumn(name = "deck_id", nullable = false)
    @JsonIgnore
    private Deck deck; 

    @Column(nullable = true)
    private LocalDate date;

    @Column(nullable = false)
    private String description;

    @Column(nullable = true)
    private FlashcardCategory flashcardCategory;

    @Column(nullable = false)
    @JsonProperty("wrong_answers")
    private String[] wrongAnswers;

    @Column(nullable = false)
    private String answer;

    // // Getters and Setters
    // public Long getId() {
    //     return id;
    // }

    // public void setId(Long id) {
    //     this.id = id;
    // }

    // public Deck getDeck() {
    //     return deck;
    // }
    
    // public void setDeck(Deck deck) {
    //     this.deck = deck;
    // }

    // public String getImageUrl() {
    //     return imageUrl;
    // }

    // public void setImageUrl(String imageUrl) {
    //     this.imageUrl = imageUrl;
    // }

    // public String[] getWrongAnswers() {
    //     return wrongAnswers;
    // }

    // public void setWrongAnswers(String[] wrongAnswers) {
    //     this.wrongAnswers = wrongAnswers;
    // }

    // public LocalDate getDate() {
    //     return date;
    // }

    // public void setDate(LocalDate date) {
    //     this.date = date;
    // }

    // public String getDescription() {
    //     return description;
    // }

    // public void setDescription(String description) {
    //     this.description = description;
    // }

    // public FlashcardCategory getFlashcardCategory() {
    //     return flashcardCategory;
    // }

    // public void setFlashcardCategory(FlashcardCategory flashcardCategory) {
    //     this.flashcardCategory = flashcardCategory;
    // }

    // public String getAnswer() {
    //     return answer;
    // }

    // public void setAnswer(String answer) {
    //     this.answer = answer;
    // }
}
