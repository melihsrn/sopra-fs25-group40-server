package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    user.setName("Firstname Lastname");
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);

    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name", is(user.getName())))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())))
        .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
  }


  @Test
  public void givenUserId_whenGetUser_thenReturnUserProfile() throws Exception {
      // Given: The user ID does not exist
      User user = new User();
      user.setId(1L);
      user.setName("testname");
      user.setUsername("testusername");
      user.setStatus(UserStatus.ONLINE);
  
      // Mock service to throw NOT_FOUND exception when an invalid ID is requested
      given(userService.getUserById(Mockito.any())).willReturn(user);
  
      // When: A request is sent to retrieve the user
      MockHttpServletRequestBuilder getRequest = get("/users/" + user.getId())
          .contentType(MediaType.APPLICATION_JSON);
  
      // Then: Expect a 404 Not Found response
      mockMvc.perform(getRequest)
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id", is(user.getId().intValue())))
          .andExpect(jsonPath("$.name", is(user.getName())))
          .andExpect(jsonPath("$.username", is(user.getUsername())))
          .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }


  @Test
  public void givenUserId_whenGetUser_thenUserIdNotFound() throws Exception {
      // Given: The user ID does not exist
      Long userId = 1L;
  
      // Mock service to throw NOT_FOUND exception when an invalid ID is requested
      given(userService.getUserById(userId))
          .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID " + userId + " not found"));
  
      // When: A request is sent to retrieve the user
      MockHttpServletRequestBuilder getRequest = get("/users/" + userId)
          .contentType(MediaType.APPLICATION_JSON);
  
      // Then: Expect a 404 Not Found response
      mockMvc.perform(getRequest)
          .andExpect(status().isNotFound());
  }
  


  @Test
  public void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setName("Test User");
    user.setUsername("testUsername");
    user.setPassword("1234");
    user.setToken("1");
    user.setStatus(UserStatus.ONLINE);

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setPassword("1234");
    userPostDTO.setUsername("testUsername");

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.name", is(user.getName())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

  @Test
  public void createUser_duplicateInput_userNameAlreadyExists() throws Exception {
      // Given: A user already exists with the same username
      UserPostDTO userPostDTO = new UserPostDTO();
      userPostDTO.setPassword("1234");
      userPostDTO.setUsername("testUsername");
  
      // Mock service to throw an exception when trying to create a duplicate user
      given(userService.createUser(Mockito.any()))
          .willThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists"));
  
      // When: A request is sent to create a user with the same username
      MockHttpServletRequestBuilder postRequest = post("/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(asJsonString(userPostDTO));
  
      // Then: Expect a 409 Conflict response
      mockMvc.perform(postRequest)
          .andExpect(status().isConflict());
  }

//   @Test
//   public void givenUpdated_whenValidInput_returnUpdatedUserProfile() throws Exception {
//       // Given: An existing user
//       User user = new User();
//       user.setId(1L);
//       user.setName("Test User");
//       user.setUsername("testUsername");
//       user.setPassword("1234");
//       user.setToken("1");
//       user.setStatus(UserStatus.ONLINE);

//       // Updated user data
//       UserGetDTO updatedUser = new UserGetDTO();
//       updatedUser.setBirthday(new Date());
//       updatedUser.setUsername("testUsername123");

//       // Mock update behavior
//       doNothing().when(userService).updateUser(Mockito.any());

//       // When: A PUT request is sent
//       MockHttpServletRequestBuilder putRequest = put("/users/" + user.getId()) // Fixed URL format
//           .contentType(MediaType.APPLICATION_JSON)
//           .content(asJsonString(updatedUser));

//       // Then: Expect a 204 No Content response
//       mockMvc.perform(putRequest)
//           .andExpect(status().isNoContent());
//   }

//   @Test
//   public void givenUpdated_whenInvalidInput_returnUserIdNotFound() throws Exception {
//       // Given: A non-existing user ID
//       Long userId = 1L;
  
//       // Updated user data
//       UserGetDTO updatedUser = new UserGetDTO();
//       updatedUser.setBirthday(new Date());
//       updatedUser.setUsername("testUsername123");
  
//       // Mock update behavior to throw an exception
//       doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID " + userId + " not found"))
//           .when(userService).updateUser(Mockito.any());
  
//       // When: A PUT request is sent
//       MockHttpServletRequestBuilder putRequest = put("/users/" + userId) 
//           .contentType(MediaType.APPLICATION_JSON)
//           .content(asJsonString(updatedUser));
  
//       // Then: Expect a 404 Not Found response
//       mockMvc.perform(putRequest)
//           .andExpect(status().isNotFound()); 
//   }
  

  

  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}