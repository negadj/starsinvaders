package spaceinvaders.gui;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;
import remixlab.proscene.Camera;
import remixlab.proscene.MouseGrabber;
import remixlab.proscene.Scene;

/**
 * Button 2D. by Jean Pierre Charalambos.
 * 
 * Base class of "2d buttons" that shows how simple is to implement a
 * MouseGrabber which can enable complex mouse interactions.
 */

public class Button2D extends MouseGrabber {
	String myText;
	PFont myFont;
	public int myWidth;
	int myHeight;
	PVector position;
	PApplet pApplet;

	public Button2D(PApplet pApplet, Scene scn, PVector p, int fontSize) {
		this(pApplet, scn, p, "", fontSize);
	}

	public Button2D(PApplet pApplet, Scene scn, PVector p, String t,
			int fontSize) {
		super(scn);
		position = p;
		myText = t;
		this.pApplet = pApplet;
		myFont = pApplet.createFont("FFScala", fontSize);
		pApplet.textFont(myFont);
		pApplet.textMode(PApplet.SCREEN);
		pApplet.textAlign(PApplet.CENTER);
		setText(t);
	}

	public void setText(String text) {
		myText = text;
		myWidth = (int) pApplet.textWidth(myText);
		myHeight = (int) (pApplet.textAscent() + pApplet.textDescent());
	}

	public void display() {
		pApplet.pushStyle();
		if (grabsMouse())
			pApplet.fill(255);
		else
			pApplet.fill(100);
		pApplet.text(myText, position.x, position.y, myWidth, myHeight);
		pApplet.popStyle();
	}

	public void checkIfGrabsMouse(int x, int y, Camera camera) {
		// Rectangular activation area
		setGrabsMouse((position.x <= x) && (x <= position.x + myWidth)
				&& (position.y <= y) && (y <= position.y + myHeight));
	}
}