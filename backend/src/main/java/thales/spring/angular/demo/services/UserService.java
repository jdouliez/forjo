package thales.spring.angular.demo.services;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.Cookie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import thales.spring.angular.demo.domain.Account;
import thales.spring.angular.demo.domain.AccountEnum;
import thales.spring.angular.demo.domain.Advisor;
import thales.spring.angular.demo.domain.Client;
import thales.spring.angular.demo.domain.Role;
import thales.spring.angular.demo.domain.RoleEnum;
import thales.spring.angular.demo.domain.UserApp;
import thales.spring.angular.demo.domain.UserSerializer;
import thales.spring.angular.demo.dto.ResetPasswordDto;
import thales.spring.angular.demo.dto.UserDto;
import thales.spring.angular.demo.exception.UserAlreadyExistException;
import thales.spring.angular.demo.repositories.AccountRepository;
import thales.spring.angular.demo.repositories.AdvisorRepository;
import thales.spring.angular.demo.repositories.RoleRepository;
import thales.spring.angular.demo.repositories.UserRepository;
import thales.spring.angular.demo.util.CryptoConverter2;

@Service
/**
 * 
 * Service managing the MVC model for user management
 *
 */
public class UserService {

	@Autowired
	/**
	 * @see UserRepository
	 */
	private UserRepository repository;

	@Autowired
	/**
	 * @see RoleRepository
	 */
	private RoleRepository repoRole;

	@Autowired
	/**
	 * @see AccountRepository
	 */
	private AccountRepository repoAccount;

	@Autowired
	/**
	 * @see AdvisorRepository
	 */
	private AdvisorRepository repoAdvisor;

	@Autowired
	/**
	 * @see ClientService
	 */
	private ClientService serviceClient;

	/**
	 * 
	 * @return the list of all users
	 */
	public List<UserApp> findAll() {
		return repository.findAll();
	}

	/**
	 * Return the user according to his identifier
	 * @param id : user ID
	 * @return the user if existing
	 */
	public Optional<UserApp> findById(Long id) {
		return repository.findById(id);
	}

	/**
	 * Returns the user according to his email
	 * @return the user
	 */
	public UserApp findByEmail(String email) {
		return repository.findByEmail(email);
	}

	/**
	 * Returns the user according to his token
	 * @param token : user token
	 * @return the user
	 */
	public UserApp findByToken(String token) {
		return repository.findByToken(token);
	}

	/**
	 * Delete the user according to his identifier
	 * @param id : user ID
	 */
	public void deleteById(Long id) {
		repository.deleteById(id);
	}

	/**
	 * Update the user
	 * @param user: the user to update
	 * @return the updated user
	 * @throws Exception
	 */
	public UserApp update(UserApp user) throws Exception {
		serviceClient.update(user.getClient());
		addMinimumRoles(user);
		return repository.save(user);
	}

	/**
	 * Ajoute les rôles minimums à un utilisateur
	 * @param user : l'utilisateur à qui les rôles seront ajoutés
	 */
	private void addMinimumRoles(UserApp user) {
		addMinimumRoles(user, null);
	}

	/**
	 
	 * Ajoute les rôles de l'utilisateur DTO si rôles existants ou rôles minimums à l'utilisateur user
	 * @param user : l'utilisateur à qui les rôles seront ajoutés
	 * @param userDto : l'utilisateur DTO dont les rôles sont à transférer à l'utilisateur user
	 */
	private void addMinimumRoles(UserApp user, UserDto userDto) {
		Role findByRole = repoRole.findByRole(RoleEnum.USER);
		if (userDto != null) {
			Integer roleId = userDto.getRole();
			if (roleId != null) {
				Optional<Role> role = repoRole.findById(Long.valueOf(roleId));
				if (role.isPresent()) {
					findByRole = role.get();
				}
			}
		}
		if (user.getRoles() == null || user.getRoles().isEmpty()) {
			user.setRoles(Arrays.asList(findByRole));
		}
	}

	/**
	 * Ajoute un token à un utilisateur
	 * @param user l'utilisateur sans token
	 */
	private void addToken(UserApp user) {
		CryptoConverter2 converter = CryptoConverter2.getInstance();
		String tokenClear = user.getEmail() + "-ForJo";
		String token = converter.convertToDatabaseColumn(tokenClear);
		user.setToken(token);
	}

	/**
	 * Met à jour la liste des utilisateurs
	 * @param users : les utilisateurs à mettre à jour
	 * @return les utilisateurs mis à jour
	 */
	public List<UserApp> updateAll(Set<UserApp> users) {
		return repository.saveAll(users);
	}

	/**
	 * Enregistrement un nouvel utilisateur (USER) en fonction des informations renseignées contenues dans userDto (USERDTO)
	 * Lui seront affectés aléatoirement des comptes
	 * Lui sera affecté un compte client 
	 * Lui sera affecté alétoirement un conseiller 
	 * Lui seront affectés des rôles minimums si nécessaire
	 * @param userDto 
	 * @return l'utilisateur enregistré
	 * @throws Exception
	 */
	public UserApp registerNewUserAccount(final UserDto userDto) throws Exception {
		if (emailExists(userDto.getEmail())) {
			throw new UserAlreadyExistException("There is already an account with that email adress: " + userDto.getEmail());
		}

		Set<Account> accounts = createRandomAccounts();

		Advisor advisor = getRandomAdvisor();

		final UserApp user = new UserApp();
		user.setEmail(userDto.getEmail());
		user.setPassword(userDto.getPassword());
		user.setSecretAnswer(userDto.getSecretAnswer());
		addMinimumRoles(user, userDto);
		addToken(user);

		final Client client = new Client();
		//accounts.forEach(System.out::println);
		accounts.forEach(account -> account.setClient(client));

		client.setFirstName(userDto.getFirstName());
		client.setLastName(userDto.getLastName());
		client.setAddress(userDto.getAddress());
		client.setAccounts(accounts);
		client.setAdvisor(advisor);

		user.setClient(client);
		UserApp userRegistered = update(user);
		repoAccount.saveAll(accounts);
		
		return userRegistered;
	}

	/**
	 * Renitialise le mot de passe si la réponse à la question secrète est correcte
	 * @param userDto
	 * @return l'utilisateur enregistré
	 */
	public UserApp resetPassword(final ResetPasswordDto userDto) {
		if (!emailExists(userDto.getEmail())) {
			throw new UserAlreadyExistException("The account doesn't exist");
		}
		UserApp user = findByEmail(userDto.getEmail());
		if (user.getSecretAnswer().equals(userDto.getSecretAnswer()))
		{
			user.setPassword(userDto.getNewPassword());
			return repository.save(user);
		}
		throw new UserAlreadyExistException("The secret answer mismatch");
	}

	/**
	 * 
	 * @return une liste de comptes créés alétoirement
	 */
	private Set<Account> createRandomAccounts() {
		Set<Account> accounts = new HashSet<Account>();
		SecureRandom rand = new SecureRandom();
		String[] ribs = { "rib1", "rib2", "rib3", "rib4", "rib5", "rib6" };

		for (AccountEnum type : AccountEnum.values()) {
			StringBuffer sb = new StringBuffer(String.valueOf(rand.nextInt(9) + 1));
			for (int i = 0; i < 3; i++) {
				sb.append(rand.nextInt(10));
			}

			Account a = new Account();
			a.setIdAccount(Long.valueOf(sb.toString()));
			if (type.equals(AccountEnum.COURANT)) {
				a.setBalance((rand.nextInt(300000) - rand.nextInt(100000)) / 100);
			} else {
				a.setBalance(rand.nextInt(500000) / 100);
			}
			a.setDocument(ribs[rand.nextInt(ribs.length)] + ".pdf");
			a.setRate(type.getRate());
			a.setType(type.getName());
			accounts.add(a);
		}
		return accounts;
	}

	/**
	 * 
	 * @return un conseiller aléatoirement parmi ceux présents en base de données
	 */
	private Advisor getRandomAdvisor() {
		List<Long> advisorIds = repoAdvisor.findAllIds();
		Advisor advisor = null;
		advisor = repoAdvisor.findById(advisorIds.get(new SecureRandom().nextInt(advisorIds.size()))).orElse(null);
		return advisor;
	}

	/**
	 * @param email : email de l'utilisateur
	 * @return true ou false si un utilisateur possède ou non cet email
	 */
	private boolean emailExists(final String email) {
		return repository.findByEmail(email) != null;
	}
	
	/**
	 * 
	 * @param registered an already registered user 
	 * @return the cookie
	 * @throws JsonProcessingException
	 */
	public Cookie generateCookie(final UserApp registered) throws JsonProcessingException {
		//Serialisation user et set cookie user
		UserSerializer serializer = new UserSerializer(registered.getClient().getIdClient(), registered);
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		// Serialize user object into JSON format
		String userSerialize = mapper.writeValueAsString(serializer);
		// Encode json to base64 and store it into cookie <user>
		Cookie cookie = new Cookie("user", Base64.getEncoder().encodeToString(userSerialize.getBytes()));
		// Change path to root for id rest path
		cookie.setPath("/");
		return cookie;
	}
}