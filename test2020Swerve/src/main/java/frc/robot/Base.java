package frc.robot;

import java.util.ArrayList;
import java.util.List;

public class Base{
    public List<MotorSwerve> motorCorners;

    public Base(){
        motorCorners = new ArrayList();
    }

    public void addCorner(int talonDrive, int talonStear, int channelA, int channelB, String name){
        motorCorners.add(new MotorSwerve(talonDrive, talonStear, channelA, channelB));

        motorCorners.get(motorCorners.size()-1).Name = name;

    }

}