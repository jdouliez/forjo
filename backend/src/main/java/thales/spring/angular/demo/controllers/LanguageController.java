package thales.spring.angular.demo.controllers;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

@RestController
@RequestMapping("/api/lang")
@CrossOrigin(origins = "*")
/**
 * API Controller : /api/lang
 */
public class LanguageController {
	private static final Logger LOGGER = LoggerFactory.getLogger(LanguageController.class);

	@Autowired
	private LocaleResolver localeResolver;

	@GetMapping
	public ResponseEntity<String> getLang(HttpServletRequest request) {
		Locale locale = localeResolver.resolveLocale(request);
		LOGGER.info("la langue dans la request est :" + locale.getLanguage());
		return new ResponseEntity<String>(locale.getLanguage(), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<Void> setLang(@RequestParam String lang, HttpServletRequest request,
			HttpServletResponse response) {		
		Locale locale = Locale.forLanguageTag(lang);
		LOGGER.info("la nouvelle langue est :" + locale.getLanguage());
		localeResolver.setLocale(request, response, locale);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
}
