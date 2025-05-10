package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Quiz;
import ch.uzh.ifi.hase.soprafs24.entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository("statisticsRepository")
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    // Additional query methods (if needed) can be defined here.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Statistics s "
            + "where s.quiz.id = :quizId and s.user.id = :userId")
    Optional<Statistics> lockByQuizIdAndUserId(Long quizId, Long userId);
}
