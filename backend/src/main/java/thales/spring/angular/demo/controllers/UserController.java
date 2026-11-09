package thales.spring.angular.demo.controllers;

import java.io.IOException;
import java.util.Base64;
import java.util.Collection;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import thales.spring.angular.demo.domain.Client;
import thales.spring.angular.demo.domain.UserApp;
import thales.spring.angular.demo.domain.UserSerializer;
import thales.spring.angular.demo.services.ClientService;
import thales.spring.angular.demo.services.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
/**
 * API Controller : /api/users
 */
public class UserController extends GenericController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private static final Logger LOGGER_HONEYPOT = LoggerFactory.getLogger("Honeypot");

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UserService userService;

    @Autowired
    private ClientService clientService;


    @GetMapping
    /**
     * GET /api/users
     *
     * @return users list
     * @see User
     */
    public ResponseEntity<Collection<UserApp>> getUsers() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    /**
     * GET /api/users/{id} No verification or authentication control.
     *
     * @param id : user id
     * @return user id 200 OK 404 NOT FOUND
     * @see User
     */
    public ResponseEntity<UserApp> getUserById(@PathVariable Long id) {
        LOGGER.debug("GET USER BY ID");
    
        Optional<UserApp> user = userService.findById(id);
    
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/me")
	/**
	 * GET api/users/me
	 * @return the current client
	 * @see Client
	 */
	public ResponseEntity<String> getCurrentUser(HttpServletRequest request) {
		LOGGER.debug("GET CURRENT USER (ME)");
		UserApp user = this.getUserByToken(request);
		return new ResponseEntity<>(user.clientInformations(), HttpStatus.OK);
	}

    @DeleteMapping(value = "/{id}")
    /**
     * DELETE /api/users/{id} No verification or authentication control. Delete
     * user identified by id
     *
     * @param id : user id
     * @return 200 OK or error HTTP
     * @see User
     */
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        LOGGER.debug("DELETE USER");
        userService.deleteById(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PutMapping
    /**
     * PUT /api/users No verification or authentication control. Update the user
     *
     * @param user : user to update
     * @return 200 OK or error HTTP
     * @see User
     */
    public ResponseEntity<Void> updateUser(@RequestBody UserApp user) throws Exception {
        LOGGER.debug("UPDATE USER");
        userService.update(user);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


    @GetMapping("/getinfos")
    /**
     * GET /api/users/getinfos Get informations from cookie
     *
     * @param cookieUser : "user" cookie
     * @param request : HTTP request including the Authorization header. This
     * header allows to identify the user by token.
     * @return 404 NOT FOUND : "Error cookie <user> not set" 404 NOT FOUND :
     * Error : user not associated to a client (a user is a client of the bank)
     * 200 OK : client informations 200 OK : no information
     * @throws IOException
     */
    public ResponseEntity<String> deserializeUser(@CookieValue(name = "user", defaultValue = "") String cookieUser, HttpServletRequest request) throws IOException {
        
        UserApp user = getUserByToken(request);
        
        if (user != null) {
            if (cookieUser.isEmpty()) {
                // if <user> cookie not set print error message
                return new ResponseEntity<>(messageSource.getMessage("err.cookieUserNotSet", null, LocaleContextHolder.getLocale()), HttpStatus.NOT_FOUND);
            } else {
                ObjectMapper mapper = new ObjectMapper();
                // Decode cookie value
                String jsonCookie = new String(Base64.getDecoder().decode(cookieUser));
                // Deserialize jsonCookie
                UserSerializer serializerObject = mapper.readValue(jsonCookie, UserSerializer.class);
                // search client object for set user client
                Optional<Client> client = clientService.findById(serializerObject.getIdClient());
               
                if (client.isPresent()) {
                    Client clientObject = client.get();
                    serializerObject.getUser().setClient(clientObject);
                    return new ResponseEntity<>(serializerObject.getUser().clientInformations(), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(messageSource.getMessage("err.UserWithoutClient", null, LocaleContextHolder.getLocale()), HttpStatus.NOT_FOUND);
                }
            }
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }

    }

    @GetMapping("/honeypot")
    /**
     * GET /api/users/honeypot trace in the honeypot log file IP address of the
     * potential hacker
     *
     * @param request : HTTP request
     * @return 200 OK
     */
    public ResponseEntity<Void> honeypot(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        LOGGER_HONEYPOT.info("POTENTIAL ATTACK : IP<" + ip + ">.");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
