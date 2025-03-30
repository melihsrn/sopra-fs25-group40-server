package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Flashcard;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {
    // List<Flashcard> findByUserId(Long userId);  // Get all flashcards for a user
    
    // List<Flashcard> findByUser(User user);

    Optional<Flashcard> findById(Long id);

    Flashcard findByImageUrl(String imageUrl);
}
