// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;


import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import com.ctre.phoenix6.swerve.SwerveRequest;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PS5Controller;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import static edu.wpi.first.units.Units.*;

import frc.robot.command.Aim_Cmd.AutoReefLevel;
import frc.robot.command.Aim_Cmd.Reef1;
import frc.robot.command.Auto_Cmd.AutoAim;
import frc.robot.command.Auto_Cmd.AutoShootCoral;
import frc.robot.command.Auto_Cmd.AutoSuckCoral;
import frc.robot.command.Auto_Cmd.L3AutoShootCoral;
import frc.robot.command.Group_Cmd.Barge;
import frc.robot.command.Group_Cmd.RL1;
import frc.robot.command.Group_Cmd.RL2;
import frc.robot.command.Group_Cmd.RL3;
import frc.robot.command.Group_Cmd.RL4;
import frc.robot.command.Group_Cmd.SetZero;
import frc.robot.command.Group_Cmd.SuckCoral;
import frc.robot.command.Group_Cmd.CoralStation;
import frc.robot.command.Single_Cmd.SetClimberAsHead;
import frc.robot.command.Swerve_CMD.SmartDrive;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.Algae;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Candle;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.Coral;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.limelight;
import static frc.robot.TargetChooser.reefMap;

public class RobotContainer {
    private final PS5Controller Driver_Ctrl = new PS5Controller(1);
    private final XboxController Assist_Ctrl = new XboxController(2);
    private final XboxController test = new XboxController(5);

    private final Joystick P1 = new Joystick(3); // BIG BUTTON
    private final Joystick P2 = new Joystick(4); // REEF BUTTON
    
    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();
    public final Algae algae = new Algae();
    public final Arm arm = new Arm();
    public final Candle candle = new Candle();
    // public final Climber climber = new Climber();
    public final Coral coral = new Coral();
    public final Elevator elevator = new Elevator();
    public final limelight limelight = new limelight();
    public final TargetChooser targetChooser = new TargetChooser();

    // Group Command
    public final Barge CMD_Barege = new Barge(arm, coral, elevator);
    public final RL1 CMD_RL1 = new RL1(arm, coral, elevator);
    public final RL2 CMD_RL2 = new RL2(arm, coral, elevator);
    public final RL3 CMD_RL3 = new RL3(arm, coral, elevator);
    public final RL4 CMD_RL4 = new RL4(arm, coral, elevator);
    public final SetZero CMD_SetZero = new SetZero(arm, coral, elevator);
    public final CoralStation CoralStation = new CoralStation(coral, arm);
    public final SuckCoral suckCoral = new SuckCoral(arm, coral);

    // Single Command
    // public final CoralShoot CMD_CoralShoot = new CoralShoot(coral, elevator);

    // Auto Command
    public final AutoAim CMD_AutoAim = new AutoAim(drivetrain);
    public final SetClimberAsHead CMD_SetClimberAsHead = new SetClimberAsHead(drivetrain);
    public final AutoShootCoral CMD_AutoShootCoral = new AutoShootCoral(coral, arm, elevator);
    public final L3AutoShootCoral CMD_L3AutoShootCoral = new L3AutoShootCoral(coral, arm, elevator);
    public final CoralStation CMD_CoralStation = new CoralStation(coral, arm);
    public final AutoSuckCoral CMD_AutoSuckCoral = new AutoSuckCoral(coral, arm);

    // ReefAim Command
    public final Reef1 CMD_Reef1 = new Reef1(drivetrain);

    // Swerve Command
    public final SmartDrive CMD_SmartDrive = new SmartDrive(drivetrain, elevator, Driver_Ctrl, Driver_Ctrl);

    private SendableChooser<Command> autoChooser;
    private double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
    private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity

    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

    private final Telemetry logger = new Telemetry(MaxSpeed);

    private final CommandXboxController joystick = new CommandXboxController(0);

    private Supplier<Command> DTP_CMD = () -> targetChooser.driveToClosestReef(drivetrain);

    private int reefLevel = 0;
    private Supplier<Integer> reefLevelSupplier = () -> {return reefLevel;};

    public RobotContainer() {
        configureBindings();
        Driver_ConfigureBindings();
        Assist_ConfigureBindings();
        P1_ConfigureBingings();
        P2_ConfigureBindings();
        
        NamedCommands.registerCommand("SetClimberAsHead", CMD_SetClimberAsHead);
        NamedCommands.registerCommand("AutoShootCoral", CMD_AutoShootCoral);
        NamedCommands.registerCommand("L3AutoShootCoral", CMD_L3AutoShootCoral);
        NamedCommands.registerCommand("AutoSuckCoral", CMD_AutoSuckCoral);
        NamedCommands.registerCommand("RL4", CMD_RL4);
        NamedCommands.registerCommand("RL3", CMD_RL3);
        NamedCommands.registerCommand("RL2", CMD_RL2);
        NamedCommands.registerCommand("RL1", CMD_RL1);
        NamedCommands.registerCommand("CoralSuck", new InstantCommand(coral::Coral_Suck));
        NamedCommands.registerCommand("AutoAim", Commands.defer(DTP_CMD, Set.of(drivetrain)));

        autoChooser = AutoBuilder.buildAutoChooser();
        SmartDashboard.putData("auto", autoChooser);

        DriverStation.silenceJoystickConnectionWarning(true);
    }

    private void Driver_ConfigureBindings() {
        new JoystickButton(Driver_Ctrl, 1).onTrue(new InstantCommand(algae::Algae_out, algae));
        new JoystickButton(Driver_Ctrl, 2).whileTrue(new InstantCommand(algae::Algae_Back, algae));
        // new JoystickButton(Driver_Ctrl, 3).whileTrue(new InstantCommand(climber::Up, climber))
        //                                                .onFalse(new InstantCommand(climber::Stop, climber));
        // new JoystickButton(Driver_Ctrl, 4).whileTrue(new InstantCommand(climber::Down, climber))
        //                                                .onFalse(new InstantCommand(climber::Stop, climber));
        new JoystickButton(Driver_Ctrl, 4).whileTrue(CoralStation);
        // new JoystickButton(Driver_Ctrl, 5).whileTrue(new InstantCommand(algae::suck, algae))
        //                                                .onFalse(new InstantCommand(algae::Stop, algae));
        new JoystickButton(Driver_Ctrl, 6).whileTrue(new InstantCommand(algae::shoot, algae))
                                                        .onFalse(new InstantCommand(algae::Stop, algae));
        new JoystickButton(Driver_Ctrl, 7).whileTrue(new InstantCommand(coral::Coral_Suck, coral))
                                                       .onFalse(new InstantCommand(coral::Coral_Stop, coral));
        new JoystickButton(Driver_Ctrl, 8).whileTrue(new InstantCommand(coral::Coral_Shoot))
                                                       .onFalse(new InstantCommand(coral::Coral_Stop, coral));

        new POVButton(Driver_Ctrl, 0).onTrue(new InstantCommand(() -> {
            switch (reefLevelSupplier.get()) {
                case 1:
                  arm.Arm_RL1();  
                  elevator.ELE_RL1();
                  break;
                case 2:
                  arm.Arm_RL2();
                  elevator.ELE_RL2();
                  break;
                case 3:
                  arm.Arm_RL3();
                  elevator.ELE_RL3();
                  break;
                case 4:
                  arm.Arm_RL4();
                  elevator.ELE_RL4();
                  break;
              }
        }, elevator));
        new POVButton(Driver_Ctrl, 90).onTrue(CMD_RL2);
        new POVButton(Driver_Ctrl, 180).onTrue(CMD_RL3);
        new POVButton(Driver_Ctrl, 270).onTrue(CMD_RL4);
    }

    private void Assist_ConfigureBindings() {
        // new JoystickButton(Assist_Ctrl, 2).whileTrue(new InstantCommand(climber::Up,climber))
        //                                                .onFalse(new InstantCommand(climber::Stop, climber));
        // new JoystickButton(Assist_Ctrl, 3).whileTrue(new InstantCommand(climber::Down, climber))
        //                                                .onFalse(new InstantCommand(climber::Stop, climber));
        new JoystickButton(Assist_Ctrl, 4).onTrue(CMD_SetZero);
        new JoystickButton(Assist_Ctrl, 5).onTrue(new InstantCommand(arm::Arm_Station, arm));
        new JoystickButton(Assist_Ctrl, 6).onTrue(new InstantCommand(drivetrain::ResetPigeon, drivetrain));
        new JoystickButton(Assist_Ctrl, 7).whileTrue(new InstantCommand(coral::L1CoralShoot, coral))
                                                       .onFalse(new InstantCommand(coral::Coral_Stop, coral));

        new POVButton(Assist_Ctrl, 0).whileTrue(new InstantCommand(elevator::ELE_Up, elevator))
                                           .onFalse(new InstantCommand(elevator::ELE_Stop, elevator));
        new POVButton(Assist_Ctrl, 180).whileTrue(new InstantCommand(elevator::ELE_Down, elevator))
                                             .onFalse(new InstantCommand(elevator::ELE_Stop, elevator));
        new POVButton(Assist_Ctrl, 90).whileTrue(new InstantCommand(arm::Arm_UP, arm))
                                            .onFalse(new InstantCommand(arm::Arm_Stop, arm));
        new POVButton(Assist_Ctrl, 270).whileTrue(new InstantCommand(arm::Arm_DOWN, arm))
                                             .onFalse(new InstantCommand(arm::Arm_Stop, arm));
    }

    private void P1_ConfigureBingings(){
        Optional<Alliance> alliance = DriverStation.getAlliance();
        if (alliance.isPresent()) {
            if (alliance.get() == Alliance.Red) {
                new JoystickButton(P1, 5).whileTrue(
                    drivetrain.driveToPose(reefMap.get(2).get(0))
                    .alongWith(new CoralStation(coral, arm))
                );
                new JoystickButton(P1, 6).whileTrue(
                    drivetrain.driveToPose(reefMap.get(1).get(0))
                    .alongWith(new AutoSuckCoral(coral, arm))
                );
            }
            else{
                new JoystickButton(P1, 5).whileTrue(
                    drivetrain.driveToPose(reefMap.get(12).get(0))
                    .alongWith(new CoralStation(coral, arm))
                );
                new JoystickButton(P1, 6).whileTrue(
                    drivetrain.driveToPose(reefMap.get(13).get(0))
                    .alongWith(new CoralStation(coral, arm))
                );
            }
        }
        new JoystickButton(P1, 9).whileTrue(CMD_Barege);
    }
    
    private void P2_ConfigureBindings(){
       Optional<Alliance> alliance = DriverStation.getAlliance();
        if (alliance.isPresent()) {
            System.out.printf("Alliance is now %s\n", alliance.get().name());
            if (alliance.get() == Alliance.Red) {
                new JoystickButton(P2, 1).whileTrue(
                    drivetrain.driveToPose(reefMap.get(7).get(0))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 7, 0, reefLevelSupplier))
                    .alongWith(new PrintCommand("=========="))
                );
                new JoystickButton(P2, 2).whileTrue(
                    drivetrain.driveToPose(reefMap.get(7).get(1))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 7, 1, reefLevelSupplier))
                );
                new JoystickButton(P2, 3).whileTrue(
                    drivetrain.driveToPose(reefMap.get(8).get(0))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 8, 0, reefLevelSupplier))
                );
                new JoystickButton(P2, 4).whileTrue(
                    drivetrain.driveToPose(reefMap.get(8).get(1))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 8, 1, reefLevelSupplier))
                );
                new JoystickButton(P2, 5).whileTrue(
                    drivetrain.driveToPose(reefMap.get(9).get(0))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 9, 0, reefLevelSupplier))
                );
                new JoystickButton(P2, 6).whileTrue(
                    drivetrain.driveToPose(reefMap.get(9).get(1))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 9, 1, reefLevelSupplier))
                );
                new JoystickButton(P2, 7).whileTrue(
                    drivetrain.driveToPose(reefMap.get(10).get(0))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 10, 0, reefLevelSupplier))
                );
                new JoystickButton(P2, 8).whileTrue(
                    drivetrain.driveToPose(reefMap.get(10).get(1))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 10, 1, reefLevelSupplier))
                );
                new JoystickButton(P2, 9).whileTrue(
                    drivetrain.driveToPose(reefMap.get(11).get(0))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 11, 0, reefLevelSupplier))
                );
                new JoystickButton(P2, 10).whileTrue(
                    drivetrain.driveToPose(reefMap.get(11).get(1))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 11, 1, reefLevelSupplier))
                );
                new JoystickButton(P2, 11).whileTrue(
                    drivetrain.driveToPose(reefMap.get(6).get(0))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 6, 0, reefLevelSupplier))
                );
                new JoystickButton(P2, 12).whileTrue(
                    drivetrain.driveToPose(reefMap.get(6).get(1))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 6, 1, reefLevelSupplier))
                );
            } else {
                new JoystickButton(P2, 1).whileTrue(
                    drivetrain.driveToPose(reefMap.get(18).get(0))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 18, 0, reefLevelSupplier))
                    .alongWith(new PrintCommand("=========="))
                );
                new JoystickButton(P2, 2).whileTrue(
                    drivetrain.driveToPose(reefMap.get(18).get(1))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 18, 1, reefLevelSupplier))
                    .alongWith(new PrintCommand("=========="))
                );
                new JoystickButton(P2, 3).whileTrue(
                    drivetrain.driveToPose(reefMap.get(17).get(0))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 17, 0, reefLevelSupplier))
                );
                new JoystickButton(P2, 4).whileTrue(
                    drivetrain.driveToPose(reefMap.get(17).get(1))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 17, 1, reefLevelSupplier))
                );
                new JoystickButton(P2, 5).whileTrue(
                    drivetrain.driveToPose(reefMap.get(22).get(0))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 22, 0, reefLevelSupplier))
                );
                new JoystickButton(P2, 6).whileTrue(
                    drivetrain.driveToPose(reefMap.get(22).get(1))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 22, 1, reefLevelSupplier))
                );
                new JoystickButton(P2, 7).whileTrue(
                    drivetrain.driveToPose(reefMap.get(21).get(0))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 21, 0, reefLevelSupplier))
                );
                new JoystickButton(P2, 8).whileTrue(
                    drivetrain.driveToPose(reefMap.get(21).get(1))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 21, 1, reefLevelSupplier))
                );
                new JoystickButton(P2, 9).whileTrue(
                    drivetrain.driveToPose(reefMap.get(20).get(0))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 20, 0, reefLevelSupplier))
                );
                new JoystickButton(P2, 10).whileTrue(
                    drivetrain.driveToPose(reefMap.get(20).get(1))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 20, 1, reefLevelSupplier))
                );
                new JoystickButton(P2, 11).whileTrue(
                    drivetrain.driveToPose(reefMap.get(19).get(0))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 19, 0, reefLevelSupplier))
                );
                new JoystickButton(P2, 12).whileTrue(
                    drivetrain.driveToPose(reefMap.get(19).get(1))
                    .alongWith(new AutoReefLevel(drivetrain, arm, elevator, 19, 1, reefLevelSupplier))
                );
            }
        } else {
            System.out.println("WARNING: Alliance NOT DETECTED!");
        }
    }

    private void configureBindings() {
        drivetrain.setDefaultCommand(CMD_SmartDrive);

        drivetrain.registerTelemetry(logger::telemeterize);

        // new JoystickButton(test, 1).whileTrue(Commands.defer(DTP_CMD, Set.of(drivetrain)));
        // new JoystickButton(test, 2).whileTrue(CoralStation);
        // new JoystickButton(test, 3).onTrue(new InstantCommand(drivetrain::ResetPigeon));
        // new JoystickButton(test, 4).onTrue(new InstantCommand(arm::Arm_Algae));
        // new JoystickButton(test, 5).onTrue(CMD_Reef1);
    }

    public Command getAutonomousCommand() {
        return autoChooser.getSelected();
    }

    public void updateReefLevel() {
        for (int i = 1; i <= 4; i++) {
            if (P1.getRawButton(i)) {
                reefLevel = 5 - i;
                System.out.printf("reefLevel is now %d\n", reefLevel);
            }
        }
    }
}
