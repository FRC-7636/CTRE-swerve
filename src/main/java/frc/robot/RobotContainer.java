// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.PS5Controller;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.command.Group_Cmd.RL1;
import frc.robot.command.Group_Cmd.RL2;
import frc.robot.command.Group_Cmd.RL3;
import frc.robot.command.Group_Cmd.RL4;
import frc.robot.command.Group_Cmd.SetZero;
import frc.robot.command.Group_Cmd.SuckCoral;
import frc.robot.command.Single_Cmd.AutoAim;
import frc.robot.command.Single_Cmd.AutoShootCoral;
import frc.robot.command.Single_Cmd.SetClimberAsHead;
import frc.robot.command.Swerve_CMD.ChassisSpeed;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.Algae;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Candle;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.Coral;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.limelight;
import frc.robot.*;


public class RobotContainer {
    private final PS5Controller Driver_Ctrl = new PS5Controller(1);
    private final XboxController Assist_Ctrl = new XboxController(2);

    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();
    public final Algae algae = new Algae();
    public final Arm arm = new Arm();
    public final Candle candle = new Candle();
    public final Climber climber = new Climber();
    public final Coral coral = new Coral();
    public final Elevator elevator = new Elevator();
    public final limelight limelight = new limelight();

    public final RL1 CMD_RL1 = new RL1(arm, coral, elevator);
    public final RL2 CMD_RL2 = new RL2(arm, coral, elevator);
    public final RL3 CMD_RL3 = new RL3(arm, coral, elevator);
    public final RL4 CMD_RL4 = new RL4(arm, coral, elevator);
    public final SetZero CMD_SetZero = new SetZero(algae, arm, candle, climber, coral, elevator);
    public final SuckCoral suckCoral = new SuckCoral(coral, arm);

    public final SetClimberAsHead CMD_SetClimberAsHead = new SetClimberAsHead(drivetrain);
    public final AutoShootCoral CMD_AutoShootCoral = new AutoShootCoral(coral, arm, elevator);
    public final SuckCoral CMD_SuckCoral = new SuckCoral(coral, arm);

    // public final ChassisSpeed CMD_ChassisSpeed = new ChassisSpeed(drivetrain, elevator);
    
    private SendableChooser<Command> autoChooser;
    private double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
    private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity

    /* Setting up bindings for necessary control of the swerve drive platform */
    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * 0.1).withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
    // private final SwerveRequest.FieldCentric drive = CMD_ChassisSpeed.SwerveDrive;

    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

    private final Telemetry logger = new Telemetry(MaxSpeed);

    private final CommandXboxController joystick = new CommandXboxController(0);

    public RobotContainer() {
        configureBindings();
        Driver_ConfigureBindings();
        Assist_ConfigureBindings();
        
        NamedCommands.registerCommand("SetClimberAsHead", CMD_SetClimberAsHead);
        NamedCommands.registerCommand("AutoShootCoral", CMD_AutoShootCoral);

        autoChooser = AutoBuilder.buildAutoChooser();
        SmartDashboard.putData("auto", autoChooser);
    }

    private void Driver_ConfigureBindings(){

        new JoystickButton(Driver_Ctrl, 1).onTrue(new InstantCommand(algae::Algae_out, algae));
        new JoystickButton(Driver_Ctrl, 2).whileTrue(new InstantCommand(algae::Algae_Back, algae));
    
        new JoystickButton(Driver_Ctrl, 3).whileTrue(new InstantCommand(climber::Up, climber)).onFalse(new InstantCommand(climber::Stop, climber));
        new JoystickButton(Driver_Ctrl, 4).whileTrue(new InstantCommand(climber::Down, climber)).onFalse(new InstantCommand(climber::Stop, climber));
    
        new JoystickButton(Driver_Ctrl, 5).whileTrue(new InstantCommand(algae::suck, algae)).onFalse(new InstantCommand(algae::Stop, algae));
        new JoystickButton(Driver_Ctrl, 6).whileTrue(new InstantCommand(algae::shoot, algae)).onFalse(new InstantCommand(algae::Stop, algae));
    
        new JoystickButton(Driver_Ctrl, 7).whileTrue(new InstantCommand(coral::Coral_Suck, coral)).onFalse(new InstantCommand(coral::Coral_Stop, coral));
        new JoystickButton(Driver_Ctrl, 8).whileTrue(new InstantCommand(coral::Coral_Shoot, coral)).onFalse(new InstantCommand(coral::Coral_Stop, coral));
                        
        new POVButton(Driver_Ctrl, 0).onTrue(CMD_RL1);
        new POVButton(Driver_Ctrl, 90).onTrue(CMD_RL2);
        new POVButton(Driver_Ctrl, 180).onTrue((CMD_RL3));
        new POVButton(Driver_Ctrl, 270).onTrue((CMD_RL4));   
    }

        private void Assist_ConfigureBindings(){
        new JoystickButton(Assist_Ctrl, 1).onTrue(new InstantCommand(coral::Coral_Suck).alongWith(new InstantCommand(arm::Arm_Station)).alongWith(new WaitCommand(0.5)).andThen(CMD_SuckCoral).andThen(new InstantCommand(coral::Coral_Suck)).alongWith(new WaitCommand(0.5)).andThen(new InstantCommand(coral::Coral_Stop)));
        // new JoystickButton(Assist_Ctrl, 1).onTrue(CMD_SuckCoral);
        // new JoystickButton(Assist_Ctrl, 1).onTrue(new InstantCommand(climber::Climb, climber));
        // new JoystickButton(Assist_Ctrl, 1).onTrue(new InstantCommand(elevator::test)).onFalse(new InstantCommand(elevator::ELE_Stop));
        new JoystickButton(Assist_Ctrl, 2).whileTrue(new InstantCommand(climber::Up, climber)).onFalse(new InstantCommand(climber::Stop, climber));
        new JoystickButton(Assist_Ctrl, 3).whileTrue(new InstantCommand(climber::Down, climber)).onFalse(new InstantCommand(climber::Stop, climber));
        
        new JoystickButton(Assist_Ctrl, 4).onTrue(CMD_SetZero);
    
        // new JoystickButton(Assist_Ctrl, 5).onTrue(suckCoral);
        new JoystickButton(Assist_Ctrl, 5).onTrue(new InstantCommand(arm::Arm_Station, arm));
        new JoystickButton(Assist_Ctrl, 6).onTrue(new InstantCommand(drivetrain::ResetPigeon, drivetrain));
        new JoystickButton(Assist_Ctrl, 7).whileTrue(new InstantCommand(coral::L1CoralShoot, coral)).onFalse(new InstantCommand(coral::Coral_Stop, coral));
        // new JoystickButton(Assist_Ctrl, 8).whileTrue(CMD_AutoAim);
    
        new POVButton(Assist_Ctrl, 0).whileTrue(new InstantCommand(elevator::ELE_Up, elevator)).onFalse(new InstantCommand(elevator::ELE_Stop, elevator));
        new POVButton(Assist_Ctrl, 180).whileTrue(new InstantCommand(elevator::ELE_Down, elevator)).onFalse(new InstantCommand(elevator::ELE_Stop, elevator));
        new POVButton(Assist_Ctrl, 90).whileTrue(new InstantCommand(arm::Arm_UP, arm)).onFalse(new InstantCommand(arm::Arm_Stop, arm));
        new POVButton(Assist_Ctrl, 270).whileTrue(new InstantCommand(arm::Arm_DOWN, arm)).onFalse(new InstantCommand(arm::Arm_Stop, arm));
        // new POVButton(Assist_Ctrl, 0).whileTrue(new InstantCommand(elevator::ELE_Up));
        // new POVButton(Assist_Ctrl, 180).whileTrue(new InstantCommand(elevator::ELE_Down));
        // new POVButton(Assist_Ctrl, 90).whileTrue(new InstantCommand(arm::Arm_UP));
        // new POVButton(Assist_Ctrl, 270).True(new InstantCommand(arm::Arm_DOWN));
        }
            
    private void configureBindings() {
        // Note that X is defined as forward according to WPILib convention,
        // and Y is defined as to the left according to WPILib convention.
        drivetrain.setDefaultCommand(
            // Drivetrain will execute this command periodically
            drivetrain.applyRequest(() ->
                drive.withVelocityX(-Driver_Ctrl.getLeftY() * MaxSpeed) // Drive forward with negative Y (forward)
                     .withVelocityY(-Driver_Ctrl.getLeftX() * MaxSpeed) // Drive left with negative X (left)
                     .withRotationalRate(-Driver_Ctrl.getRightX() * MaxAngularRate * 4) // Drive counterclockwise with negative X (left)
            )
        );

        // Driver_Ctrl.a().whileTrue(drivetrain.applyRequest(() -> brake));
        // Driver_Ctrl.b().whileTrue(drivetrain.applyRequest(() ->
        //     point.withModuleDirection(new Rotation2d(-Driver_Ctrl.getLeftY(), -Driver_Ctrl.getLeftX()))
        // ));

        // // Run SysId routines when holding back/start and X/Y.
        // // Note that each routine should be run exactly once in a single log.
        // Driver_Ctrl.back().and(Driver_Ctrl.y()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
        // Driver_Ctrl.back().and(Driver_Ctrl.x()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
        // Driver_Ctrl.start().and(Driver_Ctrl.y()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
        // Driver_Ctrl.start().and(Driver_Ctrl.x()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));

        // // reset the field-centric heading on left bumper press
        // Driver_Ctrl.leftBumper().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));

        drivetrain.registerTelemetry(logger::telemeterize);
    }

    public Command getAutonomousCommand() {    
        return autoChooser.getSelected();
    }
}
