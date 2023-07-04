package org.mtr.web.api.configuration;

import lombok.RequiredArgsConstructor;
import org.mtr.logger.MessageLogger;
//import org.mtr.web.api.component.CustomAuthenticationProvider;
import org.mtr.web.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {

    private final DataSource dataSource;
    private final UserService userDetailsService;

//    @Bean
//    public UserDetailsService userDetailsService(){
//        return this.userDetailsService;
//    }

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder auth) throws Exception {

/*        auth.jdbcAuthentication()
                .dataSource(dataSource)
                // spring security expects email, password and enabled columns:
                .usersByUsernameQuery( "SELECT email, password, 'true' FROM users WHERE email=?")
//                .authoritiesByUsernameQuery( "SELECT email, authority FROM authorities WHERE username=?")
                .authoritiesByUsernameQuery( "SELECT user_email, role_name FROM user_roles WHERE user_email=?")
                .passwordEncoder( passwordEncoder());*/
        auth.userDetailsService( this.userDetailsService)
                .passwordEncoder( passwordEncoder());
    }

    // An authentication provider is used when we don't have the username/password in our database,
    // instead we get them from some external provider (Atlassian Crowd, ldap, etc)
/*    @Bean
    public AuthenticationProvider authenticationProvider(){
        // Data Access Object which is responsible for fetching user , encoding password, etc
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService( userDetailsService());
        authProvider.setPasswordEncoder( passwordEncoder());

        // Test custom auth provider
        CustomAuthenticationProvider customAuthProvider = new CustomAuthenticationProvider();

        //return authProvider;
        return customAuthProvider;
    }*/

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    public PasswordEncoder passwordEncoder() {
        //return new BCryptPasswordEncoder();
        // TODO: replace NoOpPasswordEncoder by BCrypt or other secure encoder
        return NoOpPasswordEncoder.getInstance();
    }
}
