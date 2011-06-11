import processing.core.*;
import processing.xml.*;

import procontroll.*;
import net.java.games.input.*;
import remixlab.proscene.*;

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

import SimpleOpenNI.SimpleOpenNI;
import SimpleOpenNI.XnVSessionManager;

public class HIDeviceSingleViewer2 extends PApplet {

	/**
	 * HIDevice by Jean Pierre Charalambos.
	 * 
	 * This example illustrates the use of the HIDevice (Human Interaction
	 * Device) class to manipulate your scene through sophisticated interaction
	 * devices, such as the 3d space navigator (which is required to run the
	 * sketch).
	 * 
	 * This example requires the procontroll library:
	 * http://www.creativecomputing.cc/p5libs/procontroll/
	 * 
	 * Press 'h' to toggle the mouse and keyboard navigation help.
	 */

	// 3DConnexion SpaceNavigator Demo for rotation, translation and buttons
	// Device must be correctly installed
	// procontroll must be installed

	// Ralf L\u00f6hmer - rl@loehmer.de

	Scene scene;
	KinectHIDevice dev;

	SimpleOpenNI context;

	// NITE
	XnVSessionManager sessionManager;

	public void setup() {
		size(640, 360, P3D);

		// ControllSlider.setMultiplier OR HIDevice.set*Sensitivity
		// gives the same results. Be sure to just use one of them (no both!).
		// Here we use HIDevice.set*Sensitivity (see below)
		/**
		 * sliderXpos.setMultiplier(0.01f); // sensitivities
		 * sliderYpos.setMultiplier(0.01f); sliderZpos.setMultiplier(0.01f);
		 * sliderXrot.setMultiplier(0.0001f); sliderYrot.setMultiplier(0.0001f);
		 * sliderZrot.setMultiplier(0.0001f);
		 */

		scene = new Scene(this);
		scene.setGridIsDrawn(true);
		scene.setAxisIsDrawn(true);
		scene.setInteractiveFrame(new InteractiveFrame(scene));
		scene.interactiveFrame().translate(new PVector(30, 30, 0));

		// press 'i' to switch the interaction between the camera frame and the
		// interactive frame
		scene.setShortcut('i', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);
		// press 'f' to display frame selection hints
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);

		// How to use a HIDevice?
		// Option 1: Derivate from HIDevice and override the feeds
		/**
		 * dev = new HIDevice(scene) { public float feedXTranslation() { return
		 * sliderXpos.getValue(); } public float feedYTranslation() { return
		 * sliderYpos.getValue(); } public float feedZTranslation() { return
		 * sliderZpos.getValue(); } public float feedXRotation() { return
		 * sliderXrot.getValue(); } public float feedYRotation() { return
		 * sliderYrot.getValue(); } public float feedZRotation() { return
		 * sliderZrot.getValue(); } }; //
		 */

		// /**
		// Option 2: declare your own HIDevice and add a feed handler
		// Here we define a RELATIVE mode HIDevice (that's
		// the space navigator).

		context = new SimpleOpenNI(this);

		// mirror is by default enabled
		context.setMirror(true);

		// enable depthMap generation
		context.enableDepth();

		// enable the hands + gesture
		context.enableGesture();
		context.enableHands();

		// setup NITE
		dev = new KinectHIDevice(this, scene, context);
		// The following line would define an ABSOLUTE mode HIDevice
		// such as the wii or the kinect (see the HIDevice for details).
		// dev = new HIDevice(scene, HIDevice.Mode.ABSOLUTE);
		//dev.addHandler(this, "feed");
		// */

		dev.setTranslationSensitivity(0.1f, 0.1f, 0.1f);
		dev.setRotationSensitivity(0.0001f, 0.0001f, 0.0001f);
		scene.addDevice(dev);
	}

	public void feed(HIDevice d) {
		KinectHIDevice device = (KinectHIDevice) d;
		device.feedTranslation();
		// d.feedTranslation(sliderXpos.getValue(), sliderYpos.getValue(),
		// sliderZpos.getValue());
		// d.feedRotation(sliderXrot.getValue(), sliderYrot.getValue(),
		// sliderZrot.getValue());
	}

	public void draw() {

		context.update();
		dev.update();

		background(33, 170, 170);
		noStroke();

		// draw scene:
		fill(204, 102, 0);
		sphere(30);
		// Save the current model view matrix
		pushMatrix();
		scene.interactiveFrame().applyTransformation();// very efficient
		// Draw the interactive frame local axis
		scene.drawAxis(20);
		// Draw a box associated with the iFrame
		stroke(122);
		if (scene.interactiveFrameIsDrawn()) {
			fill(0, 255, 255);
			box(10, 15, 12);
		} else {
			fill(0, 0, 255);
			box(10, 15, 12);
		}
		popMatrix();
	}

	public void keyPressed() {
		if ((key == 'u') || (key == 'U'))
			dev.nextCameraMode();
		if ((key == 'v') || (key == 'V'))
			dev.nextIFrameMode();
	}

	static public void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#F0F0F0", "HIDeviceSingleViewer2" });
	}
}
