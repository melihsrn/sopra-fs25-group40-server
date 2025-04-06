package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.rest.dto.DeckDTO;
import ch.uzh.ifi.hase.soprafs24.entity.Deck;

import java.util.List;
import java.util.stream.Collectors;

public class DeckMapper {

    public static DeckDTO toDTO(Deck deck) {
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
        dto.setInvitation(deck.getInvitation());
        // Map the transient field: numberofAIcards
        dto.setNumberOfAICards(deck.getNumberOfAICards());
        return dto;
    }

    public static List<DeckDTO> toDTOList(List<Deck> decks) {
        return decks.stream().map(DeckMapper::toDTO).collect(Collectors.toList());
    }

    public static Deck toEntity(DeckDTO dto) {
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
        deck.setInvitation(dto.getInvitation());
        // Map the transient field: numberofAIcards (if provided)
        deck.setNumberOfAICards(dto.getNumberOfAICards());
        return deck;
    }
}
