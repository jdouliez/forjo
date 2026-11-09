package thales.spring.angular.demo.remediation.owasp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import thales.spring.angular.demo.domain.UserSerializer;
import thales.spring.angular.demo.remediation.util.OwaspCheckEnum;
import thales.spring.angular.demo.remediation.util.RemediationResponse;
import thales.spring.angular.demo.remediation.util.ResponseWrapper;
import thales.spring.angular.demo.remediation.util.StaticUtils;
import thales.spring.angular.demo.remediation.util.TestsType;
import thales.spring.angular.demo.security.jwt.JwtTokenUtil;
import thales.spring.angular.demo.util.I18n;

/**
 * Class with functions to test TOP A1 - Injections
 */
public class Top2017A8 extends TopOwaspGeneric {
    
	private final String API_ACCOUNT_XML = "api/account/xml";
	
    public Top2017A8() {
        super(I18n.getMessage("msg.A8.name"));
    }
    
    /**
     * Check if vulnerability is corrected or not
     * @return 
     */
    public List<RemediationResponse> checkOperationalTests(HttpServletRequest request) {
    	return concateneList(check_A8_SerializeCookie(request), check_A8_SerializeAccount(request));    	
    }
    
    /**
     * Check if vulnerability is always functional or not
     * @return 
     */
    public Boolean checkFunctionalTests(HttpServletRequest request) {
    	return super.isSuccessForAllRemediation(concateneList(checkFunctional_A8_SerializeCookie(request),checkFunctional_A8_SerializeAccount(request)));
    }
    
    /**
	 * Checks the functional for TOP A8 vuln: 
	 */	
	public List<RemediationResponse> checkFunctional_A8_SerializeCookie(HttpServletRequest inputRequest) {
		LOGGER.info("---------------------------------------------------------------------------");
		LOGGER.info("--------------------- checkFunctional_A8_SerializeCookie --------------------------------------------");
		LOGGER.info("---------------------------------------------------------------------------");
		
		List<RemediationResponse> remediations = new ArrayList<RemediationResponse>();	
		
		String[] loginInfosUser = login(inputRequest, USER_EMAIL, USER_PASSWORD);
		String authorizationUser = loginInfosUser[0];
		String cookieUser = loginInfosUser[1];			
		RemediationResponse remediation = getUserInfos(inputRequest, USER_EMAIL, authorizationUser, cookieUser);
		remediation.setTestsType(TestsType.FUNCTIONAL);
			
		if (remediation.getSuccess() ) {				
			checkDecodeCookie(cookieUser, remediation);			
		}
			
		remediations.add(remediation);
		
		String[] loginInfosAdmin = login(inputRequest, ADMIN_EMAIL, ADMIN_PASSWORD);
		String authorizationAdmin = loginInfosAdmin[0];
		String cookieAdmin = loginInfosAdmin[1];		
		remediation = getUserInfos(inputRequest, ADMIN_EMAIL, authorizationAdmin, cookieAdmin);
		remediation.setTestsType(TestsType.FUNCTIONAL);
		
		if (remediation.getSuccess() ) {				
			checkDecodeCookie(cookieAdmin, remediation);			
		}
		remediations.add(remediation);
		return remediations;		
	}
	
	/**
	 * Check if information in the cookie are valid for the decode opration
	 * @param cookieUser the user cookie
	 * @param remediation teh remediation which will be update during the check
	 */
	private void checkDecodeCookie(String cookieUser, RemediationResponse remediation) {
		boolean success = false;
		String comments = "";

		cookieUser = extractEncodedUser(cookieUser);

		if (cookieUser == null) {
			comments = I18n.getMessage("err.cookieUserNotSet");
		} else {
			LOGGER.info(cookieUser);
			ObjectMapper mapper = new ObjectMapper();
			String jsonCookie = new String(Base64.getDecoder().decode(cookieUser));
			try {
				UserSerializer serializerObject = mapper.readValue(jsonCookie, UserSerializer.class);
				LOGGER.info(
						"--------------------- checkFunctional_A8_SerializeCookie  serializerObject--------------------------------------------");
				LOGGER.info(serializerObject.toString());
				if (serializerObject.getIdClient() != serializerObject.getUser().getIdUser()) {
					comments = I18n.getMessage("err.cookieUser");
				} else {
					success = true;
				}
			} catch (JsonParseException e) {
				comments = e.getMessage();
				e.printStackTrace();
			} catch (JsonMappingException e) {
				comments = e.getMessage();
				e.printStackTrace();
			} catch (IOException e) {
				comments = e.getMessage();
				e.printStackTrace();
			}
		}

		remediation.setSuccess(success);
		remediation.setComments(comments);
	}
	/**
	 * 
	 * @param cookieUser the user cookie
	 * @return the part of the user parameter from the cookie
	 */
	 private String extractEncodedUser(String cookieUser) {
		if (cookieUser == null) return null;		
		LOGGER.info("--------------------- extractEncodedUser  cookieUser--------------------------------------------");
		LOGGER.info(cookieUser);
		String regex = "user=";
		int beginIndex = cookieUser.indexOf(regex);
		if ( beginIndex < 0) return null;
		int endIndex = cookieUser.indexOf(";",beginIndex+regex.length());
		if ( endIndex < regex.length()) return null;
		String extractEncodedUser = cookieUser.substring(beginIndex + regex.length(), endIndex);
		LOGGER.info(extractEncodedUser);
		return extractEncodedUser;
	
	}

	/**
	 * Checks the TOP A8 vuln: 
	 */	
	public List<RemediationResponse> check_A8_SerializeCookie(HttpServletRequest inputRequest) {
		LOGGER.info("---------------------------------------------------------------------------");
		LOGGER.info("--------------------- check_A8_SerializeCookie --------------------------------------------");
		LOGGER.info("---------------------------------------------------------------------------");
		
		List<RemediationResponse> remediations = new ArrayList<RemediationResponse>();	
		
		String[] loginInfosUser = login(inputRequest, USER_EMAIL, USER_PASSWORD);
		String authorizationUser = loginInfosUser[0];
		
		String[] loginInfosAdmin = login(inputRequest, ADMIN_EMAIL, ADMIN_PASSWORD);
		String authorizationAdmin = loginInfosAdmin[0];
		String cookieAdmin = loginInfosAdmin[1];	
		
		RemediationResponse remediation = getUserInfos(inputRequest, ADMIN_EMAIL, authorizationAdmin, cookieAdmin);
		String bodyAdmin = new String(remediation.getBody());
		
		LOGGER.info("--------------------- check_A8_SerializeCookie : TEST SPOOFED ADMIN --------------------------------------------");
		remediation = getUserInfos(inputRequest, USER_EMAIL, authorizationUser, cookieAdmin);
		
		String payload = I18n.getMessage("msg.A8.1.payload1", USER_EMAIL);
		
		remediation.setPayload(payload);
		if (bodyAdmin.equals(remediation.getBody())) {
			remediation.setSuccess(false);
			remediation.setComments(I18n.getMessage("msg.A8.1.payload1.response"));
		} else {
			remediation.setSuccess(true);
			remediation.setComments(I18n.getMessage("remediation.succeeded"));
		}
	
		remediations.add(remediation);
		
		return remediations;		
	}
	
	/**
	 * Checks the functional for the TOP A8 vuln: 
	 */	
	public List<RemediationResponse> checkFunctional_A8_SerializeAccount(HttpServletRequest inputRequest) {
		LOGGER.info("---------------------------------------------------------------------------");
		LOGGER.info("--------------------- check_A8_SerializeAccount --------------------------------------------");
		LOGGER.info("---------------------------------------------------------------------------");
		
		List<RemediationResponse> remediations = new ArrayList<RemediationResponse>();		
	
		RemediationResponse remediation = postAccountXml(inputRequest, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"<java version=\"10.0.2\" class=\"java.beans.XMLDecoder\">\n" + 
				" <object class=\"thales.spring.angular.demo.domain.Account\">\n" + 
				"  <void property=\"idAccount\">\n" + 
				"   <long>13654</long>\n" + 
				"  </void>\n" + 
				"  <void property=\"balance\">\n" + 
				"   <double>400.0</double>\n" + 
				"  </void>\n" + 
				"  <void property=\"document\">\n" + 
				"   <string>13654.pdf</string>\n" + 
				"  </void>\n" + 
				"  <void property=\"rate\">\n" + 
				"   <double>5.0</double>\n" + 
				"  </void>\n" + 
				"  <void property=\"type\">\n" + 
				"   <string>Compte courant</string>\n" + 
				"  </void>\n" + 
				" </object>\n" + 
				"</java>");

		
		remediation.setComments(I18n.getMessage("msg.A8.2.payload1.comments"));
		remediation.setTestsType(TestsType.FUNCTIONAL);
		
		if (remediation.getSuccess()) {
			remediations.add(remediation);
		} else {
			//a remediation solution is working : XMLDecoder is perhaps not used
			remediation = checkFunctional_A8_SerializeAccount_withoutXmlDecoder(inputRequest);
			remediations.add(remediation);
		}
		

		return remediations;	
	}
	
	private RemediationResponse checkFunctional_A8_SerializeAccount_withoutXmlDecoder(HttpServletRequest inputRequest) {
		
		if (StaticUtils.getAccountService().isExist(88788L))
			StaticUtils.getAccountService().deleteById(88788L);
		
		String nameOfRemediation = I18n.getMessage("msg.AX.functional.check", "A8");
		
		String payload = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
				"<account>\n" + 
				"    <idAccount>88788</idAccount>\n" + 
				"    <type>Compte courant</type>\n" + 
				"    <balance>400</balance>\n" + 
				"    <rate>5</rate>\n" + 
				"    <document>88788.pdf</document>\n" + 
				"</account>";
		
		String endPoint = API_ACCOUNT_XML;
		HttpMethod httpMethod = HttpMethod.POST;
		
		RemediationResponse remediation = new RemediationResponse();		
		remediation.setName(nameOfRemediation);
		remediation.setPayload(payload);	
		
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_AUTHORIZATION, getAuthorization());
        ArrayList<MediaType> mediaTypes = new ArrayList<MediaType>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaType.APPLICATION_XML);
        headers.setAccept(mediaTypes);
        headers.setContentType(MediaType.APPLICATION_XML);
		ResponseEntity<String> response = prepareRequest(inputRequest, endPoint, httpMethod, headers, remediation);

		Boolean success = false;
		String comments = I18n.getMessage("ok.payload.succeeded");
	
		if (response.getStatusCode() == HttpStatus.CREATED) 
		{
			success = true;			
		}

		String body = response.getBody();

		// [{"idAccount":88888,"type":"Compte courant","balance":400.0,"rate":5.0,"document":"rib4.pdf"}]
		Pattern pattern = Pattern.compile(".*idAccount\":88788.*");
		Matcher matcher = pattern.matcher(body);
		boolean match = matcher.matches();
		if (!match) {
			success = false;
		}
		
		remediation.setSuccess(success);
		remediation.setComments(comments);
		
		return remediation;
	}

	/**
	 * Checks the TOP A8 vuln: 
	 */	
	public List<RemediationResponse> check_A8_SerializeAccount(HttpServletRequest inputRequest) {
		LOGGER.info("---------------------------------------------------------------------------");
		LOGGER.info("--------------------- check_A8_SerializeAccount --------------------------------------------");
		LOGGER.info("---------------------------------------------------------------------------");
		
		List<RemediationResponse> remediations = new ArrayList<RemediationResponse>();		
	
		RemediationResponse remediation = postAccountXml(inputRequest, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
							"<java version=\"10.0.2\" class=\"java.beans.XMLDecoder\">\n" + 
							"	  <object class=\"java.io.PrintWriter\">\n" + 
							"	    <string>/tmp/reverse.sh</string>\n" + 
							"	    <void method=\"println\">\n" + 
							"	      <string>A file has been created</string>\n" + 
							"	    </void>\n" + 
							"	    <void method=\"close\"/>\n" + 
							"	  </object>>\n" + 
							"</java>");
			
		Path path = Paths.get("/tmp/reverse.sh");
		if (Files.exists(path)) {			
			remediation.setComments(I18n.getMessage("msg.A8.2.payload2.comments",remediation.getComments(), path.getParent() + "/" + path.getFileName()));
			remediation.setSuccess(false);
			try {
				Files.delete(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			remediation.setComments(I18n.getMessage("msg.A8.2.payload3.comments",remediation.getComments(), path.getParent() + "/" + path.getFileName()));						
			remediation.setSuccess(true);
		}
		
		remediations.add(remediation);
		return remediations;	
	}
	
	
	/**
	 * 
	 * @return the response of the request for API_ACCOUNT_XML
	 */
	private RemediationResponse postAccountXml(HttpServletRequest inputRequest, String dataAccountPayload) {
		String nameOfRemediation = I18n.getMessage("msg.A8.2.name");
		Boolean success = false;
		String comments = "";				
		String endPoint = API_ACCOUNT_XML;
		String payload = dataAccountPayload;
		String body = "";
		
		HttpHeaders headers = new HttpHeaders();
		headers.add(HEADER_AUTHORIZATION, getAuthorization());
        ArrayList<MediaType> mediaTypes = new ArrayList<MediaType>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaType.APPLICATION_XML);
        headers.setAccept(mediaTypes);
        headers.setContentType(MediaType.APPLICATION_XML);
        addHeaderForOwaspKey(headers, OwaspCheckEnum.CHECK_A8.toString());
		HttpEntity<String> entity = new HttpEntity<String>(payload, headers);			
		String url = buildFrontEndPath(inputRequest, endPoint);
		LOGGER.info(url);

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(url, entity , String.class);		
			body = response.getBody();
			
			Pattern pattern = Pattern.compile(".*idAccount.*");
			Matcher matcher = pattern.matcher(body);
			boolean match = matcher.matches();
			if (match) 
			{			
				success = true;		
			}
		} catch (RestClientException e) {
			comments = I18n.getMessage("err.occured");	
		} catch (Exception e) {
			body = ResponseWrapper.getBody(OwaspCheckEnum.CHECK_A8);
			comments = I18n.getMessage("err.occured : " + e.getMessage());	
		}	
		
		String tmpBody = ResponseWrapper.getBody(OwaspCheckEnum.CHECK_A8);
		
		if (tmpBody!=null) {
			if (tmpBody.contains("error")) {
				comments = I18n.getMessage("err.occured");
				body = tmpBody;
			}
		}
							
		RemediationResponse remediation = new RemediationResponse();		
		remediation.setName(nameOfRemediation);
		remediation.setPayload(payload);
		remediation.setUrl(url);
		remediation.setBody(body);
		remediation.setSuccess(success);
		remediation.setComments(comments);
		return remediation;
	}
}
