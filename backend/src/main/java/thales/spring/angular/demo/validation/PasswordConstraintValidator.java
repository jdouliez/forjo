package thales.spring.angular.demo.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validateur pour vérifier si un mot de passe est conforme à une politique de sécurité 
 * Annotation à utiliser @ValidPassword
 *
 */
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

	@Override
	public void initialize(final ValidPassword arg0) {
	}

	@Override
	/**
	 * retourne true si le mot de passe est valide
	 */
	public boolean isValid(String password, ConstraintValidatorContext context) {
		if (password != null && password.length() > 0) {
			return true;
		}
		context.disableDefaultConstraintViolation();
		return false;
	}
}
