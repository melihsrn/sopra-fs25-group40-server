package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;


/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter // Generates getters, setters automatically
@Entity
@Table(name = "user")
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String name;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = true, unique = true)
  private String token;

  // @Column(nullable = true)
  // private String fcmToken;

  @Column(nullable = false)
  private UserStatus status;

  // defining additional variables in User representation
  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private Date creationDate;

  @Column
  private Date birthday;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Deck> decks = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Score> scores = new ArrayList<>();

  @OneToMany(mappedBy = "fromUser", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Invitation> invitationsSent = new ArrayList<>();

  @OneToMany(mappedBy = "toUser", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Invitation> invitationsReceived = new ArrayList<>();


  // public Long getId() {
  //   return id;
  // }

  // public void setId(Long id) {
  //   this.id = id;
  // }

  // public String getName() {
  //   return name;
  // }

  // public void setName(String name) {
  //   this.name = name;
  // }

  // public String getUsername() {
  //   return username;
  // }

  // public void setUsername(String username) {
  //   this.username = username;
  // }

  // public String getToken() {
  //   return token;
  // }

  // public void setToken(String token) {
  //   this.token = token;
  // }

  // // public String getFcmToken() {
  // //   return fcmToken;
  // // }

  // // public void setFcmToken(String fcmToken) {
  // //   this.fcmToken = fcmToken;
  // // }

  // public UserStatus getStatus() {
  //   return status;
  // }

  // public void setStatus(UserStatus status) {
  //   this.status = status;
  // }

  // public String getPassword() {
  //   return password;
  // }

  // public void setPassword(String password) {
  //   this.password = password;
  // }

  // public Date getCreationDate() {
  //   return creationDate;
  // }

  // public void setCreationDate(Date creationDate) {
  //   this.creationDate = creationDate;
  // }

  // public Date getBirthday() {
  //   return birthday;
  // }

  // public void setBirthday(Date birthday) {
  //   this.birthday = birthday;
  // }

  // public List<Deck> getDecks() {
  //   return decks;
  // }

  // public void setDecks(List<Deck> decks) {
  //     this.decks = decks;
  // }

  // public List<Score> getScores() {
  //   return scores;
  // }

  // public void setScores(List<Score> scores) {
  //   this.scores = scores;
  // }

  // public List<Invitation> getInvitationsSent() {
  //   return invitationsSent;
  // }

  // public void setInvitationsSent(List<Invitation> invitationsSent) {
  //   this.invitationsSent = invitationsSent;
  // }

  // public List<Invitation> getInvitationsReceived() {
  //   return invitationsReceived;
  // }

  // public void setInvitationsReceived(List<Invitation> invitationsReceived) {
  //   this.invitationsReceived = invitationsReceived;
  // }


}
