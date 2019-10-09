package frc.robot;

public class HolonomicDrive{

    public double motorFrontLeft;
    public double motorFrontRight;
    public double motorRearLeft;
    public double motorRearRight;


    public HolonomicDrive(){

        //Setting the init state of the motors.
        motorFrontLeft = 0.0;
        motorFrontRight = 0.0;
        motorRearLeft = 0.0;
        motorRearRight = 0.0;
    }

    public void inputControl(double x, double y, double z)
    {
        //Getting the disatance the control has been pushed. x = (1 to -1), y = (1 to -1)
        double hypotenuse;
        try{
            hypotenuse = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)); //This is the intensity of the drive forward.
        } catch (Exception e){
            hypotenuse = 0;
        }

        //Getting the quaderant of the compass that we are working in.
        double quad = getCompassQuad(x, y);

        //Angle the control stick is pointing.
        double controlAngle = (Math.asin(x/hypotenuse)) * (180 / Math.PI);

        //Added to provent divide by 0 error.
        if(Double.isNaN(controlAngle)){
            controlAngle = 0;
        }


        //Adjusting the control angle for the quad the control is in.
        if(quad == 90){
            controlAngle = 90 - controlAngle;
        }

        if(quad == 180){
            controlAngle = controlAngle * -1;
        }

        if(quad == 270){
            controlAngle = 90 + controlAngle;
        }




        System.out.println("Quad=" +  String.format("%.3f", quad) + " CA=" +  String.format("%.3f", controlAngle) + 
                            " TA=" +  String.format("%.3f", (quad + controlAngle)) +
                            " Hy=" +  String.format("%.3f", hypotenuse));

        getMotorSpeeds(hypotenuse, quad + controlAngle + 45 , z);
    }

    //Method for finding the quad on the compass out angle it going to be added to.
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

    private void getMotorSpeeds(double hypotenuse, double theta45, double rotation){
        motorFrontLeft = (hypotenuse * Math.sin(Math.toRadians(theta45)) + rotation);
        motorFrontRight = (hypotenuse * Math.cos(Math.toRadians(theta45)) - rotation);
        motorRearLeft = (hypotenuse * Math.cos(Math.toRadians(theta45)) + rotation);
        motorRearRight = (hypotenuse * Math.sin(Math.toRadians(theta45)) - rotation);
        //System.out.println(theta45);
        //System.out.println("FL=" + motorFrontLeft + " FR=" + motorFrontRight + " RL=" + motorRearLeft + " RR=" + motorRearRight);
    }

}