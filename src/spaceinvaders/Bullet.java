package spaceinvaders;

import processing.core.PApplet;
import processing.core.PGraphics;

public class Bullet {

	float x, y, speed;
	float sx, sy;
	PApplet pApplet;
	PGraphics graphics;

	Bullet(PApplet pApplet, PGraphics graphics, float x, float y, float s) {
		
		this.graphics = graphics;
		this.x = x;
		this.y = y;
		this.sx = x;
		this.sy = y;
		speed = s;
		this.pApplet = pApplet;
	}

	public void update() {
		move();
	}

	public void move() {
		y -= speed;
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
		graphics.pushStyle();
		graphics.stroke(255);
		graphics.strokeWeight(2);
		graphics.line(x, y - 5, 0, x, y + 5, 0);

		graphics.popStyle();
	}

}
