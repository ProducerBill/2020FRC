/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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


  private TalonSRX motorStear;
  private TalonSRX motorDrive;

  //Logging items.
  File f;
  BufferedWriter bw;
  FileWriter fw;


  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    //Setting the pid
    TalonSRXConfiguration configDrive = new TalonSRXConfiguration();
    configDrive.slot0.kP = 5;
    configDrive.slot0.kI = 0;
    configDrive.slot0.kD = 0;
    configDrive.slot0.kF = 2;

    TalonSRXConfiguration configStear = new TalonSRXConfiguration();
    configStear.slot0.kP = 10;
    configStear.slot0.kI = 0;
    configStear.slot0.kD = 0;



    //baseWheels.add(new Wheel(2, 1, "FL"));  //Front Left
    //baseWheels.add(new Wheel(4, 3, "FR"));  //Front Right
    //baseWheels.add(new Wheel(8, 7, "RL"));  //Rear Left
    //baseWheels.add(new Wheel(6, 5, "RR"));  //Rear Right

    motorStear = new TalonSRX(1);
    motorStear.configAllSettings(configStear, 10);
    motorStear.setNeutralMode(NeutralMode.Coast);
    motorStear.configSelectedFeedbackSensor(FeedbackDevice.Analog);
    motorStear.setInverted(false);

    motorStear.setSelectedSensorPosition(0, 0, 10);

    motorDrive = new TalonSRX(2);
    motorDrive.configAllSettings(configDrive, 10);
    motorDrive.setNeutralMode(NeutralMode.Coast);
    motorDrive.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);

    motorDrive.setSelectedSensorPosition(0, 0, 10);

    Date date = Calendar.getInstance().getTime();  
    DateFormat dateFormat = new SimpleDateFormat("yyyymmddHHmmss");  
    String strDate = dateFormat.format(date);  

    try {
      f = new File("~
      /" + strDate + "-Output.csv");
      if(!f.exists()){
      f.createNewFile();
    }
      fw = new FileWriter(f);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    bw = new BufferedWriter(fw);


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


@Override
public void teleopInit() {
  // TODO Auto-generated method stub
  super.teleopInit();

  motorStear.setSelectedSensorPosition(0, 0, 10);
  motorDrive.setSelectedSensorPosition(0, 0, 10);

}

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

    Date date = Calendar.getInstance().getTime();  
    DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");  
    String strDate = dateFormat.format(date);  
    

    int driveVelSetting = 0;
    motorDrive.set(ControlMode.Velocity, driveVelSetting);

    int posStear = motorStear.getSelectedSensorPosition();
    int velDrive = motorDrive.getSelectedSensorPosition();

    int stearID = motorStear.getBaseID();
    int driveID = motorDrive.getBaseID();

    System.out.println("Stear: " +  motorStear.getSelectedSensorPosition() +
      "Drive: " + motorDrive.getSelectedSensorPosition());

    //motorStear.set(ControlMode.PercentOutput, 0.2);

    try {
      bw.write(strDate + "," +
              String.valueOf(stearID) + "," +

              String.valueOf(driveID) + "," +
              String.valueOf(posStear) + "," +
              String.valueOf(velDrive) + "," +
              String.valueOf(driveVelSetting) + "\r\n");
      bw.close();
      fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

  }


  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
