import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PVector;
import SimpleOpenNI.SimpleOpenNI;
import SimpleOpenNI.XnVHandPointContext;
import SimpleOpenNI.XnVPointControl;

class PointDrawer extends XnVPointControl {

	HashMap _pointLists;
	int _maxPoints;
	PApplet applet;

	int[] _colorList = null;

	SimpleOpenNI context;

	int currentHand = 0;

	public PointDrawer(PApplet applet, SimpleOpenNI context) {
		this.applet = applet;
		_maxPoints = 30;
		_pointLists = new HashMap();
		int[] _colorListT = { applet.color(255, 0, 0), applet.color(0, 255, 0),
				applet.color(0, 0, 255), applet.color(255, 255, 0) };
		_colorList = _colorListT;
		this.context = context;
	}

	public void OnPointCreate(XnVHandPointContext cxt) {
		// create a new list
		currentHand = (int) cxt.getNID();
		addPoint(cxt.getNID(), new PVector(cxt.getPtPosition().getX(), cxt
				.getPtPosition().getY(), cxt.getPtPosition().getZ()));

		applet.println("OnPointCreate, handId: " + cxt.getNID());
	}

	public void OnPointUpdate(XnVHandPointContext cxt) {
		// applet.println("OnPointUpdate " + cxt.getPtPosition());
		addPoint(cxt.getNID(), new PVector(cxt.getPtPosition().getX(),
				cxt
				.getPtPosition().getY(), cxt.getPtPosition().getZ()));
	}

	public void OnPointDestroy(long nID) {
		applet.println("OnPointDestroy, handId: " + nID);
		if (currentHand == (int) nID)
			currentHand = 0;
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

	public PVector getCurrentPoint() {
		ArrayList list = getCurrentPointList();
		PVector ret = null;
		if (list != null && !list.isEmpty()) {
			ret = (PVector) list.get(list.size() - 1);
		}
		return ret;
	}

	public ArrayList getCurrentPointList() {
		if (currentHand > 0) {
			return getPointList(currentHand);
		} else
			return null;
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

		applet.pushStyle();
		applet.noFill();

		PVector vec;
		PVector firstVec;
		PVector screenPos = new PVector();
		int colorIndex = 0;

		// draw the hand lists
		Iterator<Map.Entry> itrList = _pointLists.entrySet().iterator();
		while (itrList.hasNext()) {
			applet.strokeWeight(2);
			applet.stroke(_colorList[colorIndex % (_colorList.length - 1)]);

			ArrayList curList = (ArrayList) itrList.next().getValue();

			// draw line
			firstVec = null;
			Iterator<PVector> itr = curList.iterator();
			applet.beginShape();
			while (itr.hasNext()) {
				vec = itr.next();
				if (firstVec == null)
					firstVec = vec;
				// calc the screen pos
				context.convertRealWorldToProjective(vec, screenPos);
				applet.vertex(screenPos.x, screenPos.y);
			}
			applet.endShape();

			// draw current pos of the hand
			if (firstVec != null) {
				applet.strokeWeight(8);
				context.convertRealWorldToProjective(firstVec, screenPos);
				applet.point(screenPos.x, screenPos.y);
			}
			colorIndex++;
		}

		applet.popStyle();
	}

}
