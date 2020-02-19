package frc.robot;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThreadSocket extends Thread {
    protected Socket socket;

    public String curLine;

    public List<Byte>curData;

    public ThreadSocket(Socket clientSocket) {
        this.curData = new ArrayList<>();
        
        this.curLine = "";
        this.socket = clientSocket;
    }

    public void run() {
        InputStream inp = null;
        BufferedReader brinp = null;
        DataOutputStream out = null;
        DataInputStream in = null;
        // try {
        //     inp = socket.getInputStream();
        //     brinp = new BufferedReader(new InputStreamReader(inp));
        //     out = new DataOutputStream(socket.getOutputStream());
        // } catch (IOException e) {
        //     return;
        // }
        // String line;
        // while (true) {
        //     try {
        //         line = brinp.readLine();
        //         line = brinp.read();
        //         if ((line == null) || line.equalsIgnoreCase("QUIT")) {
        //             socket.close();
        //             return;
        //         } else {
        //             out.writeBytes(line + "\n\r");
        //             curLine = line;
        //             out.flush();
        //         }
        //     } catch (IOException e) {
        //         e.printStackTrace();
        //         return;
        //     }
        // }

        try{
            in = new DataInputStream(socket.getInputStream());


        } catch (Exception e){

        }

        while(true){
            try{
                if(in.readInt() > 0){
                    byte[] packetData = new byte[in.readInt()];
                    in.readFully(packetData);
    
                    //Adding the new array to the master list.
                    for(int i = 0; i< packetData.length; i++){
                        curData.add(packetData[i]);
                    }
    
                }

            } catch (IOException e){
                e.printStackTrace();
                return;
            }


            //Waiting 50ms pause befor looking for more data.
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }


    }
}