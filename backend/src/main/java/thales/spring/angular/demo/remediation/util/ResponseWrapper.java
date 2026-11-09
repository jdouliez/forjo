package thales.spring.angular.demo.remediation.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;

public class ResponseWrapper {
	
	private static final Map<OwaspCheckEnum, String> bodys;

	static {
		bodys = new HashMap<OwaspCheckEnum,String>();
        OwaspCheckEnum.stream().forEach(owaspCheck -> bodys.put(owaspCheck, Strings.EMPTY));
    }
	
	static void replaceBody(OwaspCheckEnum owaspValue, String body) {	
		bodys.replace(owaspValue, body);
	}
	
	public static String getBody(OwaspCheckEnum owaspValue) {
		if (owaspValue == null) 
			return null;
		else
			return bodys.get(owaspValue);
	}
}
