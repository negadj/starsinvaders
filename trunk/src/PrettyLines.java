import processing.core.*;
import processing.xml.*;

import processing.opengl.*;
import remixlab.proscene.Scene;
import spaceinvaders.actions.AddRemoveBox;
import wblut.hemesh.*;
import wblut.hemesh.modifiers.*;
import wblut.hemesh.creators.*;
import wblut.geom.*;

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

public class PrettyLines extends PApplet {

	ArrayList<LineDrawer> lineDrawers = new ArrayList<LineDrawer>();
	ArrayList<LineDrawer> allLineDrawers = new ArrayList<LineDrawer>();

	public void setup() {
		size(1280, 800);
		smooth();
		strokeWeight(0.5f);
		stroke(255);
		background(0);
		// line(0, 0, width, 0);
		// line(0, 0, 0, height);
		// line(width, 0, width, height);
		//
		// line(0, height, width, height);

	}

	public void draw() {

		ArrayList<LineDrawer> lineDrawersTemp = new ArrayList<LineDrawer>();
		ArrayList<LineDrawer> lineDrawersDelete = new ArrayList<LineDrawer>();

		for (int i = 0; i < lineDrawers.size(); i++) {

			LineDrawer drawer = lineDrawers.get(i);
			if (drawer.alive) {
				drawer.draw(this);
				if (!drawer.alive) {

					LineDrawer murdered = null;
					if (allLineDrawers.isEmpty() || frameCount % 2 == 100)
						murdered = lineDrawers.get((int) random(lineDrawers
								.size() - 1));
					else
						murdered = allLineDrawers
								.get((int) random(allLineDrawers.size() - 1));
					murdered = drawer;
					LineDrawer newDrawer = newDrawer(murdered);
					LineDrawer newDrawer2 = newDrawer(murdered);

					if (newDrawer != null)
						lineDrawersTemp.add(newDrawer);
					if (newDrawer2 != null)
						lineDrawersTemp.add(newDrawer2);

					lineDrawersDelete.add(drawer);
				}
			}
		}

		if (!lineDrawersTemp.isEmpty()) {
			lineDrawers.addAll(lineDrawersTemp);
		}
		if (!lineDrawersDelete.isEmpty()) {
			lineDrawers.removeAll(lineDrawersDelete);
			allLineDrawers.addAll(lineDrawersDelete);
		}

		smooth();
	}

	public LineDrawer newDrawer(LineDrawer lineDrawer) {

		if (lineDrawer.size < 20) {
			return null;
		}

		float newAngle;
		if (random(2) > 1)
			newAngle = lineDrawer.angle + radians(90);
		else
			newAngle = lineDrawer.angle + radians(-90);

		pushMatrix();
		translate(lineDrawer.x, lineDrawer.y);
		rotate(lineDrawer.angle);
		int growed = (int) random(lineDrawer.size);
		int newX = (int) screenX(0, growed);
		int newY = (int) screenY(0, growed);
		popMatrix();

		LineDrawer newDrawer = new LineDrawer(newX, newY, newAngle, this);
		return newDrawer;
	}

	@Override
	public void mouseClicked() {
		LineDrawer drawer = new LineDrawer(mouseX, mouseY,
				random(radians(360)), this);
		drawer.step = 0.5f;

		lineDrawers.add(drawer);
	}

	@Override
	public void keyPressed() {
		background(0);
	}

	static public void main(String args[]) {
		PApplet.main(new String[] { "--present", "--bgcolor=#F0F0F0",
				"PrettyLines" });
	}
}
