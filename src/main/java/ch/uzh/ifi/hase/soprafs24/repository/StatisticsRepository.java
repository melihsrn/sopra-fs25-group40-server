package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Quiz;
import ch.uzh.ifi.hase.soprafs24.entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("statisticsRepository")
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    // Additional query methods (if needed) can be defined here.
}
