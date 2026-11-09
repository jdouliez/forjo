package thales.spring.angular.demo.repositories;

import java.util.List;

import thales.spring.angular.demo.domain.Account;

/**
 * 
 * CUSTOM REPOSITORY ACCOUNT interface :
 * The methods will be implemented in the class AccountRepositoryCustomImpl
 */
public interface AccountRepositoryCustom {
	/**
	 *
	 * @param idClient : client ID
	 * @return the list of accounts held by the client
	 */
	public List<Account> getAccountByClientId(String idClient);

    List<Account> getAccountByClientIdAndAccountId(String idClient, String idAccount);
}
