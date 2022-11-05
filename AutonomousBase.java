package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public abstract class AutonomousBase extends LinearOpMode {
    Commands commands = new Commands();
    HardwareMapping robot = new HardwareMapping();   // Use our hardware mapping
    ObjectDetection objectDetection = new ObjectDetection();

    final double DRIVE_SPEED = 0.2;

    public void startupInit() {
        commands.init(hardwareMap);
        robot.init(hardwareMap);
        objectDetection.init(hardwareMap);
        telemetry.addData("Status:", "ready");
        telemetry.update();
    }

    public void executeCommands(PowerPlayEnums.startingSide startingSide) throws InterruptedException{

        // read signal
        PowerPlayEnums.parkingZone parkingZone = objectDetection.readParkingZone(5, 500);
        telemetry.addData("ParkingZone :", parkingZone);
        telemetry.update();

        // close claw on the cone
        commands.closeClaw();
        sleep(200);

        // raise claw off the floor
        commands.powerArm(Commands.clawPosition.Floor,0.5,3);

        commands.driveForward(DRIVE_SPEED, 60, 5);

        //  raise claw to the high position
        commands.powerArm(Commands.clawPosition.High,0.5,3);

        // keep arm in position
        commands.holdArm(0.1);

        // turn towards the junction pole
        switch (startingSide){
            case Left:
                commands.spinRight(DRIVE_SPEED, -50, 3);
                break;
            case Right:
                commands.spinLeft(DRIVE_SPEED, 50, 3);
                break;
        }

        // open claw and drop cone
        commands.openClaw();
        sleep(200);

        // move backwards away from the high junction
        commands.driveBackwards(DRIVE_SPEED, 8, 2);

        // lower claw to floor
        commands.powerArm(Commands.clawPosition.Floor,0.5,3);

        // navigate to parking location
        switch (parkingZone) {
            case Zone1Bolt:
                commands.spinLeft(DRIVE_SPEED, 90, 3);
                commands.driveForward(DRIVE_SPEED, 24, 3);
                break;
            case Zone2Bulb:
                if (startingSide == PowerPlayEnums.startingSide.Left){
                    commands.spinLeft(DRIVE_SPEED, 0, 3);
                }else{
                    commands.spinRight(DRIVE_SPEED, 0, 3);
                }
                break;
            case Zone3Panel:
                commands.spinRight(DRIVE_SPEED, -90, 3);
                commands.driveForward(DRIVE_SPEED, 22, 5);
                break;
        }

    }


}