package thales.spring.angular.demo.remediation.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import thales.spring.angular.demo.remediation.domain.OwaspModuleEnum;
import thales.spring.angular.demo.remediation.dto.OwaspPayload;
import thales.spring.angular.demo.remediation.exception.OwaspException;
import thales.spring.angular.demo.remediation.owasp.TopA1;
import thales.spring.angular.demo.remediation.owasp.TopA10;
import thales.spring.angular.demo.remediation.owasp.TopA2;
import thales.spring.angular.demo.remediation.owasp.TopA3;
import thales.spring.angular.demo.remediation.owasp.TopA4;
import thales.spring.angular.demo.remediation.owasp.TopA5;
import thales.spring.angular.demo.remediation.owasp.TopA6;
import thales.spring.angular.demo.remediation.owasp.TopA7;
import thales.spring.angular.demo.remediation.owasp.TopA8;
import thales.spring.angular.demo.remediation.owasp.TopA9;
import thales.spring.angular.demo.remediation.owasp.TopOwaspGeneric;
import thales.spring.angular.demo.util.I18n;

/**
 * Class use for check if topOwasp remediation is correct or not
 */
@Service
public class RemediationChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemediationChecker.class);
    private Map<OwaspModuleEnum, TopOwaspGeneric> topOwaspClasses;

    @Value("${frontend.hostname}")
    public String frontendHostname;
    
    @Autowired
    public TopA1 topA1;

    @Autowired
    public TopA2 topA2;

    @Autowired
    public TopA3 topA3;

    @Autowired
    public TopA4 topA4;

    @Autowired
    public TopA5 topA5;

    @Autowired
    public TopA6 topA6;

    @Autowired
    public TopA7 topA7;

    @Autowired
    public TopA8 topA8;

    @Autowired
    public TopA9 topA9;

    @Autowired
    public TopA10 topA10;

    @PostConstruct
    public void init(){

        this.topOwaspClasses = new HashMap<OwaspModuleEnum, TopOwaspGeneric>();
        
        topOwaspClasses.put(OwaspModuleEnum.A01, topA1);      
        topOwaspClasses.put(OwaspModuleEnum.A02, topA2);  
        topOwaspClasses.put(OwaspModuleEnum.A03, topA3);      
        topOwaspClasses.put(OwaspModuleEnum.A04, topA4);      
        topOwaspClasses.put(OwaspModuleEnum.A05, topA5);      
        topOwaspClasses.put(OwaspModuleEnum.A06, topA6);      
        topOwaspClasses.put(OwaspModuleEnum.A07, topA7);      
        topOwaspClasses.put(OwaspModuleEnum.A08, topA8);      
        topOwaspClasses.put(OwaspModuleEnum.A09, topA9);      
        topOwaspClasses.put(OwaspModuleEnum.A10, topA10);      

        this.injectVulnerabilitiesPayloads();
    }

    /**
     * This method is used to define all vulnerabilities payloads to test.
     * Payload are define in "/json/payloads.json"
     */
    private void injectVulnerabilitiesPayloads() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<OwaspPayload>> typeReference = new TypeReference<List<OwaspPayload>>(){};
            InputStream inputStream = TypeReference.class.getResourceAsStream("/json/payloads.json");
            List<OwaspPayload> payloads = mapper.readValue(inputStream, typeReference);
            
            for (OwaspPayload payload : payloads) {
                String categoryName = payload.getCategory();

                LOGGER.info("Adding " + String.valueOf(payload.getVulnerabilities().size())+ " test(s) to " + categoryName + " category.");
               
                if (topOwaspClasses.containsKey(OwaspModuleEnum.valueOf(categoryName))) {
                    topOwaspClasses.get(OwaspModuleEnum.valueOf(categoryName)).setVulnerabilities(payload.getVulnerabilities());
                }
                else {
                    throw new OwaspException(I18n.getMessage("msg.owasp.notfound", categoryName));
                }
            }
        } 
        catch (IOException e) {
            LOGGER.error("Error", e);
        }
        catch (OwaspException e) {
            throw e;
        }
    }

    public ResponseEntity<?> runFunctionalTests(OwaspModuleEnum topOwasp, HttpServletRequest request) throws OwaspException {
       
        TopOwaspGeneric topOwaspClass = getTopOwaspClass(topOwasp);
        topOwaspClass.updateJwtTokenIfneeded();
        Boolean testResults = topOwaspClass.checkFunctionalTests(request);
        return new ResponseEntity<>(testResults, HttpStatus.OK);
    }

    public ResponseEntity<?> runSecurityTests(OwaspModuleEnum topOwasp, HttpServletRequest request) throws OwaspException {
       
        TopOwaspGeneric topOwaspClass = getTopOwaspClass(topOwasp);
        topOwaspClass.updateJwtTokenIfneeded();
       
        if (topOwaspClass.checkFunctionalTests(request)) {
            List<RemediationResponse> remediationList = topOwaspClass.checkSecurityTests(request);
            return new ResponseEntity<>(remediationList, HttpStatus.OK);
        } 
        else {
            return new ResponseEntity<>("NOT FUNCTIONAL", HttpStatus.OK);
        }
    }

    private TopOwaspGeneric getTopOwaspClass(OwaspModuleEnum topOwasp) throws OwaspException {

        if (topOwaspClasses.containsKey(topOwasp)) {
            return this.topOwaspClasses.get(topOwasp);
        }
        else {
            throw new OwaspException(I18n.getMessage("msg.owasp.notfound", topOwasp));
        }
    }
}
