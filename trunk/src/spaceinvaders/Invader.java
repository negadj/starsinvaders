package spaceinvaders;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class Invader {

	PApplet pApplet;

	float x, y, originX, originY;
	int dir = 1;
	float speed, speedIncrement;
	int yStep, delayBetweenBullets = 1000;
	int distanceAllowed = 60;
	boolean alive = true;
	int id = -1;
	int myTime = 0;
	int frameInterval = 200;
	boolean frameOne = true;

	PImage invadersFrameOne, invadersFrameTwo;

	int lastBulletTime = -10000;
	private float bulletSpeed = -5;

	int sizeUnit = 4;

	int[][] invader1 = { { 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0 },
			{ 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0 },
			{ 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0 },
			{ 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0 },
			{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
			{ 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0 },
			{ 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0 },
			{ 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1 }, };

	PGraphics graphics;

	public Invader(PApplet pApplet, PGraphics graphics, int _x, int _y,
			int _id, float _s, float _si, int _ys, int unitSize) {
		this.graphics = graphics;
		this.pApplet = pApplet;
		x = originX = _x;
		y = originY = _y;
		id = _id;
		speed = _s;
		speedIncrement = _si;
		yStep = _ys;
	}

	public void update() {
		if (alive) {
			move();
		}
	}

	public void draw() {
		if (alive) {
			// drawMe(invader1);
			drawMe();
		}
	}

	public void move() {
		x += speed * dir;
		// if(id==20){
		// println(x+"__"+originX);
		// println((abs(originX-x)>distanceAllowed));
		// println("x="+x+"  s+d:"+speed*dir);
		// }

		// Si llegamos al punto que hay que cambiar de direcci\u00f3n...
		if (PApplet.abs(x - originX) > distanceAllowed) {
			dir = -dir;
			y += yStep;
			speed += speedIncrement;
		}
	}

	public void drawMe() {
		if (pApplet.millis() - myTime > frameInterval) {
			myTime = pApplet.millis();
			frameOne = !frameOne;
		}
		if (frameOne) {
			graphics.image(invadersFrameOne, x * sizeUnit * 4, y * sizeUnit * 4);
		} else {
			graphics.image(invadersFrameTwo, x * sizeUnit * 4, y * sizeUnit * 4);
		}
	}

	public boolean checkCollision(float xx, float yy) {
		// teniendo en cuenta que los invaders son im\u00e1genes de
		// 24x16
		if (PApplet.abs(x - xx) < 12 && PApplet.abs(y - yy) < 8) {
			kill();
			return true;
		}
		return false;
	}

	public Bullet shoot() {
		Bullet bullet = null;
		if (alive)
			if (pApplet.millis() - lastBulletTime > delayBetweenBullets) {
				bullet = new Bullet(pApplet, x, y, bulletSpeed);
				lastBulletTime = pApplet.millis();
			}
		return bullet;
	}

	// GETs y SETs
	public void setX(int _x) {
		x = _x;
	}

	public void setY(int _y) {
		y = _y;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void kill() {
		alive = false;
	}

	public boolean isAlive() {
		if (alive)
			return true;
		else
			return false;
	}

	public void drawMe(int[][] bitmap) {

		pApplet.pushMatrix();
		pApplet.translate(x, y);
		for (int i = 0; i < bitmap.length; i++) {
			pApplet.translate(sizeUnit, 0);
			for (int j = 0; j < bitmap[i].length; j++) {
				pApplet.translate(0, sizeUnit);
				if (bitmap[i][j] != 0) {
					pApplet.box(sizeUnit);
				}
			}
			pApplet.translate(0, -sizeUnit * bitmap[i].length);
		}
		pApplet.popMatrix();
	}

}
