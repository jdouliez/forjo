package thales.spring.angular.demo.remediation.owasp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import thales.spring.angular.demo.domain.UserApp;
import thales.spring.angular.demo.remediation.dto.CheckResult;
import thales.spring.angular.demo.remediation.util.RemediationResponse;
import thales.spring.angular.demo.remediation.util.TestsType;
import thales.spring.angular.demo.util.I18n;

@Component
public class TopA2 extends TopOwaspGeneric {

    public TopA2() {
        super(I18n.getMessage("msg.A2.name"));
    }

    @Override
    public Boolean checkFunctionalTests(HttpServletRequest request) {

        try {
            return checkIfUserOwnAdvisorInfoIsReachable(request);
        } catch (Exception e) {
            LOGGER.error("Technical error", e);
            return Boolean.FALSE;
        }
    }


    private RemediationResponse checkIfAdvisorPersonalInfoAreLeaked(HttpServletRequest request) {
       
        Boolean success = Boolean.TRUE;
        String comment;
       
        String url = buildFrontEndPath(request, API_ADVISOR_USER);
        HttpResponse response = simpleGetRequest(url);

        if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
            success = Boolean.FALSE;
            comment = I18n.getMessage("err.func.broken");
        }
        else {
            String advisorId;
            
            try {
                advisorId = EntityUtils.toString(response.getEntity(), "UTF-8");
                CheckResult result = checkSingleFunctionalTestWithGetMethod(request, API_ADVISOR + "/" + advisorId, Arrays.asList("SocialSecurityNumbers"), ContentType.APPLICATION_JSON);
                comment = !result.getSuccess() ? I18n.getMessage("msg.A3.step3.comment_ok") : I18n.getMessage("msg.A3.step3.comment_ko");
                success = !result.getSuccess();
            } 
            catch (Exception e) {
                LOGGER.error(I18n.getMessage("err.func.broken"), e);
                comment = I18n.getMessage("err.func.broken");
                success = Boolean.FALSE;
            }
        }

        return RemediationResponse.builder()
            .name(I18n.getMessage("msg.A3.step3.name"))
            .payload(I18n.getMessage("msg.A3.step3.payload"))
            .url("http://frontend:4200/advisor/{advisor id}")
            .success(success)
            .comments(comment)
            .body(null)
            .testsType(TestsType.SECURITY)
            .isPayloadHtml(Boolean.TRUE)
            .build(); 
    }

    private Boolean checkIfUserOwnAdvisorInfoIsReachable(HttpServletRequest request) throws Exception {

        String url = buildFrontEndPath(request, API_ADVISOR_USER);
        HttpResponse response = simpleGetRequest(url);

        if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
            return Boolean.FALSE;
        }
        else {
            String advisorId = EntityUtils.toString(response.getEntity(), "UTF-8");
            CheckResult result = checkSingleFunctionalTestWithGetMethod(request, API_ADVISOR + "/" + advisorId, Arrays.asList("\"idAdvisor\":" + advisorId + ",\"office\":"), ContentType.APPLICATION_JSON);
            return result.getSuccess();
        }
    }

    @Override
    public List<RemediationResponse> checkSecurityTests(HttpServletRequest request) {

        List<RemediationResponse> responses = new ArrayList<RemediationResponse>();

        responses.add(checkIfPasswordAreHashed(request));
        responses.add(checkIfJwtSecretIsStrong(request));
        responses.add(checkIfAdvisorPersonalInfoAreLeaked(request));

        return responses;
    }


    private RemediationResponse checkIfJwtSecretIsStrong(HttpServletRequest request) {

        Boolean success = Boolean.TRUE;
        String comment;
        String body = null;
        String bruteforceOutput = null;

        String wordlistFilePath;
        String jwttoolPath;

        try {
            wordlistFilePath = ResourceUtils.getFile("classpath:jwt_tool/common.txt").getCanonicalPath();
            jwttoolPath = ResourceUtils.getFile("classpath:jwt_tool/jwt_tool.py").getCanonicalPath();
        }
        catch (IOException e) {
            LOGGER.error("Ressource file missing", e);
            return null;
        }

        String token = jwtAuthenticationService.generateToken(USER_EMAIL);

        try {
            String isCorrectKeyPattern = "is the CORRECT key!";
            bruteforceOutput = execCommand(String.format("python3 %s %s --crack -d %s | ansi2txt | grep --color=never \"%s\"", jwttoolPath, token, wordlistFilePath, isCorrectKeyPattern));
            success = !bruteforceOutput.contains(isCorrectKeyPattern);
            comment = success ? I18n.getMessage("msg.A3.step2.payload_ok") : I18n.getMessage("msg.A3.step2.payload_ko");
        } 
        catch (Exception e) {
            LOGGER.error("Technical error", e);
            comment = I18n.getMessage("err.jwtTool.ko");
            success = Boolean.FALSE;
        }

        return RemediationResponse.builder()
                .name(I18n.getMessage("msg.A3.step2.name"))
                .payload(String.format("python3 %s %s --crack -d %s \n\n[...]\n%s\n[...]", jwttoolPath.split("/")[5], "<token>", wordlistFilePath.split("/")[5], bruteforceOutput))
                .url(null)
                .success(success)
                .comments(comment)
                .body(body)
                .testsType(TestsType.SECURITY)
                .isPayloadHtml(Boolean.FALSE)
                .build();
    }

    private RemediationResponse checkIfPasswordAreHashed(HttpServletRequest request) {

        Boolean success = Boolean.TRUE;
        String payload;

        UserApp newUser = createAndFindUser(request, userWithStrongPasswordDto);

        if (newUser == null) {
            payload = I18n.getMessage("err.registration.ko");
            success = Boolean.FALSE;
        } 
        else {
            if (newUser.getPassword().equals(userWithStrongPasswordDto.getPassword())) {
                payload = I18n.getMessage("msg.A3.step1.payload_ko");
                success = Boolean.FALSE;
            }
            else {
                payload = I18n.getMessage("msg.A3.step1.payload_ok");
                success = Boolean.FALSE;
            }
        }

        return RemediationResponse.builder()
                .name(I18n.getMessage("msg.A3.step1.name"))
                .payload(payload)
                .url(null)
                .success(success)
                .comments(I18n.getMessage("msg.A3.step1.comment"))
                .body(null)
                .testsType(TestsType.SECURITY)
                .isPayloadHtml(Boolean.TRUE)
                .build();
    }
}
