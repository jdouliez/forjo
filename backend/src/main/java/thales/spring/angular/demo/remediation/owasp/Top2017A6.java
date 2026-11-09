package thales.spring.angular.demo.remediation.owasp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.boot.web.client.RestTemplateBuilder;

import lombok.Getter;
import lombok.Setter;
import thales.spring.angular.demo.remediation.util.RemediationResponse;
import thales.spring.angular.demo.security.jwt.JwtTokenUtil;
import thales.spring.angular.demo.util.I18n;

@Getter
@Setter
/**
 * Class with functions to test TOP A1 - Injections
 */
public class Top2017A6 extends TopOwaspGeneric {

    //Options to use firefox headless
    private FirefoxOptions options;

    public Top2017A6() {
        super(I18n.getMessage("msg.A6.name"));
       
		this.setOptions(new FirefoxOptions());
        this.getOptions().setHeadless(true);
    }

    /**
     * Check if vulnerability is corrected or not
     * @return
     */
    public List<RemediationResponse> checkOperationalTests(HttpServletRequest request) {
    	return concateneList(check_A6(request), check_A6_Cookie(request));

    //    return check_A6(request);
    }

    /**
     * Check if vulnerability is always functional or not
     * @return
     */
    public Boolean checkFunctionalTests(HttpServletRequest request) {

    	RemediationResponse remediation = checkFunctional_A6_Login(request);
    	if (remediation.getSuccess()) {
    		remediation = checkFunctional_A6_Cookie(request);
    	}
    	return remediation.getSuccess();
    }

    /**
	 * Checks if login is OK
	 */
	public RemediationResponse checkFunctional_A6_Login(HttpServletRequest inputRequest) {


		LOGGER.info("---------------------------------------------------------------------------");
		LOGGER.info("--------------------- checkFunctional_A6_Login -----------------------------");
		LOGGER.info("---------------------------------------------------------------------------");

		String nameOfRemediation = I18n.getMessage("msg.AX.functional.check", "A6");
		Boolean success = false;
		String comments = "";

		RemediationResponse remediation = new RemediationResponse();
		remediation.setName(nameOfRemediation);

		String url = "http://" + frontendHostname + ":4200";

		WebDriver driver = new FirefoxDriver(this.getOptions());
		String textprint= "";
	    try {

		    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		    driver.get(url);
		    driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		    driver.findElement (By.id ("FormEmailConnect")). sendKeys (USER_EMAIL);
		    driver.findElement (By.id ("FormPasswordConnect")). sendKeys (USER_PASSWORD);

		    driver.findElement(By.cssSelector("button[type='submit']")).click();

		    driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);

		    try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOGGER.error("Error", e);
			}

		    driver.findElement(By.xpath("//h2"));
	    	textprint = driver.findElement(By.xpath("//h2")).getText();
	    	if (textprint.contains("Welcome") || textprint.contains("Bienvenue")) {
	    		success = true;
	    		comments = I18n.getMessage("err.webshell.nopresent");
	    	}
	    	LOGGER.info("--------------------- RESPONSE ELEMENT H2 : " + textprint + "-----------------------------");

	    	if (!success) {
	    		 LOGGER.info("--------------------- FIND ELEMENT H3 -----------------------------");
	  		    //FIND INVALID CREDENTIALS
	  		    try {
	  		    	driver.findElement(By.xpath("//h3"));
	  		    	textprint = driver.findElement(By.xpath("//h3")).getText();
	  		    	if (textprint.contains("Incorrect credentials") || textprint.contains("Identifiants incorrects")) {
	  			    	success = false;
	  			    	comments = textprint;
	  		    	}

	  	    	} catch (NoSuchElementException e2) {
	  	    		comments = e2.getMessage();
	  	    	}
	  		    LOGGER.info("--------------------- RESPONSE ELEMENT H3 : " + textprint + "-----------------------------");
	  		    LOGGER.info("--------------------- FIND ELEMENT H2 -----------------------------");
	    	}
	    } catch (NoSuchElementException e1) {
	    		success = false;
	    		comments = e1.getMessage();
	    }
	    finally {
	    	driver.quit();
	    }
	    remediation.setUrl(url);
	    remediation.setSuccess(success);
		remediation.setComments(comments);
		remediation.setBody(textprint);
		return remediation;
	}

	/**
	 * Checks cookie
	 */
	public RemediationResponse checkFunctional_A6_Cookie(HttpServletRequest inputRequest) {

		LOGGER.info("---------------------------------------------------------------------------");
		LOGGER.info("--------------------- checkFunctional_A6_Cookie -----------------------------");
		LOGGER.info("---------------------------------------------------------------------------");

		String nameOfRemediation =  I18n.getMessage("msg.A6.2.name");
		Boolean success = false;
		String comments = "";
		String payload = "";
		String url = "";
		String[] loginInfosUser = login(inputRequest, USER_EMAIL, USER_PASSWORD);
		String cookie = loginInfosUser[1];
		
		if (cookie.indexOf("user") < 0) {
			comments = I18n.getMessage("err.cookie");
		} 
		else {
			success = true;
		}

		RemediationResponse remediation = new RemediationResponse();
		remediation.setName(nameOfRemediation);
		remediation.setPayload(payload);
		remediation.setUrl(url);
		remediation.setSuccess(success);
		remediation.setComments(comments);
		remediation.setBody(Arrays.asList(loginInfosUser).stream().collect(Collectors.joining("|-----------|")));
		return remediation;
	}

	/**
	 * Checks indev dev
	 */
	public List<RemediationResponse> check_A6(HttpServletRequest inputRequest) {

		LOGGER.info("---------------------------------------------------------------------------");
		LOGGER.info("--------------------- check_A6_INDEV_DEV -----------------------------");
		LOGGER.info("---------------------------------------------------------------------------");

		String frontEndUrl = "http://" + frontendHostname + ":4200";

		String nameOfRemediation = I18n.getMessage("msg.A6.1.name");
		Boolean success = false;
		String comments = "";

		String payload = I18n.getMessage("msg.A6.1.payload");

		RemediationResponse remediation = new RemediationResponse();
		remediation.setName(nameOfRemediation);
		remediation.setPayload(payload);

	    String textprint= "";

	    String indexDevUrl = frontEndUrl + "/nutshell";
		//System.setProperty("webdriver.gecko.driver", "/usr/local/bin");
		WebDriver driver = new FirefoxDriver(this.getOptions());
	    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

	    driver.get(frontEndUrl);
	    driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
	    driver.findElement (By.id ("FormEmailConnect")). sendKeys (USER_EMAIL);
	    driver.findElement (By.id ("FormPasswordConnect")). sendKeys (USER_PASSWORD);

	    driver.findElement(By.cssSelector("button[type='submit']")).click();

	    driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);

	    try {
			Thread.sleep(1000);
		} 
		catch (InterruptedException e) {
			LOGGER.error("Error", e);
		}
		
		driver.get(indexDevUrl);
	    driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);

	    try {
	    	driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
	    	driver.findElement(By.xpath("//h1"));
	    	driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
	    	textprint = driver.findElement(By.xpath("//h1")).getText();
	    	if (textprint.contains("404")) {
	    		success = true;
	    		textprint = I18n.getMessage("err.webshell.nopresent");
	    		comments = I18n.getMessage("remediation.succeeded");
	    	}
	    } 
		catch (NoSuchElementException e1) {

	    	try {
	    		driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
		    	driver.findElement(By.xpath("//h3"));
		    	driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		    	textprint = driver.findElement(By.xpath("//h3")).getText();
		    	if (textprint.contains("Webshell")) {
			    	success = false;
			    	comments = I18n.getMessage("err.webshell.present");
		    	}

	    	} catch (NoSuchElementException e2) {
	    		success = false;
	    	}
	    }

	    driver.quit();
	    remediation.setUrl(indexDevUrl);
	    remediation.setSuccess(success);
		remediation.setComments(comments);
		remediation.setBody(textprint);
		List<RemediationResponse> remediations = new ArrayList<RemediationResponse>();
		remediations.add(remediation);
		return remediations;
	}

	/**
	 * Checks cookie
	 */
	public List<RemediationResponse> check_A6_Cookie(HttpServletRequest inputRequest) {

		LOGGER.info("---------------------------------------------------------------------------");
		LOGGER.info("--------------------- check_A6_COOKIE -----------------------------");
		LOGGER.info("---------------------------------------------------------------------------");

		String nameOfRemediation =  I18n.getMessage("msg.A6.2.name");
		Boolean success = false;
		String comments = "";
		String payload = "";
		String url = "";
		String[] loginInfosUser = login(inputRequest, USER_EMAIL, USER_PASSWORD);
		String cookie = loginInfosUser[1];

		if (cookie.indexOf("user") < 0) {
			comments = I18n.getMessage("err.cookie");
		} else {
			if (cookie.indexOf("Secure") < 0) {
				comments = I18n.getMessage("err.cookie.secure");
			} else if (cookie.indexOf("HttpOnly") < 0) {
				comments = I18n.getMessage("err.cookie.httponly");
			} else {
				comments = I18n.getMessage("remediation.succeeded");
				success = true;
			}
		}
		RemediationResponse remediation = new RemediationResponse();
		remediation.setName(nameOfRemediation);
		remediation.setPayload(payload);
		remediation.setUrl(url);
		remediation.setSuccess(success);
		remediation.setComments(comments);
		remediation.setBody(Arrays.asList(loginInfosUser).stream().collect(Collectors.joining("|-----------|")));
		List<RemediationResponse> remediations = new ArrayList<RemediationResponse>();
		remediations.add(remediation);
		return remediations;
	}
}
