package com.company;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import org.xbill.DNS.*;
import org.xbill.DNS.Record;

/**
 * in this class we create and send dns request
 *
 *
 *
 * Network Project
 *
 *
 * @author Seyed Nami Modarressi
 * @version 1.5
 */

public class DNS_REQ {

    /**
     * send message to local server
     *
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
        } finally {
            socket.close();
            output.close();
            read.close();
        }
    }

    /**
     * send message to DNS Server
     *
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
        } finally {
            socket.close();
            output.close();
            read.close();
        }
    }

    /**
     * send domain to DNS Server and get result
     * Recursive
     *
     * @param domain domain name
     * @return IP as String
     * @throws IOException cant send message
     */
    public String Send_Req_To_DNS_Recursive(String domain) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(out);

        // Set Flags and data

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

        // Add domain
        for (String domainPart : domain.split("\\.")) {
            byte[] domainBytes = domainPart.getBytes();
            outStream.writeByte(domainBytes.length);
            outStream.write(domainBytes);
        }

        // Done
        outStream.writeByte(0x00);

        // Type 0x01 = A
        outStream.writeShort(0x0001);

        // Class 0x01 = IN
        outStream.writeShort(0x0001);

        byte[] REQ_Packet = out.toByteArray();

        // Send DNS Packet
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket dnsReqPacket = new DatagramPacket(REQ_Packet, REQ_Packet.length, InetAddress.getByName("8.8.8.8"), 53);
        socket.send(dnsReqPacket);

        // get Response from server
        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        // Parsing response to get IP address

        DataInputStream din = new DataInputStream(new ByteArrayInputStream(buf));

        System.out.println("Response Data :");

        for (int i = 0; i < 6; i++) {
            System.out.printf("%x%n", din.readShort());
        }

        int domain_counter;
        while ((domain_counter = din.readByte()) > 0) {
            byte[] domain_part = new byte[domain_counter];

            for (int i = 0; i < domain_counter; i++) {
                domain_part[i] = din.readByte();
            }
            System.out.println(new String(domain_part));
        }

        for (int i = 0; i < 7; i++) {
            System.out.printf("%x%n", din.readShort());
        }

        short address_size = din.readShort();
        System.out.printf("%x%n", address_size);

        if (address_size == 4) {

            String Address = "";
            System.out.print("Address: ");
            for (int i = 0; i < address_size; i++) {
                Address += String.format("%d", (din.readByte()) & 0xFF) + ".";
            }

            int counter = 0;
            String result = "";
            for (char C : Address.toCharArray()) {
                counter++;
                if (counter != Address.length()) {
                    System.out.print(C);
                    result = result + C;
                }
            }

            return result;

        } else {
            System.out.println("Address : Not Found");
            return null;
        }
    }

    /**
     * send domain to DNS Server and get result
     * Iterative
     *
     * @param domain domain name
     * @throws IOException cant send message
     */
    public String Send_Req_To_DNS_Iterative(String domain) throws IOException {
        String name;
        String result = null;
        Message query, response = null;
        Record rec;
        SimpleResolver res;
        int i;
        int r = 1;
        int flag;
        String server = "198.41.0.4";
        ArrayList<String> list = new ArrayList<String>();

        i = 0;
        name = domain + ".";
        while (true) {

            flag = 0;
            if (r == 1) {
                r = 0;
            } else if (i < list.size()) {
                server = list.get(i++);
            } else {
                r = 1;
                continue;
            }

            res = new SimpleResolver(server);
            rec = Record.newRecord(Name.fromString(name), 1, 1);
            query = Message.newQuery(rec);
            try {
                response = res.send(query);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Record[] ans = response.getSectionArray(Section.ANSWER);
            if (ans.length == 0) {
                Record[] records = response.getSectionArray(Section.AUTHORITY);
                if (records.length != 0) {
                    list.clear();
                    i = 0;
                    for (Record record : records) {
                        list.add(record.rdataToString());
                    }
                } else {
                    r = 1;
                }
            } else {
                for (Record an : ans) {
                    if (an.getType() == 1) {
                        list.clear();
                        list.add(an.rdataToString());
                        System.out.println(an.rdataToString());
                        result = an.rdataToString();
                        flag = 1;
                        break;
                    } else {
                        name = domain + ".";
                        r = 1;
                    }
                }
                if (flag == 1) {
                    break;
                }
            }

        }
        return result;
    }

    /**
     * read domains from csv file and resolve them
     */
    public void Send_From_CSV() throws IOException {

        ArrayList<String> domains = new ArrayList<>();
        ArrayList<String> ips = new ArrayList<>();

        String row;

        BufferedReader csvReader = new BufferedReader(new FileReader("domain.csv"));
        while ((row = csvReader.readLine()) != null) {
            domains.add(row);
            ips.add(Send_Req_To_DNS_Recursive(row));
        }
        csvReader.close();

        FileWriter csvWriter = new FileWriter("domain.csv");
        for (int i = 0; i < domains.size(); i++) {
            csvWriter.append(domains.get(i) + " : " + ips.get(i) + "\n");
        }
        csvWriter.flush();
        csvWriter.close();
    }

}
