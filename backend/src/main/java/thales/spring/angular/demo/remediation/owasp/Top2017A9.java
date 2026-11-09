package thales.spring.angular.demo.remediation.owasp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.ResourceUtils;

import thales.spring.angular.demo.remediation.util.RemediationResponse;
import thales.spring.angular.demo.util.I18n;

/**
 * Class with functions to test TOP A1 - Injections
 */
public class Top2017A9 extends TopOwaspGeneric {
    
	private static final int vulnerableCount = 8;
    
	public Top2017A9() {
        super(I18n.getMessage("msg.A9.name"));
    }
    
    /**
     * Check if vulnerability is corrected or not
     * @return 
     */
    public List<RemediationResponse> checkOperationalTests(HttpServletRequest request) {
    	List<RemediationResponse> remediations = new ArrayList<RemediationResponse>();
    	remediations.add(checkDependency());
        return remediations;
    }
    
    /**
     * Check if vulnerability is always functional or not
     * @return 
     */
    public Boolean checkFunctionalTests(HttpServletRequest request) {
        return false;
    }
    
    public File loadDependencyCheckReport() throws FileNotFoundException {
    		    return ResourceUtils.getFile(
    		      "file:src/main/resources/dependency-check-report.html");
    		}

	
	/**
	 * Check dependency check report
	 * @return
	 */
	private RemediationResponse checkDependency() {
		String nameOfRemediation = I18n.getMessage("msg.A9.payload1.name");
		Boolean success = false;
	
		String comments = I18n.getMessage("msg.A9.payload1.step");
		String payload = "dependency-check-report.html";
		String url = "";
		String body = "";
		RemediationResponse remediation = new RemediationResponse();
		remediation.setName(nameOfRemediation);
		remediation.setPayload(payload);		
		remediation.setUrl(url);
		
		File dependencyCheckReport;
		String output;
		try {
			dependencyCheckReport = loadDependencyCheckReport();
			output = dependencyCheckReport.getAbsolutePath();
		} catch (FileNotFoundException e1) {
			output = execCommand("mvn org.owasp:dependency-check-maven:check");					
			execCommand("cp target/dependency-check-report.html src/main/resources");		
		}
		
		body = output;
		
		try {
			dependencyCheckReport = loadDependencyCheckReport();
			Document doc = Jsoup.parse(dependencyCheckReport, "UTF-8");
			body = doc.html();
			
			
			Elements elements = doc.select("span#vulnerableCount");
			for (Element e : elements) {									
					body = e.text();
					int parseVulnerableCount = Integer.parseInt(body);
					body = I18n.getMessage("msg.A9.payload1.vuln.dependencies", parseVulnerableCount);
					body += "<br>" + comments;
					int vulnerableCheck = vulnerableCount - parseVulnerableCount;
					if (vulnerableCheck > 0) {
						success = true;						
						comments = I18n.getMessage("msg.A9.payload1.success", vulnerableCheck, vulnerableCount);
					}
					else {
						comments = I18n.getMessage("msg.A9.payload1.fail");
					}
				
			}
		} catch (FileNotFoundException e) {
			comments= e.toString();
		} catch (IOException e) {
			comments= e.toString();
		}
		remediation.setComments(comments);
		remediation.setBody(body);
		remediation.setSuccess(success);
		return remediation;		
	}
}
