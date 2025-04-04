package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Invitation;
import ch.uzh.ifi.hase.soprafs24.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;


@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    Optional<Invitation> findById(Long id);

    List<Invitation> findByFromUser(User fromUser);

    List<Invitation> findByToUser(User toUser);

}
