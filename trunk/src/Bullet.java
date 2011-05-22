import processing.core.PApplet;

public class Bullet {
	float x, y, speed;
	PApplet pApplet;

	Bullet(PApplet pApplet, float x, float y, float s) {
		this.x = x;
		this.y = y;
		speed = s;
		this.pApplet = pApplet;
	}

	public void update() {
		move();
	}

	public void move() {
		y -= speed;
	}

	public void checkInvaders() {
		// for (int i = 0; i < numOfInvaders; i++) {
		// if (invaders[i].isAlive()) {
		// float ix = invaders[i].getX();
		// float iy = invaders[i].getY();
		// // teniendo en cuenta que los invaders son im\u00e1genes de
		// // 24x16
		// if (abs(ix - x) < 12 && abs(iy - y) < 8) {
		// invaders[i].kill();
		// removeMe();
		// }
		// }
		// }
	}

	public void drawMe() {
		pApplet.pushStyle();
		pApplet.stroke(255);
		pApplet.strokeWeight(2);
		pApplet.line(x, y - 5, 0, x, y + 5, 0);
		
		pApplet.popStyle();
	}

}
