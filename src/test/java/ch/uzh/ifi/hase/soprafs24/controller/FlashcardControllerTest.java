package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.FlashcardCategory;
import ch.uzh.ifi.hase.soprafs24.entity.Deck;
import ch.uzh.ifi.hase.soprafs24.entity.Flashcard;
import ch.uzh.ifi.hase.soprafs24.rest.dto.DeckDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DeckMapper;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.FlashcardMapper;
import ch.uzh.ifi.hase.soprafs24.service.FlashcardService;
import ch.uzh.ifi.hase.soprafs24.service.GoogleCloudStorageService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.Blob;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlashcardController.class)
public class FlashcardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlashcardService flashcardService;

    @MockBean
    private DeckMapper deckMapper;

    @MockBean
    private GoogleCloudStorageService googleCloudStorageService;

    @MockBean
    private FlashcardMapper flashcardMapper;

    @Test
    void testGetDecksForUser() throws Exception {
        // 1. Setup Deck entity
        Deck deck = new Deck();
        deck.setId(1L);
        deck.setTitle("Math Deck");
        deck.setDeckCategory(FlashcardCategory.MATH);
        deck.setIsPublic(true);
        deck.setIsAiGenerated(false);
        deck.setAiPrompt(null);
        deck.setFlashcards(new ArrayList<>());

        // 2. Expected DTO returned
        DeckDTO deckDTO = new DeckDTO();
        deckDTO.setId(deck.getId());
        deckDTO.setTitle(deck.getTitle());
        deckDTO.setDeckCategory(deck.getDeckCategory());
        deckDTO.setIsPublic(deck.getIsPublic());
        deckDTO.setIsAiGenerated(deck.getIsAiGenerated());
        deckDTO.setAiPrompt(deck.getAiPrompt());
        deckDTO.setFlashcards(deck.getFlashcards());

        // 3. Mock service + mapper
        Mockito.when(flashcardService.getDecks(1L)).thenReturn(List.of(deck));
        Mockito.when(deckMapper.toDTOList(List.of(deck)))
                .thenReturn(List.of(deckDTO));

        // 4. Perform GET request
        mockMvc.perform(get("/decks")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(deckDTO.getId()))
                .andExpect(jsonPath("$[0].title").value(deckDTO.getTitle()))
                .andExpect(jsonPath("$[0].deckCategory").value(deckDTO.getDeckCategory().toString()))
                .andExpect(jsonPath("$[0].isPublic").value(deckDTO.getIsPublic()))
                .andExpect(jsonPath("$[0].isAiGenerated").value(deckDTO.getIsAiGenerated()));
    }

    @Test
    void testGetDeckById() throws Exception {
        // 1. Setup Deck entity
        Deck deck = new Deck();
        deck.setId(1L);
        deck.setTitle("Math Deck");
        deck.setDeckCategory(FlashcardCategory.MATH);
        deck.setIsPublic(true);
        deck.setIsAiGenerated(false);
        deck.setAiPrompt(null);
        deck.setFlashcards(new ArrayList<>());

        // 2. Expected DTO returned
        DeckDTO deckDTO = new DeckDTO();
        deckDTO.setId(deck.getId());
        deckDTO.setTitle(deck.getTitle());
        deckDTO.setDeckCategory(deck.getDeckCategory());
        deckDTO.setIsPublic(deck.getIsPublic());
        deckDTO.setIsAiGenerated(deck.getIsAiGenerated());
        deckDTO.setAiPrompt(deck.getAiPrompt());
        deckDTO.setFlashcards(deck.getFlashcards());

        // 3. Mock service + mapper
        Mockito.when(flashcardService.getDeckById(1L)).thenReturn(deck);
        Mockito.when(deckMapper.toDTO(deck)).thenReturn(deckDTO);

            // 4. Perform GET request
            mockMvc.perform(get("/decks/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(deckDTO.getId()))
                    .andExpect(jsonPath("$.title").value(deckDTO.getTitle()))
                    .andExpect(jsonPath("$.deckCategory").value(deckDTO.getDeckCategory().toString()))
                    .andExpect(jsonPath("$.isPublic").value(deckDTO.getIsPublic()))
                    .andExpect(jsonPath("$.isAiGenerated").value(deckDTO.getIsAiGenerated()));
        
    }

    @Test
    void testGetPublicDecks_success() throws Exception {
        // Arrange — create dummy public decks
        Deck deck1 = new Deck();
        deck1.setId(1L);
        deck1.setTitle("Math Deck");
        deck1.setDeckCategory(FlashcardCategory.MATH);
        deck1.setIsPublic(true);
        deck1.setIsAiGenerated(false);
        deck1.setAiPrompt(null);
        deck1.setFlashcards(new ArrayList<>());

        Deck deck2 = new Deck();
        deck2.setId(2L);
        deck2.setTitle("Science Deck");
        deck2.setDeckCategory(FlashcardCategory.SCIENCE);
        deck2.setIsPublic(true);
        deck2.setIsAiGenerated(false);
        deck2.setAiPrompt(null);
        deck2.setFlashcards(new ArrayList<>());

        List<Deck> mockPublicDecks = Arrays.asList(deck1, deck2);

        // Mock the service call
        Mockito.when(flashcardService.getPublicDecks()).thenReturn(mockPublicDecks);

        // Act & Assert
        mockMvc.perform(get("/decks/public"))
            .andExpect(status().isOk());
    }


    @Test
    void testCreateDeck() throws Exception {
        // 1. Setup DeckDTO (the request body)
        DeckDTO deckDTO = new DeckDTO();
        deckDTO.setTitle("Math Deck");
        deckDTO.setDeckCategory(FlashcardCategory.MATH);
        deckDTO.setIsPublic(true);
        deckDTO.setIsAiGenerated(false);
        deckDTO.setAiPrompt(null);
        deckDTO.setFlashcards(new ArrayList<>());
        deckDTO.setNumberOfAICards(5);

        // 2. Setup the corresponding Deck entity (the entity that will be created)
        Deck deck = new Deck();
        deck.setId(1L);
        deck.setTitle("Math Deck");
        deck.setDeckCategory(FlashcardCategory.MATH);
        deck.setIsPublic(true);
        deck.setIsAiGenerated(false);
        deck.setAiPrompt(null);
        deck.setFlashcards(new ArrayList<>());

        // 3. Expected DeckDTO (the response body)
        DeckDTO expectedDeckDTO = new DeckDTO();
        expectedDeckDTO.setId(deck.getId());
        expectedDeckDTO.setTitle(deck.getTitle());
        expectedDeckDTO.setDeckCategory(deck.getDeckCategory());
        expectedDeckDTO.setIsPublic(deck.getIsPublic());
        expectedDeckDTO.setIsAiGenerated(deck.getIsAiGenerated());
        expectedDeckDTO.setAiPrompt(deck.getAiPrompt());
        expectedDeckDTO.setFlashcards(deck.getFlashcards());

        // 4. Mock service and mapper methods
        Mockito.when(deckMapper.toEntity(deckDTO)).thenReturn(deck);
        Mockito.when(flashcardService.createDeck(1L, deck, 5)).thenReturn(deck);
        Mockito.when(deckMapper.toDTO(deck)).thenReturn(expectedDeckDTO);

        // 5. Perform POST request
        mockMvc.perform(post("/decks/addDeck")
                        .param("userId", "1") // Simulating userId parameter
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(deckDTO))) // Converting deckDTO to JSON string
                .andExpect(status().isCreated());  // Expecting 201 Created status
    }

    // Utility method to convert an object to a JSON string
    private String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    void testUpdateDeck() throws Exception {
        // 1. Setup DeckDTO (the request body)
        DeckDTO deckDTO = new DeckDTO();
        deckDTO.setTitle("Updated Math Deck");
        deckDTO.setDeckCategory(FlashcardCategory.MATH);
        deckDTO.setIsPublic(true);
        deckDTO.setIsAiGenerated(true);
        deckDTO.setAiPrompt("New AI Prompt");
        deckDTO.setFlashcards(new ArrayList<>());

        // 2. Setup the corresponding Deck entity (the entity that will be updated)
        Deck deck = new Deck();
        deck.setId(1L);
        deck.setTitle("Updated Math Deck");
        deck.setDeckCategory(FlashcardCategory.MATH);
        deck.setIsPublic(true);
        deck.setIsAiGenerated(true);
        deck.setAiPrompt("New AI Prompt");
        deck.setFlashcards(new ArrayList<>());

        // 3. Mock service and mapper methods
        // Mock DeckMapper.toEntity to return the Deck entity
        Mockito.when(deckMapper.toEntity(deckDTO)).thenReturn(deck);  // Mock DeckMapper
        
        // Mock FlashcardService to do nothing on updateDeck call
        Mockito.doNothing().when(flashcardService).updateDeck(1L, deck);  // Mock the update service method

        // 4. Perform PUT request
        mockMvc.perform(put("/decks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(deckDTO))) // Converting deckDTO to JSON string
                .andExpect(status().isNoContent());  // Expecting 204 No Content status
    }



    @Test
    public void testGetFlashcardsForDeck() throws Exception {
        when(flashcardService.getAllFlashcardsForDeck(1L)).thenReturn(List.of());

        mockMvc.perform(get("/decks/1/flashcards"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetFlashcardById() throws Exception {
        // 1. Setup Flashcard entity
        Flashcard flashcard = new Flashcard();
        flashcard.setId(1L);
        flashcard.setAnswer("4");
        flashcard.setDescription("What is 2 + 2?");
        flashcard.setWrongAnswers(new String[]{"3", "5", "6"});
        flashcard.setImageUrl("http://example.com/2plus2.jpg");
        flashcard.setFlashcardCategory(FlashcardCategory.MATH);

        // 2. Expected DTO returned
        FlashcardDTO flashcardDTO = new FlashcardDTO();
        flashcardDTO.setId(1L);
        flashcardDTO.setAnswer("4");
        flashcardDTO.setDescription("What is 2 + 2?");
        flashcardDTO.setWrongAnswers(new String[]{"3", "5", "6"});
        flashcardDTO.setImageUrl("http://example.com/2plus2.jpg");
        flashcardDTO.setFlashcardCategory(FlashcardCategory.MATH);

        // 3. Mock service + mapper
        Mockito.when(flashcardService.getCardById(1L)).thenReturn(flashcard);
        Mockito.when(flashcardMapper.toDTO(flashcard)).thenReturn(flashcardDTO);

            // 4. Perform GET request
            mockMvc.perform(get("/flashcards/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(flashcardDTO.getId()))
                    .andExpect(jsonPath("$.description").value(flashcardDTO.getDescription()))
                    .andExpect(jsonPath("$.answer").value(flashcardDTO.getAnswer().toString()));
        
    }

    @Test
    void testUpdateFlashcardInfo() throws Exception {
        // 1. Setup 
        Flashcard flashcard = new Flashcard();
        flashcard.setId(1L);
        flashcard.setAnswer("4");
        flashcard.setDescription("What is 2 + 2?");
        flashcard.setWrongAnswers(new String[]{"3", "5", "6"});
        flashcard.setImageUrl("http://example.com/2plus2.jpg");
        flashcard.setFlashcardCategory(FlashcardCategory.MATH);

        // 2. Expected DTO
        FlashcardDTO flashcardDTO = new FlashcardDTO();
        flashcardDTO.setId(1L);
        flashcardDTO.setAnswer("4");
        flashcardDTO.setDescription("What is 2 + 2?");
        flashcardDTO.setWrongAnswers(new String[]{"3", "5", "6"});
        flashcardDTO.setImageUrl("http://example.com/2plus2.jpg");
        flashcardDTO.setFlashcardCategory(FlashcardCategory.MATH);

        // 3. Mock service and mapper methods
        Mockito.when(flashcardMapper.toDTO(flashcard)).thenReturn(flashcardDTO);

        Mockito.doNothing().when(flashcardService).updateFlashcard(1L,flashcard);

        // 4. Perform PUT request
        mockMvc.perform(put("/flashcards/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(flashcardDTO))) // Converting deckDTO to JSON string
                .andExpect(status().isNoContent());  // Expecting 204 No Content status
    }

    @Test
    void testCreateFlashcard() throws Exception {
        // 1. Setup the FlashcardDTO (request body)
        FlashcardDTO flashcardDTO = new FlashcardDTO();
        flashcardDTO.setAnswer("4");
        flashcardDTO.setDescription("What is 2 + 2?");
        flashcardDTO.setWrongAnswers(new String[]{"3", "5", "6"});
        flashcardDTO.setImageUrl("http://example.com/2plus2.jpg");
        flashcardDTO.setFlashcardCategory(FlashcardCategory.MATH);

        // Create a Deck object to associate with the Flashcard
        Deck deck = new Deck();
        deck.setId(1L);
        flashcardDTO.setDeck(deck);  // Associate the deck with FlashcardDTO

        // 2. Setup the corresponding Flashcard entity
        Flashcard flashcard = new Flashcard();
        flashcard.setAnswer("4");
        flashcard.setDescription("What is 2 + 2?");
        flashcard.setWrongAnswers(new String[]{"3", "5", "6"});
        flashcard.setImageUrl("http://example.com/2plus2.jpg");
        flashcard.setFlashcardCategory(FlashcardCategory.MATH);
        flashcard.setDeck(deck);  // Set the deck association

        // 3. Setup the created FlashcardDTO to be returned
        FlashcardDTO createdFlashcardDTO = new FlashcardDTO();
        createdFlashcardDTO.setAnswer("4");
        createdFlashcardDTO.setDescription("What is 2 + 2?");
        createdFlashcardDTO.setWrongAnswers(new String[]{"3", "5", "6"});
        createdFlashcardDTO.setImageUrl("http://example.com/2plus2.jpg");
        createdFlashcardDTO.setFlashcardCategory(FlashcardCategory.MATH);
        createdFlashcardDTO.setDeck(deck);  // Set the deck association

        // 4. Mock service and mapper methods
        Mockito.when(flashcardMapper.toEntity(flashcardDTO)).thenReturn(flashcard);  // Mock toEntity
        Mockito.when(flashcardService.createFlashcard(1L, flashcard)).thenReturn(flashcard);  // Mock createFlashcard
        Mockito.when(flashcardMapper.toDTO(flashcard)).thenReturn(createdFlashcardDTO);  // Mock toDTO

        // 5. Perform POST request to create the flashcard
        mockMvc.perform(post("/decks/{deckId}/flashcards/addFlashcard", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(flashcardDTO))) // Converting FlashcardDTO to JSON string
                .andExpect(status().isCreated());  // Expecting 201 Created status
    }



    @Test
    public void testUploadImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "dummy".getBytes());

        when(googleCloudStorageService.uploadFile(any(), any())).thenReturn("http://storage/image.png");

        mockMvc.perform(multipart("/flashcards/upload-image")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("http://storage/image.png"));
    }

    @Test
    public void testGetImage() throws Exception {
        Blob blob = Mockito.mock(Blob.class);
        when(blob.getContent()).thenReturn("imageData".getBytes());
        when(blob.getContentType()).thenReturn("image/png");

        when(googleCloudStorageService.getFileFromBucket(any())).thenReturn(blob);

        mockMvc.perform(get("/flashcards/image")
                        .param("imageUrl", "https://storage.googleapis.com/bucket/image.png"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"))
                .andExpect(content().bytes("imageData".getBytes()));
    }

    @Test
    public void testDeleteImageSuccess() throws Exception {
        when(googleCloudStorageService.deleteFile(any())).thenReturn(true);

        mockMvc.perform(delete("/flashcards/delete-image")
                        .param("imageUrl", "https://storage.googleapis.com/bucket/image.png"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteDeck() throws Exception {
        // 1. Setup the Deck ID to be deleted
        Long deckId = 1L;

        // 2. Mock the service method (mocking void method)
        Mockito.doNothing().when(flashcardService).deleteDeck(deckId);  // Mock the delete service method

        // 3. Perform DELETE request
        mockMvc.perform(delete("/decks/{id}", deckId))
                .andExpect(status().isOk());  // Expecting 200 OK status

        // 4. Verify if the service method was called correctly
        Mockito.verify(flashcardService, times(1)).deleteDeck(deckId);
    }


    @Test
    public void testDeleteFlashcard() throws Exception {

         // 1. Setup the Deck ID to be deleted
         Long deckId = 1L;
         Long id = 2L;

         // 2. Mock the service method (mocking void method)
         Mockito.doNothing().when(flashcardService).deleteFlashcard(id);  // Mock the delete service method
 
         // 3. Perform DELETE request
         mockMvc.perform(delete("/decks/{deckId}/flashcards/{id}",deckId,id))
                .andExpect(status().isOk()); // Expecting 200 OK status
 
         // 4. Verify if the service method was called correctly
         Mockito.verify(flashcardService, times(1)).deleteFlashcard(id);
    }
}
