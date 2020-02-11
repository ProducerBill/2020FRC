package frc.robot;

import java.io.*;
import java.net.*;

public class ServerTCP implements Runnable{

    ServerSocket serverSocket = null;
    Socket socket = null;

    static final int PORT = 6789;

    public ServerTCP(){

    }


    @Override
    public void run() {

        System.out.println("Starting Network");

        try {

            try {
                serverSocket = new ServerSocket(PORT);
            } catch (IOException e) {
                e.printStackTrace();
    
            }
            while (true) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    System.out.println("I/O error: " + e);
                }
                // new thread for a client
                new  ThreadSocket(socket).start();
            }
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    
}