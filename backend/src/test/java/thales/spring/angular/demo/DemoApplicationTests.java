/**
 *
 {
  "idUser" : 1,
  "email" : "testadmin@email.com",
  "token" : "adce5cc2722affa7f06c7f83d783c56c"
}
{
  "idUser" : 2,
  "email" : "testuser@email.com",
  "token" : "3d1afda6786138923cfd1a8990d0734f"
}
 */
package thales.spring.angular.demo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import thales.spring.angular.demo.domain.Account;
import thales.spring.angular.demo.domain.Advisor;
import thales.spring.angular.demo.domain.Client;
import thales.spring.angular.demo.domain.Message;
import thales.spring.angular.demo.domain.MessageStatus;
import thales.spring.angular.demo.domain.UserApp;
import thales.spring.angular.demo.dto.UserDto;
import thales.spring.angular.demo.repositories.UserRepository;
import thales.spring.angular.demo.util.CryptoConverter2;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
/**
 * Class for application testing
 * Test all end points.
 *
 */
public class DemoApplicationTests {

	private static final Logger LOGGER = LoggerFactory.getLogger("JUnitTests");

	private final String API_ACCOUNT = "/api/account";
	private final String API_ACCOUNT_USER = "/api/account/user";
	private final String API_ACCOUNT_CLIENT = "/api/account/client";
	private final String API_ACCOUNT_XML = "/api/account/xml";
	private final String API_ACCOUNT_RIB = "/api/account/rib";
	private final String API_REGISTER = "/api/registration";
	private final String API_USER = "/api/users";
	private final String API_USER_LOGIN = "/api/users/login";
	private final String API_USER_AGENT = "/api/users/useragentConnection";
	private final String API_USER_INFOS = "/api/users/getinfos";
	private final String API_USER_VERIFY = "/api/users/verify";
	private final String API_ADVISOR = "/api/advisor";
	private final String API_ADVISOR_USER = "/api/advisor/user";
	private final String API_ADVISOR_ID_CLIENT = "/api/advisor/%s/clients";
	private final String API_CLIENT = "/clients";
	private final String API_LOG = "/api/log";
	private final String API_MESSAGE = "/api/messages";
	private final String API_PDF_SEARCH = "/api/pdf/search";
	private final String API_SHELL = "/api/shell";

	private final String METHOD_GET = "METHOD GET";
	private final String METHOD_POST = "METHOD POST";
	private final String METHOD_DELETE = "METHOD DELETE";

	private final static String lineTiret = new String(new char[50]).replace('\0', '-');

	@Value("${spring.datasource.username}")
	private String username;
	
	@Value("${spring.datasource.password}")
	private String password;

	private WebDriver driver;

	@Autowired
	/**
	 * @see SetUpProperties
	 */
	SetUpProperties setup;

	@Autowired
	/**
	 * @see UserRepository
	 */
	UserRepository repoUser;

	/**
	 *
	 * @param str : a string
	 * @param word : a word belonging or not in the string
	 * @return the number of times where the word is present in the string
	 */
	static int countFindOfOccurences(String str, String word) {
		Pattern pattern = Pattern.compile(word);
		Matcher matcher = pattern.matcher(str);

		int count = 0;
		while (matcher.find())
			count++;

		return count;
	}

	/**
	 * A shell
	 * @param command : a system command
	 * @return the execution of the process which executes the command
	 */
	static String execCommand(String command) {
		int result = -1;
		StringBuffer sbuf = new StringBuffer();
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc;
			LOGGER.debug("execCommand=" + command);
			proc = rt.exec(new String[] { "sh", "-c", command });
			result = proc.waitFor();
			try (InputStream in = (result == 0) ? proc.getInputStream() : proc.getErrorStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));) {
				String s;
				while ((s = br.readLine()) != null) {
					sbuf.append(s);
				}
			}
		} catch (IOException e) {
			sbuf.append(e.getMessage());
		} catch (InterruptedException e) {
			sbuf.append(e.getMessage());
		}
		return sbuf.toString();
	}

	@LocalServerPort
	private int port;

	private String getRootUrl() {
		return "http://localhost:" + port;
	}

	@Autowired
	private TestRestTemplate restTemplate;

	private void logInitTest(String msg) {
		String title = String.format("TEST FOR : %s", msg);
		LOGGER.info(title);
	}

	private void logTest(String msg) {
		LOGGER.info(msg);
	}

	private void logResultTest(HttpStatus status) {
		String msg = String.format("Réponse attendue : %s", status);
		LOGGER.info(msg);
	}

	private void logReponseTest(HttpStatus status) {
		String msg = String.format("Réponse reçue   : %s", status);
		LOGGER.info(msg);
	}

	private void logMethodPost() {
		LOGGER.info(METHOD_POST);
	}

	private void logMethodGet() {
		LOGGER.info(METHOD_GET);
	}

	private void logMethodDelete() {
		LOGGER.info(METHOD_DELETE);
	}

	private void logTestSuccess() {
		LOGGER.info(lineTiret);
		String title = String.format("|%s|", StringUtils.center("SUCCESS", 48));
		LOGGER.info(title);
	}

	@BeforeClass
	public static void setUpClass() {
		// start logging to test specific log file
		LOGGER.info(lineTiret);
		String title = String.format("|%s|", StringUtils.center("DEBUT DES TESTS", 48));
		LOGGER.info(title);
		LOGGER.info(lineTiret);
	}

	@AfterClass
	public static void tearDownClass() {
		LOGGER.info(lineTiret);
		String title = String.format("|%s|", StringUtils.center("FIN DES TESTS", 48));
		LOGGER.info(title);
		LOGGER.info(lineTiret);
	}

	@Before
	public void setUp() {
		// start logging to test specific log file
		LOGGER.info("\n");
		LOGGER.info(lineTiret);
		String title = String.format("|%s|", StringUtils.center("DEBUT DU TEST", 48));
		LOGGER.info(title);
		LOGGER.info("Present Project Directory : "+ System.getProperty("user.dir"));
		LOGGER.info(lineTiret);
		driver = new FirefoxDriver();
	    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@After
	public void tearDown() {

		LOGGER.info(lineTiret);
		String title = String.format("|%s|", StringUtils.center("FIN DU TEST", 48));
		LOGGER.info(title);
		LOGGER.info(lineTiret);
		driver.quit();

	}

	  @Test
	  public void testSelenium() throws Exception {
	    // On se connecte au site
	    driver.get("http://localhost:4200");
	    driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
	    driver.findElement (By.id ("FormEmailConnect")). sendKeys (username);
	    driver.findElement (By.id ("FormPasswordConnect")). sendKeys (password);
	    driver.findElement(By.cssSelector("button[type='submit']")).click();
	  }

	@Test
	/**
	 * API_USER : GET ALL USERS
	 */
	public void testGetUsers() {
		String url = getRootUrl() + API_USER;
		logInitTest(url);
		logMethodGet();
		logResultTest(HttpStatus.OK);

		ResponseEntity<List<UserApp>> response = restTemplate.exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<UserApp>>() {
				});

		logReponseTest(response.getStatusCode());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));

		List<UserApp> users = response.getBody();
		Assert.assertNotNull(users);
		Assert.assertFalse(users.isEmpty());
		logTestSuccess();
	}

	@Test
	/**
	 * API_ACCOUNT : GET ALL ACCOUNTS
	 */
	public void testGetAllAccounts() {
		String url = getRootUrl() + API_ACCOUNT;
		logInitTest(url);
		logMethodGet();
		logResultTest(HttpStatus.OK);
		ResponseEntity<List<Account>> response = restTemplate.exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Account>>() {
				});
		logReponseTest(response.getStatusCode());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		List<Account> accounts = response.getBody();
		Assert.assertNotNull(accounts);
		Assert.assertFalse(accounts.isEmpty());
		logTestSuccess();
	}

	@Test
	/**
	 * API_ADVISOR : GET ALL ADVISORS
	 */
	public void testGetAllAdvisors() {
		String url = getRootUrl() + API_ADVISOR;
		logInitTest(url);
		logMethodGet();
		logResultTest(HttpStatus.OK);

		ResponseEntity<List<Advisor>> response = restTemplate.exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Advisor>>() {
				});
		logReponseTest(response.getStatusCode());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		List<Advisor> advisors = response.getBody();
		Assert.assertNotNull(advisors);
		Assert.assertFalse(advisors.isEmpty());
		logTestSuccess();
	}

	@Test
	/**
	 * API_CLIENTS : GET ALL CLIENTS
	 */
	public void testGetClients() {
		String url = getRootUrl() + API_CLIENT;
		logInitTest(url);
		logMethodGet();
		logResultTest(HttpStatus.OK);

		ResponseEntity<List<Client>> response = restTemplate.exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Client>>() {
				});
		logReponseTest(response.getStatusCode());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		List<Client> clients = response.getBody();
		Assert.assertNotNull(clients);
		Assert.assertFalse(clients.isEmpty());
		logTestSuccess();
	}

	@Test
	/**
	 * API_MESSAGE : GET ALL MESSAGES
	 */
	public void testGetAllMessages() {
		String url = getRootUrl() + API_MESSAGE;
		logInitTest(url);
		logMethodGet();
		logResultTest(HttpStatus.OK);

		HttpHeaders headers = new HttpHeaders();
		headers.add("authorization", "adce5cc2722affa7f06c7f83d783c56c");
		HttpEntity<Message> request = new HttpEntity<>(null, headers);

		ResponseEntity<List<Message>> response = restTemplate.exchange(url, HttpMethod.GET, request,
				new ParameterizedTypeReference<List<Message>>() {
				});
		logReponseTest(response.getStatusCode());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		List<Message> messages = response.getBody();
		Assert.assertNotNull(messages);
		Assert.assertFalse(messages.isEmpty());
		logTestSuccess();
	}

	@Test
	/**
	 * API_CLIENTS : GET ONE BY ID
	 */
	public void testGetClientsById() {

		ResponseEntity<List<Client>> response = restTemplate.exchange(getRootUrl() + API_CLIENT, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Client>>() {
				});
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		List<Client> clients = response.getBody();

		Long id = clients.get(0).getIdClient();
		String url = getRootUrl() + API_CLIENT + "/" + id;
		logInitTest(url);
		logMethodGet();

		Client client = restTemplate.getForObject(url, Client.class);

		assertThat(client, notNullValue());
		assertThat(client.getIdClient(), is(id));
		logTestSuccess();
	}

	@Test
	/**
	 * API_USER : GET ONE BY ID
	 */
	public void testGetUserById() {

		ResponseEntity<List<UserApp>> response = restTemplate.exchange(getRootUrl() + API_USER, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<UserApp>>() {
				});
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		List<UserApp> users = response.getBody();

		Long id = users.get(0).getIdUser();
		String url = getRootUrl() + API_USER + "/" + id;
		logInitTest(url);
		logMethodGet();

		UserApp user = restTemplate.getForObject(url, UserApp.class);

		assertThat(user, notNullValue());
		assertThat(user.getIdUser(), is(id));
		logTestSuccess();
	}

	@Test
	/**
	 * API_USER_LOGIN : LOGIN
	 */
	public void testLogin() {
		String url = getRootUrl() + API_USER_LOGIN;
		logInitTest(url);
		logMethodPost();
		logResultTest(HttpStatus.OK);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		CryptoConverter2 instance = CryptoConverter2.getInstance();
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("email", "testuser@email.com");
		map.add("password", instance.convertToDatabaseColumn("TestUser"));

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
		String body = response.getBody();

		logReponseTest(response.getStatusCode());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		logTest("response.getBody()=" + body);
		assertThat(body, notNullValue());
		// {"status":"OK","token":"{token}"}
		Pattern pattern = Pattern.compile(".*\"status\":\"OK\",\"token\":\".*");
		Matcher matcher = pattern.matcher(body);
		boolean match = matcher.matches();
		assertThat(match, is(true));
		logTestSuccess();
	}

	@Test
	/**
	 * API_USER_AGENT : TEST USER AGENT
	 */
	public void testUserAgentConnection() {
		String url = getRootUrl() + API_USER_AGENT;
		logInitTest(url);
		logMethodGet();
		logResultTest(HttpStatus.OK);

		HttpHeaders headers = new HttpHeaders();
		headers.add("authorization", "adce5cc2722affa7f06c7f83d783c56c");
		headers.add("User-Agent", "admin");

		HttpEntity<String> request = new HttpEntity<String>(null, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

		logReponseTest(response.getStatusCode());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		String body = response.getBody();
		logTest("response.getBody()=" + body);
		// {"status":"OK"}
		Pattern pattern = Pattern.compile(".*\"status\":\"ADMIN\".*");
		Matcher matcher = pattern.matcher(body);
		boolean match = matcher.matches();
		assertThat(match, is(true));
		logTestSuccess();
	}

	@Test
	/**
	 * API_ACCOUNT : GET ONE BY ID
	 */
	public void testGetAccountById() {
		Long id = Long.valueOf("13654");
		String url = getRootUrl() + API_ACCOUNT + "/" + id;
		logInitTest(url);
		logMethodGet();

		Account account = restTemplate.getForObject(url, Account.class);

		assertThat(account, notNullValue());
		assertThat(account.getIdAccount(), is(Long.valueOf("13654")));
		logTestSuccess();
	}

	@Test
	/**
	 * API_ADVISOR : GET ONE BY ID
	 */
	public void testGetAdvisorById() {

		Long id = Long.valueOf("15674659");
		String url = getRootUrl() + API_ADVISOR + "/" + id;
		logInitTest(url);
		logMethodGet();

		HttpHeaders headers = new HttpHeaders();
		headers.add("authorization", "3d1afda6786138923cfd1a8990d0734f");
		HttpEntity<String> request = new HttpEntity<String>(null, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
		String advisor = response.getBody();
		logTest("response.getBody()=" + advisor);
		assertThat(advisor, notNullValue());
		Pattern pattern = Pattern.compile(".*idAdvisor\":15674659.*");
		Matcher matcher = pattern.matcher(advisor);
		boolean match = matcher.matches();
		assertThat(match, is(true));
		logTestSuccess();
	}

	@Test
	/**
	 * API_ADVISOR_ID_CLIENT : GET CLIENTS FRON ID ADVISOR
	 */
	public void testGetClientsByIdAdvisor() {
		String url = String.format(API_ADVISOR_ID_CLIENT, "15674659");
		logInitTest(url);
		logMethodGet();
		logResultTest(HttpStatus.OK);

		HttpHeaders headers = new HttpHeaders();
		headers.add("authorization", "3d1afda6786138923cfd1a8990d0734f");
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<List<Client>> response = restTemplate.exchange(getRootUrl() + url, HttpMethod.GET, entity,
				new ParameterizedTypeReference<List<Client>>() {
				});
		logReponseTest(response.getStatusCode());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		List<Client> clients = response.getBody();
		Assert.assertNotNull(clients);
		Assert.assertFalse(clients.isEmpty());
		logTestSuccess();
	}

	@Test
	/**
	 * API_ADVISOR : ADD ADVISOR
	 */
	public void testCreateAdvisor() {
		String url = getRootUrl() + API_ADVISOR;
		logInitTest(url);
		logMethodPost();
		logResultTest(HttpStatus.CREATED);

		String json =
			  "{\n"
			+ "    \"idAdvisor\": 15674659,\n"
			+ "    \"office\": \"39 rue des Arts, Lille\",\n"
			+ "    \"clients\": [\n"
			+ "        {\n"
			+ "            \"idClient\": 3,\n"
			+ "            \"firstname\": \"Jean\",\n"
			+ "            \"lastname\": \"Thierry\",\n"
			+ "            \"address\": \"12 rue du Molinel, Lille\",\n"
			+ "            \"accounts\": [\n"
			+ "                {\n"
			+ "                    \"idAccount\": 13654,\n"
			+ "                    \"type\": \"Compte courant\",\n"
			+ "                    \"balance\": 400,\n"
			+ "                    \"rate\": 50,\n"
			+ "                    \"document\": \"13654.pdf\"\n"
			+ "                },\n"
			+ "                {\n"
			+ "                    \"idAccount\": 13655,\n"
			+ "                    \"type\": \"Livret Boosté\",\n"
			+ "                    \"balance\": 1450,\n"
			+ "                    \"rate\": 4.5,\n"
			+ "                    \"document\": \"13655.pdf\"\n"
			+ "                }\n"
			+ "            ]\n"
			+ "        }\n"
			+ "    ],\n"
			+ "    \"firstnameAdvisor\": \"Véronique\",\n"
			+ "    \"lastnameAdvisor\": \"Sanson\"\n"
			+ "}";
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<String> request = new HttpEntity<String>(json, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
		logReponseTest(response.getStatusCode());
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

		String account = response.getBody();
		logTest("response.getBody()=" + account);
		assertThat(account, notNullValue());

		Pattern pattern = Pattern.compile(".*idAdvisor\":15674659.*");
		Matcher matcher = pattern.matcher(account);
		boolean match = matcher.matches();
		assertThat(match, is(true));
		logTestSuccess();
	}

	@Test
	/**
	 * API_ACCOUNT : ADD ACCOUNT
	 */
	public void testBuildAccount() {
		String url = getRootUrl() + API_ACCOUNT;
		logInitTest(url);
		logMethodPost();
		logResultTest(HttpStatus.CREATED);
		String xml = "<account>\n" + "    <idAccount>88888</idAccount>\n" + "    <type>Compte courant</type>\n"
				+ "    <balance>400</balance>\n" + "    <rate>5</rate>\n" + "    <document>rib4.pdf</document>\n"
				+ "</account>";

		HttpHeaders headers = new HttpHeaders();
		headers.add("authorization", "3d1afda6786138923cfd1a8990d0734f");
		HttpEntity<String> request = new HttpEntity<String>(xml, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

		logReponseTest(response.getStatusCode());
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

		String account = response.getBody();
		logTest("response.getBody()=" + account);
		assertThat(account, notNullValue());

		Pattern pattern = Pattern.compile(".*idAccount\":88888.*");
		Matcher matcher = pattern.matcher(account);
		boolean match = matcher.matches();
		assertThat(match, is(true));
		logTestSuccess();
	}

	@Test
	/**
	 * API_ACCOUNT_CLIENT : ADD ACCOUNT
	 */
	public void testCreateAccountForClient() {
		ResponseEntity<List<Client>> responseClient = restTemplate.exchange(getRootUrl() + API_CLIENT, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Client>>() {
				});
		assertThat(responseClient.getStatusCode(), is(HttpStatus.OK));
		List<Client> clients = responseClient.getBody();

		Assert.assertNotNull(clients);
		Assert.assertFalse(clients.isEmpty());

		Long idClient = clients.get(0).getIdClient();
		String url = getRootUrl() + API_ACCOUNT_CLIENT + "/" + idClient;
		logInitTest(url);
		logMethodPost();
		logResultTest(HttpStatus.CREATED);

		Account account = new Account();
		account.setIdAccount(99999L);
		account.setBalance(400);
		account.setDocument("rib5.pdf");
		account.setRate(2.5);
		account.setType("Compte courant");

		HttpEntity<Account> request = new HttpEntity<>(account);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
		logReponseTest(response.getStatusCode());
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

		String body = response.getBody();
		logTest("response.getBody()=" + body);
		Assert.assertNotNull(body);
		Pattern pattern = Pattern.compile(".*idAccount\":99999.*");
		Matcher matcher = pattern.matcher(body);
		boolean match = matcher.matches();
		assertThat(match, is(true));
		logTestSuccess();
	}

	@Test
	/**
	 * API_ACCOUNT_XML : ADD ACCOUNT
	 */
	public void testAddAccountXml() {
		String url = getRootUrl() + API_ACCOUNT_XML;
		logInitTest(url);
		logMethodPost();
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<java version=\"10.0.2\" class=\"java.beans.XMLDecoder\">\n"
				+ " <object class=\"thales.spring.angular.demo.domain.Account\">\n"
				+ "  <void property=\"idAccount\">\n" + "   <long>13654</long>\n" + "  </void>\n"
				+ "  <void property=\"balance\">\n" + "   <double>400.0</double>\n" + "  </void>\n"
				+ "  <void property=\"document\">\n" + "   <string>13654.pdf</string>\n" + "  </void>\n"
				+ "  <void property=\"rate\">\n" + "   <double>5.0</double>\n" + "  </void>\n"
				+ "  <void property=\"type\">\n" + "   <string>Compte courant</string>\n" + "  </void>\n"
				+ " </object>\n" + "</java>";
		logTest("\n"+xml);
		logResultTest(HttpStatus.CREATED);
		HttpEntity<String> request = new HttpEntity<>(xml);
		ResponseEntity<Account> response = restTemplate.exchange(url, HttpMethod.POST, request, Account.class);

		logReponseTest(response.getStatusCode());
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		Account account = response.getBody();
		assertThat(account, notNullValue());
		assertThat(account.getIdAccount(), is(Long.valueOf("13654")));
		logTestSuccess();
	}

	@Test
	/**
	 * API_MESSAGE : ADD MESSAGE
	 */
	public void testCreateMessage() {
		String url = getRootUrl() + API_MESSAGE;
		logInitTest(url);
		logMethodPost();
		logResultTest(HttpStatus.CREATED);

		Message message = new Message();
		message.setSubject("subject");
		message.setContent("content");
		message.setFrom(1L);
		message.setTo(2L);
		message.setRead(Boolean.FALSE);
		message.setStatus(MessageStatus.TOREPLY);

		HttpHeaders headers = new HttpHeaders();
		headers.add("authorization", "3d1afda6786138923cfd1a8990d0734f");
		HttpEntity<Message> request = new HttpEntity<>(message, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
		logReponseTest(response.getStatusCode());
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		String body = response.getBody();
		logTest("response.getBody()=" + body);
		Assert.assertNotNull(body);
		Pattern pattern = Pattern.compile(".*idMessage.*\"subject\":\"subject\",\"content\":\"content\",\"from\":.*\"to\":2.*");
		Matcher matcher = pattern.matcher(body);
		boolean match = matcher.matches();
		assertThat(match, is(true));
		logTestSuccess();
	}

	@Test
	/**
	 * API_ACCOUNT : GET ONE BY ID
	 */
	public void testZDeleteAccountById() {
		Long id = Long.valueOf("13654");
		String url = getRootUrl() + API_ACCOUNT + "/" + id;
		logInitTest(url);
		logMethodDelete();
		logResultTest(HttpStatus.OK);

		ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
		logReponseTest(response.getStatusCode());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		logTestSuccess();
	}

	@Test
	/**
	 * API_REGISTER : ADD USER
	 */
	public void testAddRegisterUserAccount() {
		String url = getRootUrl() + API_REGISTER;
		logInitTest(url);
		logMethodPost();
		logResultTest(HttpStatus.OK);

		UserDto user = UserDto.builder()
						.firstName("firstname")
						.lastName("lastName")
						.address("address")
						.email("weak@email.com")
						.password("password")
						.matchingPassword("password")
						.secretAnswer("dupont")
						.role(1)
						.build();

		HttpEntity<UserDto> request = new HttpEntity<>(user);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

		logReponseTest(response.getStatusCode());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));

		String body = response.getBody();
		logTest("response.getBody()=" + body);
		Assert.assertNotNull(body);

		HttpHeaders headers = response.getHeaders();
		logTest("response.getCookie()=" + headers.getFirst(HttpHeaders.SET_COOKIE));

		Pattern pattern = Pattern.compile("\\{\"status\":\"OK\", \"token\":\"[a-zA-Z0-9]{32}\"\\}");
		Matcher matcher = pattern.matcher(body);
		boolean match = matcher.matches();
		assertThat(match, is(true));
		testGetinfos();
		logTestSuccess();
	}

	@Test
	/**
	 * API_REGISTER : ADD USER
	 */
	public void testGetinfos() {
		String url = getRootUrl() + API_USER_INFOS;
		logInitTest(url);
		logMethodGet();
		logResultTest(HttpStatus.OK);

		HttpHeaders headers = new HttpHeaders();
		headers.add("authorization", "7b09bc9a1d35d82b01af184db843a3f3");
		headers.add(HttpHeaders.COOKIE,
				"user=ewogICJpZENsaWVudCIgOiAzLAogICJ1c2VyIiA6IHsKICAgICJpZFVzZXIiIDogMywKICAgICJlbWFpbCIgOiAiZW1haWwiCiAgfQp9; Path=/");
		HttpEntity<String> request = new HttpEntity<>(null, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

		logReponseTest(response.getStatusCode());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		String body = response.getBody();
		logTest("response.getBody()=" + body);
		Assert.assertNotNull(body);

		boolean match = body.contains("idClient");
		assertThat(match, is(true));
		logTestSuccess();
	}

	@Test
	/**
	 * API_USER_VERIFY : VERIFY TOKEN
	 */
	public void testVerifyToken() {
		String url = getRootUrl() + API_USER_VERIFY;
		logInitTest(url);
		logMethodGet();
		logResultTest(HttpStatus.OK);

		HttpHeaders headers = new HttpHeaders();
		headers.add("authorization", "7b09bc9a1d35d82b01af184db843a3f3");
		HttpEntity<String> request = new HttpEntity<>(null, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

		logReponseTest(response.getStatusCode());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		String body = response.getBody();
		logTest("response.getBody()=" + body);

		Pattern pattern = Pattern.compile(".*\"status\":\"OK\".*");
		Matcher matcher = pattern.matcher(body);
		boolean match = matcher.matches();
		assertThat(match, is(true));
		logTestSuccess();
	}

	@Test
	/**
	 * API_ACCOUNT_USER : GET MY ACCOUNT (TOKEN)
	 */
	public void testGetUserAccounts() {
		String url = getRootUrl() + API_ACCOUNT_USER;
		logInitTest(url);
		logMethodGet();
		logResultTest(HttpStatus.OK);
		HttpHeaders headers = new HttpHeaders();
		headers.add("authorization", "3d1afda6786138923cfd1a8990d0734f");
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<List<Account>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
				new ParameterizedTypeReference<List<Account>>() {
				});
		logReponseTest(response.getStatusCode());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		List<Account> accounts = response.getBody();
		Assert.assertNotNull(accounts);
		Assert.assertFalse(accounts.isEmpty());
		logTestSuccess();
	}

	@Test
	/**
	 * API_ADVISOR_USER : GET MY ADVISOR (TOKEN)
	 */
	public void testGetUserAdvisor() {

		HttpHeaders headers = new HttpHeaders();
		headers.add("authorization", "3d1afda6786138923cfd1a8990d0734f");
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<Long> response = restTemplate.exchange(getRootUrl() + API_ADVISOR_USER, HttpMethod.GET, entity,
				Long.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		Long id = response.getBody();
		String url = getRootUrl() + API_ADVISOR + "/" + id;
		logInitTest(url);
		logMethodGet();

		ResponseEntity<String> responseAdvisor = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		String advisor = responseAdvisor.getBody();
		logTest("response.getBody()=" + advisor);
		assertThat(advisor, notNullValue());

		Pattern pattern = Pattern.compile(".*idAdvisor\":" + id + ".*");
		Matcher matcher = pattern.matcher(advisor);
		boolean match = matcher.matches();
		assertThat(match, is(true));
		logTestSuccess();
	}

	@Test
	/**
	 * API_ACCOUNT_RIB : GET MY ACCOUNT (TOKEN)
	 */
	public void testGetCompteCourant() {
		String url = getRootUrl() + API_ACCOUNT_RIB;
		logInitTest(url);
		logMethodGet();
		logResultTest(HttpStatus.OK);

		HttpHeaders headers = new HttpHeaders();
		headers.add("authorization", "3d1afda6786138923cfd1a8990d0734f");
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		logReponseTest(response.getStatusCode());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));

		String body = response.getBody();
		logTest("response.getBody()=" + body);
		Assert.assertNotNull(body);
		Pattern pattern = Pattern.compile("rib.*pdf");
		Matcher matcher = pattern.matcher(body);
		boolean match = matcher.matches();
		assertThat(match, is(true));
		logTestSuccess();
	}

	@Test
	/**
	 * API_LOG : GET ONE FILE from "/var/www/logs/" + name + ".log"
	 */
	public void testGetLogs() {

		String name = "honeypot";
		String url = getRootUrl() + API_LOG + "/" + name;
		logInitTest(url);
		logMethodGet();
		logResultTest(HttpStatus.OK);
		HttpHeaders headers = new HttpHeaders();
		headers.add("authorization", "3d1afda6786138923cfd1a8990d0734f");
		HttpEntity<String> request = new HttpEntity<String>(null, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

		logReponseTest(response.getStatusCode());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));

		String file = response.getBody();
		logTest("response.getBody()=" + file);
		assertThat(file, notNullValue());
		logTestSuccess();
	}

	@Test
	/**
	 * API_PDF_SEARCH : GET PDFS GET /api/pdf/search/{regex}
	 */
	public void testGetFiles() {
		String regex = "rib4";
		String url = getRootUrl() + API_PDF_SEARCH + "/" + regex;
		logInitTest(url);
		logMethodGet();

		HttpHeaders headers = new HttpHeaders();
		headers.add("authorization", "3d1afda6786138923cfd1a8990d0734f");
		HttpEntity<String> request = new HttpEntity<String>(null, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
		String body = response.getBody();
		logTest("response.getBody()=" + body);
		assertThat(body, notNullValue());
		logTestSuccess();
	}

	@Test
	/**
	 * API_SHELL : EXECUTE A SHELL COMMAND GET /api/shell
	 */
	public void testShell() {
		String url = getRootUrl() + API_SHELL;
		logInitTest(url);
		logMethodPost();
		logResultTest(HttpStatus.OK);

		HttpHeaders headers = new HttpHeaders();
		headers.add("authorization", "3d1afda6786138923cfd1a8990d0734f");

		String command = "ls -l";
		HttpEntity<String> request = new HttpEntity<>(command, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

		logReponseTest(response.getStatusCode());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		String body = response.getBody();
		logTest("response.getBody()=" + body);
		assertThat(body, notNullValue());
		// {"status":"OK","token":"{token}"}
		Pattern pattern = Pattern.compile(".*\"total.*");
		Matcher matcher = pattern.matcher(body);
		boolean match = matcher.matches();
		assertThat(match, is(true));
		logTestSuccess();
	}
}
