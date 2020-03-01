package frc.robot;

import java.io.*;
import java.net.*;
import java.util.List;

import frc.robot.ThreadSocket;

public class ServerTCP implements Runnable{

    ServerSocket serverSocket = null;
    Socket socket = null;

    static final int PORT = 6789;

    private ThreadSocket curThreadSocket;

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
                curThreadSocket = new ThreadSocket(socket);
                Thread tSocket = new Thread(curThreadSocket);
                tSocket.start();


            }
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public String getLatestLine(){
        if(curThreadSocket != null){
            String latest = ((ThreadSocket) curThreadSocket).curLine;

            ((ThreadSocket) curThreadSocket).curLine = "";

            return latest;
        }

        return "";
    }

    public byte[] getCurBufferData(){
        if(curThreadSocket != null){
            List<Byte> buffer = ((ThreadSocket) curThreadSocket).curData;

            if(buffer.size() > 0){

                //Setting up return object.
                byte[] out = new byte[buffer.size()];

                //Converting to a byte[]
                for(int i = 0; i< buffer.size(); i++){
                    out[i] = (byte)buffer.get(i);
                }
                
                return out;
            } else {
                return null;
            }
        }
        return null;
    }
    
}