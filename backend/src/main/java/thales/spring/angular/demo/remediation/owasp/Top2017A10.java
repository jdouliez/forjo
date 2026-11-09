package thales.spring.angular.demo.remediation.owasp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.client.RestTemplateBuilder;

import thales.spring.angular.demo.domain.UserApp;
import thales.spring.angular.demo.dto.UserDto;
import thales.spring.angular.demo.remediation.util.RemediationResponse;
import thales.spring.angular.demo.remediation.util.StaticUtils;
import thales.spring.angular.demo.security.jwt.JwtTokenUtil;
import thales.spring.angular.demo.util.I18n;

/**
 * Class with functions to test TOP A10
 */
public class Top2017A10 extends TopOwaspGeneric {
    
	private final String API_REGISTER = "api/registration";
	private static final String pathfileseculog = "logs/seculog.log";
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		
    public Top2017A10() {
        super(I18n.getMessage("msg.A10.name"));
    }
    
    /**
     * Check if vulnerability is corrected or not
     * @return 
     */
    public List<RemediationResponse> checkOperationalTests(HttpServletRequest request) {
        return check_A10_Logs(request);
    }
    
    /**
     * Check if vulnerability is always functional or not
     * @return 
     */
    public Boolean checkFunctionalTests(HttpServletRequest request) {
    	String nameOfRemediation = I18n.getMessage("msg.AX.functional.check", "A10");
		Boolean success = false;
		String comments = I18n.getMessage("msg.A10.err.file.exists");	
		
		RemediationResponse remediation = new RemediationResponse();
		remediation.setName(nameOfRemediation);
		remediation.setPayload("");		
		remediation.setUrl(pathfileseculog);
		remediation.setBody("");
		
		Path path = Paths.get(pathfileseculog);
		if (Files.exists(path)) {		
			success = true;
		}
				
		remediation.setSuccess(success);
		remediation.setComments(comments);
		
		if (remediation.getSuccess()) {
			success = false;
			checkGoodLogin(request);
			comments = I18n.getMessage("msg.A10.err.file.notempty");	
			File file = new File(path.toUri());
			if (file.length()>0) {
				success = true;
			}			
		}
		
		remediation.setSuccess(success);
		remediation.setComments(comments);
		
		return remediation.getSuccess();
    }

    /**
	 * Checks logs: 
	 */	
	public List<RemediationResponse> check_A10_Logs(HttpServletRequest inputRequest) {
		LOGGER.info("---------------------------------------------------------------------------");
		LOGGER.info("--------------------- check_A10_Logs --------------------------------------------");
		LOGGER.info("---------------------------------------------------------------------------");
			
		List<RemediationResponse> remediations = new ArrayList<RemediationResponse>();
		
		RemediationResponse remediation = checkGoodLogin(inputRequest);
		remediations.add(remediation);	
		remediation = checkBadLogin(inputRequest);
		remediations.add(remediation);	
		remediation = checkUserRegistrationLogs(inputRequest);
		remediations.add(remediation);	
		return remediations;		
	}
	
	/**
	 * 
	 * @param inputRequest
	 * @return the response of the check if a good login is tracked
	 */
	private RemediationResponse checkGoodLogin(HttpServletRequest inputRequest) {
		String nameOfRemediation = I18n.getMessage("msg.A10.step1.name");
		Boolean success = false;
		String comments = "";	
		String payload = I18n.getMessage("msg.A10.1.authentication.notracked");
		String url = "";
		String body = "";
		String[] loginInfosUser = login(inputRequest, USER_EMAIL, USER_PASSWORD);
		String cookie = loginInfosUser[1]; 
		
		boolean infoconnected = false;
		String userId = "";
		
		if (cookie.indexOf("user") < 0) {
			comments = I18n.getMessage("err.cookie");
		} else {
			payload = "grep -i \"" + LocalDateTime.now().format(formatter) + ".*security.*"+ USER_EMAIL +".*SUCCESS\" " + pathfileseculog; 
			Runtime rt = Runtime.getRuntime();
			Process proc;
			try {
				proc = rt.exec(new String[] { "sh", "-c", payload });
				InputStream is = proc.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String line;
				while (!infoconnected && (line = br.readLine()) != null) {
					LOGGER.info(line);
					if (line.indexOf(USER_EMAIL)>0) {
						success = true;
						comments = I18n.getMessage("msg.A10.1.authentication.tracked");
						infoconnected = true;
						int deb = line.indexOf('(');
						if (deb > 0) {
							int fin = line.indexOf(')');
							if (fin > deb) {
								success = false;
								userId = line.substring(deb+1, fin);
								comments += I18n.getMessage("msg.A10.1.iduser.tracked", userId);				
							}
						}
					}
					body += line;
				}
			} catch (IOException e) {
				comments = e.toString();
			}
		}
				
		RemediationResponse remediation = new RemediationResponse();
		remediation.setName(nameOfRemediation);
		remediation.setPayload(payload);		
		remediation.setUrl(url);
		remediation.setSuccess(success);
		remediation.setComments(comments);
		remediation.setBody(body);
		return remediation;
	}
	
	/**
	 * 
	 * @param inputRequest
	 * @return the response of the check if a bad login is tracked.
	 */
	private RemediationResponse checkBadLogin(HttpServletRequest inputRequest) {
		String nameOfRemediation = I18n.getMessage("msg.A10.step2.name");
		Boolean success = false;
		String comments = I18n.getMessage("msg.A10.1.authentication.failure.notracked");
		String payload = "grep -i \"" + LocalDateTime.now().format(formatter) + ".*security.*"+ USER_EMAIL +".*FAILURE\" " + pathfileseculog;
		String url = "";
		String body = "";
		login(inputRequest, USER_EMAIL, "sdsgfsdgfsdfg");
		
		boolean infotracked = false;
		
		Runtime rt = Runtime.getRuntime();
		Process proc;
		try {
			proc = rt.exec(new String[] { "sh", "-c", payload });
			InputStream is = proc.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			while (!infotracked && (line = br.readLine()) != null) {
				LOGGER.info(line);
				if (line.indexOf(USER_EMAIL)>0) {
					success = true;
					comments = I18n.getMessage("msg.A10.1.authentication.failure.tracked");
					infotracked = true;
				}
				body += line;
			}
		} catch (IOException e) {
			comments = e.toString();
		}
				
		RemediationResponse remediation = new RemediationResponse();
		remediation.setName(nameOfRemediation);
		remediation.setPayload(payload);		
		remediation.setUrl(url);
		remediation.setSuccess(success);
		remediation.setComments(comments);
		remediation.setBody(body);
		return remediation;
	}
	
	/**
	 * 
	 * @param inputRequest
	 * @return the response of the check if the user registration is tracked.
	 */
	private RemediationResponse checkUserRegistrationLogs(HttpServletRequest inputRequest) {
		String nameOfRemediation =  I18n.getMessage("msg.A10.step3.name");
		String payload = "Register a new user";
		String url = buildFrontEndPath(inputRequest, API_REGISTER); 
		Boolean success = false;
		String comments = "";
		String body = "";
		
		RemediationResponse remediation = new RemediationResponse();		
		remediation.setSuccess(success);
		remediation.setComments(comments);
		remediation.setBody(body);
		
		remediation = testUserRegistrationStrongPassword(inputRequest, remediation, userWithStrongPasswordDto);
		remediation.setName(nameOfRemediation);
		remediation.setPayload(payload);
		remediation.setUrl(url);		
		if (remediation.getSuccess()!=true) {
			return remediation;
		}
		
		comments = I18n.getMessage("msg.A10.1.registration.notracked");
		
		payload = "grep -i \"" + LocalDateTime.now().format(formatter) + ".*security.*REGISTRATION.*" + userWithStrongPasswordDto.getEmail() + "\" " + pathfileseculog;
		boolean infotracked = false;
	
		Runtime rt = Runtime.getRuntime();
		Process proc;
		try {
			proc = rt.exec(new String[] { "sh", "-c", payload });
			InputStream is = proc.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			while (!infotracked && (line = br.readLine()) != null) {
				LOGGER.info(line);
				if (line.indexOf(userWithStrongPasswordDto.getEmail())>0) {
					success = true;
					comments = I18n.getMessage("msg.A10.1.registration.tracked");
					infotracked = true;
				}
				
				if (line.indexOf(userWithStrongPasswordDto.getPassword())>0) {
					success = false;
					comments += comments = I18n.getMessage("msg.A10.1.password.tracked");
				}
				
				if (line.indexOf(userWithStrongPasswordDto.getFirstName())>0) {
					success = false;
					comments += comments = I18n.getMessage("msg.A10.1.firstname.tracked");
				}
				
				if (line.indexOf(userWithStrongPasswordDto.getLastName())>0) {
					success = false;
					comments += comments = I18n.getMessage("msg.A10.1.lastname.tracked");
				}
				
				if (line.indexOf(userWithStrongPasswordDto.getAddress())>0) {
					success = false;
					comments += comments = I18n.getMessage("msg.A10.1.address.tracked");
				}
				
				body += line;
			}
		} catch (IOException e) {
			comments = e.toString();
		}
		
		remediation.setName(nameOfRemediation);
		remediation.setPayload(payload);
		remediation.setUrl(url);
		remediation.setSuccess(success);
		remediation.setComments(comments);
		remediation.setBody(body);
		return remediation;
	}
			
	private RemediationResponse testUserRegistrationStrongPassword(HttpServletRequest inputRequest, RemediationResponse remediation, UserDto userDtoToCreate) {
		Boolean success = false;
		String comments = "";
		String body = "";
		LOGGER.info("---------------------- testUserRegistrationStrongPassword FOR USERS : " + userDtoToCreate.getEmail() +" ---------------------------");
		UserApp user = StaticUtils.getUserService().findByEmail(userDtoToCreate.getEmail());				
		if (user!=null) {
			//delete user
			LOGGER.info("---------------------- testUserRegistrationStrongPassword FOR USERS : DELETE " + userDtoToCreate.getEmail() +" (" + user.getIdUser() +") ---------------------------");
			StaticUtils.getUserService().deleteById(user.getIdUser());
		}
		registerUserAccount(inputRequest, userDtoToCreate);
		
		user = StaticUtils.getUserService().findByEmail(userDtoToCreate.getEmail());				
		if (user==null) {
			comments = I18n.getMessage("err.registration.ko");
		} else {		
			String[] loginInfosUser = login(inputRequest, userDtoToCreate.getEmail(), userDtoToCreate.getPassword());
			
			String authorizationUser = loginInfosUser[0];
			String cookieUser = loginInfosUser[1];			
			remediation = getUserInfos(inputRequest, USER_EMAIL, authorizationUser, cookieUser);
			if (!remediation.getSuccess().equals(true)) {
				comments = I18n.getMessage("err.registration.ko");
			} else  {
				if (user.isStrongPassword()) {
					success = true;					
				}
				body = "the password is " + (user.isStrongPassword() ? "strong" : "weak");
			}
		}
		
		remediation.setSuccess(success);
		remediation.setComments(comments);
		remediation.setBody(body);
		return remediation;
	}
}
