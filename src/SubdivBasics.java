import processing.core.*;
import processing.xml.*;

import toxi.color.*;
import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.geom.mesh.subdiv.*;
import toxi.math.*;
import toxi.util.*;
import toxi.processing.*;
import processing.opengl.*;

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

public class SubdivBasics extends PApplet {

	/**
	 * <p>
	 * This demo allows you to interactively explore different mesh subdivision
	 * strategies. Starting with a simple cube mesh, you can choose one of 5
	 * strategies and subdivide the mesh iteratively. At each point you can also
	 * choose to randomly deform the mesh or smooth it using a laplacian
	 * geometry filter. In wireframe mode the mesh vertices are colored based on
	 * the distance from their original position.
	 * </p>
	 * 
	 * <p>
	 * <strong>Usage:</strong>
	 * <ul>
	 * <li>move mouse to rotate camera</li>
	 * <li>1-5: choose subdivision strategy
	 * <li>s: apply current subdivision strategy once</li>
	 * <li>d: deform mesh</li>
	 * <li>l: smooth mesh</li>
	 * <li>w: wireframe on/off</li>
	 * <li>-/=: zoom in/out</li>
	 * </ul>
	 * </p>
	 */

	/*
	 * Copyright (c) 2010 Karsten Schmidt
	 * 
	 * This library is free software; you can redistribute it and/or modify it
	 * under the terms of the GNU Lesser General Public License as published by
	 * the Free Software Foundation; either version 2.1 of the License, or (at
	 * your option) any later version.
	 * 
	 * http://creativecommons.org/licenses/LGPL/2.1/
	 * 
	 * This library is distributed in the hope that it will be useful, but
	 * WITHOUT ANY WARRANTY; without even the implied warranty of
	 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
	 * General Public License for more details.
	 * 
	 * You should have received a copy of the GNU Lesser General Public License
	 * along with this library; if not, write to the Free Software Foundation,
	 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
	 */

	ToxiclibsSupport gfx;
	WETriangleMesh mesh;
	SubdivisionStrategy subdiv = new MidpointSubdivision();
	List<Vec3D> vertBackup = new ArrayList<Vec3D>();

	float currZoom = 1;
	boolean isWireframe = true;

	public void setup() {
		size(1280, 720, OPENGL);
		gfx = new ToxiclibsSupport(this);
		initMesh();
	}

	public void draw() {
		background(0);
		fill(255);
		text("subdiv: " + subdiv.getClass().getSimpleName(), 20, 20);
		translate(width / 2, height / 2, 0);
		rotateX(mouseY * 0.01f);
		rotateY(mouseX * 0.01f);
		scale(currZoom);
		if (isWireframe) {
			noFill();
			drawWireMeshDelta();
		} else {
			fill(255);
			noStroke();
			lights();
			gfx.mesh(mesh, true, 0);
		}
	}

	public void drawWireMeshDelta() {
		beginShape(LINES);
		TColor col = TColor.newHSV(0, 1, 1);
		for (WingedEdge e : mesh.edges.values()) {
			int idA = ((WEVertex) e.a).id;
			int idB = ((WEVertex) e.b).id;
			float da = e.a.distanceTo(vertBackup.get(idA));
			float db = e.b.distanceTo(vertBackup.get(idB));
			col.setHue(da * 0.05f);
			stroke(col.toARGB());
			vertex(e.a.x, e.a.y, e.a.z);
			col.setHue(db * 0.05f);
			stroke(col.toARGB());
			vertex(e.b.x, e.b.y, e.b.z);
		}
		endShape();
	}

	public void keyPressed() {
		if (key == '-') {
			currZoom -= 0.1f;
		}
		if (key == '=') {
			currZoom += 0.1f;
		}
		if (key == 'w') {
			isWireframe = !isWireframe;
		}
		if (key == 'l') {
			new LaplacianSmooth().filter(mesh, 1);
		}
		if (key == 's') {
			// subdivide all mesh edges if their length > 10
			mesh.subdivide(subdiv, 10);
			backupMesh();
		}
		if (key == 'x') {
			mesh.saveAsSTL(sketchPath("subdiv-" + DateUtils.timeStamp()
					+ ".stl"));
		}
		if (key == 'd') {
			deformMesh();
		}
		if (key == 'r') {
			initMesh();
		}
		if (key >= '1' && key <= '5') {
			switch (key) {
			case '1':
				// midpoint subdiv splits an edge in half
				subdiv = new MidpointSubdivision();
				break;
			case '2':
				// splits an edge in half and displaces midpoint along dir from
				// mesh centroid
				// in this case the point moves 22% of original edge length
				// towards mesh centroid
				subdiv = new MidpointDisplacementSubdivision(
						mesh.computeCentroid(), -0.22f);
				break;
			case '3':
				// splits edges at 33% and 66%, resulting in 3 shorter ones
				subdiv = new DualSubdivision();
				break;
			case '4':
				// splits edges at 25%, 50% and 75%, resulting in 4 shorter ones
				subdiv = new TriSubdivision();
				break;
			case '5':
				// similar to MidpointDisplacementSubdivision, only displacement
				// direction is
				// not based on relation to a fixed reference point, but uses
				// average normal vector
				// of faces attached to each edge
				subdiv = new NormalDisplacementSubdivision(0.25f);
				break;
			}
		}
	}

	public void initMesh() {
		mesh = new WETriangleMesh();
		// mesh.addMesh(new Plane(new Vec3D(), new Vec3D(0, 1, 0)).toMesh(400));
		//mesh.addMesh(new AABB(new Vec3D(0, 0, 0), 200).toMesh());
		mesh.addMesh(new XAxisCylinder(new Vec3D(200, 0, 0), 200,50).toMesh());
		backupMesh();
	}

	// keep a backup of all vertex positions for wireframe rendering
	public void backupMesh() {
		vertBackup.clear();
		for (Vec3D v : mesh.getVertices()) {
			vertBackup.add(v.copy());
		}
	}

	// randomly displace some mesh vertices
	public void deformMesh() {
		for (Vec3D v : mesh.getVertices()) {
			if (random(1) < 0.2f) {
				v.scaleSelf(random(0.8f, 1.2f));
			}
		}
		mesh.rebuildIndex();
		backupMesh();
	}

	static public void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#F0F0F0", "SubdivBasics" });
	}
}
