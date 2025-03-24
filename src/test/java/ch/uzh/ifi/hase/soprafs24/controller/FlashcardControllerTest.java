package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO;
import ch.uzh.ifi.hase.soprafs24.entity.Flashcard;
import ch.uzh.ifi.hase.soprafs24.service.FlashcardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlashcardController.class)
class FlashcardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlashcardService flashcardService;

    @Autowired
    private ObjectMapper objectMapper;

    private Flashcard flashcard;

    @BeforeEach
    void setUp() {
        flashcard = new Flashcard();
        flashcard.setId(1L);
        flashcard.setDescription("Test Flashcard");
        flashcard.setDate(LocalDate.now());
    }

    @Test
    void getFlashcardsForUser_returnsList() throws Exception {
        when(flashcardService.getAllFlashcardsForDeck(1L)).thenReturn(Collections.singletonList(flashcard));

        mockMvc.perform(get("/decks/1/flashcards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Test Flashcard"));
    }

    @Test
    void createFlashcard_createsAndReturnsDTO() throws Exception {
        
        FlashcardDTO dto = new FlashcardDTO();
        dto.setDescription("New Flashcard");
        dto.setDate(LocalDate.now());

        when(flashcardService.createFlashcard(eq(1L), any(Flashcard.class))).thenReturn(flashcard);

        mockMvc.perform(post("/decks/1/flashcards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test Flashcard"));
    }

    @Test
    void deleteFlashcard_deletesSuccessfully() throws Exception {
        doNothing().when(flashcardService).deleteFlashcard(1L);

        mockMvc.perform(delete("/decks/1/flashcards/1"))
                .andExpect(status().isOk());

        verify(flashcardService).deleteFlashcard(1L);
    }

    @Test
    void createFlashcard_withMissingDescription_returnsBadRequest() throws Exception {
        // User user = new User();
        // user.setId(1L);
        // user.setName("Test User");
        // user.setUsername("testUsername");
        // user.setToken("1");
        // user.setStatus(UserStatus.ONLINE);

        // long userId = user.getId();
        FlashcardDTO dto = new FlashcardDTO();
        dto.setDate(LocalDate.now());  // Missing description!

        mockMvc.perform(post("/decks/1/flashcards") 
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Description is required."));
    }

    @Test
    void createFlashcard_withMissingDate_returnsBadRequest() throws Exception {
        FlashcardDTO dto = new FlashcardDTO();
        dto.setDescription("Flashcard without date");
    
        mockMvc.perform(post("/decks/1/flashcards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.date").value("Date is required."));
    }
    

}
