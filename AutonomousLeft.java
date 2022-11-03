package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Left", group = "Pushbot")
public class AutonomousLeft extends AutonomousBase {

    @Override
    public void runOpMode() {
        // Initialize the hardware variables.
        startupInit();
        final double DRIVE_SPEED = 0.2;
        StartingSide startingSide = StartingSide.Left;

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            // read signal
            ParkingZone parkingZone = ParkingZone.Zone2Bulb;

            // close claw on the cone
            commands.closeClaw();

            // raise claw off the floor
            commands.powerArm(Commands.clawPosition.Low,0.5,3);

            commands.driveForward(DRIVE_SPEED, 54, 5);
            //  raise claw to the high position
            commands.powerArm(Commands.clawPosition.High,0.5,3);

            // turn towards the junction pole
            switch (startingSide){
                case Left:
                    commands.spinRight(DRIVE_SPEED, -45, 3);
                    break;
                case Right:
                    commands.spinLeft(DRIVE_SPEED, 45, 3);
                    break;
            }

            // open claw and drop cone
            commands.openClaw();

            // turn back towards the init/starting heading
            switch (startingSide){
                case Left:
                    commands.spinLeft(DRIVE_SPEED, 0, 3);
                    break;
                case Right:
                    commands.spinRight(DRIVE_SPEED, 0, 3);
                    break;
            }

            // lower claw to floor
            commands.powerArm(Commands.clawPosition.Low,0.5,3);

            switch (parkingZone) {
                case Zone1Bolt:
                    commands.spinLeft(DRIVE_SPEED, 90, 3);
                    commands.driveForward(DRIVE_SPEED, 24, 3);
                    break;
                case Zone2Bulb:
                    // already in zone 2 - stop
                    break;
                case Zone3Panel:
                    commands.spinRight(DRIVE_SPEED, -90, 3);
                    commands.driveForward(DRIVE_SPEED, 24, 5);
                    break;
            }

            sleep(30000);
        }
    }
}


//            Orientation angles = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
//            telemetry.addData("Roll (x)", "%4.2f", angles.thirdAngle);
//            telemetry.addData("Pitch (y)", "%4.2f", angles.secondAngle);
//            telemetry.addData("Yaw (z)", "%4.2f",  angles.firstAngle);
//            telemetry.update();