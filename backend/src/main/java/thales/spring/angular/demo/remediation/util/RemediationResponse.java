package thales.spring.angular.demo.remediation.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * Response for controller rest resquest
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class RemediationResponse {
	
	String name;
	String payload;
	String url;
	Boolean success;
	String comments;
	String body;
	Boolean isPayloadHtml;
	
	@Builder.Default
	TestsType testsType = TestsType.SECURITY;

	final static Logger LOGGER = LoggerFactory.getLogger(LoggingRequestInterceptor.class);

	@Override
	public String toString() {
		try {
			return new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
		} 
		catch (com.fasterxml.jackson.core.JsonProcessingException e) {
			LOGGER.error("Error", e);
			return "";
		}
	}
}
