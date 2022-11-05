package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@TeleOp(name = "TeleOpDrive", group = "TeleOp")
//@Disabled
public class TeleOpDrive extends LinearOpMode {

    static final double DRIVE_SPEED = 0.5;

    HardwareMapping robot = new HardwareMapping();   // Use our hardware mapping
    Commands commands = new Commands();

    // Used to maintain the gripper position
    double grip_position = 0.5;

    // Used to maintain the wrist position
    double wrist_position = 0;

    // handle the arm movement
    double arm_speed = 0.0;

    // wheels power
    double frontLeftPower = 0;
    double backLeftPower = 0;
    double frontRightPower = 0;
    double backRightPower = 0;


    @Override
    public void runOpMode() {
        // Initialize the hardware variables.
        robot.init(hardwareMap);
        commands.init(hardwareMap);

        telemetry.addData("Status:", "Ready");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            double drivePower = .25;  //1 is 100%, .5 is 50%

            //Driver controller ---------------------
            if (gamepad1 != null) {

                // handle the arm movement
                arm_speed = 0.0;

                // Mecanum drive controls
                double y = -gamepad1.left_stick_y; // Remember, this is reversed!
                double x = gamepad1.left_stick_x * 0.75; // Counteract imperfect strafing
                double rx = gamepad1.right_stick_x;

                //Govern the demand to a speed constant
                y *= DRIVE_SPEED;
                x *= DRIVE_SPEED;
                rx *= DRIVE_SPEED;

                // Denominator is the largest motor power (absolute value) or DRIVE_SPEED
                // This ensures all the powers maintain the same ratio, but only when
                // at least one is out of the range [-DRIVE_SPEED, DRIVE_SPEED]
                double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), DRIVE_SPEED);

                frontLeftPower = (y + x + rx) / denominator;
                backLeftPower = (y - x + rx) / denominator;
                frontRightPower = (y - x - rx) / denominator;
                backRightPower = (y + x - rx) / denominator;

                // Send calculated power to wheels
                robot.leftFrontMotor.setPower(frontLeftPower);
                robot.rightFrontMotor.setPower(frontRightPower);
                robot.leftBackMotor.setPower(backLeftPower);
                robot.rightBackMotor.setPower(backRightPower);

                if(gamepad1.dpad_up) {
                    robot.armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    robot.armMotor.setTargetPosition(200);
                    robot.armMotor.setPower(0.2);
                }else if(gamepad1.dpad_right){
                    robot.armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    robot.armMotor.setTargetPosition(600);
                    robot.armMotor.setPower(0.2);
                }else if(gamepad1.dpad_down){
                    robot.armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    robot.armMotor.setTargetPosition(1000);
                    robot.armMotor.setPower(0.2);
                }else if(gamepad1.dpad_left){
                    robot.armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    robot.armMotor.setTargetPosition(1200);
                    robot.armMotor.setPower(0.2);

                }else {
                    if (gamepad1.a) {
                        robot.armMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                        arm_speed = 0.4;
                        robot.armMotor.setPower(arm_speed);
                    } else if (gamepad1.b) {
                        robot.armMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                        arm_speed = -0.4;
                        robot.armMotor.setPower(arm_speed);
                    } else {
                        robot.armMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                        arm_speed = 0;
                        robot.armMotor.setPower(arm_speed);
                    }
                }

                // handle the gripper
                if(gamepad1.x){
                    grip_position = 0.0;
                }else if(gamepad1.y){
                    grip_position = 0.50;
                }
                robot.clawServo.setPosition(grip_position);

                int arm_pos = robot.armMotor.getCurrentPosition();

                //0-1200 = .30-.90;
                double wrist_target = .30+((.70*(double)arm_pos)/1200.0);

                // handle the wrist
                if(gamepad1.left_bumper){
                    wrist_position = 0.30;
                }else if(gamepad1.right_bumper){
                    wrist_position = wrist_target;
                }else{
                    wrist_position = 0.0;
                }

                robot.clawRotationServo.setPosition(wrist_position);

            }

            //Co-Driver controller ---------------------
            if (gamepad2 != null) {
                // Turbo driving
                if (gamepad1.left_bumper) {
                    drivePower = .5;  // set drive power to .5 when the left bumper is pressed
                } else {
                    drivePower = .25; // set drive power to .25 when the left bumper is released
                }

                // Mecanum drive controls
                double power = -gamepad1.left_stick_y;
                double side = gamepad1.left_stick_x;
                double rotate = gamepad1.right_stick_x;

                // Calculate the power for each motor
                double leftFrontPower = (power + side + rotate) * drivePower;
                double rightFrontPower = (power - side - rotate) * drivePower;
                double leftBackPower = (power - side + rotate) * drivePower;
                double rightBackPower = (power + side - rotate) * drivePower;

//                telemetry.addData("left front power:", leftFrontPower);
//                telemetry.addData("right front power:", rightFrontPower);
//                telemetry.addData("left back power:", leftBackPower);
//                telemetry.addData("right back power:", rightBackPower);
//                telemetry.update();

                // Set the power on each motor
                robot.leftFrontMotor.setPower(leftFrontPower);
                robot.leftBackMotor.setPower(leftBackPower);
                robot.rightFrontMotor.setPower(rightFrontPower);
                robot.rightBackMotor.setPower(rightBackPower);
            }

            // Show the elapsed game time and wheel power, arm position, grip position, wrist position
            telemetry.addData("Status", "Run Time: " + robot.runtime.seconds());
            telemetry.addData("Front Wheel Power left/Right", "%1.2f, %1.2f", frontLeftPower, frontRightPower);
            telemetry.addData("Back Wheel Power left/Right", "%1.2f, %1.2f", backLeftPower, backRightPower);
            telemetry.addData("Front Wheel Position left/Right", "%5d, %5d", robot.leftFrontMotor.getCurrentPosition(), robot.rightFrontMotor.getCurrentPosition());
            telemetry.addData("Back Wheel Position left/Right", "%5d, %5d", robot.leftBackMotor.getCurrentPosition(), robot.rightBackMotor.getCurrentPosition());
            telemetry.addData("arm position / speed", "%5d, %1.2f", robot.armMotor.getCurrentPosition(),arm_speed);
            telemetry.addData("grip position / demand", "%f1.2, %1.2f", robot.clawServo.getPosition(),grip_position);
            telemetry.addData("wrist position / demand", "%f1.2, %1.2f", robot.clawRotationServo.getPosition(),wrist_position);
            telemetry.update();

        }

    }

}
