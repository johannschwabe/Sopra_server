package ch.uzh.ifi.seal.soprafs19.service;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes= Application.class)
public class UserServiceTest {


    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Test
    public void createUser() {
    	userRepository.deleteAll(userRepository.findAll());
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        User createdUser = userService.createUser(testUser);

        Assert.assertNotNull(createdUser.getToken());
        Assert.assertEquals(createdUser.getStatus(),UserStatus.OFFLINE);
        Assert.assertEquals(createdUser, userRepository.findByToken(createdUser.getToken()));
        
    }
    @Test (expected = ResponseStatusException.class)
    public void createUserSameUsername() {
    	userRepository.deleteAll(userRepository.findAll());
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        User createdUser = userService.createUser(testUser);
        User createdUser2 = userService.createUser(testUser);
    }
    @Test
    public void editUser() {
    	userRepository.deleteAll(userRepository.findAll());
    	User testUser = new User();
        testUser.setUsername("Name");
        testUser.setPassword("Password");
        User createdUser = userService.createUser(testUser);
        long id =createdUser.getId();
    	userService.editProfile(createdUser.getId(), "01.01.2000", "newName", "newPassword",createdUser.getToken());
    	Assert.assertEquals(userRepository.findById(id).getBirthday(), "01.01.2000");
    	Assert.assertEquals(userRepository.findById(id).getUsername(), "newName");
    	Assert.assertEquals(userRepository.findById(id).getPassword(), "newPassword");
    }
    @Test(expected = ResponseStatusException.class)
    public void editUserWrongToken() {
    	userRepository.deleteAll(userRepository.findAll());
    	User testUser = new User();
        testUser.setUsername("Name");
        testUser.setPassword("Password");
        User createdUser = userService.createUser(testUser);
        User testUser2 = new User();
        testUser2.setUsername("Name2");
        testUser2.setPassword("Password2");
        User createdUser2 = userService.createUser(testUser);
    	userService.editProfile(createdUser.getId(), "01.01.2000", "newName", "newPassword",createdUser2.getToken());
    }
    @Test
    public void logInUser() {
    	userRepository.deleteAll(userRepository.findAll());
    	User testUser = new User();
        testUser.setUsername("newUsername");
        testUser.setPassword("newPassword");
        User createdUser = userService.createUser(testUser);
    	Assert.assertEquals(userRepository.findById(createdUser.getId()).get().getStatus(), UserStatus.OFFLINE);
        userService.verifyLogin(testUser);
    	Assert.assertEquals(userRepository.findById(createdUser.getId()).get().getStatus(), UserStatus.ONLINE);

    }
    @Test(expected = ResponseStatusException.class)
    public void logInUserWrongPassword() {
    	userRepository.deleteAll(userRepository.findAll());
    	User testUser = new User();
        testUser.setUsername("newUsername");
        testUser.setPassword("newPassword");
        User createdUser = userService.createUser(testUser);
        User testUser2 = new User();
        testUser2.setUsername("newName");
        testUser2.setPassword("newPassword2");
        userService.verifyLogin(testUser2);
    }
    @Test
    public void logOutUser() {
    	userRepository.deleteAll(userRepository.findAll());
    	User testUser = new User();
        testUser.setUsername("newName");
        testUser.setPassword("newPassword");
        User createdUser = userService.createUser(testUser);

    	userService.verifyLogin(testUser);
    	userService.logoutUser(createdUser);
    	Assert.assertEquals(createdUser.getStatus(), UserStatus.OFFLINE);
    }
    @Test
    public void getUsers() {
    	userRepository.deleteAll(userRepository.findAll());
    	User testUser = new User();
        testUser.setUsername("newName");
        testUser.setPassword("newPassword");
        User testUser2 = new User();
        testUser2.setUsername("newName2");
        testUser2.setPassword("newPassword2");
        User createdUser = userService.createUser(testUser);
        User createdUser2 = userService.createUser(testUser2);
        var iterator= userService.getUsers(createdUser.getToken()).iterator();
        Assert.assertEquals(iterator.next(), createdUser);
        Assert.assertEquals(iterator.next(), createdUser2);
        Assert.assertFalse(iterator.hasNext());
    }
    @Test(expected = ResponseStatusException.class)
    public void getUsersWrongToken() {
    	userRepository.deleteAll(userRepository.findAll());
    	User testUser = new User();
        testUser.setUsername("newName");
        testUser.setPassword("newPassword");
        User testUser2 = new User();
        testUser2.setUsername("newName2");
        testUser2.setPassword("newPassword2");
        User createdUser = userService.createUser(testUser);
        User createdUser2 = userService.createUser(testUser2);
        var iterator= userService.getUsers("Not a Token").iterator();
        Assert.assertEquals(iterator.next(), createdUser);
        Assert.assertEquals(iterator.next(), createdUser2);
        Assert.assertFalse(iterator.hasNext());
    }
    @Test
    public void getInfo() {
    	userRepository.deleteAll(userRepository.findAll());
    	User testUser = new User();
        testUser.setUsername("newName");
        testUser.setPassword("newPassword");
        User createdUser = userService.createUser(testUser);
    	Assert.assertEquals(userService.getInfo(createdUser.getId(), createdUser.getToken()),createdUser);
    }
    @Test(expected = ResponseStatusException.class)
    public void getInfoWrongToken() {
    	userRepository.deleteAll(userRepository.findAll());
    	User testUser = new User();
        testUser.setUsername("newName");
        testUser.setPassword("newPassword");
        User createdUser = userService.createUser(testUser);
    	Assert.assertEquals(userService.getInfo(createdUser.getId(), "Not a token"),createdUser);
    }
    @Test
    public void testInvalidToken() throws Exception {
        this.mockMvc.perform(get("/users").header("Access-Token","invalid-token")).andExpect(status().is4xxClientError());
    }

}

