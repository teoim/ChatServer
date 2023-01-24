package org.mtr;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class ChatServer {

    private LinkedList<Socket> clients;

    private int port;
    private ServerSocket serverSocket;
    private boolean listen = true;


    public ChatServer(int port){
        this.port = port;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("ChatServer.ChatServer(port) error: " + e.toString());
            throw new RuntimeException(e);
        }

        startServer();
    }

    private void startServer(){
        this.listen = true;
        while(listen){
            try {
                clients.add(serverSocket.accept());
            }
            catch (Exception e){
                System.out.println("ChatServer - startServer() error : " + e.toString());
            }
            finally{

            }
        }
    }

    public void stopServer(){
        this.listen = false;
    }

    private class ClientConnection implements Runnable{

        private Socket clientSocket;

        public ClientConnection(Socket client){
            this.clientSocket = client;
        }

        @Override
        public void run() {

        }
    }

}
