package frc.robot;

import java.util.ArrayList;
import java.util.List;

import frc.robot.Wheel;

public class RobotBase {

    private double curAngle;    //Angle the base is pointed orintated to the field. 0 = stright forward.
    private List<Wheel> baseWheels;

    private RobotKnowns robotKnowns;

    public RobotBase(){
        robotKnowns = new RobotKnowns();

        baseWheels = new ArrayList();

        setupWheels();
    }

    private void setupWheels(){
        
        baseWheels.add(new Wheel(1, 2, "FL"));  //Front Left
        baseWheels.add(new Wheel(3, 4, "FR"));  //Front Right
        baseWheels.add(new Wheel(5, 6, "RL"));  //Rear Left
        baseWheels.add(new Wheel(7, 8, "RR"));  //Rear Right
        
    }

    public void driveBase(JoyDriveData data){
        
        double r = Math.sqrt ((robotKnowns.WheelLength * robotKnowns.WheelLength) + (robotKnowns.WheelWidth * robotKnowns.WheelWidth));
    
        double a = data.leftX - data.rotateSpeed * (robotKnowns.WheelLength / r);
        double b = data.leftX + data.rotateSpeed * (robotKnowns.WheelLength / r);
        double c = data.leftY - data.rotateSpeed * (robotKnowns.WheelWidth / r);
        double d = data.leftY + data.rotateSpeed * (robotKnowns.WheelWidth / r);
    
        double frontLeftSpeed = Math.sqrt ((b * b) + (c * c));
        double frontRightSpeed = Math.sqrt ((b * b) + (d * d));
        double backLeftSpeed = Math.sqrt ((a * a) + (c * c));
        double backRightSpeed = Math.sqrt ((a * a) + (d * d));
        
        
        //Stop adjusting the angle if the speed is 0
        if(backRightSpeed != 0 && backLeftSpeed != 0 && frontRightSpeed != 0 && frontLeftSpeed != 0){
            
            double frontRightAngle = 0;
            double frontLeftAngle = 0;
            double backRightAngle = 0;
            double backLeftAngle = 0;

            if(data.leftX > 0){
                frontRightAngle = Math.atan2 (b, c) * (180 / Math.PI);
                frontLeftAngle = Math.atan2 (b, d) * (180 / Math.PI);
                backRightAngle = Math.atan2 (a, c) * (180 / Math.PI);
                backLeftAngle = Math.atan2 (a, d) * (180 / Math.PI);
            } else {
                frontRightAngle = 360 + (Math.atan2 (b, c) * (180 / Math.PI));
                frontLeftAngle = 360 + (Math.atan2 (b, d) * (180 / Math.PI));
                backRightAngle = 360 + (Math.atan2 (a, c) * (180 / Math.PI));
                backLeftAngle = 360 + (Math.atan2 (a, d) * (180 / Math.PI));
            }

            //Normalize the drive speed.
            if(frontLeftSpeed > 1.0 || frontRightSpeed > 1.0 || backLeftSpeed > 1.0 || backRightSpeed > 1.0){
                double greatest = 0;
                if(frontLeftSpeed > greatest){
                    greatest = frontLeftSpeed;
                }

                if(frontRightSpeed > greatest){
                    greatest = frontRightSpeed;
                }

                if(backLeftSpeed > greatest){
                    greatest = backLeftSpeed;
                }

                if(backRightSpeed > greatest){
                    greatest = backRightSpeed;
                }

                //Finding the greatest differences.
                double diff = greatest = 1.0;

                //Updating the speeds.
                frontLeftSpeed = frontLeftSpeed - diff;
                frontRightSpeed = frontRightSpeed - diff;
                backLeftSpeed = backLeftSpeed - diff;
                backRightSpeed = backRightSpeed - diff;

            }



            //Debug output.
            /*
            System.out.println("FLS: " + String.format("%.2f", frontLeftSpeed) + " FLA: " + (int)(frontLeftAngle)); // * CountToDeg));
            System.out.println("FRS: " + String.format("%.2f", frontRightSpeed) + " FRA: " + (int)(frontRightAngle));// * CountToDeg));
            System.out.println("BLS: " + String.format("%.2f", backLeftSpeed) + " BLA: " + (int)(backLeftAngle));// * CountToDeg));
            System.out.println("BRS: " + String.format("%.2f", backRightSpeed) + " BRA: " +(int)(backRightAngle));// * CountToDeg));
            */

            //Setting the results to each wheel.
            baseWheels.get(0).setSpeed(frontLeftSpeed);
            baseWheels.get(0).setStearAngle(frontLeftAngle);

            
            baseWheels.get(1).setSpeed(frontRightSpeed);
            baseWheels.get(1).setStearAngle(frontRightAngle);

            baseWheels.get(2).setSpeed(backLeftSpeed);
            baseWheels.get(2).setStearAngle(backLeftAngle);

            baseWheels.get(3).setSpeed(backRightSpeed);
            baseWheels.get(3).setStearAngle(backRightAngle);
            
        
        }


        
    }


}