package spaceinvaders;
import processing.core.PApplet;
import remixlab.proscene.InteractiveFrame;
import remixlab.proscene.Scene;

public class InteractiveFrameElement {

	protected InteractiveFrame iFrame;
	protected PApplet applet;
	protected Scene scene;

	public InteractiveFrameElement(PApplet applet, Scene scene) {
		super();
		this.applet = applet;
		this.scene = scene;
		iFrame = new InteractiveFrame(scene);
	}

}