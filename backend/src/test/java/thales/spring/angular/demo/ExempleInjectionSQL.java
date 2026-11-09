package thales.spring.angular.demo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import thales.spring.angular.demo.domain.Advisor;

/**
 * Exemple d'injection SQL
 * POUR SUPPORT DE COURS
 */
public class ExempleInjectionSQL {

	@Autowired
	DataSource dataSource;

	private static final String SELECT_FROM_ADVISOR_BY_ID =
			"SELECT ID,FIRSTNAME, LASTNAME, OFFICE FROM ADVISOR WHERE ID = %s ;";
	
	public static void main(String[] args) throws IOException {
		ExempleInjectionSQL sql = new ExempleInjectionSQL();
		List<Advisor> advisors = new ArrayList<Advisor>();

		try (Connection connexion = sql.dataSource.getConnection();
				Statement statement = connexion.createStatement();) {

			String query = String.format(SELECT_FROM_ADVISOR_BY_ID, args[0]);

			try (ResultSet resultat = statement.executeQuery(query);) {
				while (resultat.next()) {					
					advisors.add(ExempleInjectionSQL.extractAdvisor(resultat));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//advisors.forEach(System.out::println);
	}
	
	private static Advisor extractAdvisor(ResultSet resultat) throws SQLException {
		Advisor advisor = new Advisor();
		advisor.setIdAdvisor(resultat.getLong("ID"));
		advisor.setFirstName(resultat.getString("FIRSTNAME"));
		advisor.setLastName(resultat.getString("LASTNAME"));
		advisor.setOffice(resultat.getString("OFFICE"));
		return advisor;
	}
}
