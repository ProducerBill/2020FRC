package frc.robot;

import java.util.ArrayList;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;

import frc.robot.RobotKnowns;

public class Wheel {

    private TalonSRX MotorDrive;    //Motor driver for moving the wheel forward and backward.
    private TalonSRX MotorStear;    //Motor driver for pointing the wheel in the desired direction.
    private String Name;            //Name of the wheel.
    
    private RobotKnowns robotKnowns;

    private double curStearPos;
    private double curDriveVol;

    public Wheel(int driveID, int stearID, String name){
        
        robotKnowns = new  RobotKnowns();

        Name = name;    //Setting the name.
        
        //Setting up the motor driver 
        MotorDrive = new TalonSRX(driveID);
        MotorStear = new TalonSRX(stearID);

        //Setting the pid
        TalonSRXConfiguration configDrive = new TalonSRXConfiguration();
        configDrive.slot0.kP = 4;
        configDrive.slot0.kI = 0;
        configDrive.slot0.kD = 0;
        configDrive.slot0.kF = 2;

        TalonSRXConfiguration configStear = new TalonSRXConfiguration();
        configStear.slot0.kP = 10;
        configStear.slot0.kI = 0;
        configStear.slot0.kD = 0;

        //Applying configuration.
        MotorDrive.configAllSettings(configDrive, 10);
        MotorStear.configAllSettings(configStear, 10);

        MotorDrive.setSensorPhase(true);

        //Setting up the driver direction.
        MotorDrive.setInverted(false);
        MotorStear.setInverted(false);

        //Setting the breaking and coast modes.
        MotorDrive.setNeutralMode(NeutralMode.Coast);
        MotorStear.setNeutralMode(NeutralMode.Brake);

        //Setting up the encoders.
        //MotorDrive.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);     //Tried an craft drifted.
        MotorDrive.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);    //2nd Try.
        MotorStear.configSelectedFeedbackSensor(FeedbackDevice.Analog);      

        //Resetting the encoders
        MotorDrive.setSelectedSensorPosition(0, 0, 10);
        MotorStear.setSelectedSensorPosition(0, 0, 10);
        

    }

    public void diableWheel(){
        MotorStear.setNeutralMode(NeutralMode.Coast);

    }

    //Input the desire speed forward.
    public void setDriveVelocity(double controllerSpeed){

        //Converting the controller speed to the desired velocity.
        curDriveVol = robotKnowns.DriveMax * controllerSpeed * -1;

        //Setting the motor to the desire velocity.
        MotorDrive.set(ControlMode.Velocity, (int)curDriveVol);

        System.out.println(this.Name + " DV: " + getDriveVelocity() +  " DC: " + String.format("%.2f",curDriveVol));
    }

    public int getDriveVelocity(){
        return MotorDrive.getSelectedSensorVelocity(0);
    }

    public void setDrivePower(double powerInput){
        MotorDrive.set(ControlMode.PercentOutput, powerInput);
    }

    //Set the desire angle direction.
    public void setStearAngle(double desiredAngle){

        //Convert from angel to needed count.
        double desiredCount = desiredAngle * robotKnowns.StearCountPerDegree;

        //Getting the current encoder position.
        int curReading = getStearPos();
        curStearPos = (double)curReading;

        //Setting the current possition
        curStearPos = curStearPos + findClosest(desiredCount);//desiredAngle);

        
        //Debug output.
        // System.out.println(this.Name + " RC: " + wheelRotations() + " Ang: " +  String.format("%.3f",desiredAngle) + " CP: " + String.format("%.3f", curStearPos) + 
        //             " DS: " + curDriveVol + " | " + curReading);

        

        //Setting the stearing position.
        MotorStear.set(ControlMode.Position, (int)curStearPos);
    }

    public int getStearPos(){
        return MotorStear.getSelectedSensorPosition();
        //return (int)curStearPos;
    }

    private double findClosest(double sp){

        ArrayList<Double[]> closestRange = new ArrayList<>();

        //Setting up the solution table

        for(int i = wheelRotations() - 2; i< wheelRotations() + 2; i++){
            double rot = i;
            double rotPos = (robotKnowns.getStearCount(Name) * rot) + sp;
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
        return (int)(curStearPos / (robotKnowns.getStearCountPreDegree(Name) * 360)); //robotKnowns.getStearCountPerDegree(Name) * 360));
    }

}