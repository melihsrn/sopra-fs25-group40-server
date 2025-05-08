package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Deck;
import ch.uzh.ifi.hase.soprafs24.entity.Invitation;
import ch.uzh.ifi.hase.soprafs24.entity.Score;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter // Generates getters, setters automatically
public class UserGetDTO {

  private Long id;
  private String name;
  private String token;
  // private String fcmToken;
  private String username;
  private UserStatus status;
  private Date creationDate; // adding the additional variables in Get DTO so that we can fetch those information
  private Date birthday;
  private List<Deck> decks;
  private List<Score> scores;
  private List<Invitation> invitationsSent = new ArrayList<>();
  private List<Invitation> invitationsReceived = new ArrayList<>();
  // password not added because we dont display users password information
  
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

  // public String getUsername() {
  //   return username;
  // }

  // public void setUsername(String username) {
  //   this.username = username;
  // }

  // public UserStatus getStatus() {
  //   return status;
  // }

  // public void setStatus(UserStatus status) {
  //   this.status = status;
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
  //     this.scores = scores;
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
