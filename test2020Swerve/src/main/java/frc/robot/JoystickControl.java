package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

public class JoystickControl {

    public double L = 0.4572;   //Length from Front axial to back.
    public double W = 0.5588;   //Length from left to right axial.

    public double pOutX;      // out: resulting stick x value
    public double pOutY;      // out: resulting stick y value
    public double x;           // in: initial stick x value
    public double y;           // in: initial stick x value
    public double deadZoneLow; // in: distance from zero to ignore
    public double deadZoneHigh; // in: distance from unit circle to ignore

    public double CountToDeg; //Degree divided by the count we got

    public Joystick DriveStick;

    public double backRightSpeed = 0.0;
    public double backLeftSpeed = 0.0;
    public double frontRightSpeed = 0.0;
    public double frontLeftSpeed = 0.0;

    public double backRightAngle = 0.0;
    public double backLeftAngle = 0.0;
    public double frontRightAngle = 0.0;
    public double frontLeftAngle = 0.0;


    public JoystickControl(){

        DriveStick = new Joystick(0);
        deadZoneLow = .10;
        deadZoneHigh = .90;

        CountToDeg = 1.2222;


    }

    public void GetDriveData() {
        
        double LeftDriveStickX = DriveStick.getRawAxis(0) * -1;
        double LeftDriveStickY = DriveStick.getRawAxis(1) * 1;
        double RightDriveStickX = DriveStick.getRawAxis(4) * -1;

        double[] LeftDriveStick = applyRadialDeadZone(LeftDriveStickX, LeftDriveStickY);
        double[] RightDriveStick = applyRadialDeadZone(RightDriveStickX, 0);

        drive(LeftDriveStick[0], LeftDriveStick[1], RightDriveStick[0]);

    }

    public void drive (double x1, double y1, double x2) {
        double r = Math.sqrt ((L * L) + (W * W));
        y1 *= -1;
    
        double a = x1 - x2 * (L / r);
        double b = x1 + x2 * (L / r);
        double c = y1 - x2 * (W / r);
        double d = y1 + x2 * (W / r);
    
        this.backRightSpeed = Math.sqrt ((a * a) + (d * d)) * -1;
        this.backLeftSpeed = Math.sqrt ((a * a) + (c * c)) * -1;
        this.frontRightSpeed = Math.sqrt ((b * b) + (d * d));
        this.frontLeftSpeed = Math.sqrt ((b * b) + (c * c));
    
        //Stop adjusting the angle if the speed is 0
        if(this.backRightSpeed != 0 && this.backLeftSpeed != 0 && this.frontRightSpeed != 0 && this.frontLeftSpeed != 0){
            this.backRightAngle = ((Math.atan2 (a, d) / Math.PI) * 180) * -1;
            this.backLeftAngle = ((Math.atan2 (a, c) / Math.PI) * 180) * -1;
            this.frontRightAngle = 180 - ((Math.atan2 (b, d) / Math.PI) * 180);
            this.frontLeftAngle = 180 - ((Math.atan2 (b, c) / Math.PI) * 180);
        }

        //Debug output.
        System.out.println("BRS: " + String.format("%.2f", backRightSpeed) + " BRA: " +(int)(backRightAngle));// * CountToDeg));
        System.out.println("BLS: " + String.format("%.2f", backLeftSpeed) + " BLA: " + (int)(backLeftAngle));// * CountToDeg));
        System.out.println("FRS: " + String.format("%.2f", frontRightSpeed) + " FRA: " + (int)(frontRightAngle));// * CountToDeg));
        System.out.println("FLS: " + String.format("%.2f", frontLeftSpeed) + " FLA: " + (int)(frontLeftAngle)); // * CountToDeg));

        
        

    }

    public double[] applyRadialDeadZone(double inputX, double inputY){

        double mag = Math.sqrt(inputX * inputX + inputY * inputY);
 
        if (mag > deadZoneLow)
        {
            // scale such that output magnitude is in the range [0.0f, 1.0f]
            double legalRange = 1.0f - ((1.0 - deadZoneHigh) + deadZoneLow);
            double normalizedMag = Math.min(1.0f, (mag - deadZoneLow) / legalRange);
            double scale = normalizedMag / mag; 
            pOutX = inputX * scale;
            pOutY = inputY * scale;
        }
        else
        {
            // stick is in the inner dead zone
            pOutX = 0.0f;
            pOutY = 0.0f;
        }

        return new double[] { pOutX, pOutY };

    }
}

