package thales.spring.angular.demo.remediation.owasp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import thales.spring.angular.demo.util.I18n;

@Component
public class TopA10 extends TopOwaspGeneric {

    public TopA10() {
        super(I18n.getMessage("msg.A10.name"));
    }

    @Override
    public Boolean checkFunctionalTests(HttpServletRequest request) {

        try {
            return true;
        } catch (Exception e) {
            LOGGER.error("Technical error", e);
            return Boolean.FALSE;
        }
    }
}
