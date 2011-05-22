package spaceinvaders;

import processing.core.PApplet;

public class Bullet {

	float x, y, speed;
	float sx, sy;
	PApplet pApplet;

	Bullet(PApplet pApplet, float x, float y, float s) {
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
		pApplet.pushStyle();
		pApplet.stroke(255);
		pApplet.strokeWeight(2);
		pApplet.line(x, y - 5, 0, x, y + 5, 0);

		pApplet.popStyle();
	}

}
