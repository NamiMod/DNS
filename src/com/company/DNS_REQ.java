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
 * @version 0.3
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
        Name name = null;
        String result = null;
        int type = Type.A, dclass = DClass.IN;
        Message query, response = null;
        Record rec;
        SimpleResolver res = null;
        int i = 0;
        int r = 1;
        int k;
        int flag = 0;
        String server = null;
        ArrayList<String> root_list = new ArrayList<String>();
        ArrayList<String> list = new ArrayList<String>();
        root_list.add("198.41.0.4");
        root_list.add("192.228.79.201");
        root_list.add("192.33.4.12");
        root_list.add("199.7.91.13");
        root_list.add("192.203.230.10");
        root_list.add("192.5.5.241");
        root_list.add("192.112.36.4");
        root_list.add("198.97.190.53");
        root_list.add("192.36.148.17");
        root_list.add("192.58.128.30");
        root_list.add("193.0.14.129");
        root_list.add("199.7.83.42");
        root_list.add("202.12.27.33");
        i = 0;
        k = 0;
        name = Name.fromString("aut.ac.ir", Name.root);
        while (true)//i<list.size())
        {
            flag = 0;
            if (r == 1) {
                if (k < 13) {
                    server = root_list.get((k++) % 13); // %13 does round robin
                    r = 0;
                } else {
                    System.out.println("Could not resolve");
                }
            } else if (r == 0 && i < list.size())
                server = list.get(i++);
            else {
                r = 1;
                continue;
            }
            //System.out.println("server name selected: "+server);
            if (server != null)
                res = new SimpleResolver(server);
            else
                res = new SimpleResolver();

            rec = Record.newRecord(name, type, dclass);
            query = Message.newQuery(rec);
            //System.out.println(query);
            try {
                response = res.send(query);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.println(response);
            if (response == null)
                continue;
            String head = response.getHeader().toString();
            if (head.contains("status: NOERROR")) {
                //check status: NOERROR
                Record[] ans = response.getSectionArray(Section.ANSWER);
                if (ans.length == 0) {
                    //System.out.println("answer is null");

                    Record[] records = response.getSectionArray(Section.AUTHORITY);
                    if (records.length != 0) {
                        list.clear();
                        i = 0;
                        for (int j = 0; j < records.length; j++) {
                            if (records[j].getType() == Type.SOA) {
                            } else
                                list.add(records[j].rdataToString());
                            //System.out.println(records[j].rdataToString());
                        }
                    } else {
                        r = 1;
                        continue;
                    }
                } else {// check if answer has ip address
                    //list.clear();
                    for (int j = 0; j < ans.length; j++) {
                        if (ans[j].getType() == Type.A || ans[j].getType() == Type.AAAA) {
                            //System.out.println("valid ip");
                            list.clear();
                            list.add(ans[j].rdataToString());
                            System.out.println(ans[j].rdataToString());
                            result = ans[j].rdataToString();
                            flag = 1;
                            break;
                        }
                        //else if(ans[j].getType()==Type.CNAME)
                        else {
                            //list.add(ans[j].rdataToString());
                            name = Name.fromString(ans[j].rdataToString(), Name.root);
                            r = 1;
                            continue;
                        }
                        //System.out.println(ans[j].rdataToString());
                    }
                    if (flag == 1)
                        break;
                }
            } else {
                continue;
            }
        }//end of while
        return result;
    }

}
