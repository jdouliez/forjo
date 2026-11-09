package thales.spring.angular.demo.remediation.owasp;

import static java.util.Collections.emptyList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.UrlEscapers;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thales.spring.angular.demo.domain.UserApp;
import thales.spring.angular.demo.dto.UserDto;
import thales.spring.angular.demo.remediation.dto.CheckResult;
import thales.spring.angular.demo.remediation.dto.PayloadDto;
import thales.spring.angular.demo.remediation.dto.VulnerabilitiesDto;
import thales.spring.angular.demo.remediation.util.LoggingRequestInterceptor;
import thales.spring.angular.demo.remediation.util.OwaspCheckEnum;
import thales.spring.angular.demo.remediation.util.RemediationResponse;
import thales.spring.angular.demo.security.jwt.JwtAuthenticationService;
import thales.spring.angular.demo.security.jwt.JwtTokenUtil;
import thales.spring.angular.demo.services.AccountService;
import thales.spring.angular.demo.services.AdvisorService;
import thales.spring.angular.demo.services.ClientService;
import thales.spring.angular.demo.services.MessageService;
import thales.spring.angular.demo.services.UserService;
import thales.spring.angular.demo.util.I18n;

/**
 * Top owasp generic module
 */
@NoArgsConstructor
@Getter
@Setter
@Component
public abstract class TopOwaspGeneric implements ITopOwaspGeneric {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TopOwaspGeneric.class);

    protected RestTemplate restTemplate;
    protected String jwtToken;

    protected static UserDto userWithWeakPasswordDto;
    protected static UserDto userWithStrongPasswordDto;
    protected static UserDto userToBruteForceDto;

    private List<VulnerabilitiesDto> vulnerabilities;
    private String name;

    @Autowired
    protected AccountService accountService;

    @Autowired
    protected ClientService clientService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected AdvisorService advisorService;

    @Autowired
    protected MessageService messageService;

    @Autowired
    protected RestTemplateBuilder builder;
    
    @Autowired
    protected JwtTokenUtil jwtTokenUtil;

    @Autowired
    protected JwtAuthenticationService jwtAuthenticationService;

    @Value("${frontend.hostname}")
    public String frontendHostname;
    

    public TopOwaspGeneric(String name) {
        this.name = name;

        userWithWeakPasswordDto =  UserDto.builder()
                                    .firstName("firstname")
                                    .lastName("lastName")
                                    .address("address")
                                    .email("weak@email.com")
                                    .password("password")
                                    .matchingPassword("password")
                                    .secretAnswer("dupont")
                                    .role(1)
                                    .build();

        userWithStrongPasswordDto =  UserDto.builder()
                                    .firstName("firstname")
                                    .lastName("lastName")
                                    .address("address")
                                    .email("strong@email.com")
                                    .password("P@SSw0rd1234&")
                                    .matchingPassword("P@SSw0rd1234&")
                                    .secretAnswer("dupont")
                                    .role(1)
                                    .build();

        userToBruteForceDto = UserDto.builder()
                                    .firstName("firstName")
                                    .lastName("lastName")
                                    .address("address")
                                    .email("remediation_bruteforce@email.com")
                                    .password("Azertyuiop123456!")
                                    .matchingPassword("Azertyuiop123456!")
                                    .secretAnswer("dupont")
                                    .role(1)
                                    .build();
    }

    @PostConstruct
    public void postInit(){
        if (builder != null) {
            this.restTemplate = builder.build();
        
            List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
            interceptors.add(new LoggingRequestInterceptor());
            restTemplate.setInterceptors(interceptors);
        }

        generateJwtToken();
    }

    public void generateJwtToken() {
        UserDetails user = new User(USER_EMAIL, USER_PASSWORD, emptyList());  
        this.jwtToken = jwtTokenUtil.generateToken(user);
    }

    public void updateJwtTokenIfneeded() {
        if (this.jwtToken == null || this.jwtToken.isEmpty() ||  jwtTokenUtil.isTokenExpired(this.jwtToken)) {
            generateJwtToken();
        }
    }

    protected String getAuthorization() {
    	return HEADER_AUTHORIZATION_BEARER + jwtToken;
    }

    public List<RemediationResponse> checkSecurityTests(HttpServletRequest request) {

        List<RemediationResponse> responses = new ArrayList<RemediationResponse>();

        if (vulnerabilities != null) {
            for (VulnerabilitiesDto vuln : vulnerabilities) {
                for (PayloadDto payload : vuln.getPayloads()) {
                    responses.add(checkVulnerabiliyPayload(request, vuln, payload));
                }
            }
        }

        return responses;
    }


    /**
     *
     * @param request
     * @param endPoint
     * @return the url built with the scheme, servername, serverport of the
     * request and the endpoint
     */
    protected String buildFrontEndPath(HttpServletRequest request, String endPoint) {

        if (frontendHostname == null) {
            frontendHostname = request.getServerName();
        }

        return String.format("%s://%s:%d/%s", request.getScheme(), frontendHostname, request.getServerPort(), endPoint);
    }

    /**
     * Get global success evaluation
     *
     * @param remediations
     * @return the global success evaluation
     */
    protected Boolean isSuccessForAllRemediation(List<RemediationResponse> remediations) {
        boolean success = true;
       
        if (remediations == null)
            return Boolean.FALSE;

        for (RemediationResponse remediation : remediations) {
            success &= remediation.getSuccess();
        }

        return success;
    }
    
    /**
     * Add in the headers owaspValue for the key OWASP
     * @param headers
     * @param owaspValue one value from CheckOwasp Enum
     */
    protected void addHeaderForOwaspKey(HttpHeaders headers, String owaspValue) {
		if (headers.containsKey(OwaspCheckEnum.OWASP.toString())) {
        	headers.remove(OwaspCheckEnum.OWASP.toString());
        }
       
        headers.add(OwaspCheckEnum.OWASP.toString(), owaspValue);
	}

    /**
     * concanete list of remediation response in a new list
     *
     * @param args
     * @return
     */
    protected List<RemediationResponse> concateneList(List<RemediationResponse>... args) {
        return Stream.of(args)
                .flatMap(x -> x.stream())
                .collect(Collectors.toList());

    }

    protected ResponseEntity<String> prepareRequest(HttpServletRequest inputRequest, String endPoint, HttpMethod httpMethod, HttpHeaders headers, RemediationResponse remediation) {
        
        HttpEntity<String> entity = new HttpEntity<String>(remediation.getPayload(), headers);
        String url = buildFrontEndPath(inputRequest, endPoint);
        remediation.setUrl(url);
        ResponseEntity<String> response = restTemplate.exchange(url, httpMethod, entity, String.class);

        remediation.setBody(response.getBody());
        
        LOGGER.info("-----------------------------------------------------");
        LOGGER.info("prepareRequest BODY : " + remediation.getBody());
        LOGGER.info("-----------------------------------------------------");
        
        return response;
    }

    protected UserApp createAndFindUser(HttpServletRequest request, UserDto user) {

        UserApp userEntity = userService.findByEmail(user.getEmail());
        if (userEntity != null) {
            userService.deleteById(userEntity.getIdUser());
        }

        try {
            return registerUserAccount(request, user) ? userService.findByEmail(user.getEmail()) : null;
        } 
        catch (Exception e) {
            LOGGER.error("Error", e);
            return null;
        }
    }

    /**
     *
     * @param inputRequest
     * @param endPoint
     * @param httpMethod
     * @param remediation
     * @return the response of the request for the endpoint
     */
    protected ResponseEntity<String> prepareRequest(HttpServletRequest inputRequest, String endPoint, HttpMethod httpMethod, RemediationResponse remediation) {
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_AUTHORIZATION, getAuthorization());
        
        ArrayList<MediaType> mediaTypes = new ArrayList<MediaType>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaType.APPLICATION_XML);
        headers.setAccept(mediaTypes);
        
        return prepareRequest(inputRequest, endPoint, httpMethod, headers, remediation);
    }

    /**
     *
     * @param inputRequest
     * @param login
     * @param password
     * @return the token of a 32 length if login and password matches or token
     * empty and the cookie
     * @throws JsonProcessingException 
     */
    protected String [] login(HttpServletRequest inputRequest, String login, String password) {
        LOGGER.info("---------------------------------------------------------------------------");
        LOGGER.info("--------------------- login : " + login + " ------------------------------------------");
        LOGGER.info("---------------------------------------------------------------------------");

        String url = buildFrontEndPath(inputRequest, API_USER_LOGIN);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
		//headers.add(HEADER_AUTHORIZATION, getAuthorization());
        
        ObjectMapper mapper = new ObjectMapper();        
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username", login);
        map.put("password", password);
        
        String data;
		try {
			data = mapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			return new String[] { "", ""};
		}
		
        HttpEntity<String> entity = new HttpEntity<String>(data, headers);

        ResponseEntity<String> response;
        String token = "";
        String cookieUserHeaders = "";
        String body = "";
		try {
			response = restTemplate.postForEntity(url, entity, String.class);
			body = response.getBody();
	        
	        // {"token":"{token}"}
	        Pattern pattern = Pattern.compile(".*\"token\":\".*");
	        Matcher matcher = pattern.matcher(body);
	        boolean match = matcher.matches();
	       
	        if (match) {
	            String find = "token\":\"";
	            int indexOf = body.indexOf(find);
	            token = body.substring(indexOf + find.length());
	            indexOf = token.indexOf("\"");
	            token = token.substring(0, indexOf);
	            //filter(cookie -> "user".equals(cookie))
	            cookieUserHeaders = getHeaderValue(response, HttpHeaders.SET_COOKIE);	        
	        }
		} catch (RestClientException e) {
			LOGGER.info("--------------------- RestClientException : " + e + " ------------------------------------------");
			//bad password 
			if (!e.toString().contains("401")) {
				e.printStackTrace();
			}
		
		}

        
		LOGGER.info("--------------------- body  : " + body + " ------------------------------------------");
        LOGGER.info("--------------------- token : " + token + " ------------------------------------------");
        LOGGER.info("--------------------- cookieUserHeaders : " + cookieUserHeaders + " ------------------------------------------");
        return new String[] { token, cookieUserHeaders};
    }


    /**
     *
     * @param response
     * @param header
     * @return the value of the header of the response ou null if the header
     * doesn't a=exist
     */
    private static String getHeaderValue(ResponseEntity<String> response, String header) {
        try {
            return response.getHeaders().get(header).stream().collect(Collectors.joining(";"));
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }

    /**
     *
     * @return the user infos for API_USER_INFOS
     */
    protected RemediationResponse getUserInfos(HttpServletRequest inputRequest, String loginUser, String authorizationUserHeader, String cookieUserHeader) {
        String nameOfRemediation = I18n.getMessage("msg.A8.1.name");
        Boolean success = false;
        String comments = I18n.getMessage("msg.A8.1.comments");
        String endPoint = API_USER_INFOS;
        HttpMethod httpMethod = HttpMethod.GET;
        String payload = I18n.getMessage("msg.A8.1.payload", loginUser, authorizationUserHeader, cookieUserHeader);
        String authorization = authorizationUserHeader;
        LOGGER.info(authorization);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_AUTHORIZATION, HEADER_AUTHORIZATION_BEARER + authorization);
        headers.set("Cookie", cookieUserHeader);

        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        String url = buildFrontEndPath(inputRequest, endPoint);
        LOGGER.info(url);

        ResponseEntity<String> response = restTemplate.exchange(url, httpMethod, entity, String.class);

        String body = response.getBody();
        boolean match = (body.indexOf("idClient") > 0);
        if (match) {
            success = true;
        } else {
        	//infos could not be retrieved 
        	success = false;
        	comments = I18n.getMessage("err.cookieUser");
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

    /**
     * API_REGISTER : ADD USER
     *
     * @param userDtoToCreate user to create
     */
    protected boolean registerUserAccount(HttpServletRequest request, UserDto userDtoToCreate) {

        boolean result = Boolean.FALSE;

        try {

            //Register through API
            String url = buildFrontEndPath(request, API_REGISTER);
    		HttpEntity<UserDto> entity = new HttpEntity<UserDto>(userDtoToCreate);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (!response.getStatusCode().equals(HttpStatus.OK)) {
                return Boolean.FALSE;
            }

            String body = response.getBody();
            if (response.getBody() == null) {
                return Boolean.FALSE;
            }

            // Check json response
            JSONObject data = new JSONObject(body);
            result = data.has("status") && data.get("status").equals("OK");
            result &= data.has("token") && Pattern.compile("(^[A-Za-z0-9-_]*\\.[A-Za-z0-9-_]*\\.[A-Za-z0-9-_]*$)").matcher(data.get("token").toString()).matches();
        } 
        catch (RestClientException e) {
            LOGGER.error("Error during registration of a new user", e);
        }

        return result;
    }

    /**
     *
     * @param command
     * @return the execution of the command in a shell process
     */
    static String execCommand(String command) {
        int result = -1;
        StringBuffer sbuf = new StringBuffer();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc;
            LOGGER.debug("execCommand=" + command);
            proc = rt.exec(new String[]{"sh", "-c", command});
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

    @Override
	public RemediationResponse checkVulnerabiliyPayload(HttpServletRequest request, VulnerabilitiesDto vulnDto, PayloadDto payload) {

		RemediationResponse remediation = new RemediationResponse();
		remediation.setName(vulnDto.getName());
		remediation.setPayload(payload.getPayload());
		remediation.setUrl(buildFrontEndPath(request, vulnDto.getEndPoint()) + " (POST)");
        remediation.setIsPayloadHtml(payload.getIsHtml() != null ? payload.getIsHtml() : Boolean.TRUE);

		Boolean success = Boolean.FALSE;
		String comments = "";

        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpResponse response = null;

            if (HttpMethod.GET.equals(payload.getMethod())) {

                String localPayload = remediation.getPayload().contains(" ") ? UrlEscapers.urlFragmentEscaper().escape(remediation.getPayload()) : remediation.getPayload();
                localPayload = localPayload.replace(";","%3B");
                response = simpleGetRequest(buildFrontEndPath(request, vulnDto.getEndPoint() + "/" + localPayload));
            }
            else if (HttpMethod.POST.equals(payload.getMethod())) {
              
                HttpPost httpRequest = new HttpPost(buildFrontEndPath(request, vulnDto.getEndPoint()))  ;
                httpRequest.setHeader(HEADER_AUTHORIZATION, getAuthorization());

                StringEntity requestEntity = new StringEntity(payload.getPayload(), ContentType.APPLICATION_XML);
                httpRequest.setHeader("Content-type", "application/xml");
                httpRequest.setEntity(requestEntity);
                response = client.execute(httpRequest);
            }
            else {
                throw new Exception("Unsupported HTTP method in payload..");
            }

            
            if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value() || response.getStatusLine().getStatusCode() != HttpStatus.CREATED.value()) {
                comments = I18n.getMessage("err.httpstatus.ok");	
                success = Boolean.FALSE;	
            }
            
            String body = EntityUtils.toString(response.getEntity(), "UTF-8");        
            remediation.setBody(body);
            
            Pattern pattern = Pattern.compile(payload.getRegex());
            Matcher matcher = pattern.matcher(body);
            boolean match = matcher.matches();
            
            if (match) {
                comments = I18n.getMessage("err.payload.succeeded");		
            } 
            else {
                success = Boolean.TRUE;
            }
        }
        catch (Exception e) {
            LOGGER.error("Error", e);
        }
        
		remediation.setSuccess(success);
		remediation.setComments(comments);	

        return remediation;
	}


    protected HttpResponse simpleGetRequest(String url) {
        
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet httpRequest = new HttpGet(url);
            httpRequest.setHeader(HEADER_AUTHORIZATION, getAuthorization());
            return client.execute(httpRequest);
        } 
        catch (Exception e) {
            LOGGER.error("Error when performing GET request to \"" + url + "\"", e);
            return null;
        }
    }


    protected CheckResult checkSingleFunctionalTestWithGetMethod(HttpServletRequest request, String endPoint, List<String> conditions, ContentType responseType) throws Exception {

        CheckResult result = new CheckResult();

        String url = buildFrontEndPath(request, endPoint);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpRequest = new HttpGet(url);
        httpRequest.setHeader(HEADER_AUTHORIZATION, getAuthorization());
        HttpResponse response = client.execute(httpRequest);
        result.setResponse(response);

        String body = EntityUtils.toString(response.getEntity(), "UTF-8");  
        result.setReponseBody(body); 
        result.setSuccess(Boolean.TRUE);  

        if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
            LOGGER.info("Path \"" + endPoint + "\" responded with HTTP/" + response.getStatusLine().getStatusCode());
            result.setSuccess(Boolean.FALSE);
            return result;
        }

        if (ContentType.APPLICATION_JSON.equals(responseType)) {
            try {
                new JSONArray(body);
            } 
            catch (JSONException err){
                try {
                    new JSONObject(body);
                } catch (JSONException err2){
                   result.setSuccess(Boolean.FALSE);
                   LOGGER.info("App is not functionnal (Not a JSON body)");
                }
            }
        }
        else if (ContentType.TEXT_HTML.equals(responseType)) {
            try {
                Document doc = Jsoup.parse(body);
                Elements title = doc.getElementsByTag("title");
                if (! title.contains("Lion's Banking corporation")) {
                    result.setSuccess(Boolean.FALSE);
                    LOGGER.info("App is not functionnal (Not a HTML body or page title does not contain \"Lion's Banking corporation\")");
                }
           } 
           catch (Exception err){
               LOGGER.error("E", err);
               result.setSuccess(Boolean.FALSE);
               LOGGER.info("App is not functionnal (Not a HTML body or page title does not contain \"Lion's Banking corporation\")");
           }
        }
        else if (ContentType.TEXT_PLAIN.equals(responseType)) {
        }
        else {
            throw new Exception("Unsupported Content type.");
        }

        //Check if response body contains the condition (as string comparaison)
        for (String condition : conditions) {
            LOGGER.info(body);
            if (! body.contains(condition)) {
                result.setSuccess(Boolean.FALSE);
                LOGGER.info("App is not functionnal (Response body does not contain \"" + condition +"\")");
            }
        }

        return result;
    }

    protected CheckResult checkSingleFunctionalTestWithPostMethod(HttpServletRequest request, String endPoint, Object payload, ContentType requestType, List<String> conditions, ContentType responseType) {

        CheckResult result = new CheckResult();
        String url = buildFrontEndPath(request, endPoint);

        CloseableHttpClient client = HttpClients.createDefault();
        
        HttpPost httpRequest = new HttpPost(url);
        httpRequest.setHeader(HEADER_AUTHORIZATION, getAuthorization());
        httpRequest.setHeader("Content-type", requestType.getMimeType());

        try {
            // Process request as urlencoded form
            if (ContentType.APPLICATION_FORM_URLENCODED.equals(requestType)) {
                httpRequest.setEntity(new UrlEncodedFormEntity((List<? extends NameValuePair>) payload));
            }
            // Process request as simple entity (json or xml body)
            else {
                httpRequest.setEntity(new StringEntity((String) payload, requestType));
            }

            HttpResponse response = client.execute(httpRequest);
            result.setResponse(response);
           
            if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value() && response.getStatusLine().getStatusCode() != HttpStatus.CREATED.value()) {
                result.setSuccess(Boolean.FALSE);
            }
    
            // Process response as JSON 
            if (ContentType.APPLICATION_JSON.equals(responseType)) {

                String body = EntityUtils.toString(response.getEntity(), "UTF-8");
                result.setReponseBody(body);

                try {
                    new JSONArray(body);
                } 
                catch (JSONException err){
                    try {
                        new JSONObject(body);
                    } 
                    catch (JSONException err2){
                        LOGGER.info("App is not functionnal (Not a JSON body)");
                        result.setSuccess(Boolean.FALSE);
                    }
                }

                result.setSuccess(runConditions(body, conditions));
            }
            // Process response as HTML 
            else if (ContentType.TEXT_HTML.equals(responseType)) {

                String body = EntityUtils.toString(response.getEntity(), "UTF-8");
                result.setReponseBody(body);

                try {
                    Document doc = Jsoup.parse(body);
                    Elements title = doc.getElementsByTag("title");
                    if (! title.contains("Lion's Banking corporation")) {
                        LOGGER.info("App is not functionnal (Not a HTML body or page title does not contain \"Lion's Banking corporation\")");
                        result.setSuccess(Boolean.FALSE);
                    }

                    result.setSuccess(runConditions(body, conditions));
               } 
               catch (Exception err){
                   LOGGER.error("E", err);
                   LOGGER.info("App is not functionnal (Not a HTML body or page title does not contain \"Lion's Banking corporation\")");
                   result.setSuccess(Boolean.FALSE);
               }
            }
            // Process response as TEXT 
            else if (ContentType.DEFAULT_TEXT.equals(responseType)) {

                String body = EntityUtils.toString(response.getEntity(), "UTF-8");
                result.setReponseBody(body);
              
                try {
                    result.setSuccess(runConditions(body, conditions));
                } 
                catch (Exception err){
                   LOGGER.error("E", err);
                   LOGGER.info("App is not functionnal (Not a TEXT body)");
                   result.setSuccess(Boolean.FALSE);
                }
            }
            // Process response as PDF
            else if ("application/pdf".equals(responseType.getMimeType())) {

                Path temp = Files.createTempFile("forjo_", ".pdf");
                FileUtils.copyInputStreamToFile(response.getEntity().getContent(), temp.toFile());
                result.setSuccess(temp.toFile().isFile());
            }
            // Process response as unknown type 
            else {
                throw new Exception("Unsupported request content-type : " + responseType.getMimeType());
            }
        } 
        catch (Exception e) {
            LOGGER.error("Error performing HTTP request", e);
            result.setSuccess(Boolean.FALSE);
        }

        return result;
    }


    private Boolean runConditions(String responseBody, List<String> conditions) {

        Boolean success = Boolean.TRUE;

        //Check if response body contains the conditions (as string comparaison)
        for (String condition : conditions) {
    
            if (! responseBody.contains(condition)) {
                success = Boolean.FALSE;
            }
        }

        return success;
    }
}
