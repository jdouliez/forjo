package thales.spring.angular.demo.repositories;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import thales.spring.angular.demo.domain.Account;

/**
 * 
 * Implementation of the CUSTOM REPOSITORY ACCOUNT interface
 *
 */
public class AccountRepositoryCustomImpl implements AccountRepositoryCustom {
	private static final String SELECT_FROM_ACCOUNT_BY_ID_ACCOUNT_AND_ID_CLIENT =
			"SELECT ID, TYPE, BALANCE, RATE, DOCUMENT FROM ACCOUNT WHERE ID_CLIENT= %s AND ID= %s;";
	private static final String SELECT_FROM_ACCOUNT_BY_ID_CLIENT =
			"SELECT ID, TYPE, BALANCE, RATE, DOCUMENT FROM ACCOUNT WHERE ID_CLIENT= %s;";

	private static final Logger LOGGER = LoggerFactory.getLogger(AdvisorRepositoryCustomImpl.class);

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	/**
	 * @see AccountRepositoryCustom.getAccountByClientId(String idClient)
	 */
	public List<Account> getAccountByClientId(String idClient) {
		LOGGER.info(SELECT_FROM_ACCOUNT_BY_ID_CLIENT);

		List<Account> accounts = new ArrayList<Account>();

		try (Connection connexion = getDataSource().getConnection();
				Statement statement = connexion.createStatement();) {

			String query = String.format(SELECT_FROM_ACCOUNT_BY_ID_CLIENT, idClient);
			LOGGER.info(query);

			try (ResultSet resultat = statement.executeQuery(query);) {
				while (resultat.next()) {
					accounts.add(extractAccount(resultat));
				}
			}
		} catch (SQLException e) {
			LOGGER.error("Connexion error: " + e.getMessage());
		}

		LOGGER.info("Number of contracts {}", accounts.size());
		return accounts;
	}


	@Override
	public List<Account> getAccountByClientIdAndAccountId(String idClient, String idAccount) {
		List<Account> accounts = new ArrayList<>();

		try (Connection connexion = getDataSource().getConnection();
			Statement statement = connexion.createStatement();) {

			String query = String.format(SELECT_FROM_ACCOUNT_BY_ID_ACCOUNT_AND_ID_CLIENT, idClient, idAccount);
			LOGGER.info(query);

			try (ResultSet resultat = statement.executeQuery(query);) {
				while (resultat.next()) {
					accounts.add(extractAccount(resultat));
				}
			}
		} catch (SQLException e) {
			LOGGER.error("Connexion error : " + e.getMessage());
		}

		LOGGER.info("Number of accounts: {}", accounts.size());
		return accounts;
	}




	private Account extractAccount(ResultSet resultat) throws SQLException {
		Account account = new Account();
		account.setIdAccount(resultat.getLong("ID"));
		account.setType(resultat.getString("TYPE"));
		account.setBalance(Double.valueOf(resultat.getString("BALANCE")));
		account.setDocument(resultat.getString("DOCUMENT"));
		account.setRate(Double.valueOf(resultat.getString("RATE")));
		return account;
	}
}
