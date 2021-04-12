package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * in this class we create Server to get message from client
 *
 *
 *
 * Network Project
 *
 *
 * @author Seyed Nami Modarressi
 * @version 0.1
 */
public class Server {

    private ServerSocket serversocket;
    private BufferedReader input;
    private PrintWriter output;

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * start server
     *
     * @throws IOException cant read files
     */
    public void start() throws IOException {
        serversocket = new ServerSocket(5061);
        System.out.println("Connection Starting on port:" + serversocket.getLocalPort());
        while (true) {
            Socket client = serversocket.accept();
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
            try {
                String message = input.readLine();
                System.out.println("Client message : " + message);
                if (message.equals("Exit")){
                    break;
                }else {
                    output.println("Hello My Client !");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        serversocket.close();
    }
}
