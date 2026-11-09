package thales.spring.angular.demo.services;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import thales.spring.angular.demo.domain.Advisor;
import thales.spring.angular.demo.domain.Client;
import thales.spring.angular.demo.repositories.AdvisorRepository;

@Service
/**
 *
 * Service managing the VMC model for advisor management
 *
 */
public class AdvisorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdvisorService.class);

	@Autowired
	/**
	 * @see AdvisorRepository
	 */
	private AdvisorRepository repository;

	@Autowired
	/**
	 * @see ClientService
	 */
	private ClientService serviceClient;

	/**
	 *
	 * @return the list of all advisors
	 */
	public List<Advisor> findAll() {
		return repository.findAll();
	}

	/**
	 * Returns the advisor according to his ID
	 * @param id : advisor ID
	 * @return the advisor if existing
	 */
	public Optional<Advisor> findById(Long id) {
		return repository.findById(id);
	}

	/**
	 * Delete the advisor according to his identifier
	 * Update the clients assigned to this advisor
	 * @param id : advisor ID
	 */
	public void deleteById(Long id) {
		LOGGER.debug("DELETE BY ID");
		
		Optional<Advisor> advisor = findById(id);
		if (advisor.isPresent()) {
			Set<Client> clients = advisor.get().getClients();
			Iterator<Client> iterator = clients.iterator();
		
			while (iterator.hasNext()) {
				Client client = iterator.next();
				client.setAdvisor(null);
			}

			serviceClient.updateAll(clients);
			repository.deleteById(id);
		}
	}

	/**
	 * Update the advisor
	 * @param advisor: the advisor to be updated
	 * @return the updated advisor
	 */
	public Advisor update(Advisor advisor) {
		return repository.save(advisor);
	}

	/**
	 * Update advisors
	 * @param advisors: the advisors to be updated
	 * @return updated advisors
	 */
	public List<Advisor> updateAll(List<Advisor> advisors) {
		return repository.saveAll(advisors);
	}

	/**
	 * @param idAdvisor : advisor ID
	 * @param idClient : client ID
	 * @return the list of advisors assigned to a client.
	 */
	public List<Advisor> getAdvisorByClientId(String idAdvisor, String idClient) {
		return repository.getAdvisorByClientId(idAdvisor, idClient);
	}

	/**
	 * @param idAdvisor : advisor ID
	 * @return the list of advisors according to this idendifier
	 */
	public List<Advisor> getAdvisorById(String idAdvisor) {
		List<Advisor> advisors= new ArrayList<Advisor>();
		long advisorId = 0L;
		try {
			advisorId = Long.parseLong(idAdvisor);
		} catch (NumberFormatException e) {
			return advisors;
		}

		
		Optional<Advisor> oAdvisor = repository.findById(advisorId);
		if (oAdvisor.isPresent()) {
			advisors.add(oAdvisor.get());
		}

		for (Advisor advisor : advisors) {
			LOGGER.info("Recherche des clients pour l'advisor : {}", advisor.getIdAdvisor());
			List<Client> clients = serviceClient.findByAdvisor(advisor.getIdAdvisor());
			advisor.setClients(clients.stream().collect(Collectors.toSet()));
		}
		
		return advisors;
	}
}