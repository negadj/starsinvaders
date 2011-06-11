package spaceinvaders;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import spaceinvaders.opengl.FastVolumetric;
import spaceinvaders.opengl.Line;

public class Bullet {

	float x, y, speed;
	float sx, sy;
	PApplet pApplet;
	PGraphics graphics;
	FastVolumetric fastVolumetric;

	public Bullet(PApplet pApplet, PGraphics graphics, float x, float y, float s) {

		this.graphics = graphics;
		this.x = x;
		this.y = y;
		this.sx = x;
		this.sy = y;
		speed = s;
		this.pApplet = pApplet;
	}

	public void createOpenGlRenderization(FastVolumetric fastVolumetric) {
		this.fastVolumetric = fastVolumetric;
	}

	public void update() {
		move();
	}

	public void move() {
		y += speed;
	}

	public void checkCollision(Invader[] invaders) {
		for (int i = 0; i < invaders.length; i++) {
			if (invaders[i].isAlive()) {
				boolean killed = invaders[i].checkCollision(x, y);
				if (killed)
					break;
			}
		}
	}

	public void drawMe() {

		if (fastVolumetric != null) {
			Line line = new Line(new PVector(x, 0, y - 20), new PVector(x, 0,
					y + 20));
			line.width = 50;
			float[] c = { 1f, 0, 0 };
			line.color = c;
			fastVolumetric.drawLine(line);
		} else {

			graphics.pushStyle();
			graphics.stroke(255);
			graphics.strokeWeight(2);
			graphics.line(x, 0, y - 5, x, 0, y + 5);
			graphics.popStyle();
		}

		
	}
}
