package thales.spring.angular.demo.remediation.owasp;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.entity.ContentType;
import org.springframework.stereotype.Component;

import thales.spring.angular.demo.domain.UserApp;
import thales.spring.angular.demo.remediation.dto.CheckResult;
import thales.spring.angular.demo.remediation.util.RemediationResponse;
import thales.spring.angular.demo.remediation.util.TestsType;
import thales.spring.angular.demo.util.I18n;

@Component
public class TopA7 extends TopOwaspGeneric {

    public TopA7() {
        super(I18n.getMessage("msg.A7.name"));
    }

    @Override
    public Boolean checkFunctionalTests(HttpServletRequest request) {

        try {
            return checkIfLoginIsSuccessful(request, USER_EMAIL, USER_PASSWORD);
        } 
        catch (Exception e) {
            LOGGER.error("Technical error", e);
            return Boolean.FALSE;
        }
    }

    private Boolean checkIfLoginIsSuccessful(HttpServletRequest request, String email, String password) throws Exception {
        String json = "{\"username\":\"" + email + "\",\"password\":\"" + password + "\"}";
        CheckResult result = checkSingleFunctionalTestWithPostMethod(request, API_USER_LOGIN, json, ContentType.APPLICATION_JSON, Arrays.asList("\"token\":\"ey"), ContentType.APPLICATION_JSON);
        return result.getSuccess();
    }


    @Override
    public List<RemediationResponse> checkSecurityTests(HttpServletRequest request) {

        List<RemediationResponse> responses = new ArrayList<RemediationResponse>();

        responses.add(checkComplexPasswordForOldUsers());
        responses.add(checkRegistrationForNewUsersWithWeakPassword(request));
        responses.add(checkRegistrationForNewUsersWithStrongPassword(request));
        responses.add(checkLoginBruteForceAttack(request));

        return responses;
    }

    private RemediationResponse checkComplexPasswordForOldUsers() {

        List<UserApp> users = userService.findAll();
        Boolean result = !users.stream().anyMatch(x -> (x.isStrongPassword() == false));

        return RemediationResponse.builder()
                .name(I18n.getMessage("msg.A2.step1.name"))
                .payload(null)
                .url(null)
                .success(result)
                .comments(I18n.getMessage("err.password"))
                .body(null)
                .testsType(TestsType.SECURITY)
                .isPayloadHtml(Boolean.TRUE)
                .build();
    }

    private RemediationResponse checkRegistrationForNewUsersWithWeakPassword(HttpServletRequest request) {
        Boolean success;
        String comment;

        UserApp weakUser = createAndFindUser(request, userWithWeakPasswordDto);

        if (weakUser == null) {
            comment = I18n.getMessage("err.registration.ko");
            success = Boolean.TRUE;
        } 
        else {
            comment = I18n.getMessage("err.registration.ok");
            success = Boolean.FALSE;
        }

        return RemediationResponse.builder()
                .name(I18n.getMessage("msg.A2.step2.name"))
                .payload(I18n.getMessage("msg.A2.step2.payload"))
                .url(null)
                .success(success)
                .comments(comment)
                .body(null)
                .testsType(TestsType.SECURITY)
                .isPayloadHtml(Boolean.TRUE)
                .build();
    }

    private RemediationResponse checkRegistrationForNewUsersWithStrongPassword(HttpServletRequest request) {
        Boolean success = Boolean.FALSE;
        String comment;

        UserApp strongUser = createAndFindUser(request, userWithStrongPasswordDto);

        if (strongUser == null) {
            comment = I18n.getMessage("err.registration.ko");
            success = Boolean.FALSE;
        } else {
            comment = I18n.getMessage("err.registration.ok");
            success = Boolean.TRUE;
        }

        return RemediationResponse.builder()
                .name(I18n.getMessage("msg.A2.step3.name"))
                .payload(I18n.getMessage("msg.A2.step3.payload"))
                .url(null)
                .success(success)
                .comments(comment)
                .body(null)
                .testsType(TestsType.SECURITY)
                .isPayloadHtml(Boolean.TRUE)
                .build();
    }

    private RemediationResponse checkLoginBruteForceAttack(HttpServletRequest request) {

        int BRUTEFORCE_COUNT_TO_RUN = 10;
        Boolean success;
        String comments;

        // Prepare new user with fresh lock status && Check if account already exist and delete it
        UserApp userToCreate = userService.findByEmail(userToBruteForceDto.getEmail());
        if (userToCreate != null) {
            userService.deleteById(userToCreate.getIdUser());
        }

        try {
            userService.registerNewUserAccount(userToBruteForceDto);
        }
        catch (Exception e) {
            LOGGER.error("Error during user creation", e);
        }

        LocalDateTime ldt1 = LocalDateTime.now();

        try {
            for (int i = 0; i < BRUTEFORCE_COUNT_TO_RUN; i++) {
                checkIfLoginIsSuccessful(request, userToBruteForceDto.getEmail(), "wrongpassword");
                Thread.sleep(150);
            }
        }
        catch (Exception e) {
            success = Boolean.FALSE;
            comments = I18n.getMessage("msg.A2.step4.response6");
        }

        LocalDateTime ldt2 = LocalDateTime.now();
        long seconds = Duration.between(ldt1.toLocalTime(), ldt2.toLocalTime()).getSeconds();

        comments = I18n.getMessage("msg.A2.step4.response1", BRUTEFORCE_COUNT_TO_RUN, seconds);

        try {
            if (checkIfLoginIsSuccessful(request, userToBruteForceDto.getEmail(), userToBruteForceDto.getPassword())) {
                // Last attempt has succeeded after bruteforce
                success = Boolean.FALSE;
                comments += " " + I18n.getMessage("msg.A2.step4.response2");
            } else {
                // Login is not OK. Account should be blocked/locked! Hoping so..
                success = Boolean.FALSE;
                comments += " " + I18n.getMessage("msg.A2.step4.response3");
            }
        } 
        catch (Exception e) {
            success = Boolean.FALSE;
            comments = I18n.getMessage("msg.A2.step4.response6");
        }

        // Delete created account
        UserApp user = userService.findByEmail(userToBruteForceDto.getEmail());
        userService.deleteById(user.getIdUser());

        return RemediationResponse.builder()
                .name(I18n.getMessage("msg.A2.step4.name"))
                .payload(I18n.getMessage("msg.A2.step4.payload"))
                .url(null)
                .success(success)
                .comments(comments)
                .body(null)
                .testsType(TestsType.SECURITY)
                .isPayloadHtml(Boolean.TRUE)
                .build();
    }
}
