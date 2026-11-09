package thales.spring.angular.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import thales.spring.angular.demo.domain.Advisor;

@RepositoryRestResource
/**
 * 
 * Interface for the REPOSITORY ADVISOR extending:
 * - the JPA REPOSITORY ADVISOR interface 
 * - the CUSTOM REPOSITORY ADVISOR interface
 *
 */
public interface AdvisorRepository extends JpaRepository<Advisor, Long>, AdvisorRepositoryCustom {
	
	/**
	 * @see AdvisorRepositoryCustom.getAdvisorByClientId(String idAdvisor,String idClient)
	 */
	public List<Advisor> getAdvisorByClientId(String idAdvisor,String idClient);

	/**
	 * @see AdvisorRepositoryCustom.getAdvisorById(String idAdvisor)
	 */
	public List<Advisor> getAdvisorById(String idAdvisor);
	
	@Query("SELECT a.idAdvisor FROM Advisor a")
	/**
	 * implementation by @Query annotation
	 * @return the list of advisor IDs
	 * @see org.springframework.data.jpa.repository.Query
	 */
    public List<Long> findAllIds();
}
