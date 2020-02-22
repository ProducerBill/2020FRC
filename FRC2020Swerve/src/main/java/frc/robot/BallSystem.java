package frc.robot;

import java.time.Period;
import java.util.Date;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.AnalogInput;

public class BallSystem implements Runnable {

    // IR sensors
    private AnalogInput swBallLoader;
    private AnalogInput swBallConveyor;
    private AnalogInput swBallReady;

    private boolean curBallLoaderState = false; // Current state of the Ball Loader sensor
    private boolean curBallConveyorState = false; // Current state of the Conveyor sensor
    private boolean curBallReadyState = false; // Current state of the Ball Ready sensor

    private boolean isBallLoaderRunning = false; // Is the Ball Loader running?
    private boolean isBallConveyorRunning = false; // Is the Ball Conveyor running?

    private Date dateBallLoaderStart;
    private Date dateBallShooterStart;

    private int runTimeBallLoader = 5000; // ms to run ball loader.

    private TalonSRX mBallLoader;
    private TalonSRX mBallConveyor;
    private TalonSRX mBallShootL;
    private TalonSRX mBallShootR;

    private int runState = 1;
    private boolean enableSystem = false;

    private boolean fireBall = false; // Command to fire ball.

    public BallSystem() {
        // Setting up sensors.
        swBallLoader = new AnalogInput(0);
        swBallConveyor = new AnalogInput(1);
        swBallReady = new AnalogInput(2);

        // Setting up the motors.
        mBallLoader = new TalonSRX(12);
        mBallLoader.setInverted(false);
        mBallLoader.setNeutralMode(NeutralMode.Brake);

        mBallConveyor = new TalonSRX(11);
        mBallConveyor.setInverted(true);
        mBallLoader.setNeutralMode(NeutralMode.Brake);

        mBallShootL = new TalonSRX(9);
        mBallShootL.setInverted(true);
        mBallLoader.setNeutralMode(NeutralMode.Coast);

        mBallShootR = new TalonSRX(10);
        mBallShootR.setInverted(true);
        mBallLoader.setNeutralMode(NeutralMode.Coast);

        runState = 1;

    }

    public void controlBallSystem(boolean runState) {
        if (runState == true) {
            this.runState = 1;
        } else {
            this.runState = 0;
        }
    }

    @Override
    public void run() {

        // Init of timers.
        dateBallLoaderStart = new Date();

        while (this.runState == 1) {
            if(enableSystem){

                // Read the the switch state.
                readSwitchState();

                // Updating the time passed on timed items.
                long timePassedBallLoader = new Date().getTime() - dateBallLoaderStart.getTime();
                // System.out.println("Ball ms Passed: " + timePassedBallLoader);

                // If loader is true run the ball loader.
                if (curBallLoaderState == true) {
                    System.out.println("Starting the ball loader.");
                    dateBallLoaderStart = new Date();

                    // Start ball loader.
                    setBallLoaderState(true);
                }

                // If the loader is clear and the loader is running and time has elapsed stop
                // ball loader.
                if (curBallLoaderState == false && isBallLoaderRunning == true
                        && timePassedBallLoader > runTimeBallLoader) {
                    System.out.println("Stopping the ball loader.");
                    setBallLoaderState(false);
                }

                // If conveyor is true and ready is false run belt until ready is true.
                if (curBallLoaderState == true && curBallReadyState == false) {
                    setBallConveyorState(true);
                    
                }

                //Stop the ball if at the ready state.
                if(curBallReadyState == true){
                    setBallConveyorState(false);
                }

                if (fireBall) {
                    controlFireBall();
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private void readSwitchState() {
        
        if (swBallLoader.getValue() < 100) {
            curBallLoaderState = true;
        } else {
            curBallLoaderState = false;
        }

        if (swBallConveyor.getValue() < 100) {
            curBallConveyorState = true;
        } else {
            curBallConveyorState = false;
        }

        if (swBallReady.getValue() < 100) {
            curBallReadyState = true;
        } else {
            curBallReadyState = false;
        }
        

    }

    private void setBallLoaderState(boolean state) {

        isBallLoaderRunning = state;

        if (state) {
            mBallLoader.set(ControlMode.PercentOutput, 1.0);
            System.out.println("Ball Loader Started");
        } else {
             mBallLoader.set(ControlMode.PercentOutput, 0.0);
            System.out.println("Ball Loader Stopped.");
        }
    }

    private void setBallLoaderDirection(boolean state) {
        mBallLoader.setInverted(state);
    }

    private void setBallConveyorState(boolean state) {
        isBallLoaderRunning = state;

        if (state) {
            mBallConveyor.set(ControlMode.PercentOutput, 1.0);
            System.out.println("Ball Conveyor Started");
        } else {
            mBallConveyor.set(ControlMode.PercentOutput, 0.0);
            System.out.println("Ball Conveyor Stopped.");
        }
    }

    private void setBallShooter(boolean state) {
        if (state) {
            mBallShootL.set(ControlMode.PercentOutput, 0.8);
            mBallShootR.set(ControlMode.PercentOutput, 0.8);
        } else {
            mBallShootL.set(ControlMode.PercentOutput, 0.0);
            mBallShootR.set(ControlMode.PercentOutput, 0.0);
        }
    }

    private void controlFireBall() {
        // Spin up shooter
        setBallShooter(true);

        // Wait until speed settles.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //Index conveyor forward.
        setBallConveyorState(true);
    }

    //////////////////////////////////////////
    //User controls
    /////////////////////////////////////////

    public void fireBall(){
        fireBall = true;
    }

    public void setLoaderState(boolean state){
        setBallLoaderState(state);
    }



    public void enable(boolean state){
        enableSystem = state;
    }
    

    public void testSystem(){
        setBallShooter(true);
        setBallLoaderState(true);
        setBallConveyorState(true);
    }

}