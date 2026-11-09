package thales.spring.angular.demo.remediation.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import thales.spring.angular.demo.util.I18n;

@Getter
@AllArgsConstructor
public enum TestsType {
	
	FUNCTIONAL(I18n.getMessage("enum.test.functional")),
	SECURITY(I18n.getMessage("enum.test.security"));
	
	public String name;
}
