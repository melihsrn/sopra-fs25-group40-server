package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Deck;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("deckRepository")
public interface DeckRepository extends JpaRepository<Deck, Long> {

    Optional<Deck> findById(Long id);

    // List<Deck> findByCreatedBy(Long createdBy);

    // List<Deck> findByCreatedByAndIsPublic(Long createdBy, Boolean isPublic);

    List<Deck> findByIsPublicTrue();
}
