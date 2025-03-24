package ch.uzh.ifi.hase.soprafs24.entity;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.uzh.ifi.hase.soprafs24.constant.QuizStatus;

@Entity
@Table(name = "quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Deck> decks = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Score> scores = new ArrayList<>();

    @Column(nullable = false)
    private Date startTime;
 
    @Column(nullable = true)
    private Date endTime;

    @Column(nullable = false)
    private int timeLimit;

    @Column(nullable = false)
    private QuizStatus quizStatus;

    @Column(nullable = true)
    private Long winner;
    

}
