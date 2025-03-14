// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.command.Single_Cmd;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Coral;
import frc.robot.subsystems.Elevator;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class CoralShoot extends Command {
  // private final Coral coral;
  // private final Elevator elevator;

  // private double ShootSpeed;
  /** Creates a new CoralShoot. */
  // public CoralShoot(Coral coral, Elevator elevator) {
  //   this.coral = coral;
  //   this.elevator = elevator;
  //   addRequirements(coral, elevator);
  //   // Use addRequirements() here to declare subsystem dependencies.
  // }

  // // Called when the command is initially scheduled.
  // @Override
  // public void initialize() {}

  // // Called every time the scheduler runs while the command is scheduled.
  // @Override
  // public void execute() {
  //   double Elevator_Height = elevator.getAbsolutePosition();
  //   if(Elevator_Height < -50){
  //     ShootSpeed = 0.5;
  //   }
  //   else if(-5 < Elevator_Height && Elevator_Height < 50){
  //     ShootSpeed = 0.3;
  //   }
  //   else {
  //     ShootSpeed = 0.2;
  //   }
  //   coral.Coral_Shoot(ShootSpeed);
  // }

  // // Called once the command ends or is interrupted.
  // @Override
  // public void end(boolean interrupted) {}

  // // Returns true when the command should end.
  // @Override
  // public boolean isFinished() {
  //   return false;
  // }
}
