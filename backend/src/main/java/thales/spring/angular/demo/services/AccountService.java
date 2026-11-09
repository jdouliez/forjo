package thales.spring.angular.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import thales.spring.angular.demo.domain.Account;
import thales.spring.angular.demo.repositories.AccountRepository;

@Service
/**
 * 
 * Service managing the MVC model for account management
 *
 */
public class AccountService {
	
	@Autowired
	/**
	 * @see AccountRepository
	 */
	private AccountRepository repository;

	/**
	 * 
	 * @return the list of accounts
	 */
	public List<Account> findAll() {
		return repository.findAll();
	}
	
	/**
	 * Returns the account according to its identifier
	 * @param id : account ID
	 * @return the account if existing
	 */
	public Optional<Account> findById(Long id) {
		return repository.findById(id);
	}
	
	/**
	 * @param id : account ID
	 * @return true or false if account exists or not.
	 */
	public boolean isExist(Long id) {
		return findById(id).isPresent();
	}

	/**
	 * Delete the account according to its identifier
	 * @param id : account ID
	 */
	public void deleteById(Long id) {
		repository.deleteById(id);
	}

	/**
	 * Update the account
	 * @param account: the account to be updated
	 * @return the updated account
	 */
	public Account update(Account account) {
		return repository.save(account);
	}
	
	/**
	 * Update the list of accounts
	 * @param accounts: the accounts to be updated
	 * @return updated accounts
	 */
	public List<Account> updateAll(List<Account> accounts) {
		return repository.saveAll(accounts);
	}

	/**
	 *
	 * @param idClient : client ID
	 * @return the list of client accounts
	 */
	public List<Account> getAccountByClientId(String idClient) {
		return repository.getAccountByClientId(idClient);
	}


	/**
	 * @return the account according to this identifier
	 */
	public List<Account> getAccountByClientIdAndAccountId(String idClient, String idAccount) {
		List<Account> accounts = repository.getAccountByClientIdAndAccountId(idClient,idAccount);
		return accounts;
	}
}
