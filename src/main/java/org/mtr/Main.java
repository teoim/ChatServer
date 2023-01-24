package org.mtr;

import org.mtr.server.ChatServer;

public class Main {
    public static void main(String[] args) {
        ChatServer server = new ChatServer(9999);
        server.run();
    }
}