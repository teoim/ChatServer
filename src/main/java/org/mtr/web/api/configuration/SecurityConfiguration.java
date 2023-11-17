package org.mtr.web.api.configuration;

import jakarta.servlet.http.HttpServletResponse;
//import org.mtr.web.api.filters.CustomRequestHeaderTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

/*    // https://blog.devgenius.io/spring-boot-security-configuration-practically-explained-part6-a-deep-intro-to-56ce03860ad
    @Autowired
    private AuthenticationConfiguration authConfig;

    // https://blog.devgenius.io/spring-boot-security-configuration-practically-explained-part6-a-deep-intro-to-56ce03860ad
    @Bean
    public CustomRequestHeaderTokenFilter customFilter() throws Exception {
        return new CustomRequestHeaderTokenFilter(authConfig.getAuthenticationManager());
    }*/

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        try {
            http.csrf().disable();
            http.cors().disable();

            //https://blog.devgenius.io/spring-boot-security-configuration-practically-explained-part6-a-deep-intro-to-56ce03860ad
            /*http
                    .exceptionHandling()
                    .authenticationEntryPoint((request, response, authEx) -> {
                        response.setHeader("WWW-Authenticate", "Basic realm=\"Access to /signin authentication endpoint\"");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("{ \"Error\": \"" + authEx.getMessage() + " - You are not authenticated.\" }");
                    })
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
        //            .addFilterBefore( new CustomRequestHeaderTokenFilter(authConfig.getAuthenticationManager()), UsernamePasswordAuthenticationFilter.class)

                    .authorizeHttpRequests(authorize -> authorize.requestMatchers(HttpMethod.POST, "/auth/signup").permitAll()
                            .requestMatchers(HttpMethod.GET, "/auth/signin").authenticated())

                    .authorizeHttpRequests(authorize -> authorize.requestMatchers("/users").hasRole("ADMIN")
                            .requestMatchers("/items").hasAnyRole("ADMIN", "USER")
                    );*/
            //END https://blog.devgenius.io/spring-boot-security-configuration-practically-explained-part6-a-deep-intro-to-56ce03860ad

            http
                    .authorizeHttpRequests((requests) -> requests
//                            .requestMatchers("/", "/**", "/css/**", "/js/**", "/images/**").permitAll()
                            .requestMatchers("/api/auth/**", "/css/**", "/js/**", "/images/**").permitAll()
                            //        .anyRequest().permitAll()
                            .anyRequest().authenticated()
                    )
                    .formLogin((form) -> form
                            .loginPage("/api/auth/authenticate")
//                            .loginProcessingUrl("/api/auth/authenticate-post")
                            .permitAll()
                            .successForwardUrl("/dashboard")
                            .failureForwardUrl("/api/auth/register")
                    )
                    .logout((logout) -> logout
                            .permitAll()
                            .logoutSuccessUrl("/api/auth/logout")
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID"));
                    // More on logout: https://www.baeldung.com/spring-security-logout#3-invalidatehttpsessionand-deletecookies
                    // https://docs.spring.io/spring-security/reference/servlet/authentication/logout.html



            return http.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
