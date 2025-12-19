package org.mtr.web.api.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class GlobalCorsConfiguration {


    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", GlobalCorsConfiguration.getCorsConfiguration());
        return source;
    }


    /**
     *
     * @return Global cors configuration
     */
    public static CorsConfiguration getCorsConfiguration(){
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                Arrays.asList(
                        "http://cchat.ddns.net"
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

        configuration.setAllowCredentials(true);

        return configuration;
    }
}
