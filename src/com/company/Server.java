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

    private static ServerSocket serversocket;
    private static BufferedReader input;

    public static void main(String[] args) throws IOException {

        serversocket = new ServerSocket(5061);
        System.out.println("Connection Starting on port:" + serversocket.getLocalPort());
        while (true) {
            Socket client = serversocket.accept();
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            try {
                String message = input.readLine();
                System.out.println("Client message : " + message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
