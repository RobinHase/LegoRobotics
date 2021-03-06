/**
 * Line tracing program for the Lego Robotics project; does its best to follow a black line throughout the course and do a victory
 * dance on green.
 * @author Harper & Robin
 */
import java.io.File;
import java.lang.Math;
import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.Sound;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.nxt.ColorSensor;
import lejos.nxt.ColorSensor.Color;

public class Pathfinder {

	final static File ffVictory = new File ("FFVictory.wav");
	
	// Victory dance done after green is found
	public static void victoryDance (DifferentialPilot pilot) {
		System.out.println("Finish line found. Victory!");
		Sound.playSample(ffVictory, 100);
		pilot.setRotateSpeed(70);
		pilot.rotate(360);
		pilot.rotate(-360);
		Sound.beep();
	}
	
	public static void main(String[] args) {
		DifferentialPilot pilot = new DifferentialPilot(2f, 4.75f, Motor.A, Motor.C);
		TouchSensor touch = new TouchSensor(SensorPort.S4);
		ColorSensor colorSense = new ColorSensor(SensorPort.S1);
		boolean rightTurn = true;
		int turnA = -30;
		int turnB = -60;
		
		pilot.setTravelSpeed(17.5);
		pilot.setRotateSpeed(50);
		System.out.println("Hello World!");
		
		// Loops the program until the button on top of robot is pressed down (or stops within code)
		while (!Button.ENTER.isDown()) {	
		
			// Moves forward while line is black
			while (colorSense.getColorID() == Color.BLACK) {	
				System.out.println("Vroom vroom");
				pilot.travel(40, true);
			}
			
			// Allows code execution while robot is doing any traveling
			while(pilot.isMoving()) {
				
				// Algorithm to get passed the brick
				if (touch.isPressed()) {
					pilot.stop();
					pilot.setRotateSpeed(80);
					System.out.println("Something is in my way!");
					pilot.travel(-4);
					pilot.rotate(-90);
					pilot.travelArc(9, 45, true);
					while(pilot.isMoving()) {
						if (colorSense.getColorID() == Color.BLACK) {
							pilot.stop();
							pilot.travel(3);
							pilot.rotate(-105, true);
							while(pilot.isMoving()) {
								if (colorSense.getColorID() == Color.BLACK) {
									pilot.stop();
								}
							}
						}
					}
				}
				
				// Breaks loop to allow outside code execution
				if (colorSense.getColorID() != Color.BLACK) {
					pilot.stop();
				}
			}
			

			// Searches for the black line when the color sensor loses it
			while (colorSense.getColorID() != Color.BLACK) {
				pilot.setRotateSpeed(50);
				System.out.println("Line has been lost. Searching...");
				if (colorSense.getColorID() == Color.GREEN) {
					victoryDance(pilot);
					return;
				}
				if(rightTurn) {
					while (colorSense.getColorID() != Color.BLACK) {
						pilot.rotate(turnA, true);
						while(pilot.isMoving()) {
							if (colorSense.getColorID() == Color.BLACK) {
								pilot.stop();
							}
						}
						if (colorSense.getColorID() == Color.BLACK || colorSense.getColorID() == Color.GREEN) {
							break;
						}
						pilot.rotate(Math.abs(turnB), true);
						while(pilot.isMoving()) {
							if (colorSense.getColorID() == Color.BLACK) {
								pilot.stop();
								rightTurn = false;
							}
						}
						if (colorSense.getColorID() == Color.BLACK || colorSense.getColorID() == Color.GREEN) {
							break;
						}
						turnA -= 60;
						turnB -= 60;
					}
				}
				else {
					while (colorSense.getColorID() != Color.BLACK) {
						pilot.rotate(Math.abs(turnA), true);
						while (pilot.isMoving()) {
							if (colorSense.getColorID() == Color.BLACK) {
								pilot.stop();
							}
						}
						if (colorSense.getColorID() == Color.BLACK || colorSense.getColorID() == Color.GREEN) {
							break;
						}
						pilot.rotate(turnB, true);
						while (pilot.isMoving()) {
							if (colorSense.getColorID() == Color.BLACK) {
								pilot.stop();
								rightTurn = true;
							}
						}
						if (colorSense.getColorID() == Color.BLACK || colorSense.getColorID() == Color.GREEN) {
							break;
						}
						turnA -= 60;
						turnB -= 60;
					}
				}
						
				turnA = -30;
				turnB = -60;
				System.out.println("Line has been found. Continuing quest!");
			}	
		}
	}
}
