package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Iterable<User> getUsers(String Token)
    {
    	if(this.userRepository.existsByToken(Token)) {
    		return this.userRepository.findAll();
    	}
    	throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Log in to gain acces to the requested information");
    }
    
    public User createUser(User newUser) {
    	if(!userRepository.existsByUsername(newUser.getUsername())) {
    		SimpleDateFormat sdl = new SimpleDateFormat("dd.MM.yyyy");
    		java.sql.Timestamp time = new Timestamp(System.currentTimeMillis());
	        String date = sdl.format(time);
    		newUser.setToken(UUID.randomUUID().toString());
	        newUser.setStatus(UserStatus.OFFLINE);
	        newUser.setCreationdate(date);
	        userRepository.save(newUser);
	        log.debug("Created Information for User: {}", newUser);
	        return newUser;
    	}
    	throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already used");
    }
    public User verifyLogin(User testuser) {
    	if(userRepository.existsByUsername(testuser.getUsername())){
	    	User dbuser = userRepository.findByUsername(testuser.getUsername());
	    	log.debug("login request sent with: {}",dbuser);
	    	if(dbuser.getPassword().contentEquals(testuser.getPassword())) {
	            dbuser.setToken(UUID.randomUUID().toString());
	            dbuser.setStatus(UserStatus.ONLINE);
	    		return dbuser;
	    	}
    	}
    	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found/ incorrect password");

    }
    public boolean editProfile(Long id, String birthday, String username,  String password, String Token) {
    	if(userRepository.existsById(id) && userRepository.findById(id).get().getToken().equals(Token)) {
	    	var optDbUser = userRepository.findById(id);
	    	if(optDbUser.isPresent()) {
	    		User dbUser = optDbUser.get();
		    	if(birthday!=null) {
		    		System.out.print("birthday is set");
		    		dbUser.setBirthday(birthday);
		    	}
		    	if(username!=null && !userRepository.existsByUsername(username)) {
		    		System.out.print("Username is set");
		    		dbUser.setUsername(username);
		    	}
		    	if(password!=null) {
		    		System.out.print("password is set");
		    		dbUser.setPassword(password);
		    	}
		    	return true;
	    	}
	    	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

    	}
    	throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can edit only your own Profile");

    }
    public User getInfo(Long id, String Token) {
    	if(userRepository.existsByToken(Token)) {
	    	var optDbUser = userRepository.findById(id);
	    	if(optDbUser.isPresent()) {
	    		User dbUser = optDbUser.get();
	    		return dbUser;
	    	} 
	    	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
	    	
    	}
    	throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Log in to gain access");
    	
	
    }
    public void logoutUser(User loUser) {
    	if (userRepository.existsByIdAndToken(loUser.getId(), loUser.getToken())) {
    		var optDbUser = userRepository.findByIdAndToken(loUser.getId(), loUser.getToken());
    		optDbUser.setStatus(UserStatus.OFFLINE);
    	}
    }
    
    
}
