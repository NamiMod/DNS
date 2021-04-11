package com.company;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * in this class we create and send dns request
 *
 *
 *
 * Network Project
 *
 *
 * @author Seyed Nami Modarressi
 * @version 0.1
 */

public class DNS_REQ {

    public static void main(String[] args) {

        String server = "1.1.1.1";
        int port = 53;

        try {

            Socket socket = new Socket(server, port);
            PrintStream out = new PrintStream(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Read data from the server until we finish reading the document
            String line = in.readLine();
            while (line != null) {
                System.out.println(line);
                line = in.readLine();
            }

            // Close our streams
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
