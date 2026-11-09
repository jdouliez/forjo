package thales.spring.angular.demo.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import thales.spring.angular.demo.domain.UserApp;

@RestController
@RequestMapping("/api/log")
@CrossOrigin(origins = "*")
/**
 * API Controller: /api/log
 */
public class LogController extends GenericController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LogController.class);
    
    @GetMapping("/{name}")
    /**
     * GET /api/log/{name}
     * Allow to get the content of the file :  /var/www/logs/{name}.log
     * @param name name of the file to read (without extension)
     * @param request : HTTP request including the Authorization header. This header allows to identify the user by token.
     * @return file content (string) in response body. 200 OK if file recovered. Refused access if no authenticated or error HTTP
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public ResponseEntity<String> getLogs(@PathVariable String name, HttpServletRequest request) throws InstantiationException, IllegalAccessException, IOException {
       
        UserApp user = getUserByToken(request);
       
        if (user != null) {
            LOGGER.debug("GET LOG FILE \"" + name + "\"");

            try {
                byte[] encoded = Files.readAllBytes(Paths.get("/var/www/logs/" + name + ".log"));
                return new ResponseEntity<>(new String(encoded), HttpStatus.OK);
            }
            catch(NoSuchFileException e) {
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);    
            }
        } else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }
}
