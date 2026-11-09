package thales.spring.angular.demo.controllers;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import thales.spring.angular.demo.domain.Message;
import thales.spring.angular.demo.domain.MessageStatus;
import thales.spring.angular.demo.domain.UserApp;
import thales.spring.angular.demo.services.MessageService;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
/**
 * API Controller : /api/messages
 */
public class MessageController extends GenericController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);
   
    @Autowired
    private MessageService service;

    @GetMapping
    /**
     * GET /api/messages Get messages list sent by users. Messages not read by
     * administrator. Only access to an administrator
     *
     * @param request : HTTP request including the Authorization header. This
     * header allows to identify the user by token.
     * @return - 200 OK and the unread messages list - 403 FORBIDDEN and a null
     * list
     * @see Message
     */
    public ResponseEntity<Collection<Message>> getMessages(HttpServletRequest request) {
        UserApp user = getUserByToken(request);
       
        if (user != null && user.admin()) {
            Collection<Message> messages = service.findAll();
          
            for (Message message : messages) {
                message.setRead(true);
                service.update(message);
            }

            return new ResponseEntity<>(messages, HttpStatus.OK);
        } 
        else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(value = "/{id}")
    /**
     * GET /api/messages/{id} Get the message refered by id No verification or
     * authentication control
     *
     * @param id : message id
     * @return the message identified by id - 200 OK and the message - 404 NOT
     * FOUND and null
     */
    public ResponseEntity<Message> getMessage(@PathVariable Long id) {
       
        Optional<Message> message = service.findById(id);
       
        if (message.isPresent()) {
            return new ResponseEntity<>(message.get(), HttpStatus.OK);
        } 
        else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/reply/{id}")
    /**
     * GET /api/messages/reply/{id} Add a generic reply to message
     * @param id : message id
     * @return the message identified by id - 200 OK and the message - 404 NOT
     * FOUND and null
     */
    public ResponseEntity<Message> replyToMessage(@PathVariable Long id) {
        LOGGER.debug("REPLY TO MESSAGE");
        
        Optional<Message> message = service.findById(id);
        
        if (message.isPresent()) {
            Message msg = message.get();
            msg.setStatus(MessageStatus.REPLIED);
            msg = service.update(msg);
            return new ResponseEntity<>(msg, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/{id}")
    /**
     * DELETE /api/messages/{id} No verification or authentication control
     *
     * @param id : message id
     * @return HTTP response : OK 200 if deletion OK
     */
    public ResponseEntity<Void> deleteMessages(@PathVariable Long id) {
        LOGGER.debug("DELETE MESSAGE");
        service.deleteById(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PutMapping
    /**
     * PUT /api/messages/ Body request to fill out. No verification or
     * authentication control
     *
     * @param message : message (body http request) to update.
     * @return HTTP response : 200 OK si update OK
     * @see Message
     */
    public ResponseEntity<?> updateMessages(@RequestBody Message message) {
        LOGGER.debug("UPDATE MESSAGE");
        return new ResponseEntity<>(service.update(message), HttpStatus.OK);
    }

    @PostMapping
    /**
     * POST /api/messages/ Body request to fill out.
     *
     * @param message : message (body http request) to update. the sender (from)
     * of the message is filled out automatically
     * @see Message
     * @param request : HTTP request including the Authorization header. This
     * header allows to identify the user by token.
     * @return HTTP response body (string) - CREATED 201 if OK - OK 200 if
     * message not created - Error HTTP
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public ResponseEntity<?> createMessage(@RequestBody Message message, HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
       
        LOGGER.debug("CREATE MESSAGE");
        
        UserApp user = getUserByToken(request);
       
        if (user != null) {
            message.setFrom(user.getClient().getIdClient());
            return new ResponseEntity<>(service.update(message), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }
}
