package org.mtr;

import org.mtr.server.ChatServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class ChatServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatServerApplication.class, args);
        // Start a server on port 9999
        //ChatServer server = new ChatServer(9999);
        //server.run();
    }
}