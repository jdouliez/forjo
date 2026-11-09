package thales.spring.angular.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import thales.spring.angular.demo.SetUpProperties;
import thales.spring.angular.demo.security.jwt.JwtAuthenticationEntryPoint;
import thales.spring.angular.demo.security.jwt.JwtRequestFilter;

@Configuration
@EnableWebSecurity(debug=false)
/**
 * Adapter to manage Spring Security security
 *
 */
public class WebApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    /**
     * @see SetUpProperties
     */
    SetUpProperties setup;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private UserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired

    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }
    /**
     * Service offering the management of users (login/password) having access
     * to the entry point
     *
     * @return UserDetailsService
     */
    @Bean
    public UserDetailsService userDetailService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("admin").password(setup.getAuthentDigestPassword()).roles("ADMIN").build());
        return manager;
    }

    /**
     * Manages the Digest Authentication entry point
     *
     * @return DigestAuthenticationEntryPoint
     */
    public DigestAuthenticationEntryPoint digestEntryPoint() {
        DigestAuthenticationEntryPoint digestAuthenticationEntryPoint = new DigestAuthenticationEntryPoint();
        digestAuthenticationEntryPoint.setKey("Admin panel authentication");
        digestAuthenticationEntryPoint.setRealmName("Digest ForJo");
        return digestAuthenticationEntryPoint;
    }

    /**
     * Filters the Digest Authentication entry point by adding services such as
     * user management
     *
     * @see UserDetailsService
     * @param d Digest entry point
     * @return DigestAuthenticationFilter
     */
    public DigestAuthenticationFilter digestAuthentificationFilter(DigestAuthenticationEntryPoint d) {
        DigestAuthenticationFilter filter = new DigestAuthenticationFilter();
        filter.setAuthenticationEntryPoint(d);
        filter.setUserDetailsService(userDetailService());
        return filter;
    }

    /**
     * Configure security to access the application An access point has been
     * defined with the path /api/admin Digest Authentication
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers("/api/authenticate/**").permitAll()
                .antMatchers("/api/check/**").permitAll()
                .antMatchers("/api/registration").permitAll()
                .antMatchers("/api/safe/secretcode").permitAll()
                .antMatchers("/api/resetPassword").permitAll()
                .anyRequest().authenticated().and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    /**
     * Configure network security such as specific HTTP Firewall features
     */
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.ignoring().antMatchers("/h2-console/**");
        web.httpFirewall(allowUrlEncodedSemicolonHttpFirewall());
    }

    @Bean
    /**
     * Customize the HttpFirewall security object
     *
     * @return l'objet HttpFirewall
     */
    public HttpFirewall allowUrlEncodedSemicolonHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setUnsafeAllowAnyHttpMethod(true);
        firewall.setAllowUrlEncodedSlash(true);
        firewall.setAllowUrlEncodedPercent(true);
        firewall.setAllowSemicolon(true);
        return firewall;
    }
}
