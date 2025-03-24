package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Deck;

import java.util.Date;
import java.util.List;

public class UserGetDTO {

  private Long id;
  private String name;
  private String token;
  private String username;
  private UserStatus status;
  private Date creationDate; // adding the additional variables in Get DTO so that we can fetch those information
  private Date birthday;
  private List<Deck> decks;

  // password not added because we dont display users password information
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }

  public List<Deck> getDecks() {
    return decks;
  }

  public void setDecks(List<Deck> decks) {
      this.decks = decks;
  }
}
