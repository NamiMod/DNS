package com.company;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Caching
 *
 *
 *
 * Network Project
 *
 *
 * @author Seyed Nami Modarressi
 * @version 1.2
 */

public class Cache {

    /**
     * copy Temp file to Cache file
     *
     * @throws IOException cant read or write file
     */
    public void copy() throws IOException {
        FileWriter copy = new FileWriter("Cache.txt", false);
        FileReader fileReader = new FileReader("Temp.txt");
        Scanner getString = new Scanner(fileReader);

        while (getString.hasNextLine()) {
            copy.write(getString.nextLine() + '\n');
        }

        copy.close();
        fileReader.close();
        getString.close();
    }

    /**
     * update Cache
     *
     * @param domain domain name
     * @param ip     ip
     */

    public void update(String domain, String ip) {

        try {
            FileWriter clear = new FileWriter("Temp.txt", false);
            clear.write("");
            clear.close();
            FileWriter fw = new FileWriter("Temp.txt", true);
            FileReader fileReader = new FileReader("Cache.txt");
            Scanner getString = new Scanner(fileReader);
            boolean flag = false;

            while (getString.hasNextLine()) {

                String name = getString.nextLine();
                String IP = getString.nextLine();
                String counter = getString.nextLine();

                if (name.equals(domain)) {
                    fw.write(name + "\n");
                    fw.write(IP + "\n");
                    fw.write(Integer.parseInt(counter) + 1 + "\n");
                    flag = true;
                } else {
                    fw.write(name + "\n");
                    fw.write(IP + "\n");
                    fw.write(counter + "\n");
                }

            }
            if (!flag) {
                fw.write(domain + "\n");
                fw.write(ip + "\n");
                fw.write("1");
            }
            fileReader.close();
            getString.close();
            fw.close();
            copy();
        } catch (Exception e) {
            System.out.println("Cant read or write file");
        }
    }

    /**
     * get ip from cache
     *
     * @param domain name
     * @return IP as String
     */
    public String getIP(String domain) {
        try {
            FileReader fileReader = new FileReader("Cache.txt");
            Scanner getString = new Scanner(fileReader);

            while (getString.hasNextLine()) {

                String name = getString.nextLine();
                String ip = getString.nextLine();
                String counter = getString.nextLine();

                if (name.equals(domain) && Integer.parseInt(counter) > 3) {
                    return ip;
                }
            }
            fileReader.close();
            getString.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
