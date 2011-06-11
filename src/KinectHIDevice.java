import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;
import SimpleOpenNI.SimpleOpenNI;
import SimpleOpenNI.XnVFlowRouter;
import SimpleOpenNI.XnVSessionManager;
import remixlab.proscene.HIDevice;
import remixlab.proscene.Scene;

public class KinectHIDevice extends HIDevice {

	boolean rotation = false;
	PVector last = null;

	SimpleOpenNI context;

	// NITE
	XnVSessionManager sessionManager;
	XnVFlowRouter flowRouter;

	PointDrawer pointDrawer;
	
	int currentHand = 0;

	private PVector getLastPoint() {
		
		ArrayList curList = (ArrayList) pointDrawer._pointLists.get(pointDrawer._pointLists.size()-1);
		PVector last = null;

		if (curList != null && !curList.isEmpty())
			last = (PVector) curList.get(curList.size() - 1);
		else
			last = new PVector();

		return last;
	}

	public KinectHIDevice(PApplet applet, Scene scn, SimpleOpenNI context) {
		super(scn, Mode.ABSOLUTE);
		this.context = context;

		pointDrawer = new PointDrawer(applet, context);
		flowRouter = new XnVFlowRouter();
		flowRouter.SetActive(pointDrawer);
		sessionManager = context
				.createSessionManager("Click,Wave", "RaiseHand");
		sessionManager.AddListener(flowRouter);

	}

	public void update() {
		context.update(sessionManager);
	}

	public void feedTranslation() {
		super.feedTranslation(feedXTranslation(), feedYTranslation(),
				feedZTranslation());
	}

	public float feedXTranslation() {
		return getLastPoint().x;
		// return sliderXpos.getValue();
	}

	public float feedYTranslation() {
		return getLastPoint().y;
		// return sliderYpos.getValue();
	}

	public float feedZTranslation() {
		// return sliderZpos.getValue();
		return getLastPoint().z;
	}

	public float feedXRotation() {
		// return sliderXrot.getValue();
		return 0;
	}

	public float feedYRotation() {
		// return sliderYrot.getValue();
		return 0;
	}

	public float feedZRotation() {
		// return sliderZrot.getValue();
		return 0;
	}

}
