package thales.spring.angular.demo.dto;

import thales.spring.angular.demo.validation.ValidPassword;

/**
 * Class representing Password object
 * To be used on the client side (frontend)
 * Password is validated by a Validator
 * @see ValidPassword
 * 
 * Suggestions for improvement :
 * check function during password change if the new password is not equal to the old one.
 *
 */
public class PasswordDto {

    private String oldPassword;

    @ValidPassword
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
