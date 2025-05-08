package ch.uzh.ifi.hase.soprafs24.entity;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.uzh.ifi.hase.soprafs24.constant.FlashcardCategory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter // Generates getters, setters automatically
@Entity
@Table(name = "deck")
public class Deck  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = true)
    @JsonIgnore
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "invitation_id", nullable = true)
    @JsonIgnore
    private Invitation invitation;

    private String title;

    @Column(nullable = false)
    private FlashcardCategory deckCategory;

    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Flashcard> flashcards = new ArrayList<>();  // Always initialized

    @Column(nullable = false)
    private Boolean isPublic;

    @Column
    private Boolean isAiGenerated;

    @Column(length = 2048) // Optional, allows longer prompts
    private String aiPrompt;

    // Transient field: not stored in database, defaults to null.
    @Transient
    private Integer numberOfAICards;

    // // Getters and Setters
    // public Long getId() {
    //     return id;
    // }

    // public void setId(Long id) {
    //     this.id = id;
    // }

    // public FlashcardCategory getDeckCategory() {
    //     return deckCategory;
    // }

    // public void setDeckCategory(FlashcardCategory deckCategory) {
    //     this.deckCategory = deckCategory;
    // }

    // public Boolean getIsPublic() {
    //     return isPublic;
    // }

    // public void setIsPublic(Boolean isPublic) {
    //     this.isPublic = isPublic;
    // }

    // public List<Flashcard> getFlashcards() {
    //     return flashcards;
    //   }
    
    // public void setFlashcards(List<Flashcard> flashcards) {
    //     this.flashcards = flashcards;
    // }

    // public String getTitle() {
    //     return title;
    // }

    // public void setTitle(String title) {
    //     this.title = title;
    // }

    // public User getUser() {
    //     return user;
    // }
    
    // public void setUser(User user) {
    //     this.user = user;
    // }

    // public Quiz getQuiz() {
    //     return quiz;
    // }
    
    // public void setQuiz(Quiz quiz) {
    //     this.quiz = quiz;
    // }

    // public Invitation getInvitation() {
    //     return invitation;
    // }
    
    // public void setInvitation(Invitation invitation) {
    //     this.invitation = invitation;
    // }

    // public Boolean getIsAiGenerated() {
    //     return isAiGenerated;
    // }

    // public void setIsAiGenerated(Boolean isAiGenerated) {
    //     this.isAiGenerated = isAiGenerated;
    // }

    // public String getAiPrompt() {
    //     return aiPrompt;
    // }

    // public void setAiPrompt(String aiPrompt) {
    //     this.aiPrompt = aiPrompt;
    // }

    // // Getter and Setter for the transient field numberofAIcards
    // public Integer getNumberofAIcards() {
    //     return numberofAIcards;
    // }

    // public void setNumberofAIcards(Integer numberofAIcards) {
    //     this.numberofAIcards = numberofAIcards;
    // }

}
