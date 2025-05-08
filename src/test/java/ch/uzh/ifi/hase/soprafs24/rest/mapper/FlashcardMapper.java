package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Deck;
import ch.uzh.ifi.hase.soprafs24.entity.Flashcard;
import ch.uzh.ifi.hase.soprafs24.constant.FlashcardCategory;
import ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlashcardMapperTest {

    private FlashcardMapper flashcardMapper;

    @BeforeEach
    public void setup() {
        flashcardMapper = new FlashcardMapper();
    }

    @Test
    void testToDTO_mapsAllFieldsCorrectly() {
        // Arrange
        Flashcard flashcard = new Flashcard();
        flashcard.setId(1L);
        flashcard.setImageUrl("http://image.com/img.png");
        flashcard.setDate(LocalDate.of(2025, 4, 20));
        flashcard.setDescription("A description");
        flashcard.setFlashcardCategory(FlashcardCategory.SCIENCE); // Assuming enum exists
        flashcard.setAnswer("Correct Answer");
        flashcard.setWrongAnswers(new String[]{"Wrong 1", "Wrong 2"});

        Deck deck = new Deck();
        deck.setId(100L);
        flashcard.setDeck(deck);

        // Act
        FlashcardDTO dto = flashcardMapper.toDTO(flashcard);

        // Assert
        assertEquals(1L, dto.getId());
        assertEquals("http://image.com/img.png", dto.getImageUrl());
        assertEquals(LocalDate.of(2025, 4, 20), dto.getDate());
        assertEquals("A description", dto.getDescription());
        assertEquals(FlashcardCategory.SCIENCE, dto.getFlashcardCategory());
        assertEquals("Correct Answer", dto.getAnswer());
        assertArrayEquals(new String[]{"Wrong 1", "Wrong 2"}, dto.getWrongAnswers());
        assertEquals(deck, dto.getDeck());
    }

    @Test
    void testToEntity_mapsAllFieldsCorrectly() {
        // Arrange
        FlashcardDTO dto = new FlashcardDTO();
        dto.setId(2L);
        dto.setImageUrl("http://img.com/pic.jpg");
        dto.setDate(LocalDate.of(2025, 1, 15));
        dto.setDescription("Flashcard Description");
        dto.setFlashcardCategory(FlashcardCategory.MATH);
        dto.setAnswer("Answer");
        dto.setWrongAnswers(new String[]{"Wrong A", "Wrong B"});

        Deck deck = new Deck();
        deck.setId(200L);
        dto.setDeck(deck);

        // Act
        Flashcard flashcard = flashcardMapper.toEntity(dto);

        // Assert
        assertEquals(2L, flashcard.getId());
        assertEquals("http://img.com/pic.jpg", flashcard.getImageUrl());
        assertEquals(LocalDate.of(2025, 1, 15), flashcard.getDate());
        assertEquals("Flashcard Description", flashcard.getDescription());
        assertEquals(FlashcardCategory.MATH, flashcard.getFlashcardCategory());
        assertEquals("Answer", flashcard.getAnswer());
        assertArrayEquals(new String[]{"Wrong A", "Wrong B"}, flashcard.getWrongAnswers());
        assertEquals(deck, flashcard.getDeck());
    }

    @Test
    void testToDTOList_mapsListCorrectly() {
        // Arrange
        Flashcard flashcard1 = new Flashcard();
        flashcard1.setId(1L);
        flashcard1.setDescription("Card 1");

        Flashcard flashcard2 = new Flashcard();
        flashcard2.setId(2L);
        flashcard2.setDescription("Card 2");

        List<Flashcard> flashcards = Arrays.asList(flashcard1, flashcard2);

        // Act
        List<FlashcardDTO> dtoList = flashcardMapper.toDTOList(flashcards);

        // Assert
        assertNotNull(dtoList);
        assertEquals(2, dtoList.size());
        assertEquals("Card 1", dtoList.get(0).getDescription());
        assertEquals("Card 2", dtoList.get(1).getDescription());
    }
}
