/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.joyController;
import frc.robot.JoyDriveData;
import frc.robot.ServerTCP;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private joyController jCDriver;   //Drivers controller
  private joyController jCShooter;  //Shooter controller
  private RobotBase rbase;    //Robot chassis.
  private Runnable bSystem; //Ball shooter system.
  private Runnable nServer;  //Com server

  private VictorSPX lift;
  private VictorSPX poll;



  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    //Setting up the controllers.
    
    
    jCDriver = new joyController(0);  //Driver controller.

    jCShooter = new joyController(1); //Setting up the shooter controller.


    rbase = new RobotBase();    //Robot chassis.
    
    
    bSystem = new BallSystem();  //Starting ball system.
    ((BallSystem) bSystem).controlBallSystem(true); // Allow the system to run ball system.
    Thread threadBSystem = new Thread(bSystem);
    threadBSystem.start();

    //Setting up the server.
    nServer = new ServerTCP();
    Thread threadNServer = new Thread(nServer);
    //threadNServer.start();
   
    lift = new VictorSPX(14);
    poll = new VictorSPX(13);

    poll.setInverted(false);

  }

  public void disabledInit() {
    // TODO Auto-generated method stub
    super.disabledInit();

    ((BallSystem) bSystem).enable(false);  //Shutting down ball system.


    rbase.disableBase();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);

  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }

    /* //If we use ascii formatted commands.
    String command = ((ServerTCP) nServer).getLatestLine();
    if(command != ""){
      System.out.println("Command:" + command);
    }
*/

    byte[] command = ((ServerTCP) nServer).getCurBufferData();

    if(command != null){
      System.out.println(command.toString());
    }
    


  }


  @Override
  public void teleopInit() {
    // TODO Auto-generated method stub
    super.teleopInit();

    ((BallSystem) bSystem).enable(true);  //Starting ball system.
  }


  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

    //Getting the analolg stick data.
    JoyDriveData curDriverData = jCDriver.getDriveData(0.5);
    JoyDriveData curShooterData = jCShooter.getDriveData(0.25);

    //Getting the controller button states.
    curDriverData = jCDriver.getButtonState(curDriverData); //Getting the button state.
    curShooterData = jCShooter.getButtonState(curShooterData);  //Getting the shooter button state.


    //if(curShooterData.xButton == true){
      rbase.driveBase(curDriverData);
    //} else {
    //  rbase.driveBase(curShooterData);
    //}
    //This needs to be moved to the other controller.
    if(curShooterData.aButton == true){
      System.out.println("Shoot");
      ((BallSystem) bSystem).fireBall();
    }

    if(curShooterData.bButton == true){
      ((BallSystem) bSystem).setConveyorDirection(!((BallSystem) bSystem).conveyorDir);
    }

    if(curShooterData.yButton == true){
      lift.set(ControlMode.PercentOutput, 0.5);
    } else {
      lift.set(ControlMode.PercentOutput, 0.0);
    }

    if(curShooterData.xButton == true){
      poll.set(ControlMode.PercentOutput, 0.5);
    } else {
      poll.set(ControlMode.PercentOutput, 0.0);
    }

  }

@Override
public void testInit() {
  // TODO Auto-generated method stub
  super.testInit();

  poll.setInverted(true);


}

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {

    //Getting the analolg stick data.
    JoyDriveData curDriverData = jCDriver.getDriveData(0.5);
    JoyDriveData curShooterData = jCShooter.getDriveData(0.25);

    //Getting the controller button states.
    curDriverData = jCDriver.getButtonState(curDriverData); //Getting the button state.
    curShooterData = jCShooter.getButtonState(curShooterData);  //Getting the shooter button state.

    
    if(curShooterData.xButton == true){
      poll.set(ControlMode.PercentOutput, 0.5);
    } else {
      poll.set(ControlMode.PercentOutput, 0.0);
    }
    
  }
}
