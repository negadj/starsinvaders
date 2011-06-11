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
import SimpleOpenNI.XnVFlowRouter;
import SimpleOpenNI.XnVSessionManager;

public class HIDeviceSingleViewer extends PApplet {

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
	HIDevice dev;

	XnVSessionManager sessionManager;
	XnVFlowRouter flowRouter;
	PointDrawer pointDrawer;

	SimpleOpenNI context;

	boolean translation = true;

	public void setup() {
		size(640, 360, P3D);

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

		dev = new HIDevice(scene);
		// The following line would define an ABSOLUTE mode HIDevice
		// such as the wii or the kinect (see the HIDevice for details).
		dev = new HIDevice(scene, HIDevice.Mode.ABSOLUTE);
		dev.addHandler(this, "feed");
		// */

		dev.setTranslationSensitivity(0.1f, 0.1f, 0.1f);
		dev.setRotationSensitivity(0.01f, 0.01f, 0.01f);
		scene.addDevice(dev);

		context = new SimpleOpenNI(this);

		// mirror is by default enabled
		context.setMirror(true);

		// enable depthMap generation
		context.enableDepth();

		// enable the hands + gesture
		context.enableGesture();
		context.enableHands();

		// setup NITE
		sessionManager = context
				.createSessionManager("Click,Wave", "RaiseHand");

		pointDrawer = new PointDrawer(this, context);
		flowRouter = new XnVFlowRouter();
		flowRouter.SetActive(pointDrawer);

		sessionManager.AddListener(flowRouter);
	}

	public void feed(HIDevice d) {
		PVector last = pointDrawer.getCurrentPoint();
		if (last != null) {
			if (translation)
				d.feedTranslation(last.x, last.y, -last.z);
			else
				d.feedRotation(last.x, last.y, last.z);
		}
	}

	public void draw() {

		context.update();

		// update nite
		context.update(sessionManager);

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
		if ((key == '<'))
			translation = !translation;
		if ((key == '>'))
			context.setMirror(!context.mirror());

	}

	static public void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#F0F0F0", "HIDeviceSingleViewer" });
	}
}
