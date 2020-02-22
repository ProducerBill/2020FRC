package frc.robot;

public class JoyDriveData{
    public double driveSpeed;
    public double driveAngle;
    public double rotateSpeed;
    public double leftX;
    public double leftY;

    public boolean aButton;
    public boolean bButton;
    public boolean xButton;
    public boolean yButton;
    public boolean bumperLeft;
    public boolean bumperRight;

    public double triggerLeft;
    public double triggerRight;

    public JoyDriveData(){
        driveSpeed = 0;
        driveAngle = 0;
        rotateSpeed = 0;
        leftX = 0;
        leftY = 0;

        aButton = false;
        bButton = false;
        xButton = false;
        yButton = false;

        triggerLeft = 0;
        triggerRight = 0;
    }
}