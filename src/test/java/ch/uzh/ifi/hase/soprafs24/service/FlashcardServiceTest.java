package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Deck;
import ch.uzh.ifi.hase.soprafs24.entity.Flashcard;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.DeckRepository;
import ch.uzh.ifi.hase.soprafs24.repository.FlashcardRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FlashcardServiceTest {

    @Mock
    private FlashcardRepository flashcardRepository;

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FlashcardService flashcardService;

    public FlashcardServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllFlashcardsForUser_returnsFlashcards() {
        User user = new User();
        user.setId(1L);

        Deck deck = new Deck();
        deck.setId(1L);
        deck.setTitle("Test Flashcard");

        user.getDecks().add(deck);

        when(deckRepository.findById(1L)).thenReturn(Optional.of(deck));

        List<Flashcard> flashcards = flashcardService.getAllFlashcardsForDeck(1L);

        assertEquals(1, flashcards.size());
        assertEquals("Test Flashcard", flashcards.get(0).getDescription());
    }


    @Test
    void createFlashcard_savesFlashcard() {
        Deck deck = new Deck();
        deck.setId(1L);

        Flashcard flashcard = new Flashcard();
        flashcard.setDescription("New Flashcard");
        flashcard.setDate(LocalDate.now());

        when(deckRepository.findById(1L)).thenReturn(Optional.of(deck));
        when(flashcardRepository.save(any(Flashcard.class))).thenReturn(flashcard);

        Flashcard createdFlashcard = flashcardService.createFlashcard(1L, flashcard);

        assertEquals("New Flashcard", createdFlashcard.getDescription());
        verify(flashcardRepository).save(flashcard);
    }

    @Test
    void deleteFlashcard_deletesSuccessfully() {
        Flashcard flashcard = new Flashcard();
        flashcard.setId(1L);

        when(flashcardRepository.findById(1L)).thenReturn(Optional.of(flashcard));

        flashcardService.deleteFlashcard(1L);

        verify(flashcardRepository, times(1)).delete(flashcard);  // Matches the actual service implementation
    }

}
