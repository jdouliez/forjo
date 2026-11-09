package thales.spring.angular.demo.repositories;

import java.util.List;

import thales.spring.angular.demo.domain.Advisor;

/**
 * 
 * CUSTOM REPOSITORY ADVISOR interface:
 * The methods will be implemented in the AdvisorRepositoryCustomImpl
 */
public interface AdvisorRepositoryCustom {
	/**
	 * 
	 * @param idAdvisor : advisor ID
	 * @param idClient : client ID
	 * @return the advisor having as idAdvisor identifier and being the advisor of the client idClient
	 */
	public List<Advisor> getAdvisorByClientId(String idAdvisor,String idClient);
	/**
	 * 
	 * @param idAdvisor : advisor ID
	 * @return the advisor with idAdvisor ID
	 */
	public List<Advisor> getAdvisorById(String idAdvisor);

}
