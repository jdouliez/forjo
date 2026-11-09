package thales.spring.angular.demo.dto;

import thales.spring.angular.demo.validation.ValidPassword;

/**
 * Class representing Data needed from the user to reset the password
 * To be used on the client side (frontend)
 * Password is validated by a Validator
 * @see ValidPassword
 *
 */
public class ResetPasswordDto {

    private String answer;

    private String email;

    @ValidPassword
    private String newPassword;

    public String getSecretAnswer() { return answer; }

    public void setSecretAnswer( String answer ) { this.answer = answer; }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
