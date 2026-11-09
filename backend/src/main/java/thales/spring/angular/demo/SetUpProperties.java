package thales.spring.angular.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter @Setter
@ConfigurationProperties(prefix = "setup")
/**
 * 
 * setup.* parameters from application.properties
 * 
 *
 */
public class SetUpProperties {
	/**
	 * Populate the database (test data) if true 
	 * @see application.properties
	 * @see setup.load-data-test=true|false
	 */
	private String loadDataTest;
	
	/**
	 * @return the boolean value from load-data-test parameter
	 */
	public Boolean isLoadDataTest() {
		return Boolean.valueOf(loadDataTest);
	}
	
	/**
	 * @see application.properties
	 * @see authent-digest-password=<password>
	 */
	private String authentDigestPassword;
}