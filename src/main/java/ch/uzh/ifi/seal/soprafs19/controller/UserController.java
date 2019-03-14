package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService service;

    UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/users")
    Iterable<User> all(@RequestHeader("Access-Token") String Token) {
        return service.getUsers(Token);
    }

    @PostMapping("/users")
    User createUser(@RequestBody User newUser) {
        return this.service.createUser(newUser);
    }
    @PostMapping("/login")
    User verifyLogin(@RequestBody User user) {
        return this.service.verifyLogin(user);
    }
    @CrossOrigin
    @PutMapping("users/{id}")
    boolean editProfile(@PathVariable Long id, @RequestBody User clUser, @RequestHeader("Access-Token") String Token) {
    	return this.service.editProfile(id, clUser.getBirthday(), clUser.getUsername(), clUser.getPassword(),Token);
    }
    @GetMapping("/users/{id}")
    User getInfo(@PathVariable long id,@RequestHeader("Access-Token") String Token) {
    	return this.service.getInfo(id,Token);
    }
    @PostMapping("/logout")
    void logoutUser(@RequestBody User loUser) {
    	this.service.logoutUser(loUser);
    }
}
