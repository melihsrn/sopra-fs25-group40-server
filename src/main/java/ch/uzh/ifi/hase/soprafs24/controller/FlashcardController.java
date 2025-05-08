package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.rest.dto.DeckDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO;
import ch.uzh.ifi.hase.soprafs24.entity.Flashcard;
import ch.uzh.ifi.hase.soprafs24.entity.Deck;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.FlashcardMapper;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DeckMapper;
import ch.uzh.ifi.hase.soprafs24.service.FlashcardService;
import ch.uzh.ifi.hase.soprafs24.service.GoogleCloudStorageService;

import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.google.cloud.storage.Blob;

import java.io.IOException;
import java.util.List;

@RestController
public class FlashcardController {

    private final FlashcardService flashcardService;
    private final GoogleCloudStorageService googleCloudStorageService;
    private FlashcardMapper flashcardMapper;
    private DeckMapper deckMapper;

    public FlashcardController(FlashcardService flashcardService, GoogleCloudStorageService googleCloudStorageService,
                                FlashcardMapper flashcardMapper, DeckMapper deckMapper) {
        this.flashcardService = flashcardService;
        this.googleCloudStorageService = googleCloudStorageService;
        this.flashcardMapper= flashcardMapper;
        this.deckMapper= deckMapper;
    }

    @GetMapping("/decks")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<DeckDTO> getDecksForUser(@RequestParam Long userId) {
        List<Deck> decks = flashcardService.getDecks(userId);
        return deckMapper.toDTOList(decks);
    }

    @GetMapping("/decks/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public DeckDTO getDeckById(@PathVariable Long id) {
        // Fetch user from the service layer
        Deck deck = flashcardService.getDeckById(id);

        // Convert entity to DTO and return
        return deckMapper.toDTO(deck);
    }

    @GetMapping("/decks/public")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<DeckDTO> getPublicDecks() {
        List<Deck> publicDecks = flashcardService.getPublicDecks();
        return deckMapper.toDTOList(publicDecks);
    }

    @PostMapping("/decks/addDeck")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public DeckDTO createDeck(@RequestParam Long userId, @Valid @RequestBody DeckDTO deckDTO) {
        Deck deck = deckMapper.toEntity(deckDTO);
        // If numberOfCards is not provided, default to 5 when AI generation is enabled
        System.out.println("NUMBER OF AI CARDS: " + deckDTO.getNumberOfAICards());
        int numberOfCards = (deckDTO.getIsAiGenerated() != null && deckDTO.getIsAiGenerated() && deckDTO.getNumberOfAICards() != null)
                ? deckDTO.getNumberOfAICards()
                : ((deckDTO.getIsAiGenerated() != null && deckDTO.getIsAiGenerated()) ? 5 : 0);
        Deck createdDeck = flashcardService.createDeck(userId, deck, numberOfCards);
        return deckMapper.toDTO(createdDeck);
    }

    @PutMapping("/decks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateDeck(@PathVariable Long id, @RequestBody DeckDTO deckDTO) {
        flashcardService.updateDeck(id, deckMapper.toEntity(deckDTO));
    }

    @DeleteMapping("/decks/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void deleteDeck(@PathVariable Long id) {
        flashcardService.deleteDeck(id);
    }

    @GetMapping("/decks/{deckId}/flashcards")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<FlashcardDTO> getAllFlashcardsForDeck(@PathVariable Long deckId) {
        List<Flashcard> flashcards = flashcardService.getAllFlashcardsForDeck(deckId);
        return flashcardMapper.toDTOList(flashcards);
    }

    @GetMapping("/flashcards/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public FlashcardDTO getFlashcardById(@PathVariable Long id) {
        // Fetch user from the service layer
        Flashcard flashcard = flashcardService.getCardById(id);

        // Convert entity to DTO and return
        return flashcardMapper.toDTO(flashcard);
    }

    @PutMapping("/flashcards/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateFlashcardInfo(@PathVariable Long id, @RequestBody FlashcardDTO updatedFlashcard) {
        flashcardService.updateFlashcard(id, flashcardMapper.toEntity(updatedFlashcard));
    }

    @DeleteMapping("/decks/{deckId}/flashcards/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void deleteFlashcard(@PathVariable Long id,@PathVariable Long deckId) {
        flashcardService.deleteFlashcard(id);
    }

    @PostMapping("/decks/{deckId}/flashcards/addFlashcard")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public FlashcardDTO createFlashcard(@PathVariable Long deckId,@Valid @RequestBody FlashcardDTO flashcardDTO) {
        Flashcard flashcard = flashcardMapper.toEntity(flashcardDTO);
        Flashcard createdFlashcard = flashcardService.createFlashcard(deckId, flashcard);
        return flashcardMapper.toDTO(createdFlashcard);
    }

    @PostMapping("/flashcards/upload-image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("No file uploaded");
            }

            // Generate a unique filename to avoid conflicts
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // Upload the file to Google Cloud Storage
            String fileUrl = googleCloudStorageService.uploadFile(file.getBytes(), fileName);

            // Return the file URL (this is what frontend will use to display the image)
            return ResponseEntity.ok(fileUrl);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }



    @DeleteMapping("/flashcards/delete-image")
    public ResponseEntity<String> deleteImage(@RequestParam("imageUrl") String imageUrl) {
        try {
            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Image URL must be provided");
            }

            boolean deleted = googleCloudStorageService.deleteFile(imageUrl);
            if (deleted) {
                flashcardService.removeImageFromFlashcard(imageUrl);
                return ResponseEntity.ok("Image deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found or already deleted");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete image");
        }
    }



    @GetMapping("/flashcards/image")
    public ResponseEntity<byte[]> getFlashcardImage(@RequestParam String imageUrl) {
        // Extract filename from URL: https://storage.googleapis.com/fs25-group40-bucket/1741277876898_IMG_4993.jpeg
        String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

        Blob blob = googleCloudStorageService.getFileFromBucket(filename);
        if (blob == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] content = blob.getContent();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(blob.getContentType()));  // Handles JPEG, PNG, etc.
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

}
    

