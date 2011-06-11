package spaceinvaders.opengl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.media.opengl.GL;

import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;
import remixlab.proscene.Scene;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

public class FastVolumetric {

	// peasy camera control

	PGraphicsOpenGL pgl;

	GL gl;

	Texture linetex;

	int nbVLine = 12;

	VolumeLineRenderer vr;

	Scene scene;

	ArrayList<Line> lines = new ArrayList<Line>();

	PApplet applet;

	public FastVolumetric(PApplet applet, PGraphicsOpenGL pgl) {
		super();

		// size(1000, 800, GLConstants.GLGRAPHICS);
		// hint(ENABLE_OPENGL_4X_SMOOTH);
		// hint(ENABLE_ACCURATE_TEXTURES);
		// hint(ENABLE_NATIVE_FONTS);

		// scene = new Scene(this);
		// scene.setRadius(1000);
		this.applet = applet;
		// pgl = (PGraphicsOpenGL) applet.g;
		this.pgl = pgl;

		gl = pgl.gl;

		applet.ambientLight(255, 255, 255, 0, 0, 0);

		try {

			linetex = TextureIO.newTexture(
					new File(applet.dataPath("glow1.bmp")), true);
			// http://farm6.static.flickr.com/5009/5307555840_7ff73e086e.jpg

		}

		catch (IOException e) {

			applet.println(e);

		}

		vr = new VolumeLineRenderer(applet);

		vr.lineWidth(275);

		lines.add(new Line(new PVector(0, 0, 0), new PVector(0, 0, 250)));

	}

	public void draw() {

		for (int i = 0; i < lines.size(); i++) {
			Line line = lines.get(i);
			drawLine(line);
		}

		if (applet.frameCount % 30 == 0)
			applet.println(applet.frameRate);

	}

	public void drawLine(Line line) {
		float[] p1 = { line.p1.x, line.p1.y, line.p1.z };
		float[] p2 = { line.p2.x, line.p2.y, line.p2.z };
		vr.lineWidth(line.width);
		drawLine(line.color, p1, p2);
	}

	public void drawLine(float[] c, float[] p1, float[] p2) {
		gl.glColor3fv(c, 0);
		vr.renderLine(p1, p2);
	}

	public void endDraw() {
		vr.endRender();

		gl.glDisable(GL.GL_BLEND);

		gl.glDepthFunc(GL.GL_LESS);

		gl.glDepthMask(true);

		gl.glDisable(GL.GL_CULL_FACE);

		// pshader.endShader();

		linetex.disable();

		pgl.endGL();
	}

	public void beginDraw() {
		// background(0);
		applet.noFill();
		applet.stroke(255);
		applet.strokeWeight(1);

		// sphere(100);

		pgl.beginGL();

		linetex.enable();

		linetex.bind();

		gl.glEnable(GL.GL_CULL_FACE);

		gl.glFrontFace(GL.GL_CCW);

		gl.glCullFace(GL.GL_BACK);

		gl.glDepthMask(false);

		gl.glDepthFunc(GL.GL_ALWAYS);

		gl.glEnable(GL.GL_BLEND);

		gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);

		vr.beginRender();
	}

	public void addLine(Line line) {
		lines.add(line);
	}

	public Line addLine(PVector p1, PVector p2, float[] color) {
		Line line = new Line(p1, p2, color);
		lines.add(line);
		return line;
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Les offsets des vertices par rapport \ufffd la ligne normale
	 */

	float vline[][] = { { 0.0f, 0.0f, 0.0f }, { 100.0f, 0.0f, 0.0f }

	,

	{

	0.0f, 0.0f, 0.0f

	}

	, {

	0.0f, 100.0f, 0.0f

	}

	,

	{

	0.0f, 0.0f, 0.0f

	}

	, {

	0.0f, 0.0f, 100.0f

	}

	,

	{

	150.0f, 0.0f, 0.0f

	}

	, {

	250.0f, 0.0f, 0.0f

	}

	,

	{

	150.0f, 0.0f, 0.0f

	}

	, {

	150.0f, 10.0f, 0.0f

	}

	,

	{

	150.0f, 0.0f, 0.0f

	}

	, {

	150.0f, 0.0f, 100.0f

	}

	,

	{

	100.0f, 0.0f, 200.0f

	}

	, {

	200.0f, 50.0f, 250.0f

	}

	,

	{

	200.0f, 50.0f, 250.0f

	}

	, {

	300.0f, -10.0f, 220.0f

	}

	,

	{

	300.0f, -10.0f, 220.0f

	}

	, {

	350.0f, 20.0f, 200.0f

	}

	,

	{

	-60.0f, 100.0f, 30.0f

	}

	, {

	-50.0f, 0.0f, 20.0f

	}

	,

	{

	-60.0f, 0.0f, 20.0f

	}

	, {

	-30.0f, 150.0f, -20.0f

	}

	,

	{

	-20.0f, 110.0f, -10.0f

	}

	, {

	-60.0f, 30.0f, 0.5f

	}

	};

	float vlineColor[][] =

	{

	{

	1.0f, 0.0f, 0.0f

	}

	,

	{

	0.0f, 1.0f, 0.0f

	}

	,

	{

	0.0f, 0.0f, 1.0f

	}

	,

	{

	1.0f, 1.0f, 1.0f

	}

	,

	{

	1.0f, 1.0f, 1.0f

	}

	,

	{

	1.0f, 1.0f, 1.0f

	}

	,

	{

	1.0f, 1.0f, 0.2f

	}

	,

	{

	1.0f, 1.0f, 0.2f

	}

	,

	{

	1.0f, 1.0f, 0.2f

	}

	,

	{

	1.0f, 1.0f, 0.2f

	}

	,

	{

	1.0f, 1.0f, 0.2f

	}

	,

	{

	1.0f, 1.0f, 0.2f

	}

	};

	static public void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#F0F0F0", "FastVolumetric" });
	}
}
