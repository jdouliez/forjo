package thales.spring.angular.demo.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import thales.spring.angular.demo.domain.UserApp;
import thales.spring.angular.demo.security.jwt.JwtTokenUtil;
import thales.spring.angular.demo.services.UserService;

public abstract class GenericController {

	@Autowired
	/**
	 * @see UserService
	 */
	protected UserService serviceUser;
	
    @Autowired
	protected JwtTokenUtil jwtTokenUtil;
	
	protected UserApp getUserByToken(HttpServletRequest request) {

		if (request.getHeader("Authorization") != null && request.getHeader("Authorization").contains("Bearer ")) {
			String token = request.getHeader("Authorization").replace("Bearer ", "");
			String email = jwtTokenUtil.getUsernameFromToken(token);
			return serviceUser.findByEmail(email);
		}
		else 
			return null;
	}
}
