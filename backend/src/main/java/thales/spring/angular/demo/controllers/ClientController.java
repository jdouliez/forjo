package thales.spring.angular.demo.controllers;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import thales.spring.angular.demo.domain.Account;
import thales.spring.angular.demo.domain.Client;
import thales.spring.angular.demo.services.AccountService;
import thales.spring.angular.demo.services.ClientService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "*")
/**
 *  API Controller : /clients
 */
public class ClientController extends GenericController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientController.class);

	@Autowired
	private ClientService service;

	@Autowired
	private AccountService serviceAccount;

	@GetMapping
	/**
	 * GET /clients
	 * No verification or authentication control
	 * @return the list of all clients
	 * @see Client
	 */
	public ResponseEntity<Collection<Client>> getClients() {
		LOGGER.debug("GET CLIENTS");
		return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
	}

	@GetMapping(value = "/{id}")
	/**
	 * GET /clients/{id}
	 * @param id : client id
	 * @return the client identified by id
	 * @see Client
	 */
	public ResponseEntity<Client> getClient(@PathVariable Long id) {
		LOGGER.debug("GET CLIENT BY ID");
		Optional<Client> client = service.findById(id);
		if (client.isPresent()) {
			return new ResponseEntity<>(client.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping(value = "/{id}/accounts")
	/**
	 * GET /clients/{id}/accounts
	 * No verification or authentication control
	 * @param id : lient id
	 * @return client accounts refered by id 
	 * @see Account
	 * @see Client
	 */
	public ResponseEntity<Collection<Account>> getAccountByClientId(@PathVariable Long id) {
		LOGGER.debug("GET ACCOUNT BY CLIENT ID");
		return new ResponseEntity<>(serviceAccount.getAccountByClientId(Long.toString(id)), HttpStatus.OK);
	}

	@DeleteMapping(value = "/{id}")
	/**
	 * DELETE /clients/{id}
	 * Delete a client
	 * No verification or authentication control
	 * @param id : client id
	 * @return 200 OK if client id deleted or error HTTP
	 */
	public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
		LOGGER.debug("DELETE CLIENT");
		service.deleteById(id);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@PutMapping
	/**
	 * PUT /api/clients
	 * Update the client
	 * No verification or authentication control
	 * @param client : client to update
	 * @return 200 OK if client updated or error HTTP
	 */
	@ResponseBody
	public ResponseEntity<?> updateClient(@RequestBody Client client) throws Exception {
		LOGGER.debug("UPDATE CLIENT");
		
		try {
			Client updated = service.update(client);
			return new ResponseEntity<>(updated, HttpStatus.OK);
		}
		catch(Exception e ){
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping
	/**
	 * POST /clients/
	 * Add a client
	 * No verification or authentication control
	 * @param client : client to create
	 * @return 201 CREATE if client created or error HTTP
	 */
	public ResponseEntity<?> createClient(@RequestBody Client client) throws Exception {
		LOGGER.debug("CREATE CLIENT");
		return new ResponseEntity<>(service.update(client), HttpStatus.CREATED);
	}
}
