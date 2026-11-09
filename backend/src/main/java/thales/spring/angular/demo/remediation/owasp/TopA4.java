package thales.spring.angular.demo.remediation.owasp;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.entity.ContentType;
import org.springframework.stereotype.Component;

import thales.spring.angular.demo.remediation.dto.CheckResult;
import thales.spring.angular.demo.util.I18n;

@Component
public class TopA4 extends TopOwaspGeneric {

    public TopA4() {
        super(I18n.getMessage("msg.A4.name"));
    }

    @Override
    public Boolean checkFunctionalTests(HttpServletRequest request) {

        try {
            return checkAccountCreation(request);
        } 
        catch (Exception e) {
            LOGGER.error("Technical error", e);
            return Boolean.FALSE;
        }
    }

    private Boolean checkAccountCreation(HttpServletRequest request) throws Exception {

        if (accountService.isExist(88788L))
			accountService.deleteById(88788L);
            
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + 
                     "<account>" + 
                     "    <idAccount>88788</idAccount>" + 
                     "    <type>Compte courant</type>" + 
                     "    <balance>400</balance>" + 
                     "    <rate>5</rate>" + 
                     "    <document>88788.pdf</document>" + 
                     "</account>";

        // Check that /account page respond and accounts data are present
        CheckResult result = checkSingleFunctionalTestWithPostMethod(request, API_ACCOUNT, xml, ContentType.APPLICATION_XML, Arrays.asList("88788", "Compte courant"), ContentType.APPLICATION_JSON);
        return result.getSuccess();
    }
}
