package thales.spring.angular.demo.remediation.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.springframework.core.io.Resource;

import lombok.Getter;
import thales.spring.angular.demo.SetUpProperties;
import thales.spring.angular.demo.security.jwt.JwtAuthenticationService;
import thales.spring.angular.demo.services.AccountService;
import thales.spring.angular.demo.services.AdvisorService;
import thales.spring.angular.demo.services.MessageService;
import thales.spring.angular.demo.services.UserService;

public final class StaticUtils {

	@Getter
	private static AccountService accountService;

	@Getter
	private static UserService userService;

	@Getter
	private static MessageService messageService;

	@Getter
	private static JwtAuthenticationService jwtAuthenticationService;

	@Getter
	private static AdvisorService advisorService;

	@Getter
	private static SetUpProperties setupProperties;

	@Getter
	private static Resource resourceFileRockYou;

	@Getter
	private static Resource resourceFileCommon;

	@Getter
	private static Resource resourceJwtTool;

	@Getter
	private static Resource resourceDependencycheck;

	@Getter
	private static String dataSourcePassword;

	@Getter
	private static String dataSourceUsername;

	public static void setAccountService(AccountService accountService) {
		StaticUtils.accountService = accountService;
	}

	private StaticUtils() {
	}

	public static void setUserService(UserService userService) {
		StaticUtils.userService = userService;
	}

	public static void setMessageService(MessageService messageService) {
		StaticUtils.messageService = messageService;
	}

	public static void setJwtAuthenticationService(JwtAuthenticationService jwtAuthenticationService) {
		StaticUtils.jwtAuthenticationService = jwtAuthenticationService;
	}

	public static void setAdvisorService(AdvisorService advisorService){ StaticUtils.advisorService=advisorService; }

	public static void setSetUpProperties(SetUpProperties setup) {
		StaticUtils.setupProperties = setup;
	}

	public static void setResourceFileRockYou(Resource resourceFileRockYou) {
		StaticUtils.resourceFileRockYou = resourceFileRockYou;
	}

	public static void setResourceFileCommon(Resource resourceFileCommon) {
		StaticUtils.resourceFileCommon = resourceFileCommon;
	}

	public static void setResourceJwtTool(Resource resourceJwtTool) {
		StaticUtils.resourceJwtTool = resourceJwtTool;
	}

	public static void setDataSourcePassword(String dataSourcePassword) {
		StaticUtils.dataSourcePassword = dataSourcePassword;
	}

	public static void setDataSourceUsername(String dataSourceUsername) {
		StaticUtils.dataSourceUsername = dataSourceUsername;
	}

	public static void setResourceDependencycheck(Resource resourceDependencycheck) {
		StaticUtils.resourceDependencycheck = resourceDependencycheck;
	}

	public static List<String> readFileInList(String fileName) {

		List<String> lines = Collections.emptyList();
		try {
			lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
		}
		catch (IOException e) {

			// do something
			e.printStackTrace();
		}
		return lines;
	}

}
