package ch.uzh.ifi.hase.soprafs24.rest.dto;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter // Generates getters, setters automatically
public class UserLoginDTO {
    private String username;
    private String password;

    // // Getters and Setters
    // public String getUsername() { 
    //     return username; 
    // }

    // public void setUsername(String username) { 
    //     this.username = username; 
    // }

    // public String getPassword() { 
    //     return password; 
    // }

    // public void setPassword(String password) { 
    //     this.password = password; 
    // }
}
