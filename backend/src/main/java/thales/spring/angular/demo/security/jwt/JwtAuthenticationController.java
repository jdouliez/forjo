package thales.spring.angular.demo.security.jwt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import thales.spring.angular.demo.controllers.GenericController;
import thales.spring.angular.demo.domain.UserApp;
import thales.spring.angular.demo.services.UserService;

@RestController
@CrossOrigin
public class JwtAuthenticationController extends GenericController {

	/**
	 * THE LOGGER FOR TOP A10 TESTS
	 */
	private static final Logger LOGGER_SECURITY = LoggerFactory.getLogger("SecurityInfoLogger");
    private static final Logger LOGGER_HONEYPOT = LoggerFactory.getLogger("Honeypot");

    @Autowired
    private JwtAuthenticationService jwtAuthenticationService;
    
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/api/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
        jwtAuthenticationService.authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        setUserCookie(authenticationRequest.getUsername(), response);
        
        String token = jwtAuthenticationService.generateToken(authenticationRequest.getUsername());
      	LOGGER_SECURITY.info("LOGIN USER - " + authenticationRequest.getUsername() + " : SUCCESS");
        return ResponseEntity.ok(new JwtResponse(token));
    }

    private void setUserCookie(String email, HttpServletResponse response) throws JsonProcessingException {
        UserApp user = this.userService.findByEmail(email);        
        response.addCookie(userService.generateCookie(user));
    }

    @RequestMapping(value = "/api/verify", method = RequestMethod.GET)
    public ResponseEntity<?> verifyToken() throws Exception {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @GetMapping("/api/authenticate/honeypot")
    /**
     * GET /api/authenticate/honeypot trace in the honeypot log file IP address of the potential hacker
     *
     * @param request : HTTP request
     * @return 200 OK
     */
    public ResponseEntity<Void> honeypot(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        LOGGER_HONEYPOT.info("POTENTIAL ATTACK - IP <" + ip + "> :  Use of the hidden 'admin checkbox' during the login form post");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
