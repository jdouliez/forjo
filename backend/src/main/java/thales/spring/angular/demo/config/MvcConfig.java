package thales.spring.angular.demo.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
@ComponentScan(basePackages = "thales.spring.angular.demo.config")
/**
 * 
 * a configuration class for the FORJO application
 *
 */
public class MvcConfig implements WebMvcConfigurer {
	
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(Locale.ENGLISH);
		return slr;
	}

	@Bean
	/**
	 * Intercept the locale parameter and add param 'lang' to change the application langage.
	 * @return the new locale changes
	 */
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}

	@Override
	/**
	 * add our specific locale changes to the registry
	 */
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}
	
	@Bean
	/**
	 * 
	 * @return the bundle message source for the application.
	 * @see i18n/messages.properties
	 */
	public ResourceBundleMessageSource messageSource() {
	 ResourceBundleMessageSource source = new ResourceBundleMessageSource();
	 
	 source.setBasename("i18n/messages");  // name of the resource bundle 
	 source.setDefaultEncoding("UTF-8");
	 source.setUseCodeAsDefaultMessage(true);
	 
	 return source;
	}
}
