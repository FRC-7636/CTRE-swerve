// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.command.Auto_Cmd;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.Coral;
import frc.robot.subsystems.Elevator;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class AutoShootCoral extends SequentialCommandGroup {
  /** Creates a new AutoShootCoral. */
  private final Coral coral;
  private final Arm arm;
  private final Elevator elevator;
  // private final CommandSwerveDrivetrain commandSwerveDrivetrain;
  public AutoShootCoral(Coral coral, Arm arm, Elevator elevator) {
    this.coral = coral;
    this.arm = arm;
    this.elevator = elevator;
    // this.commandSwerveDrivetrain= commandSwerveDrivetrain;
    // Add your commands in the addCommands() call, e.g.
    // addCommands(new FooCommand(), new BarCommand());
    addRequirements(coral, arm, elevator);
    
    addCommands(new InstantCommand(() -> elevator.ELE_RL4(), elevator));
    addCommands(new InstantCommand(() -> arm.Arm_RL4(), arm));
    addCommands(new WaitCommand(1));
    addCommands(new InstantCommand(() -> coral.Coral_Shoot(), coral));
    addCommands(new WaitCommand(1.5));
    addCommands(new InstantCommand(() -> coral.Coral_Stop(), coral));
    addCommands(new InstantCommand(() -> elevator.ELE_Floor(), elevator));
    addCommands(new InstantCommand(() -> arm.Arm_StartUp(), arm));
    // addCommands(new InstantCommand(()-> commandSwerveDrivetrain.ResetPigeon()));
  }
}
