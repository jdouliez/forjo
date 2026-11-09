package thales.spring.angular.demo.services;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import thales.spring.angular.demo.domain.Client;
import thales.spring.angular.demo.repositories.ClientRepository;

@Service
/**
 * 
 * Service managing the MVC model for client management
 *
 */
public class ClientService {

	private static final Logger LOGGER = LoggerFactory.getLogger("ClientService");

	@Autowired
	/**
	 * @see ClientRepository
	 */
	private ClientRepository repository;

	/**
	 * 
	 * @return the list of all clients
	 */
	public List<Client> findAll() {
		return repository.findAll();
	}

	/**
	 * Returnsthe client according to his identifier
	 * @param id : client ID
	 * @return the client if existing
	 */
	public Optional<Client> findById(Long id) {
		return repository.findById(id);
	}
	
	/**
	 * @param id : client ID
	 * @return true or false if existing client or not.
	 */
	public boolean isExist(Long id) {
		return findById(id).isPresent();
	}

	/**
	 * Delete the client according to his identifier
	 * @param id : client ID
	 */
	public void deleteById(Long id) {
		repository.deleteById(id);		
	}
	
	/**
	 * Update the client
	 * @param account: the client to update
	 * @return the updated client @return
	 * @throws Exception
	 */
	public Client update(Client client) throws Exception {

		if (client == null) {
			return null;
		} 		
		else if (client.getIdClient() == null) {
			return repository.save(client);		
		}
		else if (isExist(client.getIdClient())) {
			Client clientFromDb = repository.findById(client.getIdClient()).get();

			if (client.getFirstName() != null && !client.getFirstName().isEmpty()) {
				clientFromDb.setFirstName(client.getFirstName());
			}

			if (client.getLastName() != null && !client.getLastName().isEmpty()) {
				clientFromDb.setLastName(client.getLastName());
			}

			if (client.getAddress() != null && !client.getAddress().isEmpty()) {
				clientFromDb.setAddress(client.getAddress());
			}

			if (client.getPictureUrl() != null && !client.getPictureUrl().isEmpty()) {
				clientFromDb.setPictureUrl(client.getPictureUrl());

				try {
					LOGGER.info("Getting profile picture at " + client.getPictureUrl());
					String base64 = getByteArrayFromImageURL(client.getPictureUrl());	

					if (base64 != null) {
						LOGGER.info("Image encoded in base64 size is : " + base64.length());													
						clientFromDb.setPictureBase64Data(base64);
						return repository.save(clientFromDb);	
					}
					else {
						return null;
					}
				}
				catch (DataIntegrityViolationException e) {
					if (e.getStackTrace().toString().contains("Value too long for column \"PICTURE_BASE64")) {
						throw new Exception("Image too big");
					}
				}	
				catch (Exception e) {
					LOGGER.error("Erreur lors de la mise à jour du client : " + e.getMessage());	
					throw new Exception(e.getMessage());						
				}				
			}

			return repository.save(clientFromDb);		
		}
		else {
			return null;		
		}		
	}

	/**
	 * Update the list of clients
	 * @param accounts: clients to be updated
	 * @return updated clients
	 */
	public List<Client> updateAll(Set<Client> clients) {
		return repository.saveAll(clients);		
	}
	
	/**
	 * @param idAdvisor : advisor ID
	 * @return the list of clients assigned to this advisor
	 */
	public List<Client> findByAdvisor(Long idAdvisor) {
		return repository.findByAdvisor(idAdvisor);
	}

	private String getByteArrayFromImageURL(String url) throws Exception {

		try {
			URL imageUrl = new URL(url);
			URLConnection ucon = imageUrl.openConnection();
			InputStream is = ucon.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int read = 0;
			while ((read = is.read(buffer, 0, buffer.length)) != -1) {
				baos.write(buffer, 0, read);
			}
			baos.flush();
			return Base64.getEncoder().encodeToString(baos.toByteArray());
		} 
		catch (Exception e) {
			LOGGER.error("Error during base64 encoding url ressource : " + e.getMessage());
			throw e;
		}		
	}
}
