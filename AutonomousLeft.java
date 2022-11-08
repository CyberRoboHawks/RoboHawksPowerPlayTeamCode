package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Left", group = "Pushbot")
public class AutonomousLeft extends AutonomousBase {

    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize the hardware variables.
        startupInit();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            executeCommands(Constants.startingSide.Left);
            sleep(30000);
        }
    }
}


//            Orientation angles = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
//            telemetry.addData("Roll (x)", "%4.2f", angles.thirdAngle);
//            telemetry.addData("Pitch (y)", "%4.2f", angles.secondAngle);
//            telemetry.addData("Yaw (z)", "%4.2f",  angles.firstAngle);
//            telemetry.update();