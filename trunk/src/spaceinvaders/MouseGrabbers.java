package spaceinvaders;

import processing.core.*;
import processing.xml.*;

import remixlab.proscene.*;
import remixlab.proscene.*;
import remixlab.proscene.Scene.Button;
import spaceinvaders.actions.AddRemoveBox;
import spaceinvaders.gui.Button2D;
import spaceinvaders.opengl.FastVolumetric;

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
import codeanticode.glgraphics.GLGraphicsOffScreen;
import codeanticode.glgraphics.GLTextureWindow;
import deadpixel.keystone.CornerPinSurface;
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

	Invaders invaders;

	SimpleOpenNI_User3d kinect;

	int radius = 100;
	GLGraphicsOffScreen glg1;

	Keystone ks;
	CornerPinSurface surface;
	int userCalibrated = 0;
	boolean tracking = false;

	FastVolumetric fastVolumetric;

	boolean kinectMode = true;

	public void setup() {
		size(1024, 768, GLConstants.GLGRAPHICS);
		hint(ENABLE_OPENGL_4X_SMOOTH);
		hint(ENABLE_ACCURATE_TEXTURES);
		hint(ENABLE_NATIVE_FONTS);

		glg1 = new GLGraphicsOffScreen(this, width, height, true, 4);

		ks = new Keystone(this);
		surface = ks.createCornerPinSurface(width, height, 20);

		// The texture of the keystoned canvas is attached to the output window.
		// Anything is drawn on this canvas will be seen in the output window.

		scene = new Scene(this, glg1);

		fastVolumetric = new FastVolumetric(this, glg1);

		DesktopEvents desktopEvents = new DesktopEvents(scene);
		registerMouseEvent(desktopEvents);

		scene.setMouseTracking(true);
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);

		scene.setGridIsDrawn(true);
		// scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		scene.setRadius(4000);
		scene.showAll();
		scene.enableMouseHandling();
		scene.camera().setPosition(new PVector(0, 0, -1500));
		scene.camera().lookAt(new PVector(0, 0, 3000));

		scene.camera().orientation().rotate(new PVector(0, 0, 1));

		myColor = 125;
		boxes = new ArrayList();
		addBox();

		gridFloor = new Grid(this, glg1, scene, 20);
		gridFloor.c = color(0, 255, 0);
		gridFloor.createOpenGlRenderization(fastVolumetric);

		invaders = new Invaders(this, glg1, scene, 10);
		invaders.c = color(0, 255, 0);
		invaders.h = 4;
		invaders.w = 4;
		invaders.fastVolumetric = fastVolumetric;

		if (kinectMode) {
			kinect = new SimpleOpenNI_User3d();
			kinect.setup(this, glg1);

			println(kinect.context.isInit());
		} else {
			println("NoKinectMode");
		}

		frameRate(60);

	}

	public void draw() {

		background(0);
		glg1.beginDraw();
		scene.beginDraw();

		if (kinectMode)
			kinect.draw();

		for (int i = 0; i < boxes.size(); i++) {
			Box box = (Box) boxes.get(i);
			box.draw(true);
		}

		invaders.draw();
		fastVolumetric.beginDraw();
		gridFloor.draw();
		// fastVolumetric.draw();
		invaders.drawShoots();
		fastVolumetric.endDraw();

		text(frameRate, 50, 30);

		if (kinectMode)
			checkUserAndApplyHeadTrackingCamera();
		else
			moveShip();

		scene.endDraw();
		glg1.endDraw();

		surface.render(glg1.getTexture());

		text(frameRate, 10, 10);
		text("userCalibrated:" + userCalibrated + " tracking:" + tracking, 10,
				20);

	}

	private void moveShip() {

		PVector vector = scene.camera().position();
		invaders.setPositionFire(vector);

	}

	private void checkUserAndApplyHeadTrackingCamera() {

		tracking = false;

		if (kinect.context.isTrackingSkeleton(userCalibrated)) {
			// SimpleOpenNI.SKEL_HEAD
			PVector vector = new PVector();
			kinect.context.getJointPositionSkeleton(userCalibrated,
					SimpleOpenNI.SKEL_HEAD, vector);
			scene.camera().setPosition(vector);
			scene.camera().lookAt(((Box) boxes.get(0)).getPosition());
			tracking = true;
			invaders.setPositionFire(vector);
			doShoot(userCalibrated);
		} else {
			invaders.startShoot = 0;
		}
	}

	private void doShoot(int userId) {

		if (kinect.checkShootPosition(userId)) {
			invaders.shooting();
		}
	}

	@Override
	public void keyPressed() {
		switch (key) {
		case 'q':
			gridFloor.w++;
			break;
		case 'a':
			gridFloor.w--;
			break;
		case '+':
			scene.camera().setFieldOfView(scene.camera().fieldOfView() + 0.05f);
			break;
		case '-':
			scene.camera().setFieldOfView(scene.camera().fieldOfView() - 0.05f);
			break;
		case '=':
			// enter/leave calibration mode, where surfaces can be warped
			// & moved
			ks.toggleCalibration();
			break;
		case '¿':
			scene.camera()
					.frame()
					.rotate(new Quaternion(new PVector(0, 0, 1),
							-PApplet.QUARTER_PI / 100));
			break;
		case '?':
			scene.camera().frame().rotate(new Quaternion(new PVector(0, 0, 1),

			PApplet.QUARTER_PI / 100));
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
				invaders.nave.decrementX();
			} else if (keyCode == RIGHT) {
				invaders.nave.incrementX();
			}
		} else {
			// si le damos a la tecla espacio
			if (key == '<') {
				invaders.shoot();
			}
		}
		if (kinectMode)
			kinect.keyPressed();

	}

	public void addBox() {
		Box box = new Box(this, glg1, scene);
		box.setSize(200, 200, 200);
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

	@Override
	public void mouseMoved() {
		super.mouseMoved();
		PVector mouse = surface.getTransformedMouse();
		gridFloor.iFrame.checkIfGrabsMouse((int) mouse.x, (int) mouse.y,
				scene.camera());
		if (gridFloor.iFrame.grabsMouse()) {
			println("grabbsss!!!");
		}
	}

	public void mouseDragged() {
		super.mouseDragged();
		PVector mouse = surface.getTransformedMouse();
		gridFloor.iFrame.checkIfGrabsMouse((int) mouse.x, (int) mouse.y,
				scene.camera());
		if (gridFloor.iFrame.grabsMouse()) {
			println("grabbsss!!!");
		}
	}

	public void onNewUser(int userId) {
		println("onNewUser - userId: " + userId);
		println("  start pose detection");

		kinect.context.startPoseDetection("Psi", userId);
	}

	public void onLostUser(int userId) {
		println("onLostUser - userId: " + userId);
		userCalibrated = 0;
	}

	public void onStartCalibration(int userId) {
		println("onStartCalibration - userId: " + userId);
	}

	public void onEndCalibration(int userId, boolean successfull) {
		println("onEndCalibration - userId: " + userId + ", successfull: "
				+ successfull);

		if (successfull) {
			println("  User calibrated !!!");
			userCalibrated = userId;
			kinect.context.startTrackingSkeleton(userId);
		} else {
			println("  Failed to calibrate user !!!");
			println("  Start pose detection");
			kinect.context.startPoseDetection("Psi", userId);
			userCalibrated = 0;
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
		// PApplet.main(new String[] { "--bgcolor=#F0F0F0",
		// "spaceinvaders.MouseGrabbers" });
		MouseGrabbers grabbers = new MouseGrabbers();
		grabbers.kinectMode = false;
		grabbers.runSketch(new String[] { "--bgcolor=#F0F0F0" });
	}
}
