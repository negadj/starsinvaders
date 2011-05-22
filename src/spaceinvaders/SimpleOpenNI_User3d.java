package spaceinvaders;

import processing.core.*;
import processing.xml.*;

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

public class SimpleOpenNI_User3d {

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

	PApplet applet;

	boolean drawPoints = true;
	PGraphics graphics;

	public void setup(PApplet applet, PGraphics graphics) {
		this.graphics = graphics;
		// size(1024,768,P3D); // strange, get drawing error in the
		// cameraFrustum if i use P3D, in opengl there is no problem
		this.applet = applet;
		context = new SimpleOpenNI(applet);

		// disable mirror
		context.setMirror(false);

		// enable depthMap generation
		context.enableDepth();

		// enable skeleton generation for all joints
		context.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);

		graphics.stroke(255, 255, 255);
		graphics.smooth();
	}

	public void draw() {
		// update the cam
		context.update();

		int[] depthMap = context.depthMap();
		int steps = 3; // to speed up the drawing, draw every third point
		int index;
		PVector realWorldPoint;

		graphics.stroke(100);
		if (drawPoints)
			for (int y = 0; y < context.depthHeight(); y += steps) {
				for (int x = 0; x < context.depthWidth(); x += steps) {
					index = x + y * context.depthWidth();
					if (depthMap[index] > 0) {
						// draw the projected point
						realWorldPoint = context.depthMapRealWorld()[index];
						graphics.point(realWorldPoint.x, realWorldPoint.y,
								realWorldPoint.z);
					}
				}
			}

		// draw the skeleton if it's available
		if (context.isTrackingSkeleton(1))
			drawSkeleton(1);

		// draw the kinect cam
		context.drawCamFrustum();
	}

	// draw the skeleton with the selected joints
	public void drawSkeleton(int userId) {
		graphics.strokeWeight(3);

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

		graphics.strokeWeight(1);

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

		graphics.stroke(255, 0, 0, confidence * 200 + 55);
		graphics.line(jointPos1.x, jointPos1.y, jointPos1.z, jointPos2.x,
				jointPos2.y, jointPos2.z);

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

		graphics.pushMatrix();
		graphics.translate(pos.x, pos.y, pos.z);

		// set the local coordsys
		graphics.applyMatrix(orientation);

		// coordsys lines are 100mm long
		// x - r
		graphics.stroke(255, 0, 0, confidence * 200 + 55);
		graphics.line(0, 0, 0, length, 0, 0);
		// y - g
		graphics.stroke(0, 255, 0, confidence * 200 + 55);
		graphics.line(0, 0, 0, 0, length, 0);
		// z - b
		graphics.stroke(0, 0, 255, confidence * 200 + 55);
		graphics.line(0, 0, 0, 0, 0, length);
		graphics.popMatrix();
	}

	// -----------------------------------------------------------------
	// SimpleOpenNI user events

	// -----------------------------------------------------------------
	// Keyboard events

	public void keyPressed() {
		switch (applet.key) {
		case 'm':
			context.setMirror(!context.mirror());
			break;

		case 'ñ':
			drawPoints = !drawPoints;
			break;
		}
	}

	static public void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#F0F0F0", "SimpleOpenNI_User3d" });
	}
}
