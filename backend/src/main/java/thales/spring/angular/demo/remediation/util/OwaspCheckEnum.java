package thales.spring.angular.demo.remediation.util;

import java.util.stream.Stream;

public enum OwaspCheckEnum {
	OWASP,
	CHECK_A1,
	CHECK_A2,
	CHECK_A3,
	CHECK_A4,
	CHECK_A5,
	CHECK_A6,
	CHECK_A7,
	CHECK_A8,
	CHECK_A9,
	CHECK_A10;
	
	
	public static Stream<OwaspCheckEnum> stream() {
		return Stream.of(OwaspCheckEnum.values());
	}
	
	public static OwaspCheckEnum get(String value) {
		if (value == null) return null;
        return stream()
                .filter(x -> value.equals(x.toString()))
                .findAny()
                .orElse(null);
	}
}
