package com.company;

import java.io.*;
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

    /**
     * send message to local server
     * @throws IOException cant send message
     */
    public void Send_Message_To_Local_Server() throws IOException {
        Socket socket = new Socket("127.0.0.1", 5061);
        PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        try {
            output.println("Hello Server");
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            socket.close();
            output.close();
            read.close();
        }
    }


    /**
     * send message to DNS Server
     * @throws IOException cant send message
     */
    public void Send_Message_To_DNS_Server() throws IOException {
        Socket socket = new Socket("8.8.8.8", 53);
        PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        try {
            output.println("www.google.com");
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            socket.close();
            output.close();
            read.close();
        }
    }

}
