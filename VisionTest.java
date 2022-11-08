package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="Vision Test", group="Pushbot")
@Disabled
public class VisionTest extends LinearOpMode {
    ObjectDetection objectDetection = new ObjectDetection();

    @Override
    public void runOpMode() {
        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        objectDetection.init(hardwareMap);

        telemetry.addData("webcam", "ready");
        telemetry.update();
        // Trying to call objectDetection will not work before game start

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {


            sleep(30000);
        }
    }
}
