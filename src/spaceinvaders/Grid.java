package spaceinvaders;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PGraphics3D;
import processing.core.PVector;
import remixlab.proscene.InteractiveFrame;
import remixlab.proscene.Quaternion;
import remixlab.proscene.Scene;
import spaceinvaders.opengl.FastVolumetric;
import spaceinvaders.opengl.Line;

/**
 * Box. by Jean Pierre Charalambos.
 * 
 * This class is part of the Mouse Grabber example.
 * 
 * Any object that needs to be "pickable" (such as the Box), should be attached
 * to its own InteractiveFrame. That's all there is to it.
 * 
 * The built-in picking proscene mechanism actually works as follows. At
 * instantiation time all InteractiveFrame objects are added to a mouse grabber
 * pool. Scene parses this pool every frame to check if the mouse grabs a
 * InteractiveFrame by projecting its origin onto the screen. If the mouse
 * position is close enough to that projection (default implementation defines a
 * 10x10 pixel square centered at it), the object will be picked.
 * 
 * Override InteractiveFrame.checkIfGrabsMouse if you need a more sophisticated
 * picking mechanism.
 * 
 * Observe that this class is used among many examples, such as MouseGrabber
 * CajasOrientadas, PointUnderPixel and ScreenDrawing. Hence, it's quite
 * complete, but its functionality is not totally exploited by this example.
 */

public class Grid extends InteractiveFrameElement {
	public float w, h, size;
	public int c;
	float sizeUnit;
	PGraphics graphics;
	Line[] linesW;
	Line[] linesH;
	boolean delegateRender = false;
	FastVolumetric fastVolumetric;

	public Grid(PApplet applet, PGraphics graphics, Scene scene, float sizeUnit) {
		super(applet, scene);
		this.graphics = graphics;
		this.sizeUnit = sizeUnit;
		setSize();
		setColor();
		setPosition();
		iFrame.setGrabsMouseThreshold(25);
	}

	public void createOpenGlRenderization(FastVolumetric fastVolumetric) {
		this.fastVolumetric = fastVolumetric;

		linesW = new Line[(int) w + 1];
		for (int x = 0; x <= w; x++) {
			float[] color = {0,1,0};
			linesW[x] = new Line(new PVector(x * size, 0, 0), new PVector(x
					* size, 0, h * size), color);
		}

		linesH = new Line[(int) h + 1];
		for (int x = 0; x <= h; x++) {
			float[] color = {0,1,0};
			linesH[x] = new Line(new PVector(0, 0, x * size), new PVector(w
					* size, 0, x * size), color);
		}

		delegateRender = true;
	}

	// don't draw local axis
	public void draw() {
		draw(true);
	}

	public void draw(boolean drawAxis) {

		graphics.pushMatrix();
		graphics.pushStyle();
		// Multiply matrix to get in the frame coordinate system.
		// scene.parent.applyMatrix(iFrame.matrix()) is handy but inefficient
		iFrame.applyTransformation((PGraphics3D) graphics); // optimum
		scene.drawAxis(10);
		if (drawAxis)
			scene.drawAxis(size * 1.3f);
		// graphics.noStroke();
		graphics.strokeWeight(1);
		graphics.noFill();
		if (iFrame.grabsMouse())
			graphics.stroke(255, 0, 0);
		else
			graphics.stroke(getColor());

		if (delegateRender)
			drawOPENGLGrid();
		else
			drawGrid();

		graphics.popStyle();
		graphics.popMatrix();
	}

	private void drawOPENGLGrid() {
		for (int i = 0; i < linesH.length; i++) {
			fastVolumetric.drawLine(linesH[i]);
		}

		for (int i = 0; i < linesW.length; i++) {
			fastVolumetric.drawLine(linesW[i]);
		}
	}

	private void drawGrid() {
		float halfH = size * h / 2;
		float halfW = size * w / 2;
		graphics.pushMatrix();
		graphics.translate(-halfH, 0, 0);
		// graphics.translate(-size * h / 2, 0, size);
		for (int z = 0; z < w; z++) {
			for (int x = 0; x < h; x++) {
				graphics.box(size, 0, size);
				graphics.translate(size, 0, 0);
			}
			graphics.translate(-size * h, 0, size);
		}
		graphics.popMatrix();
	}

	// sets size randomly
	public void setSize() {
		w = 5;
		h = 30;
		size = 10 * sizeUnit;
	}

	public void setSize(float myW, float myH, float mySize) {
		w = myW;
		h = myH;
		size = mySize;
	}

	public int getColor() {
		return c;
	}

	// sets color randomly
	public void setColor() {
		c = applet.color(applet.random(0, 255), applet.random(0, 255),
				applet.random(0, 255));
	}

	public void setColor(int myC) {
		c = myC;
	}

	public PVector getPosition() {
		return iFrame.position();
	}

	// sets position randomly
	public void setPosition() {
		float low = -100;
		float high = 100;
		iFrame.setPosition(new PVector(applet.random(low, high), applet.random(
				low, high), applet.random(low, high)));
	}

	public void setPosition(PVector pos) {
		iFrame.setPosition(pos);
	}

	public Quaternion getOrientation() {
		return iFrame.orientation();
	}

	public void setOrientation(PVector v) {
		PVector to = PVector.sub(v, iFrame.position());
		iFrame.setOrientation(new Quaternion(new PVector(0, 1, 0), to));
	}
}