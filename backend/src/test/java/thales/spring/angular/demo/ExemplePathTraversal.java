package thales.spring.angular.demo;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exemple de remédiation au path traversam
 * POUR SUPPORT DE COURS
 */
public class ExemplePathTraversal {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExemplePathTraversal.class);

	public static boolean containsIllegals(String toExamine) {
	    Pattern pattern = Pattern.compile("[.=~#@*+%{}<>\\[\\]|\"\\_^]");
	    Matcher matcher = pattern.matcher(toExamine);
	    return matcher.find();
	}
	public static void main(String[] args) throws IOException {
		String toExamine = args[0];
		LOGGER.info("Is forbidden : " + ExemplePathTraversal.containsIllegals(toExamine));			
	}
}
