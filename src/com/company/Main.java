package com.company;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        DNS_REQ req = new DNS_REQ();
        req.Send_Message_To_Local_Server();
    }
}
