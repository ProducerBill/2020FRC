package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpiutil.math.MathUtil;
import frc.robot.JoyDriveData;

public class joyController{
    
    public Joystick jController;    //Joystick object for the WPI controller class.
    public double deadZoneLow; // in: distance from zero to ignore
    public double deadZoneHigh; // in: distance from unit circle to ignore

    public double driveScale = 0.5;

    public joyController(int joystickID){

        jController = new Joystick(joystickID);     //Connection to the controller.

        //Setting up analog dead bands.
        deadZoneLow = 0.12;
        deadZoneHigh = 0.9;

    }

    //Gets the drive informaiton. Speed and direction and angle.
    public JoyDriveData getDriveData(double desiredScale){
        
        //Setting the drive scaler setting.
        driveScale = desiredScale;

        JoyDriveData tempData = new JoyDriveData();
        
        double[] leftXY = applyRadialDeadZone(jController.getRawAxis(0), jController.getRawAxis(1) * -1);    //Filtering output from the controller left stick
       
        //Set as axis 2 for sim but 4 for robot.
        double[] rightXY = applyRadialDeadZone(jController.getRawAxis(4), 0);   //Filtering output from the conrolller right  sitck.

        //Getting the angle
        if(leftXY[0] != 0.0 || leftXY[1] != 0.0 || rightXY[0] != 0.0){
            double r = Math.sqrt(Math.pow(leftXY[0], 2) + Math.pow(leftXY[1], 2));

            //Getting the quaderant of the compass that we are working in.
            double quad = getCompassQuad(leftXY[0], leftXY[1]);
            //Angle the control stick is pointing.
            double controlAngle = (Math.asin(leftXY[0]/r)) * (180 / Math.PI);

            //Added to provent divide by 0 error.
            if(Double.isNaN(controlAngle)){
                controlAngle = 0;
            }

            //Adjusting the control angle for the quad the control is in.
            if(quad == 90){
                controlAngle = 180 - controlAngle;
            }

            if(quad == 180){
                controlAngle = 180 + (controlAngle * -1);
            }

            if(quad == 270){
                //controlAngle = 90 + controlAngle;
                controlAngle = 360 + controlAngle;
            }

            //System.out.println("R: " + String.format("%.2f",r) + " |contA: " + String.format("%.3f",controlAngle));

            tempData.driveAngle = controlAngle;
            tempData.driveSpeed = r;
            tempData.rotateSpeed = rightXY[0];
            tempData.leftX = leftXY[0];
            tempData.leftY = leftXY[1];
        }

        return tempData;
    }


    public double[] applyRadialDeadZone(double inputX, double inputY){

        double mag = Math.sqrt(inputX * inputX + inputY * inputY);

        double pOutX = 0;
        double pOutY = 0;
 
        if (mag > deadZoneLow)
        {
            // scale such that output magnitude is in the range [0.0f, 1.0f]
            double legalRange = 1.0f - ((1.0 - deadZoneHigh) + deadZoneLow);
            double normalizedMag = Math.min(1.0f, (mag - deadZoneLow) / legalRange);
            double scale = normalizedMag / mag; 
            pOutX = inputX * (scale * driveScale);
            pOutY = inputY * (scale * driveScale);
        }
        else
        {
            // stick is in the inner dead zone
            pOutX = 0.0f;
            pOutY = 0.0f;
        }

        return new double[] { pOutX, pOutY };

    }

    public JoyDriveData getButtonState(JoyDriveData curJoyData){
        curJoyData.aButton = jController.getRawButton(0);
        curJoyData.bButton = jController.getRawButton(1);
        curJoyData.xButton = jController.getRawButton(2);
        curJoyData.yButton = jController.getRawButton(3);
        curJoyData.bumperLeft = jController.getRawButton(4);
        curJoyData.bumperRight = jController.getRawButton(5);

        return curJoyData;
    }

    public JoyDriveData getTriggerState(JoyDriveData curJoyData){
        curJoyData.triggerLeft = jController.getRawAxis(2);
        curJoyData.triggerRight = jController.getRawAxis(3);

        return curJoyData;
    }

    private double getCompassQuad(double x, double y){
        //=IF(AND(B3>=0,B2>=0),0,IF(AND(B3<0,B2>0),90,IF(AND(B3<=0,B2<=0),180,IF(AND(B3>0,B2<0),270,0))))
        if(y >= 0 && x >= 0){
            return 0;
        }
        else if(y < 0 && x > 0){
            return 90;
        }
        else if(y < 0 && x < 0){
            return 180;
        }
        else if(y >= 0 && x < 0){
            return 270;
        }
        else if(y < 0){
            return 180;
        }
        
        return 0;

        //throw new ArithmeticException("Error Finding compass quad. You should never see this.");
    }

}