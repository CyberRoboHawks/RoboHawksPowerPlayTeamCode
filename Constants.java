package org.firstinspires.ftc.teamcode;

public class Constants {

    // Chassis Speed Constants
    public static final double DRIVE_SPEED = 0.2;
    public static final double TURBO_DRIVE_SPEED = 0.5;
    public static final double TURTLE_DRIVE_SPEED = 0.1;

    // Arm Speed Constants
    public static final double ARM_SPEED = 0.2;
    // Arm Position Constants
    public static final int ARM_FLOOR = 0; // The position to pick up Cones from the Floor
    public static final int ARM_DRIVE = 100; // The position just off the Floor but safe to drive
    public static final int ARM_LOW_J = 375; // The position for bottom of Cone over Low Junction
    public static final int ARM_MID_J = 550; // The position for bottom of Cone over Mid Junction
    public static final int ARM_HIGH_J = 1200; // The position for bottom of Cone over High Junction

    // Gripper Position Constants
    public static final double INIT_GRIPPER = 0.5;
    public static final double OPEN_GRIPPER = 0.5;
    public static final double CLOSE_GRIPPER = 0.0;

    // Wrist Position Constants
    public static final double INIT_WRIST = 0.0;
    public static final double FLOOR_WRIST = 0.3;

    public enum liftPosition {
        Floor,
        Drive,
        LowJunction,
        MidJunction,
        HighJunction
    }

    public enum parkingZone {
        Zone1Bolt,
        Zone2Bulb,
        Zone3Panel
    }

    public enum startingSide {
        Left,
        Right
    }

    public enum strafeDirection {
        Right,
        Left
    }
}