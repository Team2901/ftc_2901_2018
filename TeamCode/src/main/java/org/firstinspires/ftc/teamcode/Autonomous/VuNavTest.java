package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.teamcode.Hardware.CoachBotHardware;

@Autonomous (name = "VuNavTest")
public class VuNavTest extends LinearOpMode {

    static final double COUNTS_PER_MOTOR_REV = 1120;
    static final double MOTORREV_PER_WHEELREV = 2;
    static final double WHEEL_DIAMETER_INCHES = 4.0;
    static final double INCHES_TO_ENCODERCOUNTS = (1 / (WHEEL_DIAMETER_INCHES * 3.1415)) *
            MOTORREV_PER_WHEELREV * COUNTS_PER_MOTOR_REV;
    VuforiaLocalizer vuforia;
    public final double MM_TO_INCHES = 0.0393701;
    public final double INCHES_TO_MM = 25.4;
    public final double FIELD_RADIUS = 1828.8;


    public OpenGLMatrix phoneLocation = getMatrix(90, 0, -90, 0, (float)(4*INCHES_TO_MM), 0);


    CoachBotHardware robot = new CoachBotHardware();

    double x;
    double y;
    double z;
    float angleVu;

    double xGoal = 24;
    double yGoal = 48;

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);


        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "AW/DxXD/////AAAAGYJtW/yP3kG0pVGawWtQZngsJNFQ8kp1Md8CaP2NP72Q0on4mGKPLt/lsSnMnUkCFNymrXXOjs0eHMDTvijWRIixEe/sJ4KHEVf1fhf0kqUB29+dZEvh4qeI7tlTU6pIy/MLW0a/t9cpqMksBRFqXIrhtR/vw7ZnErMTZrJNNXqmbecBnRhDfLncklzgH2wAkGmQDn0JSP7scEczgrggcmerXy3v6flLDh1/Tt2QZ8l/bTcEJtthE82i8/8p0NuDDhUyatFK1sZSSebykRz5A4PDUkw+jMTV28iUytrr1QLiQBwaTX7ikl71a1XkBHacnxrqyY07x9QfabtJf/PYNFiU17m/l9DB6Io7DPnnIaFP";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.getInstance().createVuforia(parameters);
        vuforia.setFrameQueueCapacity(1);
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);
        VuforiaTrackables roverRuckus = this.vuforia.loadTrackablesFromAsset("RoverRuckus");

        VuforiaTrackable blue = roverRuckus.get(0);
        VuforiaTrackable red = roverRuckus.get(1);
        VuforiaTrackable front = roverRuckus.get(2);
        VuforiaTrackable back = roverRuckus.get(3);

        blue.setName("blue");
        red.setName("red");
        front.setName("front");
        back.setName("back");


        OpenGLMatrix blueTrackablePosition = getMatrix(90, 0, -90, (float) FIELD_RADIUS, 0, (float) 152.4);
        OpenGLMatrix frontTrackablePosition = getMatrix(90, 0, 0, 0, (float) FIELD_RADIUS, (float) 152.4);
        OpenGLMatrix redTrackablePosition = getMatrix(90, 0, 90, (float) -FIELD_RADIUS, 0, (float) 152.4);
        OpenGLMatrix backTrackablePosition = getMatrix(90, 0, 180, 0, (float) -FIELD_RADIUS, (float) 152.4);


        blue.setLocation(blueTrackablePosition);
        red.setLocation(redTrackablePosition);
        front.setLocation(frontTrackablePosition);
        back.setLocation(backTrackablePosition);

        ((VuforiaTrackableDefaultListener) blue.getListener()).setPhoneInformation(phoneLocation, parameters.cameraDirection);
        ((VuforiaTrackableDefaultListener) red.getListener()).setPhoneInformation(phoneLocation, parameters.cameraDirection);
        ((VuforiaTrackableDefaultListener) front.getListener()).setPhoneInformation(phoneLocation, parameters.cameraDirection);
        ((VuforiaTrackableDefaultListener) back.getListener()).setPhoneInformation(phoneLocation, parameters.cameraDirection);

        waitForStart();

        roverRuckus.activate();
        OpenGLMatrix location = null;
        while (location == null) {
            location = getLocation(blue, red, front, back);

            VectorF translation = location.getTranslation();

            Orientation orientation = Orientation.getOrientation(location,
                    AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);

             x = (translation.get(0) * MM_TO_INCHES);
             y = (translation.get(1) * MM_TO_INCHES);
             z = (translation.get(2) * MM_TO_INCHES);
             angleVu = orientation.thirdAngle;

            telemetry.addData("X", "%.2f", x);
            telemetry.addData("Y", "%.2f", y);
            telemetry.addData("Z", "%.2f", z);
            telemetry.addData("Angle", "%.2f", angleVu);
            telemetry.update();


            idle();
        }


        goToPosition(x, y, xGoal, yGoal, angleVu);

        while (opModeIsActive()) {
            idle();
        }
    }


    double getPower(double currentPosition, double goal) {
       /*
        If under halfway to the goal, have the robot speed up by .01 for every angle until it is
        over halfway there
         */

        if (currentPosition < goal / 2) {

            return (.01 * currentPosition + (Math.signum(currentPosition) * .075));
        } else {
// Starts to slow down by .01 per angle closer to the goal.
            return (.01 * (goal - currentPosition + (Math.signum(currentPosition) * .075)));
        }
    }

    public void goToPosition(double startX, double startY, double goalX, double goalY, double angleVu) {
        double angleImu = robot.getAngle();

        robot.offset = angleVu - angleImu;

        double xDiff = goalX - startX;
        double yDiff = goalY - startY;

        double angleGoal = Math.atan2(yDiff, xDiff);

        angleImu = robot.getAngle();

        while (Math.abs(angleGoal - angleImu) > 1) {
            angleImu = robot.getAngle();

            robot.leftMotor.setPower(-getPower(angleImu, angleGoal));
            robot.rightMotor.setPower(getPower(angleImu, angleGoal));

            telemetry.addData("Goal Angle", angleGoal);
            telemetry.addData("angleGoal-angle ", angleGoal - angleImu);
            telemetry.addData("Power", getPower(angleImu, angleGoal));
            telemetry.update();
            idle();
        }

        robot.leftMotor.setPower(0);
        robot.rightMotor.setPower(0);

        double distanceToGoal = Math.sqrt((Math.pow(yDiff, 2) + Math.pow(xDiff, 2)));

        int encodersToGoal = (int) (INCHES_TO_ENCODERCOUNTS * distanceToGoal);

        telemetry.addData("distance to goal", distanceToGoal);
        telemetry.addData("encoders to goal", encodersToGoal);
        telemetry.update();

        robot.leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.rightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        robot.leftMotor.setTargetPosition(encodersToGoal);
        robot.rightMotor.setTargetPosition(encodersToGoal);

        robot.leftMotor.setPower(.75);
        robot.rightMotor.setPower(.75);

        while (robot.leftMotor.isBusy()) {
            telemetry.addData("left Counts", robot.leftMotor.getCurrentPosition());
            telemetry.addData("right Counts", robot.rightMotor.getCurrentPosition());
            telemetry.addData("distance to goal", distanceToGoal);
            telemetry.addData("encoders to goal", encodersToGoal);
            telemetry.update();

            idle();
        }

        robot.leftMotor.setPower(0);
        robot.rightMotor.setPower(0);

    }

    public OpenGLMatrix getMatrix(float ax, float ay, float az, float dx, float dy, float dz) {

        return OpenGLMatrix.translation(dx, dy, dz).multiplied
                (Orientation.getRotationMatrix(AxesReference.EXTRINSIC,
                        AxesOrder.XYZ, AngleUnit.DEGREES, ax, ay, az));
    }

    public OpenGLMatrix getLocation(VuforiaTrackable blue, VuforiaTrackable red,
                                    VuforiaTrackable front, VuforiaTrackable back) {
        OpenGLMatrix location = null;
        OpenGLMatrix blueLocation = null;
        OpenGLMatrix redLocation = null;
        OpenGLMatrix backLocation = null;
        OpenGLMatrix frontLocation = null;

        while (location == null) {
            blueLocation = ((VuforiaTrackableDefaultListener)
                    blue.getListener()).getUpdatedRobotLocation();
            redLocation = ((VuforiaTrackableDefaultListener)
                    red.getListener()).getUpdatedRobotLocation();
            backLocation = ((VuforiaTrackableDefaultListener)
                    back.getListener()).getUpdatedRobotLocation();
            frontLocation = ((VuforiaTrackableDefaultListener)
                    front.getListener()).getUpdatedRobotLocation();

            if (blueLocation != null) {
                location = blueLocation;
            } else if (redLocation != null) {
                location = redLocation;
            } else if (backLocation != null) {
                location = backLocation;
            } else if (frontLocation != null) {
                location = frontLocation;
            }
            idle();
        }
        return location;
    }
}
