import processing.core.*;
import processing.xml.*;

import SimpleOpenNI.*;

import java.applet.*;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.FocusEvent;
import java.awt.Image;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.zip.*;
import java.util.regex.*;

public class SimpleOpenNI_NITE_Hands extends PApplet {

	/*
	 * --------------------------------------------------------------------------
	 * SimpleOpenNI NITE Hands
	 * --------------------------------------------------
	 * ------------------------ Processing Wrapper for the OpenNI/Kinect library
	 * http://code.google.com/p/simple-openni
	 * ------------------------------------
	 * -------------------------------------- prog: Max Rheiner / Interaction
	 * Design / zhkd / http://iad.zhdk.ch/ date: 03/19/2011 (m/d/y)
	 * --------------
	 * -------------------------------------------------------------- This
	 * example works with multiple hands, to enable mutliple hand change the ini
	 * file in /usr/etc/primesense/XnVHandGenerator/Nite.ini:
	 * [HandTrackerManager] AllowMultipleHands=1 TrackAdditionalHands=1 on
	 * Windows you can find the file at: C:\Program Files (x86)\Prime
	 * Sense\NITE\Hands\Data\Nite.ini
	 * --------------------------------------------
	 * --------------------------------
	 */

	SimpleOpenNI context;

	// NITE
	XnVSessionManager sessionManager;
	XnVFlowRouter flowRouter;

	PointDrawer pointDrawer;

	public void setup() {
		context = new SimpleOpenNI(this);

		// mirror is by default enabled
		context.setMirror(true);

		// enable depthMap generation
		context.enableDepth();

		// enable the hands + gesture
		context.enableGesture();
		context.enableHands();

		// setup NITE
		sessionManager = context
				.createSessionManager("Click,Wave", "RaiseHand");

		pointDrawer = new PointDrawer();
		flowRouter = new XnVFlowRouter();
		flowRouter.SetActive(pointDrawer);

		sessionManager.AddListener(flowRouter);

		size(context.depthWidth(), context.depthHeight());
		smooth();
	}

	public void draw() {
		background(200, 0, 0);
		// update the cam
		context.update();

		// update nite
		context.update(sessionManager);

		// draw depthImageMap
		image(context.depthImage(), 0, 0);

		// draw the list
		pointDrawer.draw();

		text(frameRate, 10, 10);
	}

	public void keyPressed() {
		switch (key) {
		case 'e':
			// end sessions
			sessionManager.EndSession();
			println("end session");
			break;
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////
	// session callbacks

	public void onStartSession(PVector pos) {
		println("onStartSession: " + pos);
	}

	public void onEndSession() {
		println("onEndSession: ");
	}

	public void onFocusSession(String strFocus, PVector pos, float progress) {
		println("onFocusSession: focus=" + strFocus + ",pos=" + pos
				+ ",progress=" + progress);
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////
	// PointDrawer keeps track of the handpoints

	class PointDrawer extends XnVPointControl {
		HashMap _pointLists;
		int _maxPoints;
		int[] _colorList = { color(255, 0, 0), color(0, 255, 0),
				color(0, 0, 255), color(255, 255, 0) };

		public PointDrawer() {
			_maxPoints = 30;
			_pointLists = new HashMap();
		}

		public void OnPointCreate(XnVHandPointContext cxt) {
			// create a new list
			println("mano"+cxt.getNID());
			addPoint(cxt.getNID(), new PVector(cxt.getPtPosition().getX(), cxt
					.getPtPosition().getY(), cxt.getPtPosition().getZ()));

			println("OnPointCreate, handId: " + cxt.getNID());
		}

		public void OnPointUpdate(XnVHandPointContext cxt) {
			// println("OnPointUpdate " + cxt.getPtPosition());
			addPoint(cxt.getNID(), new PVector(cxt.getPtPosition().getX(), cxt
					.getPtPosition().getY(), cxt.getPtPosition().getZ()));
		}

		public void OnPointDestroy(long nID) {
			println("OnPointDestroy, handId: " + nID);

			// remove list
			if (_pointLists.containsKey(nID))
				_pointLists.remove(nID);
		}

		public ArrayList getPointList(long handId) {
			ArrayList curList;
			if (_pointLists.containsKey(handId))
				curList = (ArrayList) _pointLists.get(handId);
			else {
				curList = new ArrayList(_maxPoints);
				_pointLists.put(handId, curList);
			}
			return curList;
		}

		public void addPoint(long handId, PVector handPoint) {
			ArrayList curList = getPointList(handId);

			curList.add(0, handPoint);
			if (curList.size() > _maxPoints)
				curList.remove(curList.size() - 1);
		}

		public void draw() {
			if (_pointLists.size() <= 0)
				return;

			pushStyle();
			noFill();

			PVector vec;
			PVector firstVec;
			PVector screenPos = new PVector();
			int colorIndex = 0;

			// draw the hand lists
			Iterator<Map.Entry> itrList = _pointLists.entrySet().iterator();
			while (itrList.hasNext()) {
				strokeWeight(2);
				stroke(_colorList[colorIndex % (_colorList.length - 1)]);

				ArrayList curList = (ArrayList) itrList.next().getValue();

				// draw line
				firstVec = null;
				Iterator<PVector> itr = curList.iterator();
				beginShape();
				while (itr.hasNext()) {
					vec = itr.next();
					if (firstVec == null)
						firstVec = vec;
					// calc the screen pos
					context.convertRealWorldToProjective(vec, screenPos);
					vertex(screenPos.x, screenPos.y);
				}
				endShape();

				// draw current pos of the hand
				if (firstVec != null) {
					strokeWeight(8);
					context.convertRealWorldToProjective(firstVec, screenPos);
					point(screenPos.x, screenPos.y);
				}
				colorIndex++;
			}

			popStyle();
		}

	}

	static public void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#F0F0F0",
				"SimpleOpenNI_NITE_Hands" });
	}
}
