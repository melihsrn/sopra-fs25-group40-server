package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.rest.dto.FlashcardDTO;
import ch.uzh.ifi.hase.soprafs24.entity.Flashcard;

import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.*;

@Mapper
public class FlashcardMapper {

    public static FlashcardDTO toDTO(Flashcard flashcard) {
        FlashcardDTO dto = new FlashcardDTO();
        dto.setId(flashcard.getId());
        dto.setImageUrl(flashcard.getImageUrl());
        dto.setDate(flashcard.getDate());
        dto.setDescription(flashcard.getDescription());
        dto.setFlashcardCategory(flashcard.getFlashcardCategory());
        dto.setAnswer(flashcard.getAnswer());
        dto.setWrongAnswers(flashcard.getWrongAnswers());
        dto.setIsPublic(flashcard.getIsPublic());
        dto.setDeck(flashcard.getDeck());
        return dto;
    }

    public static List<FlashcardDTO> toDTOList(List<Flashcard> flashcards) {
        return flashcards.stream().map(FlashcardMapper::toDTO).collect(Collectors.toList());
    }

    public static Flashcard toEntity(FlashcardDTO dto) {
        Flashcard flashcard = new Flashcard();
        flashcard.setId(dto.getId());
        flashcard.setImageUrl(dto.getImageUrl());
        flashcard.setDate(dto.getDate());
        flashcard.setDescription(dto.getDescription());
        flashcard.setFlashcardCategory(dto.getFlashcardCategory());
        flashcard.setAnswer(dto.getAnswer());
        flashcard.setWrongAnswers(dto.getWrongAnswers());
        flashcard.setIsPublic(dto.getIsPublic());
        flashcard.setDeck(dto.getDeck());
        return flashcard;
    }
}
