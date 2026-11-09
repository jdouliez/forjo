/**
 * 
 * DemoApplication is the starter class for FORJO application
 */

package thales.spring.angular.demo;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import thales.spring.angular.demo.domain.Advisor;
import thales.spring.angular.demo.domain.Role;
import thales.spring.angular.demo.domain.RoleEnum;
import thales.spring.angular.demo.dto.UserDto;
import thales.spring.angular.demo.repositories.AccountRepository;
import thales.spring.angular.demo.repositories.AdvisorRepository;
import thales.spring.angular.demo.repositories.ClientRepository;
import thales.spring.angular.demo.repositories.RoleRepository;
import thales.spring.angular.demo.repositories.UserRepository;
import thales.spring.angular.demo.services.UserService;

@SpringBootApplication
public class DemoApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(DemoApplication.class);
	
	@Autowired
	/**
	 * make the DAO tier for the CLIENT datatable
	 * @see ClientRepository
	 */
	ClientRepository repoClient;

	@Autowired
	/**
	 * make the DAO tier for the ACCOUNT datatable
	 * @see AccountRepository
	 */
	AccountRepository repoAccount;

	@Autowired
	/**
	 * make the DAO tier for the ADVISOR datatable
	 * @see AdvisorRepository
	 */
	AdvisorRepository repoAdvisor;

	@Autowired
	/**
	 * make the DAO tier for the USER datatable
	 * @see UserRepository
	 */
	UserRepository repoUser;

	@Autowired
	/**
	 * réalise la couche DAO pour la table ROLE
	 * make the DAO tier for the ACCOUNT datatable
	 * @see RoleRepository
	 */
	RoleRepository repoRole;

	@Autowired
	/**
	 * Service : manage methods in relation to USER tier
	 * @see UserService
	 */
	private UserService serviceUser;

	@Autowired
	/**
	 * @see SetUpProperties
	 */
	SetUpProperties setup;
	
	/**
	 * Starting point to FORJO application
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	/**
	 * Init the application.
	 * For example :  populate database in :
	 * - USER and ADMIN roles
	 * @see RoleEnum
	 * 
	 * @return ApplicationRunner
	 */
	ApplicationRunner init() {
		return args -> {				
			createRoleIfNotFound(RoleEnum.USER);
			createRoleIfNotFound(RoleEnum.ADMIN);

			initAdvisors();

			if (setup.isLoadDataTest()) {
				initUsersTest();
			}
		};
	}
	
	@Transactional
	/**
	 * Create the role if not exists in DataBase
	 * @param typeRole : the role (Enum type) to create
	 * @return the created object ROLE
	 * @see RoleEnum
	 * @see Role
	 */
	public Role createRoleIfNotFound(final RoleEnum typeRole) {
		Role role = repoRole.findByRole(typeRole);
	
		if (role == null) {
			role = new Role(typeRole);
		}
	
		role = repoRole.save(role);
		return role;
	}
	
	/**
	 * Populate ADVISOR datatable from a JSON file
	 */
	private void initAdvisors() throws IOException, JsonParseException, JsonMappingException {
		// read json and write to db
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<Advisor>> typeReference = new TypeReference<List<Advisor>>() {
		};
		InputStream inputStream = TypeReference.class.getResourceAsStream("/json/advisors.json");

		List<Advisor> advisors = mapper.readValue(inputStream, typeReference);
		repoAdvisor.saveAll(advisors);
	}

	/**
	 * Populate USER datatable from a JSON file
	 */
	private void initUsersTest() throws IOException, JsonParseException, JsonMappingException  {
		
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<UserDto>> typeReference = new TypeReference<List<UserDto>>() {};
		InputStream inputStream = TypeReference.class.getResourceAsStream("/json/usersdto.json");

		List<UserDto> users = mapper.readValue(inputStream, typeReference);

		for (UserDto userDto : users) {
			try {
				serviceUser.registerNewUserAccount(userDto);
			} 
			catch (Exception e) {
				LOGGER.error("Error during new user registration.");
			}
		}

		LOGGER.debug(" -------------- INIT USERS IN DB --------------");
		LOGGER.info("NOMBRE DE CONSEILLERS  =  " + repoAdvisor.findAllIds().size());
		LOGGER.info("NOMBRE DE COMPTES      =  " + repoAccount.findAllIds().size());
		LOGGER.info("NOMBRE DE USERS        =  " + repoUser.findAllIds().size());
		LOGGER.info("NOMBRE DE CLIENTS      =  " + repoClient.findAllIds().size());
		LOGGER.debug(" -------------- END INIT USERS IN DB --------------");
	}
}
