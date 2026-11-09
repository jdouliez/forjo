package thales.spring.angular.demo;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.NonNull;

@Entity
/**
 * Exemple d'entité SPRING pour implémentation de chiffrement automatique d'un mot de passe en base de données
 * POUR SUPPORT DE COURS
 */
public class TestUser {
	@Id 
	@GeneratedValue
	private Long id;
	@NonNull
	private String login;
	@NonNull
	@Convert(converter = TestCryptoConverter.class)
	private String password;
}


