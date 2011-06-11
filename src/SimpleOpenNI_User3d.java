import processing.core.*;
import processing.xml.*;
import remixlab.proscene.Scene;

import SimpleOpenNI.*;

import java.applet.*;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.FocusEvent;
import java.awt.Image;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.zip.*;
import java.util.regex.*;

public class SimpleOpenNI_User3d extends PApplet {

	/*
	 * --------------------------------------------------------------------------
	 * SimpleOpenNI User3d Test
	 * --------------------------------------------------
	 * ------------------------ Processing Wrapper for the OpenNI/Kinect library
	 * http://code.google.com/p/simple-openni
	 * ------------------------------------
	 * -------------------------------------- prog: Max Rheiner / Interaction
	 * Design / zhdk / http://iad.zhdk.ch/ date: 02/16/2011 (m/d/y)
	 * --------------
	 * -------------------------------------------------------------- this demos
	 * is at the moment only for 1 user, will be implemented later
	 * --------------
	 * --------------------------------------------------------------
	 */

	SimpleOpenNI context;
	float zoomF = 0.5f;
	float rotX = radians(180); // by default rotate the hole scene 180deg around
								// the x-axis,
								// the data from openni comes upside down
	float rotY = radians(0);

	Scene scene;

	public void setup() {
		size(1024, 768, OPENGL); // strange, get drawing error in the
									// cameraFrustum
									// if i use P3D, in opengl there is no
									// problem
		hint(ENABLE_OPENGL_4X_SMOOTH);
		hint(ENABLE_ACCURATE_TEXTURES);
		hint(ENABLE_NATIVE_FONTS);
		context = new SimpleOpenNI(this);
		scene = new Scene(this);
		scene.setRadius(1000);
		// disable mirror
		context.setMirror(false);

		// enable depthMap generation
		context.enableDepth();

		// enable skeleton generation for all joints
		context.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);

		stroke(255, 255, 255);
		smooth();
		perspective(95, PApplet.parseFloat(width) / PApplet.parseFloat(height),
				10, 150000);
	}

	public void draw() {
		// update the cam
		context.update();

		// background(0, 0, 0);

		// set the scene pos

		int[] depthMap = context.depthMap();
		int steps = 3; // to speed up the drawing, draw every third point
		int index;
		PVector realWorldPoint;

		translate(0, 0, -1000); // set the rotation center of the scene 1000
								// infront of the camera

		stroke(100);
		for (int y = 0; y < context.depthHeight(); y += steps) {
			for (int x = 0; x < context.depthWidth(); x += steps) {
				index = x + y * context.depthWidth();
				if (depthMap[index] > 0) {
					// draw the projected point
					realWorldPoint = context.depthMapRealWorld()[index];
					point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z);
				}
			}
		}

		// draw the skeleton if it's available
		for (int i = 0; i < 10; i++)
			if (context.isTrackingSkeleton(i)) {
				drawSkeleton(i);
				checkShootPosition(i);
			} else
				text("hola", 100, 100, 0);

		// draw the kinect cam
		// context.drawCamFrustum();
	}

	public void checkShootPosition(int userId) {

		PVector head = new PVector();
		context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_NECK, head);
		PVector hip = new PVector();
		context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HIP,
				hip);

		float distTorso = PVector.dist(head, hip);

		PVector handLeft = new PVector();
		context.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HAND,
				handLeft);

		float distHandHip = PVector.dist(handLeft, hip);

		if (distHandHip > distTorso * 1.33) {

//			PVector handRight = new PVector();
//			context.getJointPositionSkeleton(userId,
//					SimpleOpenNI.SKEL_RIGHT_HAND, handRight);
//
//			float distHands = PVector.dist(handLeft, handRight);
//
//			if (distHands < distTorso / 5) {
				stroke(0, 255, 0);
				System.out.println("disparando.......................");
//			}
		} else {
			stroke(255, 0, 0);
		}

	}

	// public void checkShootPosition() {
	//
	// // queremos comprobar que el angulo entre vector (cadera,cabeza) y
	// // vector(cadera,pies) es aprox 180, es decir estamos rectos, luego
	// // comprobamos longitud del vector hombro-brazo para comprobar
	// // estiramiento y después comprobamos el angulo entre vectores
	// // cabeza-cadera y hombro-mano que debe ser aprox 90
	//
	// PVector head = new PVector();
	// context.getJointPositionSkeleton(1, SimpleOpenNI.SKEL_HEAD, head);
	// PVector rightHand = new PVector();
	// context.getJointPositionSkeleton(1, SimpleOpenNI.SKEL_RIGHT_HAND,
	// rightHand);
	// PVector rightShoulder = new PVector();
	// context.getJointPositionSkeleton(1, SimpleOpenNI.SKEL_RIGHT_SHOULDER,
	// rightShoulder);
	// PVector rightHip = new PVector();
	// context.getJointPositionSkeleton(1, SimpleOpenNI.SKEL_RIGHT_HIP,
	// rightHip);
	//
	// PVector rightFoot = new PVector();
	// context.getJointPositionSkeleton(1, SimpleOpenNI.SKEL_RIGHT_FOOT,
	// rightFoot);
	//
	// PVector torsoLine = rightHip.get();
	// torsoLine.sub(head);
	//
	// PVector legsLine = rightHip.get();
	// torsoLine.sub(rightFoot);
	//
	// float bodyAngle = PVector.angleBetween(torsoLine, legsLine);
	// bodyAngle = degrees(bodyAngle);
	// if(bodyAngle < 150 || bodyAngle > 210)
	// ;// no hacemos nada estamos agachados
	//
	// PVector handLine = rightShoulder.get();
	// handLine.sub(rightHand);
	// float angleRad = PVector.angleBetween(torsoLine, handLine);
	// float angle = degrees(angleRad);
	// text(angleRad + " " + angle, 100, 100, 0);
	//
	// }

	// draw the skeleton with the selected joints
	public void drawSkeleton(int userId) {
		strokeWeight(3);

		// to get the 3d joint data
		drawLimb(userId, SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK);

		drawLimb(userId, SimpleOpenNI.SKEL_NECK,
				SimpleOpenNI.SKEL_LEFT_SHOULDER);
		drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER,
				SimpleOpenNI.SKEL_LEFT_ELBOW);
		drawLimb(userId, SimpleOpenNI.SKEL_LEFT_ELBOW,
				SimpleOpenNI.SKEL_LEFT_HAND);

		drawLimb(userId, SimpleOpenNI.SKEL_NECK,
				SimpleOpenNI.SKEL_RIGHT_SHOULDER);
		drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER,
				SimpleOpenNI.SKEL_RIGHT_ELBOW);
		drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW,
				SimpleOpenNI.SKEL_RIGHT_HAND);

		drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER,
				SimpleOpenNI.SKEL_TORSO);
		drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER,
				SimpleOpenNI.SKEL_TORSO);

		drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_HIP);
		drawLimb(userId, SimpleOpenNI.SKEL_LEFT_HIP,
				SimpleOpenNI.SKEL_LEFT_KNEE);
		drawLimb(userId, SimpleOpenNI.SKEL_LEFT_KNEE,
				SimpleOpenNI.SKEL_LEFT_FOOT);

		drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_RIGHT_HIP);
		drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_HIP,
				SimpleOpenNI.SKEL_RIGHT_KNEE);
		drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_KNEE,
				SimpleOpenNI.SKEL_RIGHT_FOOT);

		strokeWeight(1);

	}

	public void drawLimb(int userId, int jointType1, int jointType2) {
		PVector jointPos1 = new PVector();
		PVector jointPos2 = new PVector();
		float confidence;

		// draw the joint position
		confidence = context.getJointPositionSkeleton(userId, jointType1,
				jointPos1);
		confidence = context.getJointPositionSkeleton(userId, jointType2,
				jointPos2);

		stroke(255, 0, 0, confidence * 200 + 55);
		line(jointPos1.x, jointPos1.y, jointPos1.z, jointPos2.x, jointPos2.y,
				jointPos2.z);

		drawJointOrientation(userId, jointType1, jointPos1, 50);
	}

	public void drawJointOrientation(int userId, int jointType, PVector pos,
			float length) {
		// draw the joint orientation
		PMatrix3D orientation = new PMatrix3D();
		float confidence = context.getJointOrientationSkeleton(userId,
				jointType, orientation);
		if (confidence < 0.001f)
			// nothing to draw, orientation data is useless
			return;

		pushMatrix();
		translate(pos.x, pos.y, pos.z);

		// set the local coordsys
		applyMatrix(orientation);

		// coordsys lines are 100mm long
		// x - r
		stroke(255, 0, 0, confidence * 200 + 55);
		line(0, 0, 0, length, 0, 0);
		// y - g
		stroke(0, 255, 0, confidence * 200 + 55);
		line(0, 0, 0, 0, length, 0);
		// z - b
		stroke(0, 0, 255, confidence * 200 + 55);
		line(0, 0, 0, 0, 0, length);
		popMatrix();
	}

	// -----------------------------------------------------------------
	// SimpleOpenNI user events

	public void onNewUser(int userId) {
		println("onNewUser - userId: " + userId);
		println("  start pose detection");

		context.startPoseDetection("Psi", userId);
	}

	public void onLostUser(int userId) {
		println("onLostUser - userId: " + userId);
	}

	public void onStartCalibration(int userId) {
		println("onStartCalibration - userId: " + userId);
	}

	public void onEndCalibration(int userId, boolean successfull) {
		println("onEndCalibration - userId: " + userId + ", successfull: "
				+ successfull);

		if (successfull) {
			println("  User calibrated !!!");
			context.startTrackingSkeleton(userId);
		} else {
			println("  Failed to calibrate user !!!");
			println("  Start pose detection");
			context.startPoseDetection("Psi", userId);
		}
	}

	public void onStartPose(String pose, int userId) {
		println("onStartdPose - userId: " + userId + ", pose: " + pose);
		println(" stop pose detection");

		context.stopPoseDetection(userId);
		context.requestCalibrationSkeleton(userId, true);

	}

	public void onEndPose(String pose, int userId) {
		println("onEndPose - userId: " + userId + ", pose: " + pose);
	}

	// -----------------------------------------------------------------
	// Keyboard events

	public void keyPressed() {
		switch (key) {
		case ' ':
			context.setMirror(!context.mirror());
			break;
		}

		switch (keyCode) {
		case LEFT:
			rotY += 0.1f;
			break;
		case RIGHT:
			// zoom out
			rotY -= 0.1f;
			break;
		case UP:
			if (keyEvent.isShiftDown())
				zoomF += 0.01f;
			else
				rotX += 0.1f;
			break;
		case DOWN:
			if (keyEvent.isShiftDown()) {
				zoomF -= 0.01f;
				if (zoomF < 0.01f)
					zoomF = 0.01f;
			} else
				rotX -= 0.1f;
			break;
		}
	}

	static public void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#F0F0F0", "SimpleOpenNI_User3d" });
	}
}
