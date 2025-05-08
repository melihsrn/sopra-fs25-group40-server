package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.constant.FlashcardCategory;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.DeckDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeckMapperTest {

    private DeckMapper deckMapper;

    @BeforeEach
    public void setup() {
        deckMapper = new DeckMapper();
    }

    @Test
    void testToDTO_mapsAllFieldsCorrectly() {
        // Arrange
        Deck deck = new Deck();
        deck.setId(1L);
        deck.setTitle("History Deck");
        deck.setDeckCategory(FlashcardCategory.HISTORY);
        deck.setIsPublic(true);
        deck.setIsAiGenerated(true);
        deck.setAiPrompt("Generate history facts");
        deck.setNumberOfAICards(5);

        User user = new User();
        user.setUsername("john");
        deck.setUser(user);

        Flashcard flashcard = new Flashcard();
        flashcard.setDescription("Who was the first president?");
        deck.setFlashcards(List.of(flashcard));

        Quiz quiz = new Quiz();
        quiz.setId(10L);
        deck.setQuiz(quiz);

        Invitation invitation = new Invitation();
        invitation.setId(20L);
        deck.setInvitation(invitation);

        // Act
        DeckDTO dto = deckMapper.toDTO(deck);

        // Assert
        assertEquals(deck.getId(), dto.getId());
        assertEquals(deck.getTitle(), dto.getTitle());
        assertEquals(deck.getDeckCategory(), dto.getDeckCategory());
        assertEquals(deck.getIsPublic(), dto.getIsPublic());
        assertEquals(deck.getIsAiGenerated(), dto.getIsAiGenerated());
        assertEquals(deck.getAiPrompt(), dto.getAiPrompt());
        assertEquals(deck.getNumberOfAICards(), dto.getNumberOfAICards());
        assertEquals(deck.getUser(), dto.getUser());
        assertEquals(deck.getFlashcards(), dto.getFlashcards());
        assertEquals(deck.getQuiz(), dto.getQuiz());
    }

    @Test
    void testToEntity_mapsAllFieldsCorrectly() {
        // Arrange
        DeckDTO dto = new DeckDTO();
        dto.setId(2L);
        dto.setTitle("Science Deck");
        dto.setDeckCategory(FlashcardCategory.SCIENCE);
        dto.setIsPublic(false);
        dto.setIsAiGenerated(false);
        dto.setAiPrompt("Explain gravity");
        dto.setNumberOfAICards(3);

        User user = new User();
        user.setUsername("alice");
        dto.setUser(user);

        Flashcard flashcard = new Flashcard();
        flashcard.setDescription("What is gravity?");
        dto.setFlashcards(List.of(flashcard));

        Quiz quiz = new Quiz();
        quiz.setId(11L);
        dto.setQuiz(quiz);

        // Act
        Deck deck = deckMapper.toEntity(dto);

        // Assert
        assertEquals(dto.getId(), deck.getId());
        assertEquals(dto.getTitle(), deck.getTitle());
        assertEquals(dto.getDeckCategory(), deck.getDeckCategory());
        assertEquals(dto.getIsPublic(), deck.getIsPublic());
        assertEquals(dto.getIsAiGenerated(), deck.getIsAiGenerated());
        assertEquals(dto.getAiPrompt(), deck.getAiPrompt());
        assertEquals(dto.getNumberOfAICards(), deck.getNumberOfAICards());
        assertEquals(dto.getUser(), deck.getUser());
        assertEquals(dto.getFlashcards(), deck.getFlashcards());
        assertEquals(dto.getQuiz(), deck.getQuiz());
    }

    @Test
    void testToDTOList_mapsListCorrectly() {
        // Arrange
        Deck deck1 = new Deck();
        deck1.setId(1L);
        deck1.setTitle("Deck 1");

        Deck deck2 = new Deck();
        deck2.setId(2L);
        deck2.setTitle("Deck 2");

        List<Deck> deckList = Arrays.asList(deck1, deck2);

        // Act
        List<DeckDTO> dtoList = deckMapper.toDTOList(deckList);

        // Assert
        assertEquals(2, dtoList.size());
        assertEquals(1L, dtoList.get(0).getId());
        assertEquals("Deck 1", dtoList.get(0).getTitle());
        assertEquals(2L, dtoList.get(1).getId());
        assertEquals("Deck 2", dtoList.get(1).getTitle());
    }
}
