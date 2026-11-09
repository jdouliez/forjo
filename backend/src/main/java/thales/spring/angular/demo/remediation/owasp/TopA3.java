package thales.spring.angular.demo.remediation.owasp;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.ContentType;
import org.springframework.stereotype.Component;

import thales.spring.angular.demo.domain.Account;
import thales.spring.angular.demo.domain.UserApp;
import thales.spring.angular.demo.remediation.dto.CheckResult;
import thales.spring.angular.demo.util.I18n;

@Component
public class TopA3 extends TopOwaspGeneric {

    public TopA3() {
        super(I18n.getMessage("msg.A3.name"));
    }

    @Override
    public Boolean checkFunctionalTests(HttpServletRequest request) {

        try {
            return checkAccountFunctionalTest(request) && checkRibDocumentFunctionalTest(request);
        }
        catch (Exception e) {
            LOGGER.error("Technical error", e);
            return Boolean.FALSE;
        }
    }

    private Boolean checkAccountFunctionalTest(HttpServletRequest request) throws ClientProtocolException, IOException, Exception {

        Boolean success = Boolean.TRUE;

        // Check that /account page respond and accounts data are present
        CheckResult result = checkSingleFunctionalTestWithGetMethod(request, API_ACCOUNT, Arrays.asList("Livret Boosté", "Livret A"), ContentType.APPLICATION_JSON);
        success &= result.getSuccess();

        // Check that /account/XXXX page respond and account data are present
        UserApp user = userService.findAll().stream().filter(c -> c.getEmail().equals(USER_EMAIL)).collect(Collectors.toList()).get(0); 
        List<Account> accounts = accountService.getAccountByClientId(String.valueOf(user.getClient().getIdClient()));
        CheckResult result2 = checkSingleFunctionalTestWithGetMethod(request, API_ACCOUNT + "/" + String.valueOf(accounts.get(0).getIdAccount()), Arrays.asList(accounts.get(0).getType(), String.valueOf(accounts.get(0).getBalance()), accounts.get(0).getDocument()), ContentType.APPLICATION_JSON);
        success &= result2.getSuccess();

        return success;
    }

    private Boolean checkRibDocumentFunctionalTest(HttpServletRequest request) throws ClientProtocolException, IOException, Exception {

        // Check that /account/pdf/search/XXXX page respond and document data are present
        UserApp user = userService.findAll().stream().filter(c -> c.getEmail().equals(USER_EMAIL)).collect(Collectors.toList()).get(0); 
        List<Account> accounts = accountService.getAccountByClientId(String.valueOf(user.getClient().getIdClient()));
        CheckResult result = checkSingleFunctionalTestWithGetMethod(request, API_SEARCH + "/" + accounts.get(0).getDocument().replace(".pdf", ""), Arrays.asList(accounts.get(0).getDocument()), ContentType.APPLICATION_JSON);
        return result.getSuccess();
    }
}
