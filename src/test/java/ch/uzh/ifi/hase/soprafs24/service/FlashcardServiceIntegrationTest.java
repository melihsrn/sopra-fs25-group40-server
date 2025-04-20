package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.FlashcardCategory;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Deck;
import ch.uzh.ifi.hase.soprafs24.entity.Flashcard;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.DeckRepository;
import ch.uzh.ifi.hase.soprafs24.repository.FlashcardRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

@WebAppConfiguration
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FlashcardServiceIntegrationTest {

    @Autowired
    private FlashcardService flashcardService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private FlashcardRepository flashcardRepository;

    private User testUser;
    private Deck testDeck;
    private Flashcard testFlashcard;

    @Transactional
    @BeforeEach
    public void setup() {
        // Create a test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setPassword("testPassword");
        testUser.setCreationDate(new Date());
        testUser.setStatus(UserStatus.ONLINE);
        userRepository.save(testUser);

        // Create a test deck
        testDeck = new Deck();
        testDeck.setId(1L);
        testDeck.setTitle("Test Deck");
        testDeck.setDeckCategory(FlashcardCategory.SCIENCE);
        testDeck.setUser(testUser);
        testDeck.setIsPublic(false);
        deckRepository.save(testDeck);

        // Flush and clear session to avoid LazyInitializationException
        userRepository.flush();
        deckRepository.flush();

        // Create a test flashcard
        testFlashcard = new Flashcard();
        testFlashcard.setId(1L);
        testFlashcard.setDescription("Test Question");
        testFlashcard.setAnswer("Test Answer");
        testFlashcard.setWrongAnswers(new String[]{"Wrong Answer 1", "Wrong Answer 2"});
        testFlashcard.setDeck(testDeck);
        flashcardRepository.save(testFlashcard);

        flashcardRepository.flush();
    }

    @Test
    public void testCreateDeck() {
        Deck deck = new Deck();
        deck.setId(2L);
        deck.setTitle("New Deck");
        deck.setDeckCategory(FlashcardCategory.ANIMALS);
        deck.setUser(testUser);
        deck.setIsPublic(true);

        Deck savedDeck = flashcardService.createDeck(testUser.getId(), deck, 5);
        assertNotNull(savedDeck);
        assertEquals("New Deck", savedDeck.getTitle());
        assertEquals(FlashcardCategory.ANIMALS, savedDeck.getDeckCategory());
        assertEquals(testUser.getId(), savedDeck.getUser().getId());
    }

    @Test
    public void testGetDecks() {
        List<Deck> decks = flashcardService.getDecks(testUser.getId());
        assertNotNull(decks);
        assertFalse(decks.isEmpty());
        assertEquals(testDeck.getTitle(), decks.get(0).getTitle());
    }

    @Test
    public void testGetDeckById() {
        Deck fetchedDeck = flashcardService.getDeckById(testDeck.getId());
        assertNotNull(fetchedDeck);
        assertEquals(testDeck.getId(), fetchedDeck.getId());
    }

    @Test
    public void testCreateFlashcard() {
        Flashcard flashcard = new Flashcard();
        flashcard.setId(1L);
        flashcard.setDescription("New Question");
        flashcard.setAnswer("New Answer");
        flashcard.setWrongAnswers(new String[]{"Wrong Answer 1", "Wrong Answer 2"});

        Flashcard createdFlashcard = flashcardService.createFlashcard(testDeck.getId(), flashcard);
        assertNotNull(createdFlashcard);
        assertEquals("New Question", createdFlashcard.getDescription());
        assertEquals("New Answer", createdFlashcard.getAnswer());
    }

    @Test
    public void testGetAllFlashcardsForDeck() {
        List<Flashcard> flashcards = flashcardService.getAllFlashcardsForDeck(testDeck.getId());
        assertNotNull(flashcards);
        assertFalse(flashcards.isEmpty());
        assertEquals(testFlashcard.getDescription(), flashcards.get(0).getDescription());
    }

    @Test
    public void testUpdateFlashcard() {
        testFlashcard.setDescription("Updated Question");
        testFlashcard.setAnswer("Updated Answer");
        flashcardRepository.save(testFlashcard);

        Flashcard updatedFlashcard = flashcardService.getCardById(testFlashcard.getId());
        assertEquals("Updated Question", updatedFlashcard.getDescription());
        assertEquals("Updated Answer", updatedFlashcard.getAnswer());
    }

    @Test
    public void testDeleteFlashcard() {
        Flashcard flashcardToDelete = new Flashcard();
        flashcardToDelete.setId(2L);
        flashcardToDelete.setDescription("Delete This Flashcard");
        flashcardToDelete.setAnswer("Correct Answer");
        flashcardToDelete.setWrongAnswers(new String[]{"Wrong Answer 1", "Wrong Answer 2"});
        flashcardToDelete.setDeck(testDeck);

        Flashcard savedFlashcard = flashcardRepository.save(flashcardToDelete);
        Long flashcardId = savedFlashcard.getId();

        flashcardService.deleteFlashcard(flashcardId);
        assertThrows(ResponseStatusException.class, () -> flashcardService.getCardById(flashcardId));
    }

    @Test
    public void testDeleteDeck() {
        Deck deckToDelete = new Deck();
        deckToDelete.setId(2L);
        deckToDelete.setTitle("Deck to Delete");
        deckToDelete.setDeckCategory(FlashcardCategory.ANIMALS);
        deckToDelete.setUser(testUser);
        deckToDelete.setIsPublic(true);

        Deck savedDeck = deckRepository.save(deckToDelete);
        Long deckId = savedDeck.getId();

        flashcardService.deleteDeck(deckId);
        assertThrows(ResponseStatusException.class, () -> flashcardService.getDeckById(deckId));
    }

    @Test
    public void testGetPublicDecks() {
        List<Deck> publicDecks = flashcardService.getPublicDecks();
        assertNotNull(publicDecks);
        assertTrue(publicDecks.isEmpty());  // Since there are no public decks created yet
    }
}
