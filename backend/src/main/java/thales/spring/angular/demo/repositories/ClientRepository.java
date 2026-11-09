package thales.spring.angular.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import thales.spring.angular.demo.domain.Client;

@RepositoryRestResource
/**
 * 
 * Interface for CLIENT REPOSITORY extending:
 * - the JPA REPOSITORY CLIENT interface 
 *
 */
public interface ClientRepository extends JpaRepository<Client, Long> {

	@Query("SELECT c FROM Client c WHERE c.advisor.idAdvisor = :idAdvisor")
	/**
	 * implementation by @Query annotation
	 * @param idAdvisor: the advisor ID
	 * @return the list of clients assigned to the advisor
	 * @see org.springframework.data.jpa.repository.Query
	 */
	public List<Client> findByAdvisor(@Param("idAdvisor") Long idAdvisor);
	
	@Query("SELECT t.idClient FROM Client t")
	/**
	 * implementation by @Query annotation
	 * @return the list of clients IDs
	 * @see org.springframework.data.jpa.repository.Query
	 */
    public List<Long> findAllIds();
}
