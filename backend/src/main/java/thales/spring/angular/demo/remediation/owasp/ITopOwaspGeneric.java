package thales.spring.angular.demo.remediation.owasp;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import thales.spring.angular.demo.remediation.dto.PayloadDto;
import thales.spring.angular.demo.remediation.dto.VulnerabilitiesDto;
import thales.spring.angular.demo.remediation.util.RemediationResponse;

/**
 * Top owasp check interface
 */
public interface ITopOwaspGeneric {

    static final String USER_EMAIL = "remediation-user@email.com";
    static final String USER_PASSWORD = "R3m3dl@tl0n5";
    
    static final String ADMIN_EMAIL = "remediation-admin@email.com";
    static final String ADMIN_PASSWORD = "R3m3dl@tl0n5";
    
    static final String API_USER_LOGIN = "api/authenticate";
    static final String API_USER_INFOS = "api/users/getinfos";
    static final String API_ADMIN = "api/admin";
    static final String API_REGISTER = "api/registration";
    static final String API_USER = "api/users";
    static final String API_ADVISOR = "api/advisor";
    static final String API_ACCOUNT = "api/account";
    static final String API_ACCOUNT_RIB = "api/account/rib";
    static final String API_SEARCH= "api/pdf/search";
    static final String API_ADVISOR_USER = "api/advisor/user";
    static final String API_VERIFY = "api/verify";
    static final String API_DOWNLOAD_FILE = "api/downloadFile";
    static final String API_MESSAGE = "api/messages";

    static final String HEADER_AUTHORIZATION = "Authorization";
    static final String HEADER_AUTHORIZATION_BEARER = "Bearer ";
    

    /**
     * Method to check if functionnality is still working
     */
    public Boolean checkFunctionalTests(HttpServletRequest request);

    /**
     * Method to check if vulnerabilities are fixed
     */
    public List<RemediationResponse> checkSecurityTests(HttpServletRequest request);

    /**
     * Method to check if a vulnerability payload
     */
    public RemediationResponse checkVulnerabiliyPayload(HttpServletRequest request, VulnerabilitiesDto vulnDto, PayloadDto payload);
}
