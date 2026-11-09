package thales.spring.angular.demo.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import thales.spring.angular.demo.domain.UserApp;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
/**
 * API Controller : /api/admin
 *
 */
public class AdminController extends GenericController {

    private static final Logger LOGGER = LoggerFactory.getLogger("AdminController");
   
    //Api rest point for admin
    @GetMapping(produces = "text/plain; charset=UTF-8")
    /**
     * GET /api/admin
     *
     * @param request : HTTP request including the "Token" header
     * @return Authorize the access to the admin page if "Token" header matches
     * with an user
     * @see User
     */
    public ResponseEntity<String> adminPanel(HttpServletRequest request) {
        UserApp user = getUserByToken(request);
       
        if (user != null) {
            LOGGER.info("Admin connexion successful for user \"" + user.getEmail() + "\"");
            return new ResponseEntity<>(null, HttpStatus.OK);
        } 
        else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }
}
