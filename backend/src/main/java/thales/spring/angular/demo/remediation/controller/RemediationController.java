package thales.spring.angular.demo.remediation.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import thales.spring.angular.demo.SetUpProperties;
import thales.spring.angular.demo.remediation.domain.OwaspModuleEnum;
import thales.spring.angular.demo.remediation.util.RemediationChecker;

/**
 *
 * Remediation controller
 * 
 */
@RestController
@RequestMapping("/api/check")
@CrossOrigin(origins = "*")
public class RemediationController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("RemediationController");
    
	@Autowired
    private RemediationChecker checker;
    
	@Autowired
	SetUpProperties setup;
        
	@Value("classpath:rockyou.txt")
	Resource resourceFileRockYou;

	@Value("classpath:Jwt_Tool/common.txt")
	Resource resourceFileCommon;

	@Value("classpath:Jwt_Tool/jwt_tool.py")
	Resource resourceJwtTool;

	@Value("classpath:dependency-check-report.html")
	Resource resourceDependencycheck;
	
	@Value("${spring.datasource.username}")
	private String dataSourceUsername;
	
	@Value("${spring.datasource.password}")
	private String dataSourcePassword;
    
    @Autowired
    public RemediationController() {
        //this.checker = new RemediationChecker(builder, jwtTokenUtil);
    }

    /**
     * Check if vulnerability is always functional in app
     * @param topOwasp
     * @return 
     */
    @GetMapping(value = "/functional/{topOwasp}")
    public ResponseEntity<?> checkFunctional(@PathVariable OwaspModuleEnum topOwasp, HttpServletRequest request) {    	
        LOGGER.info("CHECKING FUNCTIONAL TESTS FOR TOP " + topOwasp);
        return this.checker.runFunctionalTests(topOwasp, request);
    }
    

	/**
     * Check the differents vulnerabilities belonging to a OWASP TOP 10 module
     * @param topOwasp
	 * @param request
     * @return 
     */
    @GetMapping(value = "/security/{topOwasp}")
    public ResponseEntity<?> checkRemediation(@PathVariable OwaspModuleEnum topOwasp, HttpServletRequest request) {
        LOGGER.info("CHECK SECURITY TESTS FOR TOP " + topOwasp);
        return this.checker.runSecurityTests(topOwasp, request);
    }
}
