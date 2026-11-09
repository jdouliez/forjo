package thales.spring.angular.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exemple d'injection de commande
 * POUR SUPPORT DE COURS
 */

public class ExempleInjectionCommande {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExempleInjectionCommande.class);

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			LOGGER.warn("No arguments");
			System.exit(1);
		}

		String cmd = "ls -l " + args[0];
		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(new String[] { "sh", "-c", cmd });

		InputStream is = proc.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line;
		while ((line = br.readLine()) != null) {
			LOGGER.info(line);
		}
	}
}
