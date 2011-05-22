import processing.core.PApplet;
import processing.core.PImage;

class invader {

	PApplet pApplet;

	float x, y, originX, originY;
	int dir = 1;
	float speed, speedIncrement;
	int yStep;
	int distanceAllowed = 60;
	boolean alive = true;
	int id = -1;
	int myTime = 0;
	int frameInterval = 200;
	boolean frameOne = true;

	PImage invadersFrameOne, invadersFrameTwo;

	invader(PApplet pApplet, int _x, int _y, int _id, float _s, float _si,
			int _ys) {

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
			pApplet.image(invadersFrameOne, x, y);
		} else {
			pApplet.image(invadersFrameTwo, x, y);
		}
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

}
