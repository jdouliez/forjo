package thales.spring.angular.demo.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import thales.spring.angular.demo.dto.UserDto;

/**
 * Validateur pour vérifier que deux champs sont identiques
 * En l'occurence, ici, ce sont deux mots de passe.
 * Annotation à utiliser @PasswordMatches
 *
 */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(final PasswordMatches constraintAnnotation) {
    }

    @Override
    /**
     * @param obj : de type UserDto
     * @return retourne true si les champs password et matchingPassword sont conformes
     */
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        final UserDto user = (UserDto) obj;
        return user.getPassword().equals(user.getMatchingPassword());
    }

}
