package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Flashcard;
import ch.uzh.ifi.hase.soprafs24.entity.Deck;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.DeckRepository;
import ch.uzh.ifi.hase.soprafs24.repository.FlashcardRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;


@Service
@Transactional
public class FlashcardService {

    private final FlashcardRepository flashcardRepository;
    private final UserRepository userRepository;
    private final DeckRepository deckRepository;
    private final ChatGptService chatGptService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FlashcardService(FlashcardRepository flashcardRepository,
                            UserRepository userRepository,
                            DeckRepository deckRepository,
                            ChatGptService chatGptService) {
        this.flashcardRepository = flashcardRepository;
        this.userRepository = userRepository;
        this.deckRepository = deckRepository;
        this.chatGptService = chatGptService;

        // Register the JavaTimeModule to properly handle Java 8 date/time types.
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        // Disable writing dates as timestamps.
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    public List<Deck> getDecks(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return new ArrayList<>(user.getDecks());
    }

    public List<Deck> getPublicDecks() {
        return deckRepository.findByIsPublicTrue();
    }

    public Deck getDeckById(Long deckId) {
        Optional<Deck> existingDeckOpt = deckRepository.findById(deckId);
        if (existingDeckOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Deck not found");
        }
        Deck existingDeck = existingDeckOpt.get();

        return existingDeck;
    }

    /**
     * Helper method to parse the JSON string into a list of Flashcard objects.
     * Assumes that the JSON is a list of flashcards where each flashcard contains
     * the fields: description, answer, and wrongAnswers.
     */
    private List<Flashcard> parseFlashcardsFromJson(String jsonResponse) {
        try {
            // Parse the JSON into a tree
            JsonNode root = objectMapper.readTree(jsonResponse);
            // Extract the "flashcards" node
            JsonNode flashcardsNode = root.get("flashcards");
            if (flashcardsNode == null || !flashcardsNode.isArray()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Invalid flashcards format in ChatGPT response: " + jsonResponse);
            }
            // Deserialize the flashcards array into a list of Flashcard objects
            return objectMapper.readValue(flashcardsNode.toString(), new TypeReference<List<Flashcard>>() {});
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error parsing flashcards from ChatGPT response", e);
        }
    }


    // @Transactional
    public Deck createDeck(Long userId, Deck deck, Integer numberOfCards) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        deck.setUser(user);

        if (Boolean.TRUE.equals(deck.getIsAiGenerated())) {
            // Build the prompt using deck details and numberOfCards
            String prompt = "title of Card Deck is '" + deck.getTitle() + "' "
                    + "with deck category " + deck.getDeckCategory().toString() + " "
                    + " with user given instructions - " + deck.getAiPrompt() + " ";

            // Call ChatGPT API via ChatGptService
            String jsonResponse = chatGptService.generateFlashcards(prompt, numberOfCards);
            // Extract the generated text from the API response
            String generatedJson = chatGptService.extractGeneratedText(jsonResponse);
            // Parse the JSON response into Flashcard objects
            List<Flashcard> flashcards = parseFlashcardsFromJson(generatedJson);

            // Assign the deck to each flashcard so that deck_id is not null
            for (Flashcard flashcard : flashcards) {
                flashcard.setDeck(deck);
            }
            deck.setFlashcards(flashcards);
        }

        deckRepository.save(deck);
        deckRepository.flush();

        return deck;
    }


    public void updateDeck(Long id, Deck updatedDeck) {
        Deck existingDeck = getDeckById(id);
        existingDeck.setTitle(updatedDeck.getTitle());
        existingDeck.setDeckCategory(updatedDeck.getDeckCategory());
        existingDeck.setIsPublic(updatedDeck.getIsPublic());

        List<Flashcard> flashcards = existingDeck.getFlashcards();
        
        for (Flashcard flashcard : flashcards) {
            flashcard.setFlashcardCategory(existingDeck.getDeckCategory());
        }

        flashcardRepository.saveAll(flashcards);
        deckRepository.save(existingDeck);
        deckRepository.flush();
        flashcardRepository.flush();
    }

    public void deleteDeck(Long id) {
        if (!deckRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Deck not found");
        }
        deckRepository.deleteById(id);
        deckRepository.flush();
    }

    public List<Flashcard> getAllFlashcardsForDeck(Long deckId) {
        Deck deck = getDeckById(deckId);
        return new ArrayList<>(deck.getFlashcards());
    }

    public Flashcard getCardById(Long cardId) {
        return flashcardRepository.findById(cardId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Flashcard with ID " + cardId + " not found"));
    } 


    public Flashcard createFlashcard(Long deckId, Flashcard flashcard) {
        Deck deck = getDeckById(deckId);
        flashcard.setDeck(deck);
        flashcard.setFlashcardCategory(deck.getDeckCategory());
        checkIfAnswerIsDuplicated(flashcard);
        flashcardRepository.save(flashcard);
        flashcardRepository.flush();;
        return flashcard; 
    }
    
    private void checkIfAnswerIsDuplicated(Flashcard flashcardToBeChecked) {
        String correctAnswer = flashcardToBeChecked.getAnswer();
        String[] wrongAnswers = flashcardToBeChecked.getWrongAnswers();
    
        for (String wrongAnswer : wrongAnswers) {
            if (correctAnswer.equals(wrongAnswer)) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The correct answer must not appear in the wrong answers list!"
                );
            }
        }
    }

    public void updateFlashcard(Long flashcardId, Flashcard updatedFlashcard) {
        Optional<Flashcard> existingFlashcardOptional = flashcardRepository.findById(flashcardId);

        if (existingFlashcardOptional.isPresent()) {
            Flashcard existingFlashcard = existingFlashcardOptional.get();

            // Update fields
            if (updatedFlashcard.getDate() != null) {
                existingFlashcard.setDate(updatedFlashcard.getDate());
            }
            if (updatedFlashcard.getDescription() != null) {
                existingFlashcard.setDescription(updatedFlashcard.getDescription());
            }
            // flashcard category and isPublic is dependent on the deck so it cannot be updated
            if (updatedFlashcard.getAnswer() != null) {
                existingFlashcard.setAnswer(updatedFlashcard.getAnswer());
            }
            if (updatedFlashcard.getWrongAnswers() != null) {
                existingFlashcard.setWrongAnswers(updatedFlashcard.getWrongAnswers());
            }
            if (updatedFlashcard.getImageUrl() != null) {
                existingFlashcard.setImageUrl(updatedFlashcard.getImageUrl());
            }

            checkIfAnswerIsDuplicated(existingFlashcard);

            flashcardRepository.save(existingFlashcard);
            flashcardRepository.flush();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Flashcard not found");
        }
    }

    public void deleteFlashcard(Long flashcardId) {
        Flashcard flashcard = flashcardRepository.findById(flashcardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Flashcard not found"));

        flashcardRepository.delete(flashcard);  // Use `delete()` instead of `deleteById()` for consistency with your test
        flashcardRepository.flush();
    }

    public void removeImageFromFlashcard(String imageUrl) {
        Flashcard flashcard = flashcardRepository.findByImageUrl(imageUrl);
        if (flashcard != null) {
            flashcard.setImageUrl(null);  // Remove the image URL from the flashcard
            flashcardRepository.save(flashcard);  // Save the updated flashcard
            flashcardRepository.flush();
        }
    }


}
