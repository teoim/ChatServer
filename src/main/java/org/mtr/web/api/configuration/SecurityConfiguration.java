package org.mtr.web.api.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
@Profile("!test")
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
            http.cors().configurationSource(corsConfigurationSource());
//            http.cors(Customizer.withDefaults());   // by default, it will search for a bean with name corsConfigurationSource

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

            return http.build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowCredentials(true);

        configuration.setAllowedOrigins(
                Arrays.asList(
                    "http://cchat.ddns.net"
                    , "http://cchat.go.ro"
                    , "http://localhost:8080"
                ));

        configuration.setAllowedMethods(
                Arrays.asList(
                        HttpMethod.GET.name()
                        , HttpMethod.POST.name()
                        , HttpMethod.PUT.name()
                        , HttpMethod.DELETE.name()
                        , HttpMethod.OPTIONS.name()
                ));

        configuration.setAllowedHeaders(
                Arrays.asList(
                        HttpHeaders.CONTENT_TYPE
                        , HttpHeaders.AUTHORIZATION
                        , HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN
                        , HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD
                        , HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS
                ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
