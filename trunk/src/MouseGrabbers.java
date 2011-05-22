import Box;
import Grid;
import InteractiveFrameElement;
import Invaders;
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

	Grid grid;

	Invaders invaders;

	public void setup() {
		size(640, 360, P3D);
		smooth();
		scene = new Scene(this);
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
		buttons[0][0] = new AddRemoveBox(this, scene, new PVector(10, 10), "+",
				32, true);
		buttons[0][1] = new AddRemoveBox(this, scene, new PVector(
				10 + buttons[0][0].myWidth, 10), "-", 32, false);
		scene.setGridIsDrawn(true);
		// scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		scene.setRadius(150);
		scene.showAll();

		myColor = 125;
		boxes = new ArrayList();
		addBox();

		grid = new Grid(this, scene);
		grid.c = color(0, 255, 0);

		invaders = new Invaders(this, scene);
		invaders.c = color(0, 255, 0);
	}

	public void draw() {
		// Proscene sets the background to black by default. If you need to
		// change
		// it, don't call background() directly but use scene.background()
		// instead.
		for (int i = 0; i < buttons.length; i++) {
			buttons[i][0].display();
			buttons[i][1].display();
		}

		for (int i = 0; i < boxes.size(); i++) {
			Box box = (Box) boxes.get(i);
			box.draw(true);
		}

		grid.draw();
		invaders.draw();
	}

	@Override
	public void keyPressed() {
		switch (key) {
		case 'q':
			grid.w++;
			break;
		case 'a':
			grid.w--;
			break;
		case 'w':
			grid.h++;
			break;
		case 's':
			grid.h--;
			break;
		case 'e':
			grid.size++;
			break;
		case 'd':
			grid.size--;
			break;
		default:

			break;
		}

		if (key == CODED) {
			// y si lo es, si es la flecha izquierda o derecha
			if (keyCode == LEFT) {
				// nave.decrementX();
			} else if (keyCode == RIGHT) {
				// nave.incrementX();
			}
		} else {
			// si le damos a la tecla espacio
			if (key == '<') {
				invaders.shoot(this);
			}
		}
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

	static public void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#F0F0F0", "MouseGrabbers" });
	}
}
