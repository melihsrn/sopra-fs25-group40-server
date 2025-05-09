package ch.uzh.ifi.hase.soprafs24.entity;

import java.util.Date;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter // Generates getters, setters automatically
@Entity
@Table(name = "invitation")
public class Invitation  implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @OneToOne(mappedBy = "invitation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}
            , orphanRemoval = false)
    @JsonIgnore
    private Quiz quiz;

    @OneToMany(mappedBy = "invitation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Deck> decks = new ArrayList<>();

    @Column(nullable = false)
    private int timeLimit;

    @ManyToOne
    @JoinColumn(name = "from_user_id", nullable = false)
    @JsonIgnore
    private User fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id", nullable = false)
    @JsonIgnore
    private User toUser;

    @Column(nullable = false)
    private Boolean isAccepted;

    @Column(nullable = true)
    private Date isAcceptedDate;
    
}
//     // The quiz session associated with this invitation.
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "quiz_id", nullable = false)
//     private Quiz quiz;

//     // Status of the invitation (e.g., PENDING, ACCEPTED, DECLINED)
//     @Column(nullable = false)
//     private InvitationStatus status;
