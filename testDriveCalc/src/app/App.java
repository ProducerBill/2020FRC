package app;

import java.util.ArrayList;
import java.util.List;

import app.HolonomicDrive;
import app.cordinates;

public class App {

    static HolonomicDrive driveSystem;
    
    public static void main(String[] args) throws Exception {
        
        driveSystem = new HolonomicDrive();

        List<cordinates>testPoints = new ArrayList<>();
        testPoints.add(new cordinates(0.0,1.0,0.0));
        //testPoints.add(new cordinates(0.1,1.0,0.0));
        //testPoints.add(new cordinates(0.5,1.0,0.0));
        testPoints.add(new cordinates(1.0,1.0,0.0));
        //testPoints.add(new cordinates(1.0,0.5,0.0));
        //testPoints.add(new cordinates(1.0,0.1,0.0));
        testPoints.add(new cordinates(1.0,0.0,0.0));
        //testPoints.add(new cordinates(1.0,-0.1,0.0));
        //testPoints.add(new cordinates(1.0,-0.5,0.0));
        testPoints.add(new cordinates(1.0,-1.0,0.0));
        //testPoints.add(new cordinates(0.5,-1.0,0.0));
        //testPoints.add(new cordinates(0.1,-1.0,0.0));
        testPoints.add(new cordinates(0.0,-1.0,0.0));
        //testPoints.add(new cordinates(-0.1,-1.0,0.0));
        //testPoints.add(new cordinates(-0.5,-1.0,0.0));
        testPoints.add(new cordinates(-1.0,-1.0,0.0));
        //testPoints.add(new cordinates(-1.0,-0.5,0.0));
        //testPoints.add(new cordinates(-1.0,-0.1,0.0));
        testPoints.add(new cordinates(-1.0,0.0,0.0));
        //testPoints.add(new cordinates(-1.0,0.1,0.0));
        //testPoints.add(new cordinates(-1.0,0.5,0.0));
        testPoints.add(new cordinates(-1.0,1.0,0.0));
        //testPoints.add(new cordinates(-0.5,1.0,0.0));
        testPoints.add(new cordinates(-0.1,1.0,0.0));
        testPoints.add(new cordinates(-0.001,1.0,0.0));
        testPoints.add(new cordinates(0.0,0.0,0.5));

        for(int i = 0; i< testPoints.size(); i++)
        {
            driveSystem.inputControl(testPoints.get(i).x , testPoints.get(i).y, testPoints.get(i).z);

            System.out.println("(" + testPoints.get(i).x + "," + testPoints.get(i).y + "," + testPoints.get(i).z +") " +
                                "FL=" + String.format("%.3f", driveSystem.motorFrontLeft) + 
                                " FR=" + String.format("%.3f", driveSystem.motorFrontRight) +
                                " RL=" + String.format("%.3f", driveSystem.motorRearLeft) + 
                                " RR=" + String.format("%.3f", driveSystem.motorRearRight));
        }


    }
}