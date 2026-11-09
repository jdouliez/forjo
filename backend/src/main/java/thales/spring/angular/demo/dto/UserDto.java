package thales.spring.angular.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thales.spring.angular.demo.validation.PasswordMatches;
import thales.spring.angular.demo.validation.ValidPassword;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@PasswordMatches
/**
 * Class representing the User object
 * To be used on the client side (frontend)
 * Incorporates the notion of password validation (in accordance with security policy)
 * @see ValidPassword
 * Incorporates password checking: the password and password confirmation match
 * @see @Pass/wordMatches
 */
public class UserDto {
    //@NotNull
    @JsonProperty("firstname")
    private String firstName;

    //@NotNull
    @JsonProperty("lastname")
    private String lastName;

    @JsonProperty("secretAnswer")
    private String secretAnswer;
    
    private String address;

    @ValidPassword
    private String password;

    //@NotNull
    @JsonProperty("matchingpassword")
    private String matchingPassword;

    //@NotNull
    @JsonProperty("email")
    private String email;

    private Integer role;

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("UserDto [firstName=").append(firstName).append(", lastName=").append(lastName).append(", address=").append(address).append(", password=").append(password).append(", matchingPassword=").append(matchingPassword).append(", email=").append(email)
                .append(", role=").append(role).append("]");
        return builder.toString();
    }
}
