package thales.spring.angular.demo.remediation.owasp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.springframework.stereotype.Component;

import thales.spring.angular.demo.domain.Account;
import thales.spring.angular.demo.domain.Advisor;
import thales.spring.angular.demo.domain.Message;
import thales.spring.angular.demo.domain.UserApp;
import thales.spring.angular.demo.remediation.dto.CheckResult;
import thales.spring.angular.demo.remediation.util.RemediationResponse;
import thales.spring.angular.demo.remediation.util.TestsType;
import thales.spring.angular.demo.util.I18n;

@Component
public class TopA1 extends TopOwaspGeneric {

    public TopA1() {
        super(I18n.getMessage("msg.A1.name"));
    }

    @Override
    public Boolean checkFunctionalTests(HttpServletRequest request) {

        try {
            return checkIfDownloadRibFileIsPossible(request)
                && checkIfAccessAccountsIsPossible(request)
                && checkIfAccessAdvisorsIsPossible(request);
        } catch (Exception e) {
            LOGGER.error("Technical error", e);
            return Boolean.FALSE;
        }
    }

    private boolean checkIfAccessAccountsIsPossible(HttpServletRequest request) throws Exception {
        CheckResult result = checkSingleFunctionalTestWithGetMethod(request, API_ACCOUNT, Arrays.asList("idAccount", "type", "rate", "document"), ContentType.APPLICATION_JSON);
        return result.getSuccess();
    }

    private boolean checkIfAccessAdvisorsIsPossible(HttpServletRequest request) throws Exception {
        CheckResult result = checkSingleFunctionalTestWithGetMethod(request, API_ADVISOR, Arrays.asList("firstnameAdvisor", "lastnameAdvisor", "clients", "accounts"), ContentType.APPLICATION_JSON);
        return result.getSuccess();
    }

    private Boolean checkIfDownloadRibFileIsPossible(HttpServletRequest request) throws Exception {

        String url = buildFrontEndPath(request, API_ACCOUNT_RIB);
        HttpResponse response = simpleGetRequest(url);
        String filename = EntityUtils.toString(response.getEntity(), "UTF-8");

        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("filename", filename));

        CheckResult result = checkSingleFunctionalTestWithPostMethod(request, API_DOWNLOAD_FILE, params, ContentType.APPLICATION_FORM_URLENCODED, null, ContentType.create("application/pdf", Consts.UTF_8));
        return result.getSuccess();
    }

    @Override
    public List<RemediationResponse> checkSecurityTests(HttpServletRequest request) {

        List<RemediationResponse> responses = new ArrayList<RemediationResponse>();

        responses.add(checkIfDownloadRibPathTraversalExist(request));
        responses.add(checkIfDownloadRibPathTraversalUrlEncodedExist(request));
        responses.add(checkIfDownloadRibPathTraversalReplaceAllExist(request));

        responses.add(checkIfAllAccountsAreAccessible(request));
        responses.add(checkIfAllAdvisorsAreAccessible(request));
        responses.add(checkIfAllUsersAreAccessible(request));
        responses.add(checkIfAdminPanelIsAccessible(request));

        responses.add(checkIfAnotherUserAccountIsAccessible(request));
        responses.add(checkIfAnotherAdvisorDataAreAccessible(request));
        responses.add(checkIfAnotherMessageIsAccessible(request));

        responses.add(checkIfSecretDataIsAccessibleForAllUsers(request));
        responses.add(checkIfSecretDataIsAccessibleForAnotherUser(request));
        
        return responses;
    }

    private RemediationResponse checkIfSecretDataIsAccessibleForAllUsers(HttpServletRequest request) {
        Boolean success = Boolean.TRUE;
        CheckResult result;
        String responseBody = null;
        
        try {
            result = checkSingleFunctionalTestWithGetMethod(request, API_USER, Arrays.asList("secretQuestion", "secretAnswer"), ContentType.APPLICATION_JSON);
            responseBody = result.getReponseBody();
            success = !result.getSuccess();
        } 
        catch (Exception e) {
            LOGGER.error("Technical error", e);
            success = Boolean.FALSE;
        }

        return RemediationResponse.builder()
            .name(I18n.getMessage("msg.A5.10.name"))
            .payload(I18n.getMessage("msg.A5.10.payload"))
            .url(buildFrontEndPath(request, API_USER))
            .success(success)
            .comments(null)
            .body(responseBody)
            .testsType(TestsType.SECURITY)
            .isPayloadHtml(Boolean.TRUE)
            .build(); 
    }

    private RemediationResponse checkIfSecretDataIsAccessibleForAnotherUser(HttpServletRequest request) {
        Boolean success = Boolean.TRUE;
        CheckResult result;
        String responseBody = null;
        
        UserApp anotherUser = userService.findAll().stream().filter(user -> user.getEmail() != USER_EMAIL).findFirst().get();

        try {
            result = checkSingleFunctionalTestWithGetMethod(request, API_USER + "/" + anotherUser.getClient().getIdClient(), Arrays.asList("secretQuestion", "secretAnswer"), ContentType.APPLICATION_JSON);
            responseBody = result.getReponseBody();
            success = !result.getSuccess();
        } 
        catch (Exception e) {
            LOGGER.error("Technical error", e);
            success = Boolean.FALSE;
        }

        return RemediationResponse.builder()
            .name(I18n.getMessage("msg.A5.10.name"))
            .payload(I18n.getMessage("msg.A5.10.payload"))
            .url(buildFrontEndPath(request, API_USER + "/" + anotherUser.getClient().getIdClient()))
            .success(success)
            .comments(null)
            .body(responseBody)
            .testsType(TestsType.SECURITY)
            .isPayloadHtml(Boolean.TRUE)
            .build(); 
    }

    private RemediationResponse checkIfAnotherMessageIsAccessible(HttpServletRequest request) {
        Boolean success = Boolean.TRUE;
        CheckResult result;
        String responseBody = null;

        Message otherUsersMessage = messageService.update(Message.builder().content("Testing remediations").from(99L).to(100L).subject("Crossing fingers...").read(Boolean.FALSE).build());
        Long otherUsersMessagesId = otherUsersMessage.getIdMessage();
        
        try {
            result = checkSingleFunctionalTestWithGetMethod(request, API_MESSAGE + "/" + otherUsersMessagesId, Arrays.asList("from", "status", "\"idMessage\":" + otherUsersMessagesId), ContentType.APPLICATION_JSON);
            responseBody = result.getReponseBody();
            success = !result.getSuccess();
        } 
        catch (Exception e) {
            LOGGER.error("Technical error", e);
            success = Boolean.FALSE;
        }

        return RemediationResponse.builder()
            .name(I18n.getMessage("msg.A5.9.name"))
            .payload(I18n.getMessage("msg.A5.9.payload"))
            .url(buildFrontEndPath(request, API_MESSAGE + "/" + otherUsersMessagesId))
            .success(success)
            .comments(null)
            .body(responseBody)
            .testsType(TestsType.SECURITY)
            .isPayloadHtml(Boolean.TRUE)
            .build();
    }

    private RemediationResponse checkIfAnotherAdvisorDataAreAccessible(HttpServletRequest request) {
        Boolean success = Boolean.TRUE;
        CheckResult result;
        String responseBody = null;

        UserApp currentUser = userService.findByEmail(USER_EMAIL);
        Advisor anotherAdvisor = advisorService.findAll().stream().filter(a -> a.getIdAdvisor() != currentUser.getClient().getAdvisor().getIdAdvisor()).findAny().get();
        Long anotherAdvisorId = anotherAdvisor.getIdAdvisor();
        
        try {
            result = checkSingleFunctionalTestWithGetMethod(request, API_ADVISOR + "/" + anotherAdvisorId, Arrays.asList("clients", "office", "\"idAdvisor\":" + anotherAdvisorId), ContentType.APPLICATION_JSON);
            responseBody = result.getReponseBody();
            success = !result.getSuccess();
        } 
        catch (Exception e) {
            LOGGER.error("Technical error", e);
            success = Boolean.FALSE;
        }

        return RemediationResponse.builder()
            .name(I18n.getMessage("msg.A5.8.name"))
            .payload(I18n.getMessage("msg.A5.8.payload"))
            .url(buildFrontEndPath(request, API_ACCOUNT + "/" + anotherAdvisorId))
            .success(success)
            .comments(null)
            .body(responseBody)
            .testsType(TestsType.SECURITY)
            .isPayloadHtml(Boolean.TRUE)
            .build();
    }

    private RemediationResponse checkIfAnotherUserAccountIsAccessible(HttpServletRequest request) {
        Boolean success = Boolean.TRUE;
        CheckResult result;
        String responseBody = null;

        UserApp anotherUser = userService.findAll().stream().filter(user -> user.getEmail() != USER_EMAIL).findAny().get();
        List<Account> accountsFromDB = accountService.getAccountByClientId(String.valueOf(anotherUser.getClient().getIdClient()));
        Long anotherIdAccount = accountsFromDB.get(0).getIdAccount();
        
        try {
            result = checkSingleFunctionalTestWithGetMethod(request, API_ACCOUNT + "/" + anotherIdAccount, Arrays.asList("balance", "document", "idAccount:" + anotherIdAccount), ContentType.APPLICATION_JSON);
            responseBody = result.getReponseBody();
            success = !result.getSuccess();
        } 
        catch (Exception e) {
            LOGGER.error("Technical error", e);
            success = Boolean.FALSE;
        }

        return RemediationResponse.builder()
            .name(I18n.getMessage("msg.A5.7.name"))
            .payload(I18n.getMessage("msg.A5.7.payload"))
            .url(buildFrontEndPath(request, API_ACCOUNT + "/" + anotherIdAccount))
            .success(success)
            .comments(null)
            .body(responseBody)
            .testsType(TestsType.SECURITY)
            .isPayloadHtml(Boolean.TRUE)
            .build();
    }

    private RemediationResponse checkIfAdminPanelIsAccessible(HttpServletRequest request) {
        Boolean success = Boolean.TRUE;
        CheckResult result;
        String responseBody = null;
        
        try {
            result = checkSingleFunctionalTestWithGetMethod(request, API_ADMIN, new ArrayList<>(), ContentType.TEXT_PLAIN);
            responseBody = result.getReponseBody();   
            success = !result.getSuccess();         
        } 
        catch (Exception e) {
            LOGGER.error("Technical error", e);
            success = Boolean.FALSE;
        }

        return RemediationResponse.builder()
            .name(I18n.getMessage("msg.A5.6.name"))
            .payload(I18n.getMessage("msg.A5.6.payload"))
            .url(buildFrontEndPath(request, API_ADMIN))
            .success(success)
            .comments(null)
            .body(responseBody)
            .testsType(TestsType.SECURITY)
            .isPayloadHtml(Boolean.TRUE)
            .build(); 
    }

    private RemediationResponse checkIfAllUsersAreAccessible(HttpServletRequest request) {
        Boolean success = Boolean.TRUE;
        CheckResult result;
        String responseBody = null;
        
        try {
            result = checkSingleFunctionalTestWithGetMethod(request, API_USER, new ArrayList<>(), ContentType.APPLICATION_JSON);
            responseBody = result.getReponseBody();
            JSONArray users = new JSONArray(responseBody);

            if (users.length() > 1) {
                success = Boolean.FALSE;
            }
        } 
        catch (Exception e) {
            LOGGER.error("Technical error", e);
            success = Boolean.FALSE;
        }

        return RemediationResponse.builder()
            .name(I18n.getMessage("msg.A5.5.name"))
            .payload(I18n.getMessage("msg.A5.5.payload"))
            .url(buildFrontEndPath(request, API_USER))
            .success(success)
            .comments(null)
            .body(responseBody)
            .testsType(TestsType.SECURITY)
            .isPayloadHtml(Boolean.TRUE)
            .build(); 
    }

    private RemediationResponse checkIfDownloadRibPathTraversalExist(HttpServletRequest request) {

        String payload = "../../../../../../etc/passwd";

        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("filename", payload));
        CheckResult result = checkSingleFunctionalTestWithPostMethod(request, API_DOWNLOAD_FILE, params, ContentType.APPLICATION_FORM_URLENCODED, Arrays.asList("root:x:0:0:root:/root:/bin/bash"), ContentType.DEFAULT_TEXT);
        String comment = !result.getSuccess() ? I18n.getMessage("msg.A5.2.comment_ok") : I18n.getMessage("msg.A5.2.comment_ko");
       
        return RemediationResponse.builder()
            .name(I18n.getMessage("msg.A5.2.name"))
            .payload(payload)
            .url(buildFrontEndPath(request, API_DOWNLOAD_FILE) + " (POST)")
            .success(!result.getSuccess())
            .comments(comment)
            .body(result.getReponseBody())
            .testsType(TestsType.SECURITY)
            .isPayloadHtml(Boolean.TRUE)
            .build(); 
    }

    private RemediationResponse checkIfDownloadRibPathTraversalUrlEncodedExist(HttpServletRequest request) {

        String payload = "..%2F..%2F..%2F..%2F..%2F..%2Fetc%2Fpasswd";

        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("filename", payload));
        CheckResult result = checkSingleFunctionalTestWithPostMethod(request, API_DOWNLOAD_FILE, params, ContentType.APPLICATION_FORM_URLENCODED, Arrays.asList("root:x:0:0:root:/root:/bin/bash"), ContentType.DEFAULT_TEXT);
        String comment = !result.getSuccess() ? I18n.getMessage("msg.A5.2.comment_ok") : I18n.getMessage("msg.A5.2.comment_ko");
       
        return RemediationResponse.builder()
            .name(I18n.getMessage("msg.A5.2.name"))
            .payload(payload)
            .url(buildFrontEndPath(request, API_DOWNLOAD_FILE) + " (POST)")
            .success(!result.getSuccess())
            .comments(comment)
            .body(result.getReponseBody())
            .testsType(TestsType.SECURITY)
            .isPayloadHtml(Boolean.TRUE)
            .build(); 
    }

    private RemediationResponse checkIfDownloadRibPathTraversalReplaceAllExist(HttpServletRequest request) {

        String payload = "..././..././..././..././..././..././etc/passwd";

        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("filename", payload));
        CheckResult result = checkSingleFunctionalTestWithPostMethod(request, API_DOWNLOAD_FILE, params, ContentType.APPLICATION_FORM_URLENCODED, Arrays.asList("root:x:0:0:root:/root:/bin/bash"), ContentType.DEFAULT_TEXT);
        String comment = !result.getSuccess() ? I18n.getMessage("msg.A5.2.comment_ok") : I18n.getMessage("msg.A5.2.comment_ko");
       
        return RemediationResponse.builder()
            .name(I18n.getMessage("msg.A5.2.name"))
            .payload(payload)
            .url(buildFrontEndPath(request, API_DOWNLOAD_FILE) + " (POST)")
            .success(!result.getSuccess())
            .comments(comment)
            .body(result.getReponseBody())
            .testsType(TestsType.SECURITY)
            .isPayloadHtml(Boolean.TRUE)
            .build(); 
    }

    private RemediationResponse checkIfAllAccountsAreAccessible(HttpServletRequest request) {
        
        Boolean success = Boolean.TRUE;
        CheckResult result;
        String responseBody = null;

        UserApp user = userService.findByEmail(USER_EMAIL);
        List<Account> accountsFromDB = accountService.getAccountByClientId(String.valueOf(user.getClient().getIdClient()));
        
        try {
            result = checkSingleFunctionalTestWithGetMethod(request, API_ACCOUNT, new ArrayList<>(), ContentType.APPLICATION_JSON);
            responseBody = result.getReponseBody();
            JSONArray accounts = new JSONArray(responseBody);

            if (accounts.length() > accountsFromDB.size()) {
                success = Boolean.FALSE;
            }
        } 
        catch (Exception e) {
            LOGGER.error("Technical error", e);
            success = Boolean.FALSE;
        }

        return RemediationResponse.builder()
            .name(I18n.getMessage("msg.A5.4.name"))
            .payload(I18n.getMessage("msg.A5.4.payload", accountsFromDB.size()))
            .url(buildFrontEndPath(request, API_ACCOUNT))
            .success(success)
            .comments(null)
            .body(responseBody)
            .testsType(TestsType.SECURITY)
            .isPayloadHtml(Boolean.TRUE)
            .build(); 
    }

    private RemediationResponse checkIfAllAdvisorsAreAccessible(HttpServletRequest request) {
        Boolean success = Boolean.TRUE;
        CheckResult result;
        String responseBody = null;
        
        try {
            result = checkSingleFunctionalTestWithGetMethod(request, API_ADVISOR, new ArrayList<>(), ContentType.APPLICATION_JSON);
            responseBody = result.getReponseBody();
            JSONArray advisors = new JSONArray(responseBody);

            if (advisors.length() > 1) {
                success = Boolean.FALSE;
            }
        } 
        catch (Exception e) {
            LOGGER.error("Technical error", e);
            success = Boolean.FALSE;
        }

        return RemediationResponse.builder()
            .name(I18n.getMessage("msg.A5.3.name"))
            .payload(I18n.getMessage("msg.A5.3.payload"))
            .url(buildFrontEndPath(request, API_ADVISOR))
            .success(success)
            .comments(null)
            .body(responseBody)
            .testsType(TestsType.SECURITY)
            .isPayloadHtml(Boolean.TRUE)
            .build(); 
    }
}
