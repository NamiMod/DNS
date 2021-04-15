package com.company;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        DNS_REQ req = new DNS_REQ();
        Cache cache = new Cache();
        String domain;
        String mode;
        Scanner getDomain = new Scanner(System.in);

        System.out.println("Enter Your Domain :");
        domain = getDomain.nextLine();
        System.out.println("Recursive(R) / Iterative (I) : ");
        mode = getDomain.nextLine();

        if (mode.equals("R")) {

        } else if (mode.equals("I")) {

        } else {
            System.out.println("Invalid input !");
        }

    }
}
