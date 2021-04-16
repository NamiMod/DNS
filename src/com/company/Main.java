package com.company;

import java.io.IOException;
import java.util.Scanner;
/**
 * very simple menu for program !
 *
 *
 *
 * Network Project
 *
 *
 * @author Seyed Nami Modarressi
 * @version 1.0
 */
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

            String cacheResult = cache.getIP(domain);
            if (cacheResult != null) {
                System.out.println("Address (from cache) : " + cacheResult);
            } else {
                String result = req.Send_Req_To_DNS_Recursive(domain);
                if (result != null) {
                    cache.update(domain, result);
                }
            }

        } else if (mode.equals("I")) {

            String cacheResult = cache.getIP(domain);
            if (cacheResult != null) {
                System.out.println("Address (from cache) : " + cacheResult);
            } else {
                String result = req.Send_Req_To_DNS_Iterative(domain);
                if (result != null) {
                    cache.update(domain, result);
                }
            }

        } else {
            System.out.println("Invalid input !");
        }

    }
}