package frc.robot;

import java.util.ArrayList;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import frc.robot.RobotKnowns;

public class Wheel {

    private TalonSRX MotorDrive;    //Motor driver for moving the wheel forward and backward.
    private TalonSRX MotorStear;    //Motor driver for pointing the wheel in the desired direction.
    private String Name;            //Name of the wheel.
    
    private RobotKnowns robotKnowns;

    private double curStearPos;
    private double curDrivePos;

    public Wheel(int driveID, int stearID, String name){
        
        robotKnowns = new  RobotKnowns();

        Name = name;    //Setting the name.
        /*
        //Setting up the motor driver 
        MotorDrive = new TalonSRX(driveID);
        MotorStear = new TalonSRX(stearID);

        //Setting up the driver direction.
        MotorDrive.setInverted(false);
        MotorStear.setInverted(false);

        //Setting the breaking and coast modes.
        MotorDrive.setNeutralMode(NeutralMode.Coast);
        MotorStear.setNeutralMode(NeutralMode.Brake);

        //Setting up the encoders.
        MotorDrive.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);
        MotorStear.configSelectedFeedbackSensor(FeedbackDevice.Analog);

        //Resetting the encoders
        MotorDrive.setSelectedSensorPosition(0, 0, 10);
        MotorStear.setSelectedSensorPosition(0, 0, 10);
        */

    }

    //Input the desire speed forward.
    public void setSpeed(double countSpeed){
        //MotorDrive.set(ControlMode.Velocity, countSpeed);
    }

    public int getDriveSpeed(){
        return MotorDrive.getSelectedSensorVelocity(0);
    }

    //Set the desire angle direction.
    public void setStearAngle(double desiredAngle){
        curStearPos = curStearPos + findClosest(desiredAngle);
        System.out.println(this.Name + " RC: " + wheelRotations() + " Ang: " + String.format("%.3f", curStearPos));




        //MotorStear.set(ControlMode.Position, countPos);
    }

    public int getStearPos(){
        return MotorStear.getSelectedSensorPosition(0);
    }

    private double findClosest(double sp){

        ArrayList<Double[]> closestRange = new ArrayList<>();

        //Setting up the solution table

        for(int i = wheelRotations() - 2; i< wheelRotations() + 2; i++){
            double rot = i;
            double rotPos = (360 * rot) + sp;
            double diff = curStearPos - rotPos;
            double diffABS = Math.abs(diff);

            closestRange.add(new Double[]{rot,rotPos,diff,diffABS});

        }

        int lowestPos = 999;
        double curLowest = 9999999;

        //Finding the lowest change.
        for(int i = 0; i< closestRange.size(); i++){
            if(curLowest > closestRange.get(i)[3]){
                lowestPos = i;
                curLowest = closestRange.get(i)[3];
            }
        }

        //Returning the inverse movement.
        return closestRange.get(lowestPos)[2] * -1.0;

    }

    private int wheelRotations(){
        return (int)(curStearPos / robotKnowns.StearCountPerDegree);
    }

}