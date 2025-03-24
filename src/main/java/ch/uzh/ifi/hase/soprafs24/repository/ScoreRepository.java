package ch.uzh.ifi.hase.soprafs24.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs24.entity.Score;

@Repository("scoreRepository")
public interface ScoreRepository extends JpaRepository<Score,Long>{
    Optional<Score> findById(Long id);
} 
