package thales.spring.angular.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import thales.spring.angular.demo.domain.UserApp;

@RepositoryRestResource
/**
 * 
 * Interface for the REPOSITORY USER extending:
 * the JPA REPOSITORY USER interface 
 *
 */
public interface UserRepository extends JpaRepository<UserApp, Long> {
	/**
	 * 
	 * @param email : a user's email
	 * return the user according to the EMAIL field/column of the ENTITY/USER TABLE
	 */
	UserApp findByEmail(String email);
	/**
	 * 
	 * @param token : a user's token
	 * @return the user according to the TOKEN field/column of the ENTITY/TABLE USER
	 */
	UserApp findByToken(String token);

	@Query("SELECT t.idUser FROM UserApp t")
	/**
	 * implementation by @Query annotation
	 * @return the list of user IDs
	 * @see org.springframework.data.jpa.repository.Query
	 */
	public List<Long> findAllIds();
}
