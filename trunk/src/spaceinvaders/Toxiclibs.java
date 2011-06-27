package spaceinvaders;
import processing.core.*;
import processing.xml.*;

import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.volume.*;
import toxi.math.noise.*;
import processing.opengl.*;
import remixlab.proscene.Scene;
import codeanticode.glgraphics.*;
import javax.media.opengl.*;

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

public class Toxiclibs {

	TriangleMesh mesh = new WETriangleMesh();

	// used to store mesh on GPU
	GLModel surf;

	Scene scene;

	PApplet applet;

	public Toxiclibs(PApplet applet) {

		this.applet = applet;
	}

	public void createCylinder(ReadonlyVec3D pos, float radius, float length) {

		// mesh.addMesh(new Plane(new Vec3D(), new Vec3D(0, 1, 0)).toMesh(400));
		// mesh.addMesh(new AABB(new Vec3D(0, 0, 0), 200).toMesh());
		mesh.addMesh(new XAxisCylinder(pos, radius, length).toMesh());

		// update lighting information
		mesh.computeVertexNormals();
		// get flattened vertex array
		float[] verts = mesh.getMeshAsVertexArray();
		// in the array each vertex has 4 entries (XYZ + 1 spacing)
		int numV = verts.length / 4;
		float[] norms = mesh.getVertexNormalsAsArray();

		surf = new GLModel(applet, numV, PApplet.TRIANGLES, GLModel.STATIC);
		surf.beginUpdateVertices();
		for (int i = 0; i < numV; i++)
			surf.updateVertex(i, verts[4 * i], verts[4 * i + 1],
					verts[4 * i + 2]);
		surf.endUpdateVertices();

		surf.initNormals();
		surf.beginUpdateNormals();
		for (int i = 0; i < numV; i++)
			surf.updateNormal(i, norms[4 * i], norms[4 * i + 1],
					norms[4 * i + 2]);
		surf.endUpdateNormals();

		// Setting the color of all vertices to green, but not used, see
		// comments in the draw() method.
		surf.initColors();
		surf.beginUpdateColors();
		for (int i = 0; i < numV; i++)
			surf.updateColor(i, 0, 255, 0, 225);
		surf.endUpdateColors();

		// Setting model shininess.
		surf.setShininess(32);
	}

	public void draw(GLGraphicsOffScreen renderer) {
		// background(128);
		// translate(width / 2, height / 2, 0);
		// rotateX(mouseY * 0.01f);
		// rotateY(mouseX * 0.01f);
		// scale(currScale);
		applet.lights();

//		renderer.beginGL();
//
//		renderer.gl.glEnable(GL.GL_LIGHTING);
//
//		// Disabling color tracking, so the lighting is determined using the
//		// colors
//		// set only with glMaterialfv()
//		renderer.gl.glDisable(GL.GL_COLOR_MATERIAL);
//
//		// Enabling color tracking for the specular component, this means that
//		// the
//		// specular component to calculate lighting will obtained from the
//		// colors
//		// of the model (in this case, pure green).
//		// This tutorial is quite good to clarify issues regarding lighting in
//		// OpenGL:
//		// http://www.sjbaker.org/steve/omniv/opengl_lighting.html
//		// renderer.gl.glEnable(GL.GL_COLOR_MATERIAL);
//		// renderer.gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR);
//
//		renderer.gl.glEnable(GL.GL_LIGHT0);
//		renderer.gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT,
//				new float[] { 0.1f, 0.1f, 0.1f, 1 }, 0);
//		renderer.gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE,
//				new float[] { 1, 0, 0, 1 }, 0);
//		renderer.gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[] {
//				-1000, 600, 2000, 0 }, 0);
//		renderer.gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, new float[] { 1, 1,
//				1, 1 }, 0);
//
		renderer.model(surf);
//
//		defineLights();
//
//		// back to processing
//		renderer.endGL();
	}

	void defineLights() {
		// Orange point light on the right
		applet.pointLight(150, 100, 0, // Color
				200, -150, 0); // Position

		// Blue directional light from the left
		applet.directionalLight(0, 102, 255, // Color
				1, 0, 0); // The x-, y-, z-axis direction

		// Yellow spotlight from the front
		applet.spotLight(255f, 255f, 109f, // Color
				0f, 40f, 200f, // Position
				0f, -0.5f, -0.5f, // Direction
				PApplet.PI / 2f, 2f); // Angle, concentration
	}

}
