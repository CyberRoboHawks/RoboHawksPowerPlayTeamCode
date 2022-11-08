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
    static final double DRIVE_SPEED = 0.2;

    /* Declare OpMode members. */
    HardwareMapping robot = new HardwareMapping();   // Use our hardware mapping
    private ElapsedTime runtime = new ElapsedTime();

    // Used to maintain the gripper position
    double grip_position = Constants.OPEN_GRIPPER;

    // Used to maintain the wrist position
    double wrist_position = Constants.INIT_WRIST;

    // Used to maintain the arm position
    int arm_position = Constants.ARM_FLOOR;

    @Override
    public void runOpMode() {
        // Initialize the hardware variables.
        robot.init(hardwareMap);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        // run until the end of the match (driver presses STOP)

        while (opModeIsActive()) {

            //Driver controller ---------------------
            if (gamepad1 != null) {
                // Mecanum drive controls
                double y = -gamepad1.left_stick_y*DRIVE_SPEED; // Remember, this is reversed!
                double x = gamepad1.left_stick_x *0.75*DRIVE_SPEED; // Counteract imperfect strafing
                double rx = gamepad1.right_stick_x* DRIVE_SPEED;


                // Denominator is the largest motor power (absolute value) or DRIVE_SPEED
                // This ensures all the powers maintain the same ratio, but only when
                // at least one is out of the range [-DRIVE_SPEED, DRIVE_SPEED]
                double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), DRIVE_SPEED);
                double frontLeftPower = ((y + x + rx) / denominator)*DRIVE_SPEED;
                double backLeftPower = ((y - x + rx) / denominator)*DRIVE_SPEED;
                double frontRightPower = ((y - x - rx) / denominator)*DRIVE_SPEED;
                double backRightPower = ((y + x - rx) / denominator)*DRIVE_SPEED;

                // Send calculated power to wheels
                robot.leftFrontMotor.setPower(frontLeftPower);
                robot.rightFrontMotor.setPower(frontRightPower);
                robot.leftBackMotor.setPower(backLeftPower);
                robot.rightBackMotor.setPower(backRightPower);

                // handle the arm movement
                double arm_speed = 0.0;

                if(gamepad2.dpad_up) {
                    arm_position = Constants.ARM_DRIVE;
                    robot.armMotor.setTargetPosition(arm_position);
                    robot.armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    robot.armMotor.setPower(0.2);
                }else if(gamepad2.dpad_right){
                    arm_position = Constants.ARM_LOW_J;
                    robot.armMotor.setTargetPosition(arm_position);
                    robot.armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    robot.armMotor.setPower(0.2);
                }else if(gamepad2.dpad_down){
                    arm_position = Constants.ARM_MID_J;
                    robot.armMotor.setTargetPosition(arm_position);
                    robot.armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    robot.armMotor.setPower(0.2);
                }else if(gamepad2.dpad_left){
                    arm_position = Constants.ARM_HIGH_J;
                    robot.armMotor.setTargetPosition(arm_position);
                    robot.armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    robot.armMotor.setPower(0.2);
                }else if(gamepad2.left_stick_button){
                    arm_position = Constants.ARM_FLOOR;
                    robot.armMotor.setTargetPosition(arm_position);
                    robot.armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    robot.armMotor.setPower(0.2);
                }else {
                    if (gamepad2.a) {
                        robot.armMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                        arm_speed = 0.4;
                        robot.armMotor.setPower(arm_speed);
                        arm_position = Constants.ARM_FLOOR;
                    } else if (gamepad2.b) {
                        robot.armMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                        arm_speed = -0.4;
                        robot.armMotor.setPower(arm_speed);
                        arm_position = Constants.ARM_FLOOR;
                    } else {
                        robot.armMotor.setTargetPosition(arm_position);
                        robot.armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                        robot.armMotor.setPower(Constants.ARM_SPEED);
                    }
                }



                // handle the gripper
                if(gamepad2.x){
                    grip_position = 0.0;
                }else if(gamepad2.y){
                    grip_position = 0.50;
                }
                robot.clawServo.setPosition(grip_position);
                int arm_pos = robot.armMotor.getCurrentPosition();
                //0-1200 = .30-.90;
                double wrist_target = .30+((.70*(double)arm_pos)/1500.0);
                // handle the wrist
                if(gamepad2.left_bumper){
                    wrist_position = 0.30;
                }else if(gamepad2.right_bumper){
                    wrist_position = 0.0;
                }else{
                    wrist_position = wrist_target;
                }

                robot.clawRotationServo.setPosition(wrist_position);

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

            //Co-Driver controller ---------------------
            if (gamepad2 != null) {

            }


        }

    }

}
