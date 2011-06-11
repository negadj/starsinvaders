package spaceinvaders.opengl;

import processing.core.PVector;

public class Line {

	public PVector p1;
	public PVector p2;

	public float[] color = { 1, 0, 0 };

	public float width = 10;

	public Line(PVector p1, PVector p2, float[] color) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.color = color;
	}

	public Line(PVector p1, PVector p2) {
		super();
		this.p1 = p1;
		this.p2 = p2;
	}

}
