package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes= Application.class)
public class UserControllerTest {


    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Test
    public void testGetUsers() throws Exception {
    	userRepository.deleteAll(userRepository.findAll());
        User testUser = new User();
        testUser.setUsername("Username");
        testUser.setPassword("Password");
        testUser = userService.createUser(testUser);
        this.mockMvc.perform(get("/users").header("Access-Token",testUser.getToken())).andExpect(status().isOk());
    }
    @Test
    public void testGetUsersWrongToken() throws Exception {
        this.mockMvc.perform(get("/users").header("Access-Token","not-a-token")).andExpect(status().is4xxClientError());
    }
    @Test
    public void testGetUserInfo() throws Exception {
    	userRepository.deleteAll(userRepository.findAll());
        User testUser = new User();
        testUser.setUsername("Username");
        testUser.setPassword("Password");
        testUser = userService.createUser(testUser);
        this.mockMvc.perform(get("/users/"+testUser.getId().toString()).header("Access-Token",testUser.getToken())).andExpect(status().isOk());
    }
    @Test
    public void testGetUserInfoOtherToken() throws Exception {
    	userRepository.deleteAll(userRepository.findAll());
        User testUser = new User();
        testUser.setUsername("Username");
        testUser.setPassword("Password");
        testUser = userService.createUser(testUser);
        User testUser2 = new User();
        testUser2.setUsername("Username2");
        testUser2.setPassword("Password2");
        testUser2 = userService.createUser(testUser2);
        this.mockMvc.perform(get("/users/"+testUser.getId().toString()).header("Access-Token",testUser2.getToken())).andExpect(status().isOk());
    }
    @Test
    public void testGetUserInfoRandomToken() throws Exception {
    	userRepository.deleteAll(userRepository.findAll());
        User testUser = new User();
        testUser.setUsername("Username");
        testUser.setPassword("Password");
        testUser = userService.createUser(testUser);
        this.mockMvc.perform(get("/users/"+testUser.getId().toString()).header("Access-Token","Random-Token")).andExpect(status().is4xxClientError());
    }

    
}

