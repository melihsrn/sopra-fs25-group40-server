package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.rest.dto.DeckDTO;
import ch.uzh.ifi.hase.soprafs24.entity.Deck;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class DeckMapper {

    public DeckDTO toDTO(Deck deck) {
        DeckDTO dto = new DeckDTO();
        dto.setId(deck.getId());
        dto.setTitle(deck.getTitle());
        dto.setDeckCategory(deck.getDeckCategory());
        dto.setIsPublic(deck.getIsPublic());
        dto.setUser(deck.getUser());
        dto.setIsAiGenerated(deck.getIsAiGenerated());
        dto.setAiPrompt(deck.getAiPrompt());
        dto.setFlashcards(deck.getFlashcards());
        dto.setQuiz(deck.getQuiz());
        // Map the transient field: numberofAIcards
        dto.setNumberOfAICards(deck.getNumberOfAICards());
        return dto;
    }

    public List<DeckDTO> toDTOList(List<Deck> decks) {
        return decks.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Deck toEntity(DeckDTO dto) {
        Deck deck = new Deck();
        deck.setId(dto.getId());
        deck.setTitle(dto.getTitle());
        deck.setDeckCategory(dto.getDeckCategory());
        deck.setIsPublic(dto.getIsPublic());
        deck.setUser(dto.getUser());
        deck.setIsAiGenerated(dto.getIsAiGenerated());
        deck.setAiPrompt(dto.getAiPrompt());
        deck.setFlashcards(dto.getFlashcards());
        deck.setQuiz(dto.getQuiz());
        // Map the transient field: numberofAIcards (if provided)
        deck.setNumberOfAICards(dto.getNumberOfAICards());
        return deck;
    }
}