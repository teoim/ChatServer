package org.mtr.web.api.configuration;

import org.mtr.web.api.utilities.StompEndpointHandshakeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Arrays;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketBrokerConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/generalChat")
                .setAllowedOrigins(
                        "http://cchat.ddns.net"
                        , "http://cchat.go.ro"
                        , "http://localhost:8080"
                )
                .setHandshakeHandler(new StompEndpointHandshakeHandler())
                .withSockJS();

        registry.addEndpoint("/sendPrivateText")
                .setAllowedOrigins(
                        "http://cchat.ddns.net"
                        , "http://cchat.go.ro"
                        , "http://localhost:8080"
                )
                .setHandshakeHandler(new StompEndpointHandshakeHandler())
                .withSockJS();

        registry.addEndpoint("/sendICEMessage")
                .setAllowedOrigins(
                        "http://cchat.ddns.net"
                        , "http://cchat.go.ro"
                        , "http://localhost:8080"
                )
                .setHandshakeHandler(new StompEndpointHandshakeHandler())
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app")
//                .enableStompBrokerRelay("/topic", "/queue")   // https://www.youtube.com/watch?v=nxakp15CACY&t=4395s
//                    //.setVirtualHost("...")
//                    .setUserDestinationBroadcast("/topic/unresolved-user-destination")
                .enableSimpleBroker("/topic", "/queue");        // TODO we'll use the memory-based broker for now, but for scaling we should use a BrokerRelay (RabbitMQ, ...)

    }
}
