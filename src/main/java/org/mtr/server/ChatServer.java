package org.mtr.server;

import org.mtr.logger.ErrorLogger;
import org.mtr.logger.MessageLogger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer implements Runnable {
    private ErrorLogger errLog;
    private MessageLogger msgLog;
    private ArrayList<ClientConnection> clients;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private boolean listen;


    public ChatServer(int port){
        this.threadPool = Executors.newCachedThreadPool();
        this.clients = new ArrayList<>();
        this.listen = true;
        try {
            if(port < 1) serverSocket = new ServerSocket(9999);
            else serverSocket = new ServerSocket(port);
        } catch ( IOException e) {
            errLog.log( e, this.getClass().getSimpleName(), "ChatServer(int port)");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        this.threadPool = Executors.newCachedThreadPool();
        msgLog.log("SERVER - run () - Client thread pool started: " + threadPool);
        Socket clientSocket;
        msgLog.log("SERVER - run() - Running. Waiting for connections ...");
        try {
            while (listen) {
                clientSocket = this.serverSocket.accept();  // wait for clients to connect
                msgLog.log("Connected to client " + clientSocket.toString());

                ClientConnection clientConnection = new ClientConnection(clientSocket);
                this.clients.add(clientConnection); // add client to list of clients connected
                threadPool.execute(clientConnection);   // run client in a thread pool
            }
        }
        catch (Exception e){
            errLog.log(e, this.getClass().getSimpleName(), "run()");
            stopServer();
        }
        finally{}
    }

    public void stopServer() {
        this.listen = false;
        if(!serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                errLog.log(e, this.getClass().getSimpleName(), "stopServer()");
            }
        }
        for(ClientConnection client : clients){
            client.shutdown();
        }
    }

    public void broadcast(String msg){
        for (ClientConnection client : clients){
            client.sendMessage(msg);
            msgLog.log(msg);
        }
    }


    private class ClientConnection implements Runnable{
        private Socket clientSocket;
        private BufferedReader clientInput;
        private PrintWriter clientOutput;
        private String nickName;

        public ClientConnection(Socket client){
            this.clientSocket = client;
        }

        @Override
        public void run() {
            try {
                clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                clientOutput = new PrintWriter(clientSocket.getOutputStream(), true);

                String inputMessage;
                //clientOutput.println("SERVER> Hello, please enter nickname: ");
                sendMessage("SERVER> Hello, please enter nickname: ");
                this.nickName = clientInput.readLine();
                //clientOutput.println("SERVER> Welcome, " + nickName + "!");
                sendMessage("SERVER> Welcome, " + nickName + "!");
                broadcast("SERVER> " + nickName + " joined the chat!");

                //TODO: Improve chatting with clients:
                while((inputMessage = clientInput.readLine()) != null ){
                    // TODO: more validations on messages from clients
                    if( inputMessage.startsWith("/nick ")){
                        // nickname change
                        String[] nick = inputMessage.split(" ", 2);
                        if(nick.length == 2) {
                            String msg = this.nickName + " changed name to " + nick[1];
                            broadcast(msg);
                            msgLog.log(msg);
                            this.nickName = nick[1];
                            clientOutput.println("Nickname changed to " + this.nickName);
                        } else {
                            clientOutput.println("Nickname not changed - please check nickname provided.");
                        }
                    }
                    else if( inputMessage.startsWith("/q ") || inputMessage.startsWith("/quit ")){
                        broadcast(nickName + " left the chat.");
                        msgLog.log(nickName + " left the chat.");
                        shutdown();
                    }
                    else {
                        broadcast(nickName + ": " + inputMessage);
                    }
                }
            }
            catch(IOException e) {
                errLog.log(e, this.getClass().getSimpleName(), "run()");
                shutdown();
            }
            finally{
                broadcast("SERVER: I don't know what i'm doing.");
            }
        }

        private void sendMessage(String msg){
            clientOutput.println(msg);
        }

        public void shutdown() {
            try {
                msgLog.log("SERVER - Shutting down client: " + this);
                clientInput.close();
                clientOutput.close();
                clientSocket.close();
                clients.remove(this);
                msgLog.log("SERVER - Client shot down correctly: " + this);
            } catch (IOException e) {
                errLog.log(e, this.getClass().getSimpleName(), "shutdown()");
            }
        }
    }

}
