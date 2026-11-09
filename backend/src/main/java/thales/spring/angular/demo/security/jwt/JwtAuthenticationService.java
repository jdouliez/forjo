package thales.spring.angular.demo.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import thales.spring.angular.demo.services.UserDetailsService;

@Service
/**
 * 
 * Service use for authentication
 *
 */
public class JwtAuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
	protected JwtTokenUtil jwtTokenUtil;
    
    /**
     * Authenticate the user by username and password
     * @param username
     * @param password
     * @throws Exception
     */
    public void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)); 
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
    
    /**
     * 
     * @param username : the username of an authenicated suer
     * @return the token for a user by username
     */
	public String generateToken(String username) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return jwtTokenUtil.generateToken(userDetails);
	}
}
