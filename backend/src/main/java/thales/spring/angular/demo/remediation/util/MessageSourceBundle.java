package thales.spring.angular.demo.remediation.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;

public class MessageSourceBundle {

	@Bean
	/**
	 * 
	 * @return the bundle message source for the application.
	 * @see i18n/messages.properties
	 */
	public static ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasename("i18n/messages"); // name of the resource bundle
		source.setDefaultEncoding("UTF-8");
		source.setUseCodeAsDefaultMessage(true);
		return source;
	}
}
