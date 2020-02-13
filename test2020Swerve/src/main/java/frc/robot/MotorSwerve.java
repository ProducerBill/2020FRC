package frc.robot;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.controller.*;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;


public class MotorSwerve{
    private TalonSRX motorDrive;
    private TalonSRX motorStear;
    private Encoder encoderStear;
    private PIDController turnPID;

    public String Name;

    public MotorSwerve(int talonDriveID, int talonStearID, int encoderA, int encoderB)
    {
        motorDrive = new TalonSRX(talonDriveID);
        motorStear = new TalonSRX(talonStearID);

        motorStear.setNeutralMode(NeutralMode.Brake);

        encoderStear = new Encoder(encoderA, encoderB, true, EncodingType.k1X);
        //encoderStear.setPIDSourceType(PIDSourceType.kDisplacement);
        encoderStear.reset();

        //setting up pid
        turnPID = new PIDController(0.2, 0, 0);
        turnPID.setIntegratorRange(-1.0, 1.0);
        turnPID.setTolerance(1);

    }

    public void setDriveSpeed(double speed){
        motorDrive.set(ControlMode.PercentOutput, speed);
    }

    public void setStearAngle(double angle){

        double curStearEncoder = (double)encoderStear.get();

        turnPID.setSetpoint(angle);
        
        double motorSetting = turnPID.calculate(curStearEncoder * 0.93333);

        motorSetting = motorSetting * -.1;

       // System.out.println("ENC: " + curStearEncoder + " | " + curStearEncoder * 0.93333 + " MS: " +
        //        motorSetting + " " + turnPID.atSetpoint());

        //limiting the speed of turning.
        if(motorSetting > 1.0){
            motorSetting = 1.0;
        }
        
        motorStear.set(ControlMode.PercentOutput, motorSetting);
    }

    public void resetEncoders(){
        encoderStear.reset();
    }

    public double getDriveSpeed(){
        return motorDrive.getMotorOutputPercent();
    }

    public double getStearAngle(){
        return (double)encoderStear.get();
    }

}