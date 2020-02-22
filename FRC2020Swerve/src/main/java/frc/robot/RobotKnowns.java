package frc.robot;

public class RobotKnowns{

    public double StearCountPerDegree;     //Count per degree of turn.
    public double StearCountPerRot = 864.0; //Count per rotation of stearing
    public double[] StearCounts = {857.31, 843.77, 857.40, 860.35};     //Stearing Count FL, FR, RL, RR
    public double DriveCountPerRotation = 500.0;        //Count of the meters traveled per rev of wheel.
    public double DriveMin = 0.0;             //Min drive count velocity.
    public double DriveMax;                 //Max drive count velocity. meter / 100ms
    public double VendorDriveMax = 3.5052;  //Meters/Second.
    public double WheelLength = 0.4572;   //Length in meters from Front axial to back.
    public double WheelWidth = 0.5588;   //Length in meters from left to right axial.
    public double WheelDia = 0.1016;       //Wheel dia in meters. 
    public double WheelCircumference;   //Wheel circumference.


    public RobotKnowns(){

        StearCountPerDegree =  StearCountPerRot / 360.0; 
        WheelCircumference = Math.PI * WheelDia;
        DriveMax = ((VendorDriveMax / WheelCircumference) * DriveCountPerRotation) * 0.1;

        //Setting up the stearing counts.
        StearCounts = new double[]{ 864.0, 864.0, 864.0, 864.0 };

    }

    public double getStearCountPerDegree(){
        return 0.0;
    }

    public double getStearCount(String name){
        switch(name){
            case "FL":
            return StearCounts[0];

            case "FR":
            return StearCounts[1];

            case "RL":
            return StearCounts[2];

            case "RR":
            return StearCounts[3];
        }

        return 0.0;
    }

    public double getStearCountPreDegree(String name){
        return getStearCount(name) / 360.0;
    }

}