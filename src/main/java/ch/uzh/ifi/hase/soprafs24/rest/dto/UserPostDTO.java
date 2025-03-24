package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.Date;

// let Post DTO to get name,username, birthday (optional since we marked it as nullable in User.java) 
// and password from user in order to be able to create a new user
public class UserPostDTO {

  private String name;

  private String username;

  private String password; 

  private Date birthday;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  // let app get password
  public String getPassword() {
    return password; 
  }

  public void setPassword(String password) { 
    this.password = password; 
  }

  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }
}
