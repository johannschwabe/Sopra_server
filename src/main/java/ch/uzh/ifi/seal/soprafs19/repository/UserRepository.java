package ch.uzh.ifi.seal.soprafs19.repository;

import ch.uzh.ifi.seal.soprafs19.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("userRepository")
public interface UserRepository extends CrudRepository<User, Long> {
	User findByUsername(String username);
	User findByUsernameAndPassword(String username, String password);
	User findByToken(String token);
	User findById(long id);
	User findByIdAndToken(long id, String token);
	boolean existsByIdAndToken(long id, String token);
	boolean existsByToken(String Token);
	boolean existsByUsername(String username);
}
