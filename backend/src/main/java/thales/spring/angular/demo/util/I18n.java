package thales.spring.angular.demo.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import thales.spring.angular.demo.remediation.util.MessageSourceBundle;

public class I18n {
    private static MessageSource messageSource = MessageSourceBundle.messageSource();
    
    public static String getMessage(String key) {
    	return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());		
    }

    public static String getMessage(String key, Object ... arguments) {
        return String.format(getMessage(key), arguments);
    }
}
