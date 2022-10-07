package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Commands extends HardwareMapping {
    static final double COUNTS_PER_MOTOR_REV = 480;    // eg: TETRIX Motor Encoder (1440 - 60:1; 960 - 40:1, 480 - 20:1)
    static final double WHEEL_DIAMETER_INCHES = 4.0;   // For figuring circumference
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV) / (WHEEL_DIAMETER_INCHES * 3.14159);

    //    static final double AUTONOMOUS_DRIVE_SPEED = .4;
//    static final double AUTONOMOUS_TURN_SPEED = .2;
    private final ElapsedTime runtime = new ElapsedTime();

    public enum strafeDirection {
        Right,
        Left
    }

    // Strafe right
    public void strafeRight(double power, double distanceInInches, double timeout) {

        encoderDriveStrafe(power, distanceInInches, strafeDirection.Right, timeout);
    }

    // Strafe left
    public void strafeLeft(double power, double distanceInInches, double timeout) {
        // TODO call encoderDriveStrafe to the Left
        encoderDriveStrafe(power, distanceInInches, strafeDirection.Left,timeout);
    }


    // Drive forward
    public void driveForward(double power, double distanceInInches, double timeout) {
        encoderDriveStraight(power, distanceInInches, timeout);
    }

    // Drive backward
    public void driveBackwards(double power, double distanceInInches, double timeout) {
        encoderDriveStraight(power, -distanceInInches, timeout);
    }

    // Rotate left (counter-clockwise)
    public void spinLeft(double power, double heading, double timeout) {
        gyroTurn(-power, power, heading, timeout);
    }

    // Rotate right (clockwise)
    public void spinRight(double power, double heading, double timeout) {
        gyroTurn(power, -power, heading, timeout);
    }

    //
    public void quickSpin(double power, double heading, double timeout) {
        double currentAngle = getAngle();
        if (currentAngle < 0) {
            if (heading < currentAngle || (currentAngle < -80 && heading > 90)) {
                if (heading == 180) {
                    heading = -179;
                }
                spinRight(power, heading, timeout);
            } else {
                spinLeft(power, heading, timeout);
            }
        } else {
            if (heading < currentAngle || (currentAngle > 80 && heading < -90)) {
                spinRight(power, heading, timeout);
            } else {
                spinLeft(power, heading, timeout);
            }
        }
    }

    private void gyroTurn(double leftMotorPower, double rightMotorPower, double heading, double timeout) {
        runtime.reset();

        while (Math.abs(getRemainingAngle(heading)) >= 10 && (runtime.seconds() < timeout)) {
            leftFrontMotor.setPower(leftMotorPower);
            leftBackMotor.setPower(leftMotorPower);
            rightFrontMotor.setPower(rightMotorPower);
            rightBackMotor.setPower(rightMotorPower);
        }
        stopDrivingMotors();
    }

    private double getRemainingAngle(double targetAngle) {
        // calculate angle in -179 to +180 range  (
        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return targetAngle - angles.firstAngle;
    }

    private double getAngle() {
        return imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
    }

    private void encoderRunToPosition(double power, int leftFrontTarget, int leftBackTarget, int rightFrontTarget, int rightBackTarget, double timeoutS) {
        leftFrontMotor.setTargetPosition(leftFrontTarget);
        leftBackMotor.setTargetPosition(leftBackTarget);
        rightFrontMotor.setTargetPosition(rightFrontTarget);
        rightBackMotor.setTargetPosition(rightBackTarget);

        // Turn On RUN_TO_POSITION
        setMotorRunMode(DcMotor.RunMode.RUN_TO_POSITION);

        // reset the timeout time and start motion.
        runtime.reset();
        leftBackMotor.setPower(Math.abs(power));
        leftFrontMotor.setPower(Math.abs(power));
        rightBackMotor.setPower(Math.abs(power));
        rightFrontMotor.setPower(Math.abs(power));

        // keep looping while we are still active, and there is time left, and both motors are running.
        // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
        // its target position, the motion will stop.  This is "safer" in the event that the robot will
        // always end the motion as soon as possible.
        // However, if you require that BOTH motors have finished their moves before the robot continues
        // onto the next step, use (isBusy() || isBusy()) in the loop test.
        while (
                (runtime.seconds() < timeoutS) &&
                        (leftBackMotor.isBusy()
                                && leftFrontMotor.isBusy()
                                && rightBackMotor.isBusy()
                                && rightFrontMotor.isBusy())) {
        }

        // Stop all motion;
        stopDrivingMotors();

        // Turn off RUN_TO_POSITION
        setMotorRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void encoderDriveStraight(double power, double distanceInches, double timeoutS) {
        int newMotorPosition = (int) (distanceInches * COUNTS_PER_INCH);

        // Determine new target positions, and pass to motor controller
        int leftBackTarget = leftBackMotor.getCurrentPosition() + newMotorPosition;
        int leftFrontTarget = leftFrontMotor.getCurrentPosition() + newMotorPosition;
        int rightBackTarget = rightBackMotor.getCurrentPosition() + newMotorPosition;
        int rightFrontTarget = rightFrontMotor.getCurrentPosition() + newMotorPosition;

        encoderRunToPosition(power, leftFrontTarget, leftBackTarget, rightFrontTarget, rightBackTarget, timeoutS);
    }

    private void encoderDriveStrafe(double power, double distanceInches, strafeDirection direction, double timeoutS) {
        int leftBackTarget = 0;
        int leftFrontTarget = 0;
        int rightBackTarget = 0;
        int rightFrontTarget = 0;

        // strafe offset to add 140% for difference in requested and actual movement
        distanceInches = distanceInches * 1.40;
        int newMotorPosition = (int) (distanceInches * COUNTS_PER_INCH);

        // Determine new target position, and pass to motor controller
        if (direction == strafeDirection.Left) {
            //TODO Left strafe
             leftBackTarget = leftBackMotor.getCurrentPosition() + newMotorPosition;
             leftFrontTarget = leftFrontMotor.getCurrentPosition() - newMotorPosition;
             rightBackTarget = rightBackMotor.getCurrentPosition() - newMotorPosition;
             rightFrontTarget = rightFrontMotor.getCurrentPosition() + newMotorPosition;

        } else {
            //TODO Right strafe
             leftBackTarget = leftBackMotor.getCurrentPosition() - newMotorPosition;
             leftFrontTarget = leftFrontMotor.getCurrentPosition() + newMotorPosition;
             rightBackTarget = rightBackMotor.getCurrentPosition() + newMotorPosition;
             rightFrontTarget = rightFrontMotor.getCurrentPosition() - newMotorPosition;
        }

        encoderRunToPosition(power, leftFrontTarget, leftBackTarget, rightFrontTarget, rightBackTarget, timeoutS);
    }

    public void setMotorRunMode(DcMotor.RunMode mode) {
        leftBackMotor.setMode(mode);
        leftFrontMotor.setMode(mode);
        rightBackMotor.setMode(mode);
        rightFrontMotor.setMode(mode);
    }

    private void stopDrivingMotors() {
        leftBackMotor.setPower(0);
        leftFrontMotor.setPower(0);
        rightBackMotor.setPower(0);
        rightFrontMotor.setPower(0);
    }
}