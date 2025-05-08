package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO;
import ch.uzh.ifi.hase.soprafs24.entity.Flashcard;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class FlashcardMapper {

    public FlashcardDTO toDTO(Flashcard flashcard) {
        FlashcardDTO dto = new FlashcardDTO();
        dto.setId(flashcard.getId());
        dto.setImageUrl(flashcard.getImageUrl());
        dto.setDate(flashcard.getDate());
        dto.setDescription(flashcard.getDescription());
        dto.setFlashcardCategory(flashcard.getFlashcardCategory());
        dto.setAnswer(flashcard.getAnswer());
        dto.setWrongAnswers(flashcard.getWrongAnswers());
        dto.setDeck(flashcard.getDeck());
        return dto;
    }

    public List<FlashcardDTO> toDTOList(List<Flashcard> flashcards) {
        return flashcards.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Flashcard toEntity(FlashcardDTO dto) {
        Flashcard flashcard = new Flashcard();
        flashcard.setId(dto.getId());
        flashcard.setImageUrl(dto.getImageUrl());
        flashcard.setDate(dto.getDate());
        flashcard.setDescription(dto.getDescription());
        flashcard.setFlashcardCategory(dto.getFlashcardCategory());
        flashcard.setAnswer(dto.getAnswer());
        flashcard.setWrongAnswers(dto.getWrongAnswers());
        flashcard.setDeck(dto.getDeck());
        return flashcard;
    }
}