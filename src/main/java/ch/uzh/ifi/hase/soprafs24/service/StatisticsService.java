// StatisticsService.java
package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Statistics;
import ch.uzh.ifi.hase.soprafs24.entity.Quiz;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class StatisticsService {

    @Autowired
    private StatisticsRepository statisticsRepository;

    public void recordQuizStats(User user, Quiz quiz, int score, int attempts, long timeTakenMillis) {
//        Statistics stats = new Statistics();
        Statistics stats = statisticsRepository
                .lockByQuizIdAndUserId(quiz.getId(), user.getId())  // ← uses the lock
                .orElseGet(() -> {
                    Statistics s = new Statistics();
                    s.setQuiz(quiz);
                    s.setUser(user);
                    return s;
                });

//        stats.setUser(user);
//        stats.setQuiz(quiz);
        stats.setScore(score);
        stats.setNumberOfAttempts(attempts);
        // Convert milliseconds to seconds if you want, or store as is
        stats.setTimeTaken(timeTakenMillis / 1000);
        stats.setQuizDate(new Date());
        statisticsRepository.save(stats);
    }
}
