package thales.spring.angular.demo.controllers;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import thales.spring.angular.demo.domain.UserApp;
import thales.spring.angular.demo.dto.ResetPasswordDto;
import thales.spring.angular.demo.dto.UserDto;
import thales.spring.angular.demo.exception.UserAlreadyExistException;
import thales.spring.angular.demo.security.jwt.JwtAuthenticationService;
import thales.spring.angular.demo.services.UserService;

@RestController
@CrossOrigin(origins = "*")
/**
 * API Controller : /api/registration
 */
public class RegistrationController {
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	/**
	 * @see UserService
	 */
	private UserService service;
	
    @Autowired
    private JwtAuthenticationService jwtAuthenticationService;
	
	@PostMapping(value = "/api/registration")
	/**
	 * POST /api/registration
	 * Register a new user 
	 * @param accountDto : the user to register. Type of the user : UserDto
	 * @param response : HTTP response
	 * token is saved in response HTTP.
	 * cookie is set.
	 * @return 
	 * - 200 OK {"status":"KO", "message":"Exsting email"}
	 * - 200 OK {"status":"KO", "message":new user adding failed."}
	 * - 200 OK {"status":"OK", "token":"{token}"}
	 * @throws JsonProcessingException
	 * @see UserDto
	 * @see User
	 */
	public ResponseEntity<String>  registerUserAccount(@RequestBody @Valid final UserDto accountDto, HttpServletResponse response) throws Exception {
        final UserApp registered;
        try {
            registered = service.registerNewUserAccount(accountDto);
        } catch (UserAlreadyExistException e) {
            return new ResponseEntity<>("{\"status\":\"KO\", \"message\":\"" + messageSource.getMessage("msg.allReadyExistMessage", null, LocaleContextHolder.getLocale()) + ".\"}", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("{\"status\":\"KO\", \"message\":\" unknown error \"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (registered == null) {
            return new ResponseEntity<>("{\"status\":\"KO\", \"message\":\"" + messageSource.getMessage("msg.msg.failedAddUser", null, LocaleContextHolder.getLocale())+ ".\"}", HttpStatus.OK);
        } else {
            Cookie cookie = service.generateCookie(registered);
            response.addCookie(cookie);
            String token = jwtAuthenticationService.generateToken(registered.getEmail());
            return new ResponseEntity<>("{\"status\":\"OK\", \"token\":\"" + token + "\"}", HttpStatus.OK);
        }
    }

	@PostMapping(value = "/api/resetPassword")
	/**
	 * POST /api/resetPassword
	 * Reset Password if answer match
	 * @param ResetPasswordDto : Data usefull to reset password
	 * @param response : HTTP response
	 * token is saved in response HTTP.
	 * cookie is set.
	 * @return
	 * - 200 OK {"status":"KO", "message":"Failed reset password"}
	 * - 200 OK {"status":"OK"}
	 * @throws JsonProcessingException
	 * @see UserDto
	 * @see User
	 */
	public ResponseEntity<String>  resetPasswordAccount(@RequestBody @Valid final ResetPasswordDto accountDto, HttpServletResponse response) throws JsonProcessingException {
		final UserApp registered;
		try {
			registered = service.resetPassword(accountDto);
		} catch (UserAlreadyExistException e) {
			return new ResponseEntity<>("{\"status\":\"KO\", \"message\":\"" + messageSource.getMessage("msg.failedResetPassowrd", null, LocaleContextHolder.getLocale()) + ".\"}", HttpStatus.OK);
		}

		if (registered == null) {
			return new ResponseEntity<>("{\"status\":\"KO\", \"message\":\"" + messageSource.getMessage("msg.msg.failedResetPassowrd", null, LocaleContextHolder.getLocale())+ ".\"}", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("{\"status\":\"OK\"}", HttpStatus.OK);
		}
	}
}