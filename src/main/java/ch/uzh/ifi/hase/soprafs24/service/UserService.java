package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.UUID;
import java.util.Date;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  // @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE); // set status ONLINE since the user will immediately start using app after registering

    // ensure that we encode password and create a creation date while adding a new user
    newUser.setCreationDate(new Date());
    if (newUser.getPassword() != null) {
      newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
    }
    checkIfUserExists(newUser);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
      String providedUsername = userToBeCreated.getUsername();

      if (providedUsername == null || providedUsername.trim().isEmpty()) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username must not be empty.");
      }

      User userByUsername = userRepository.findByUsername(providedUsername);
      if (userByUsername != null) {
          throw new ResponseStatusException(HttpStatus.CONFLICT, "Username '" + providedUsername + "' is already taken. Please choose a different one.");
      }
  }

  private User findByToken(String token) {
    return userRepository.findByToken(token).orElse(null);
  }

  public void isStatusAvailable(UserStatus status) {
    if (status.equals(UserStatus.OFFLINE) || status.equals(UserStatus.PLAYING)){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User not available!");
    }
  }

  public Long getUserIdFromToken(String token) {
    User user = findByToken(token);
    return user != null ? user.getId() : null;
  }

  public void updateUser(Long userId, User updatedUser) {
      Optional<User> existingUserOptional = userRepository.findById(userId);

      if (existingUserOptional.isPresent()) {
          User existingUser = existingUserOptional.get();

          // Update fields
          if (updatedUser.getUsername() != null) {
              existingUser.setUsername(updatedUser.getUsername());
          }
          if (updatedUser.getBirthday() != null) {
              existingUser.setBirthday(updatedUser.getBirthday());
          }
          // if (updatedUser.getFcmToken() != null) {
          //   existingUser.setFcmToken(updatedUser.getFcmToken());
          // }

          userRepository.save(existingUser);
      } else {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");
      }
  }

  // implement function to get user by their ID
  public User getUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID " + id + " not found"));
  } 

  // login function
  public User loginUser(String username, String password) {
    User user = userRepository.findByUsername(username);
      if (username == null || username.trim().isEmpty()) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username must not be empty.");
      }
      if (password == null || password.trim().isEmpty()) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must not be empty.");
      }
      if (user == null) {
          throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No user registered with username '" + username + "'. Please check your credentials or register.");
      }

      if (!passwordEncoder.matches(password, user.getPassword())) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials.");
    }

    user.setToken(UUID.randomUUID().toString()); // Generate new session token
    user.setStatus(UserStatus.ONLINE); // set status to online
    userRepository.save(user); // save
    userRepository.flush();  // since data is only persisted in the database once, call flush

    return user;
  }

  // logout function
  public User logoutUser(Long id) {
    User user = getUserById(id);
    user.setToken(null); // reset session information
    user.setStatus(UserStatus.OFFLINE); // set status offline
    userRepository.save(user); // save
    userRepository.flush(); // flush
    return user;
  }



}
