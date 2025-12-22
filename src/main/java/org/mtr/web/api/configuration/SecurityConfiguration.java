package org.mtr.web.api.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;


@Configuration
@EnableWebSecurity
@Profile("!test")
public class SecurityConfiguration {

    CorsConfigurationSource corsConfigurationSource;

    @Autowired
    public SecurityConfiguration(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        try {
            http.csrf().disable();
            http.cors().configurationSource(corsConfigurationSource);
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
}
