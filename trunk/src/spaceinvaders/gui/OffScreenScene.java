package spaceinvaders.gui;

import processing.core.PApplet;
import processing.core.PGraphics3D;
import remixlab.proscene.Scene;

public class OffScreenScene extends Scene {

	public OffScreenScene(PApplet p, PGraphics3D renderer) {
		super(p, renderer);
	}

	
	public void setMouseTracking(boolean enable) {
//		mouseTrckn = enable;
	}
}
