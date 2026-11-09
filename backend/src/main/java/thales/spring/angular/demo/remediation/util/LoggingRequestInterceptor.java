package thales.spring.angular.demo.remediation.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    final static Logger LOGGER = LoggerFactory.getLogger(LoggingRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        traceRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        return traceResponse(request, response);
    }

    private void traceRequest(HttpRequest request, byte[] body) throws IOException {
    	
        LOGGER.debug("=========================== New Remediation Request ===========================");
        LOGGER.debug("URI         : {}", request.getURI());
        LOGGER.debug("Method      : {}", request.getMethod());
        LOGGER.debug("Headers     : {}", request.getHeaders() );
        LOGGER.debug("Request body: {}", new String(body, "UTF-8"));
        LOGGER.debug("================================= Request End =================================");
    }

    private ClientHttpResponse traceResponse(HttpRequest request, ClientHttpResponse response) throws UnsupportedEncodingException, IOException {
       
        final ClientHttpResponse responseWrapper = new BufferingClientHttpResponseWrapper(response);
        StringBuilder inputStringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(responseWrapper.getBody(), "UTF-8"));
        String line = bufferedReader.readLine();
        
        while (line != null) {
            inputStringBuilder.append(line);
            inputStringBuilder.append('\n');
            line = bufferedReader.readLine();
        }
        
        if (request.getHeaders().getFirst(OwaspCheckEnum.OWASP.name()) != null)
            ResponseWrapper.replaceBody(OwaspCheckEnum.valueOf(request.getHeaders().getFirst(OwaspCheckEnum.OWASP.name()).toString()), inputStringBuilder.toString());
      
        LOGGER.debug("=========================== New Remediation Response ===========================");
        LOGGER.debug("Status code    : {}", responseWrapper.getStatusCode());
        LOGGER.debug("Status text    : {}", responseWrapper.getStatusText());
        LOGGER.debug("Headers        : {}", responseWrapper.getHeaders());
        LOGGER.debug("Response body: {}", inputStringBuilder.toString());
        LOGGER.debug("================================= Response End =================================");
      
        return responseWrapper;
    }

}