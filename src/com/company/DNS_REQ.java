package com.company;

import java.io.*;
import java.net.*;

/**
 * in this class we create and send dns request
 *
 *
 *
 * Network Project
 *
 *
 * @author Seyed Nami Modarressi
 * @version 0.3
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
            output.println("Hello");
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
     * send domain to DNS Server and get result
     * @throws IOException cant send message
     * @param domain domain name
     */
    public void Send_Req_To_DNS(String domain) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(out);

        //  -> Create DNS Packet <-

        // Identifier
        outStream.writeShort(0x9999);

        // Write Query Flags
        outStream.writeShort(0x0100);

        // Number of Questions
        outStream.writeShort(0x0001);

        // Answer Record Count
        outStream.writeShort(0x0000);

        // Authority Record Count
        outStream.writeShort(0x0000);

        // Additional Record Count
        outStream.writeShort(0x0000);

        for (String domainPart : domain.split("\\.")) {
            byte[] domainBytes = domainPart.getBytes();
            outStream.writeByte(domainBytes.length);
            outStream.write(domainBytes);
        }

        // No more parts
        outStream.writeByte(0x00);

        // Type 0x01 = A
        outStream.writeShort(0x0001);

        // Class 0x01 = IN
        outStream.writeShort(0x0001);

        byte[] Frame = out.toByteArray();

        // Send DNS Packet
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket dnsReqPacket = new DatagramPacket(Frame, Frame.length,InetAddress.getByName("8.8.8.8"), 53);
        socket.send(dnsReqPacket);

        // Await response from DNS server
        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        DataInputStream din = new DataInputStream(new ByteArrayInputStream(buf));

        System.out.println("Transaction ID: 0x" + String.format("%x", din.readShort()));
        System.out.println("Flags: 0x" + String.format("%x", din.readShort()));
        System.out.println("Questions: 0x" + String.format("%x", din.readShort()));
        System.out.println("Answers RRs: 0x" + String.format("%x", din.readShort()));
        System.out.println("Authority RRs: 0x" + String.format("%x", din.readShort()));
        System.out.println("Additional RRs: 0x" + String.format("%x", din.readShort()));

        int recLen = 0;
        while ((recLen = din.readByte()) > 0) {
            byte[] record = new byte[recLen];

            for (int i = 0; i < recLen; i++) {
                record[i] = din.readByte();
            }

            System.out.println("Record: " + new String(record, "UTF-8"));
        }


        System.out.println("Record Type: 0x" + String.format("%x", din.readShort()));
        System.out.println("Class: 0x" + String.format("%x", din.readShort()));
        System.out.println("Field: 0x" + String.format("%x", din.readShort()));
        System.out.println("Type: 0x" + String.format("%x", din.readShort()));
        System.out.println("Class: 0x" + String.format("%x", din.readShort()));
        System.out.println("TTL: 0x" + String.format("%x", din.readInt()));

        short addrLen = din.readShort();
        System.out.println("Len: 0x" + String.format("%x", addrLen));

        System.out.print("Address: ");
        for (int i = 0; i < addrLen; i++ ) {
            System.out.print("" + String.format("%d", (din.readByte() & 0xFF)) + ".");
        }
    }


}
