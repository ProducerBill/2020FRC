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
        try {
            inp = socket.getInputStream();
            in = new DataInputStream(socket.getInputStream());
            brinp = new BufferedReader(new InputStreamReader(inp));
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            return;
        }
        String line = "";
        while (true) {
            try {
                //line = brinp.readLine();
                // if(in.readInt() > 0){
                //     line = String.valueOf((char)brinp.read());
                // }
                // if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                //     socket.close();
                //     return;
                // } else {
                //     out.writeBytes(line + "\n\r");
                //     curLine = line;
                //     out.flush();
                // }
                byte[] inData = new byte[100];
                in.read(inData);

                for(int i = 0; i< inData.length; i++){
                    curData.add(inData[i]);
                }

                //Process data in buffer.
                processDataIn();
                checkBuffer();

                //line = new String(inData, "UTF-8");

                //System.out.println(line);

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

    }

    private void processDataIn(){
        
        //Going through data
        for(int i = 0; i< curData.size(); i++){
            if(i < curData.size() - 4){
                //Looking for the '\r\n'
                if((byte)curData.get(i) == (byte)92 &&
                    (byte)curData.get(i + 1) == (byte)114 &&
                    (byte)curData.get(i + 2) == (byte)92 &&
                    (byte)curData.get(i + 3) == (byte)110){
                        
                    
                        //Found a packet. Bring all the data in local.
                        List<Byte> curCommand = new ArrayList<>();
                        byte[] outData = new byte[i + 4];

                        for(int x = 0; x< i + 4; x++){
                            curCommand.add(curData.get(x));
                            outData[x] = curData.get(x);
                        }

                        //Removing from buffer.
                        for(int x = 0; x< outData.length; x++){
                            curData.remove(0);
                        }

                        
                    try{
                        System.out.print(new String(outData, "UTF-8"));
                    } catch (Exception e){
                        System.out.println(e.getMessage());
                    }

                }//End of found packet.
            } //end of if data position.
        }
    }

    private void checkBuffer(){
        int curDataCount = 0;
        int checkThreashold = 500;

        if(curData.size() > checkThreashold){
            for(int i = 0; i< curData.size(); i++){
                curDataCount += (int)curData.get(i);
            }
        }

        if(curDataCount < checkThreashold * 0.1){
            curData.clear();
        }

    
    }
}