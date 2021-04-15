package com.company;

import java.io.FileReader;
import java.util.Scanner;

public class Cache {


    public Cache(){


    }

    public void update(String domain , String ip){

    }

    public String getIP(String domain){
        try {
            FileReader fileReader = new FileReader("Cache.txt");
            Scanner getString = new Scanner(fileReader);

            while (getString.hasNextLine()) {

                String name = getString.nextLine();
                String ip = getString.nextLine();
                String counter = getString.nextLine();

                if (name.equals(domain) && Integer.parseInt(counter) > 3){
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
