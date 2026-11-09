package thales.spring.angular.demo.controllers;

import java.beans.ExceptionListener;
import java.beans.XMLEncoder;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import javax.servlet.http.HttpServletRequest;

import thales.spring.angular.demo.domain.Advisor;
import thales.spring.angular.demo.domain.Client;
import thales.spring.angular.demo.domain.UserApp;
import thales.spring.angular.demo.services.AdvisorService;
import thales.spring.angular.demo.services.ClientService;

@RestController
@RequestMapping("/api/advisor")
@CrossOrigin(origins = "*")
/**
 * API Controller : /api/advisor
 */
public class AdvisorController extends GenericController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdvisorController.class);

	@Autowired
	private AdvisorService service;

	@Autowired
	private ClientService serviceClient;
        
	@GetMapping
	/**
	 * GET /api/advisor
	 * No verification or authentication control
	 * @return the list of all advisors
	 */
	public ResponseEntity<Collection<Advisor>> getAdvisors() {
		LOGGER.debug("GET ADVISORS");
		return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
	}

	@GetMapping(value = "/{id}")
	/**
	 * GET /api/advisor/{id}
	 * Control by token user
	 * @param id : advisor id
	 * @param request : HTTP request including the "Authorization" header
	 * @return the advisor identified by id
	 * @see Advisor
	 */
	public ResponseEntity<Collection<Advisor>> getAdvisorById(@PathVariable String id, HttpServletRequest request) {
            LOGGER.debug("GET ADVISOR- BY ID");
            List<Advisor> advisors = service.getAdvisorById(id);
            if (advisors.isEmpty()) {
                    return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(advisors, HttpStatus.OK);
	}
	
	@GetMapping(value = "/user")
	/**
	 * GET /api/advisor/user
	 * Control by token user
	 * @param request : HTTP request including the "Authorization" header
	 * @return advisor id related to the user. A user has an advisor
	 */
	public ResponseEntity<Long> getClientAdvisorId(HttpServletRequest request) {
		LOGGER.debug("GET CLIENT ADVISOR ID");
       
		UserApp user = getUserByToken(request);
		
		if (user != null) {
			return new ResponseEntity<>(user.getClient().getAdvisor().getIdAdvisor(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(null, HttpStatus.OK);
		}
	}

	@GetMapping(value = "/{id}/clients")
	/**
	 * GET /api/advisor/{id}/clients
	 * No verification or authentication control
	 * @param id : advisor id
	 * @return : the clients of the advisor
	 * @see Client
	 */
	public ResponseEntity<Collection<Client>> getClients(@PathVariable Long id) {
		LOGGER.debug("GET CLIENTS");
		return new ResponseEntity<>(serviceClient.findByAdvisor(id), HttpStatus.OK);
	}

	@DeleteMapping(value = "/{id}")
	/**
	 * DELETE /api/advisor/{id}
	 * No verification or authentication control
	 * @param id : advisor id
	 * @return HTTP response: OK 200 if deletion is OK
	 */
	public ResponseEntity<Void> deleteAdvisorById(@PathVariable Long id) {
		LOGGER.debug("DELETE ADVISOR BY ID");
		service.deleteById(id);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@PutMapping
	/**
	 * PUT /api/advisor/
	 * Body request to fill out.
	 * No verification or authentication control
	 * @param advisor : advisor to update. advisor bypassed in body request .
	 * @return HTTP response : CREATED 201 si update OK
	 * @see Advisor
	 */
	public ResponseEntity<?> updateAdvisors(@RequestBody Advisor advisor) {
		LOGGER.debug("UPDATE ADVISOR");
		return new ResponseEntity<>(service.update(advisor), HttpStatus.CREATED);
	}

	@PostMapping(produces = { "application/json", "application/xml" }, consumes = { "application/json", "application/xml" })
	/**
	 * POST /api/advisor/
	 * Body request to fill out. Only "application/json" or "application/xml" mapping
	 * No verification or authentication control
	 * @param advisor : advisor to create. advisor bypassed in body request .
	 * @return HTTP response body (string). HTTP : CREATED 201 if OK
	 * @see Advisor
	 */
	public ResponseEntity<?> createAdvisor(@RequestBody Advisor advisor) throws JsonParseException, JsonMappingException, IOException {
		LOGGER.debug("CREATE ADVISOR");
		// APPELER JUSTE POUR AVOIR LA STRUCTURE XML D'UN CONSEILLER
		serializeToXML(advisor);
		return new ResponseEntity<>(service.update(advisor), HttpStatus.CREATED);
	}

	/**
	 * Create a XML file representing the advisor
	 * @param advisor : the advisor
	 * @throws IOException if file writing problem
	 * @see Advisor
	 */
	private static void serializeToXML(Advisor advisor) throws IOException {
		try (FileOutputStream fos = new FileOutputStream("/tmp/advisors.xml");
				XMLEncoder encoder = new XMLEncoder(fos)) {
			encoder.setExceptionListener(new ExceptionListener() {
				public void exceptionThrown(Exception e) {
					LOGGER.warn("Exception! :" + e.toString());
				}
			});
			encoder.writeObject(advisor);
		}
	}
}
