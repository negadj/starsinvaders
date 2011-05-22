import processing.core.PApplet;

public class LineDrawer {

	float angle = 0;
	float size = 1;
	boolean alive = true;
	int color = 255;
	int x, y;
	float step = 0.5f;

	public LineDrawer(int x, int y, float angle, PApplet applet) {
		super();
		this.angle = angle;
		this.x = x;
		this.y = y;
		color = applet.color(255, 0, 0);
	}

	public void draw(PApplet pApplet) {

		if (alive) {
			pApplet.pushMatrix();
			pApplet.translate(x, y);
			pApplet.rotate(angle);
			int screenX = (int) pApplet.screenX(0, size + 1);
			int screenY = (int) pApplet.screenY(0, size + 1);

			if (screenX > 0 && screenX < pApplet.width && screenY > 0
					&& screenY < pApplet.height) {
				pApplet.strokeWeight(1);
				pApplet.stroke(color, 60);
				pApplet.line(0, 0, 0, size);
				size += step;
				//angle += step;
			} else {
				alive = false;
			}
			pApplet.popMatrix();

			int cTemp = pApplet.get(screenX, screenY);
			if (pApplet.red(cTemp) > 250) {
				alive = false;
			}
		}

	}
}
