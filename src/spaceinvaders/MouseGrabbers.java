package spaceinvaders;

import processing.core.*;
import processing.xml.*;

import remixlab.proscene.*;
import remixlab.proscene.*;
import remixlab.proscene.Scene.Button;
import spaceinvaders.actions.AddRemoveBox;
import spaceinvaders.gui.Button2D;

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

import codeanticode.glgraphics.GLConstants;
import deadpixel.keystone.Keystone;

public class MouseGrabbers extends PApplet {

	/**
	 * Mouse Grabbers. by Jean Pierre Charalambos.
	 * 
	 * This example illustrates the picking mechanism built-in proscene, which
	 * represents one of the three interactive mechanisms found in proscene
	 * (camera and interactive frame, being the other two). Once you select a
	 * box it will be highlighted and you can manipulate it with the mouse. Try
	 * the different mouse buttons to see what happens.
	 * 
	 * The displayed texts are interactive. Click on them to see what happens.
	 * 
	 * Press 'h' to display the global shortcuts in the console. Press 'H' to
	 * display the current camera profile keyboard shortcuts and mouse bindings
	 * in the console.
	 */

	Scene scene;
	ArrayList boxes;
	Button2D[][] buttons = new Button2D[1][2];
	int myColor;

	Grid gridFloor;
	Grid gridOrientationView;

	Invaders invaders;

	SimpleOpenNI_User3d kinect;

	int radius = 100;

	boolean applyOrientation = false;

	public void setup() {
		size(800, 600, OPENGL);
		// hint(ENABLE_OPENGL_4X_SMOOTH);
		// hint(ENABLE_ACCURATE_TEXTURES);
		// hint(ENABLE_NATIVE_FONTS);
		scene = new Scene(this);
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
		buttons[0][0] = new AddRemoveBox(this, scene, new PVector(10, 10), "+",
				32, true);
		buttons[0][1] = new AddRemoveBox(this, scene, new PVector(
				10 + buttons[0][0].myWidth, 10), "-", 32, false);
		scene.setGridIsDrawn(true);
		// scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		scene.setRadius(3000);
		scene.showAll();
		scene.camera().setPosition(new PVector(0, 0, -1500));
		scene.camera().lookAt(new PVector(0, 0, 3000));
		scene.showAll();
		scene.camera().orientation().rotate(new PVector(0, 0, 1));

		myColor = 125;
		boxes = new ArrayList();
		addBox();

		gridFloor = new Grid(this, scene, 20);
		gridFloor.c = color(0, 255, 0);

		gridOrientationView = new Grid(this, scene, 20);
		gridOrientationView.c = color(255, 0, 255);

		invaders = new Invaders(this, scene, 20);
		invaders.c = color(0, 255, 0);

		kinect = new SimpleOpenNI_User3d();
		kinect.setup(this);

//		ks = new Keystone(this);
//		surface = ks.createCornerPinSurface(width, height, 20);
	}

	public void draw() {
		// Proscene sets the background to black by default. If you need to
		// change
		// it, don't call background() directly but use scene.background()
		// instead.
		// lights();
		// ambientLight(255,255,255);
		// specular(155);
		// for (int i = 0; i < buttons.length; i++) {
		// buttons[i][0].display();
		// buttons[i][1].display();
		// }
		//

		kinect.draw();

		for (int i = 0; i < boxes.size(); i++) {
			Box box = (Box) boxes.get(i);
			box.draw(true);
		}

		gridFloor.draw();
		gridOrientationView.draw(true);
		invaders.draw();

		text(frameRate, 50, 30);

		if (kinect.context.isTrackingSkeleton(1)) {
			// SimpleOpenNI.SKEL_HEAD
			PVector vector = new PVector();
			kinect.context.getJointPositionSkeleton(1, SimpleOpenNI.SKEL_HEAD,
					vector);
			scene.camera().setPosition(vector);
			scene.camera().lookAt(((Box) boxes.get(0)).getPosition());
		}

		if (applyOrientation) {
			Quaternion orientation = gridOrientationView.getOrientation();
			Quaternion clone = new Quaternion(orientation.x, orientation.y,
					orientation.z, orientation.w);
			scene.camera().setOrientation(orientation);
		}

	}

	@Override
	public void keyPressed() {
		switch (key) {
		case '?':
			applyOrientation = !applyOrientation;
		case 'q':
			gridFloor.w++;
			break;
		case 'a':
			gridFloor.w--;
			break;
		case 'i':
			scene.camera()
					.frame()
					.rotate(new Quaternion(new PVector(0, 0, 1),
							-PApplet.QUARTER_PI / 100));
			break;
		case 'k':
			gridFloor.w--;
			break;

		case 'o':
			radius += 10;
			scene.setRadius(radius);
			println(radius);
			break;
		case 'l':
			radius -= 10;
			scene.setRadius(radius);
			println(radius);
			break;
		case 'w':
			gridFloor.h++;
			break;
		case 's':
			gridFloor.h--;
			break;
		case 'e':
			gridFloor.size++;
			break;
		case 'd':
			gridFloor.size--;
			break;
		default:

			break;
		}

		if (key == CODED) {
			// y si lo es, si es la flecha izquierda o derecha
			if (keyCode == LEFT) {
				invaders.nave.incrementX();
			} else if (keyCode == RIGHT) {
				invaders.nave.decrementX();
			}
		} else {
			// si le damos a la tecla espacio
			if (key == '<') {
				invaders.shoot(this);
			}
		}
		kinect.keyPressed();

	}

	public void addBox() {
		Box box = new Box(this, scene);
		box.setSize(20, 20, 20);
		box.setColor(color(0, 0, 255));
		boxes.add(box);
	}

	public void removeBox() {
		if (boxes.size() > 0) {
			scene.removeFromMouseGrabberPool(((InteractiveFrameElement) boxes
					.get(0)).iFrame);
			boxes.remove(0);
		}
	}

	public void onNewUser(int userId) {
		println("onNewUser - userId: " + userId);
		println("  start pose detection");

		kinect.context.startPoseDetection("Psi", userId);
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
			kinect.context.startTrackingSkeleton(userId);
		} else {
			println("  Failed to calibrate user !!!");
			println("  Start pose detection");
			kinect.context.startPoseDetection("Psi", userId);
		}
	}

	public void onStartPose(String pose, int userId) {
		println("onStartdPose - userId: " + userId + ", pose: " + pose);
		println(" stop pose detection");

		kinect.context.stopPoseDetection(userId);
		kinect.context.requestCalibrationSkeleton(userId, true);

	}

	public void onEndPose(String pose, int userId) {
		println("onEndPose - userId: " + userId + ", pose: " + pose);
	}

	static public void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#F0F0F0",
				"spaceinvaders.MouseGrabbers" });
	}
}
