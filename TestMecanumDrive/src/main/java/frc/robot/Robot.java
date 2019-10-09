/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.drive.RobotDriveBase;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

import frc.robot.HolonomicDrive;

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

//Talons for dirving
  public TalonSRX motorDriveRightFront;  //RF x + y + r
  public TalonSRX motorDriveRightRear; //RR -x + y + -r
  public TalonSRX motorDriveLeftFront; //LF -x + y + r
  public TalonSRX motorDriveLeftRear; //LR x + y + -r

  public HolonomicDrive driveSystem;

  private Joystick controllerDriving;

  int conDriverLeftStickAxis = 1;
  int conDiverRightStickAxis = 5;

  double[] leftSpeedBuffer = new double[7];
  double[] rightSpeedBuffer = new double[7];
  int counterLeftSpeed = 0;
  int counterRightSpeed = 0;


  
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    /*
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    */
    
    motorDriveRightRear = new TalonSRX(0);
    motorDriveRightRear.set(ControlMode.PercentOutput, 0);
    motorDriveRightRear.configFactoryDefault();
    motorDriveRightRear.setNeutralMode(NeutralMode.Brake);
    motorDriveRightRear.setInverted(true); //The four of these make it so that we dont have to re-wire, just a software fix
    
    motorDriveLeftFront = new TalonSRX(5);
    motorDriveLeftFront.set(ControlMode.PercentOutput, 0);
    motorDriveLeftFront.configFactoryDefault();
    motorDriveLeftFront.setNeutralMode(NeutralMode.Brake);
    motorDriveLeftFront.setInverted(false);

    motorDriveLeftRear = new TalonSRX(1);
    motorDriveLeftRear.set(ControlMode.PercentOutput, 0);
    motorDriveLeftRear.configFactoryDefault();
    motorDriveLeftRear.setNeutralMode(NeutralMode.Brake);
    motorDriveLeftRear.setInverted(false);

    motorDriveRightFront = new TalonSRX(2);
    motorDriveRightFront.set(ControlMode.PercentOutput, 0);
    motorDriveRightFront.configFactoryDefault();
    motorDriveRightFront.setNeutralMode(NeutralMode.Brake);
    motorDriveRightFront.setInverted(true);

    controllerDriving = new Joystick(0);

    driveSystem = new HolonomicDrive();
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
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

    //Dead bands.
    double x = controllerDriving.getRawAxis(0);
    double y = controllerDriving.getRawAxis(1) * -1;
    double z = controllerDriving.getRawAxis(4);

    if(x < 0.2 && x > -0.2){ x = 0.0;}
    if(y < 0.2 && y > -0.2){y = 0.0;}
    if(z < 0.2 && z > -0.2){ z = 0.0;}

    //System.out.println(x + ", " + y + ", " + z);

    //double[] motorSpeeds = OperatorControl(controllerDriving.getRawAxis(4) / 2,
    //                                       controllerDriving.getRawAxis(5) / 2,
    //                                        0);//controllerDriving.getRawAxis(0) / 2);


    //double[] motorSpeeds = holonomicCalc(x / 2, y / 2, z/2);

    driveSystem.inputControl(-.2, .01, z);
    
    
    motorDriveLeftFront.set(ControlMode.PercentOutput, driveSystem.motorFrontLeft);
    motorDriveRightFront.set(ControlMode.PercentOutput, driveSystem.motorFrontRight);
    motorDriveLeftRear.set(ControlMode.PercentOutput, driveSystem.motorRearLeft);
    motorDriveRightRear.set(ControlMode.PercentOutput, driveSystem.motorRearRight);
    
  }


  double[] OperatorControl( double x, double y, double z) {
    double r = Math.hypot(x, y);
    double robotAngle = Math.atan2(y, x) - Math.PI / 4;
    double rightX = x;
    final double v1 = r * Math.cos(robotAngle) + rightX;
    final double v2 = r * Math.sin(robotAngle) - rightX;
    final double v3 = r * Math.sin(robotAngle) + rightX;
    final double v4 = r * Math.cos(robotAngle) - rightX;

    /*
    leftFront.setPower(v1);
    rightFront.setPower(v2);
    leftRear.setPower(v3);
    rightRear.setPower(v4);
    */
    return new double[] {v1, v2, v3, v4};
  }

double[] holonomicCalc(double x, double y, double z){
  double frontLeft = -y - x - z;//(-1 * y) + (-1 * x) + (-1 *z);
  double frontRight = y - x - z;//y + (-1 * x) + (-1 * z);
  double rearLeft = y + x - z;  //y + x + (-1 * z);
  double rearRight = -y + x - z; //(-1 * y) + x + (-1 * z);

  System.out.println(frontLeft + ", " + frontRight + ", " + rearLeft + ", " + rearRight);

  return new double[] {frontLeft, frontRight, rearLeft, rearRight };

}

double[] mecanDrive(){
  return new double[]{};
}

double rangeClip(double input){
  if(input < -1){
    return -1;
  }

  if(input > 1){
    return 1;
  }

  return 0;
}

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
