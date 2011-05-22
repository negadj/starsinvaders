package spaceinvaders.actions;

import processing.core.PApplet;
import processing.core.PVector;
import remixlab.proscene.Camera;
import remixlab.proscene.Scene;
import remixlab.proscene.Scene.Button;
import spaceinvaders.MouseGrabbers;
import spaceinvaders.gui.Button2D;

public class AddRemoveBox extends Button2D {

	boolean addBox;

	MouseGrabbers mouseGrabbers;

	public AddRemoveBox(PApplet pApplet, Scene scn, PVector p, String t,
			int fontSize, boolean addB) {
		super(pApplet, scn, p, t, fontSize);
		addBox = addB;
	}

	public void mouseClicked(Button button, int numberOfClicks, Camera camera) {
		if (numberOfClicks == 1) {
			if (addBox)
				mouseGrabbers.addBox();
			else
				mouseGrabbers.removeBox();
		}
	}
}
