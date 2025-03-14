// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.command.Group_Cmd;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Coral;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class SuckCoral extends SequentialCommandGroup {
  private final Arm arm;
  private final Coral coral;
  /** Creates a new Coral. */
  public SuckCoral(Arm arm, Coral coral) {
    this.arm = arm;
    this.coral = coral;
    // Add your commands in the addCommands() call, e.g.
    // addCommands(new FooCommand(), new BarCommand());
    addCommands(Commands.runOnce(() -> coral.Coral_Suck(), coral));
    addCommands(Commands.runOnce(() -> arm.Arm_Station(), arm));
    new WaitCommand(0.5);
    if(coral.CoralVelocity < -40){
      if(coral.getCoral){
        addCommands(Commands.runOnce(() -> coral.Coral_Stop(), coral));
      }
      else{
        addCommands(Commands.runOnce(() -> coral.Coral_Suck(), coral));
      }
    }
    
  }
}
