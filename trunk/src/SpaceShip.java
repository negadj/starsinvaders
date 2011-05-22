import processing.core.PApplet;
import processing.core.PImage;

class SpaceShip {
	int x, y;
	int xStep, delayBetweenBullets;
	int lastBulletTime = -10000;
	PImage spaceShip;
	PApplet pApplet;

	public SpaceShip(PApplet pApplet, int _x, int _y, int _s, int _d) {
		x = _x;
		y = _y;
		xStep = _s;
		delayBetweenBullets = _d;
		this.pApplet = pApplet;
	}

	public void update() {
		drawMe();
		checkInvaders();
	}

	public void drawMe() {
		// println("_____________"+x+"oo"+y);
		pApplet.image(spaceShip, x, y);
	}

	public void incrementX() {
		x += xStep;
	}

	public void decrementX() {
		x -= xStep;
	}

	public void setX(int _newX) {
		x = _newX;
	}

//	public void shoot(PApplet pApplet) {
//		if (pApplet.millis() - lastBulletTime > delayBetweenBullets) {
//			bulletsList.add(new Bullet(pApplet, x, 10, bulletSpeed));
//			lastBulletTime = millis();
//		}
//	}

	public void checkInvaders() {
//		for (int i = 0; i < numOfInvaders; i++) {
//			if (invaders[i].isAlive()) {
//				float iy = invaders[i].getY();
//				if (iy >= height - spaceShipDistanceToBottom - 15) {
//					// SE ACAB\u00d3 EL JUEGO
//					fill(255, 0, 0);
//					stroke(255, 0, 0);
//					rect(0, 0, width, height);
//				}
//			}
//		}
	}
}
