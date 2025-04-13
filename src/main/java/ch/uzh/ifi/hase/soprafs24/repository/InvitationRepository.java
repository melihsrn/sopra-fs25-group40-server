package ch.uzh.ifi.hase.soprafs24.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ch.uzh.ifi.hase.soprafs24.entity.Invitation;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    // Additional query methods (if needed) can be defined here.
}
