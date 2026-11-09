package thales.spring.angular.demo.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import thales.spring.angular.demo.domain.Message;

@RepositoryRestResource
/**
 * 
 * Interface for REPOSITORY MESSAGE extending:
 * - the JPA REPOSITORY MESSAGE interface 
 *
 */
public interface MessageRepository extends JpaRepository<Message, Long> {
	/**
	 * 
	 * @param read : one of the possible values of the READ field/column : true or false
	 * @return the list of messages according to the READ field/column of the ENTITY/ MESSAGE TABLE
	 */
    List<Message> findByRead(Boolean read);
}
