package thales.spring.angular.demo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import thales.spring.angular.demo.domain.Account;

@RepositoryRestResource
/**
 * 
 * Interface for REPOSITORY ACCOUNT extending:
 * - the JPA REPOSITORY ACCOUNT interface 
 * - the CUSTOM REPOSITORY ACCOUNT interface
 *
 */
public interface AccountRepository extends JpaRepository<Account, Long> , AccountRepositoryCustom {
	/**
	 */
	public List<Account> getAccountByClientId(String idClient);
	
	@Query("SELECT a.idAccount FROM Account a") 
	/**
	 * implementation by @Query annotation
	 * @return the list of account identifiers
	 * @see org.springframework.data.jpa.repository.Query
	 */
    public List<Long> findAllIds();


	/**
	 */
	public List<Account> getAccountByClientIdAndAccountId(String idClient, String idAccount);

	public Optional<Account> findByidAccountAndClient_IdClient(Long accountID, Long idClient);
}
