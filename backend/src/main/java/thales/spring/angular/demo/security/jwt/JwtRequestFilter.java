package thales.spring.angular.demo.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import thales.spring.angular.demo.exception.UserNotFoundException;
import thales.spring.angular.demo.services.UserDetailsService;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	protected static final Logger LOGGER = LoggerFactory.getLogger("JwtRequestFilter");
	
    @Autowired
    private UserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
    	LOGGER.debug("New {} request from {} on path {} ", request.getMethod(), request.getLocalAddr(), request.getServletPath());

        String requestTokenHeader = request.getHeader("Authorization");
        String jwtToken = null;
        String username = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
       
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                logger.warn("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                logger.warn("JWT Token has expired");
            }

            // Once we get the token validate it.
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

                    // if token is valid configure Spring Security to manually set  authentication
                    if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        // After setting the Authentication in the context, we specify
                        // that the current user is authenticated. So it passes the
                        // Spring Security Configurations successfully.
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                } 
                catch (UserNotFoundException e) {
                    logger.error("User \"" + username + "\" has not been found from JWT");
                    return;
                }
            }
        } 
        else if (requestTokenHeader != null && requestTokenHeader.startsWith("None")) {
            logger.debug("Bypassing authorization with header \"None\" for path \"" + request.getContextPath() + "\"");
        } 
        else {
            logger.warn("JWT Token does not begin with Bearer String");
        }

        chain.doFilter(request, response);
    }
}
