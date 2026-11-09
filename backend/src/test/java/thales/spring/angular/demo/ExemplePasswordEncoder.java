package thales.spring.angular.demo;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

/**
 * Exemple de chiffrement de mot de passe
 * POUR SUPPORT DE COURS
 */
public class ExemplePasswordEncoder {
	
	public String bCryptPasswordEncoder(String plaintextPassword) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(plaintextPassword);
		return hashedPassword;
	}
	
	public String pbkdf2PasswordEncoder(String plaintextPassword) {
		Pbkdf2PasswordEncoder passwordEncoder = new Pbkdf2PasswordEncoder();
		String hashedPassword = passwordEncoder.encode(plaintextPassword);
		return hashedPassword;
	}
		
	public String scryptPasswordEncoder(String plaintextPassword) {
		SCryptPasswordEncoder passwordEncoder = new SCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(plaintextPassword);
		return hashedPassword;
	}
	
	public String messageDigestPasswordEncoder(String plaintextPassword) {
		return DigestUtils.md5Hex(plaintextPassword);
	}
}
