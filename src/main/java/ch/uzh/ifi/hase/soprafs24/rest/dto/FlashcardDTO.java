package ch.uzh.ifi.hase.soprafs24.rest.dto;


import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import ch.uzh.ifi.hase.soprafs24.constant.FlashcardCategory;
import ch.uzh.ifi.hase.soprafs24.entity.Deck;

public class FlashcardDTO {

    private Long id;

    private Deck deck;

    private String imageUrl;

    private String description;

    private LocalDate date;

    private String answer;

    private FlashcardCategory flashcardCategory;

    private String[] wrongAnswers;

    @NotNull
    private boolean isPublic;

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FlashcardCategory getFlashcardCategory() {
        return flashcardCategory;
    }

    public void setFlashcardCategory(FlashcardCategory flashcardCategory) {
        this.flashcardCategory = flashcardCategory;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String[] getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(String[] wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

}
