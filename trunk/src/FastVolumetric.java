import processing.core.*;
import processing.xml.*;

import processing.opengl.PGraphicsOpenGL;
import remixlab.proscene.Scene;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
import javax.media.opengl.GL;
import java.nio.ByteBuffer;
import com.sun.opengl.util.BufferUtil;

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

public class FastVolumetric extends PApplet {

	// peasy camera control

	PGraphicsOpenGL pgl;

	GL gl;

	Texture linetex;

	int nbVLine = 12;

	VolumeLineRenderer vr;

	Scene scene;

	public void setup() {

		size(1000, 800, OPENGL);
		scene = new Scene(this);
		pgl = (PGraphicsOpenGL) g;

		gl = pgl.gl;

		ambientLight(255, 255, 255, 0, 0, 0);

		try {

			linetex = TextureIO.newTexture(new File(dataPath("glow2.bmp")),
					true);
			// http://farm6.static.flickr.com/5009/5307555840_7ff73e086e.jpg

		}

		catch (IOException e) {

			println(e);

		}

		vr = new VolumeLineRenderer();

		vr.lineWidth(10);

	}

	public void draw() {

		background(0);
		noFill();
		stroke(255);
		strokeWeight(1);

		sphere(100);

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

		for (int vl = 0; vl < nbVLine; vl++)

		{

			gl.glColor3fv(vlineColor[vl], 0);

			vr.renderLine(vline[vl * 2], vline[vl * 2 + 1]);

		}

		vr.endRender();

		gl.glDisable(GL.GL_BLEND);

		gl.glDepthFunc(GL.GL_LESS);

		gl.glDepthMask(true);

		gl.glDisable(GL.GL_CULL_FACE);

		// pshader.endShader();

		linetex.disable();

		pgl.endGL();

	}

	class VolumeLineRenderer {

		int textureID, gpuProgVolumeLine_lineTex;

		// int gpuProgVolumeLine_other;

		int gpuProgVolumeLine_other;

		float lWidth;

		int gpuProgVolumeLine_lineWidth;

		boolean isRendering;

		VolumeLineRenderer volumeLineRenderer = null;

		GLSLShader gpuProgVolumeLine = null;

		PGraphicsOpenGL pgl = (PGraphicsOpenGL) g;

		public VolumeLineRenderer() {

			this.textureID = 0;

			this.lWidth = 1.0f;

			this.isRendering = false;

			gl = pgl.gl;

			this.gpuProgVolumeLine = new GLSLShader(gl);

			File lvs = new File(dataPath("vshader.vert"));

			File lfs = new File(dataPath("fshader.frag"));

			if (lvs.exists() && lfs.exists()) {

				try {

					gpuProgVolumeLine.loadVertexShader(lvs.toString());

					gpuProgVolumeLine.loadFragmentShader(lfs.toString());

					gpuProgVolumeLine.useShaders();

				}

				catch (Exception e) {

					println(e);
					exit();

				}

			}

			else {

				exit();

			}

			this.gpuProgVolumeLine_other = this.gpuProgVolumeLine
					.getAttribLocation("other");

			this.gpuProgVolumeLine_lineWidth = this.gpuProgVolumeLine
					.getUniformLocation("lineWidth");

			this.gpuProgVolumeLine_lineTex = this.gpuProgVolumeLine
					.getUniformLocation("lineTex");

		}

		public VolumeLineRenderer getInstance()

		{

			if (volumeLineRenderer == null) {

				volumeLineRenderer = new VolumeLineRenderer();

			}

			return volumeLineRenderer;

		}

		public void destroyInstance() {

			if (volumeLineRenderer != null) {

				volumeLineRenderer = null;

			}

		}

		public void lineTexture(int texID)

		{

			this.textureID = texID;

		}

		public void lineWidth(float lwidth) {

			if (lwidth > 0.0f) {

				this.lWidth = lwidth;

			}

		}

		public void beginRender() {

			if (this.isRendering) {

				return;

			}

			this.isRendering = true;

			this.gpuProgVolumeLine.startShader();

			gl.glUniform1iARB(this.gpuProgVolumeLine_lineTex, 0);

			gl.glUniform1fARB(this.gpuProgVolumeLine_lineWidth, this.lWidth);

		}

		public void renderLine(float[] p1, float[] p2) {

			gl.glBegin(gl.GL_TRIANGLE_STRIP);

			gl.glNormal3fv(vline_vertexOffset[0], 0);

			gl.glTexCoord2f(vline_texCoord[0][0], vline_texCoord[0][1]);

			gl.glVertexAttrib4f(this.gpuProgVolumeLine_other, p2[0], p2[1],
					p2[2], 1.0f);

			gl.glVertex3f(p1[0], p1[1], p1[2]);

			gl.glNormal3fv(vline_vertexOffset[1], 0);

			gl.glTexCoord2f(vline_texCoord[1][0], vline_texCoord[1][1]);

			gl.glVertexAttrib4f(this.gpuProgVolumeLine_other, p2[0], p2[1],
					p2[2], 1.0f);

			gl.glVertex3fv(p1, 0);

			gl.glNormal3fv(vline_vertexOffset[2], 0);

			gl.glTexCoord2fv(vline_texCoord[2], 0);

			gl.glVertexAttrib4f(this.gpuProgVolumeLine_other, p2[0], p2[1],
					p2[2], 1.0f);

			gl.glVertex3fv(p1, 0);

			gl.glNormal3fv(vline_vertexOffset[3], 0);

			gl.glTexCoord2fv(vline_texCoord[3], 0);

			gl.glVertexAttrib4f(this.gpuProgVolumeLine_other, p2[0], p2[1],
					p2[2], 1.0f);

			gl.glVertex3fv(p1, 0);

			gl.glNormal3fv(vline_vertexOffset[4], 0);

			gl.glTexCoord2fv(vline_texCoord[4], 0);

			gl.glVertexAttrib4f(this.gpuProgVolumeLine_other, p1[0], p1[1],
					p1[2], 1.0f);

			gl.glVertex3fv(p2, 0);

			gl.glNormal3fv(vline_vertexOffset[5], 0);

			gl.glTexCoord2fv(vline_texCoord[5], 0);

			gl.glVertexAttrib4f(this.gpuProgVolumeLine_other, p1[0], p1[1],
					p1[2], 1.0f);

			gl.glVertex3fv(p2, 0);

			gl.glNormal3fv(vline_vertexOffset[6], 0);

			gl.glTexCoord2fv(vline_texCoord[6], 0);

			gl.glVertexAttrib4f(this.gpuProgVolumeLine_other, p1[0], p1[1],
					p1[2], 1.0f);

			gl.glVertex3fv(p2, 0);

			gl.glNormal3fv(vline_vertexOffset[7], 0);

			gl.glTexCoord2fv(vline_texCoord[7], 0);

			gl.glVertexAttrib4f(this.gpuProgVolumeLine_other, p1[0], p1[1],
					p1[2], 1.0f);

			gl.glVertex3fv(p2, 0);

			gl.glEnd();

		}

		public void endRender()

		{

			if (!this.isRendering) {

				return;

			}

			this.isRendering = false;

			this.gpuProgVolumeLine.endShader();

		}

	}

	class GLSLShader

	{

		GL gl;

		int programObject;

		int vertexShader;

		int fragmentShader;

		GLSLShader(GL gl0)

		{

			gl = gl0;

			programObject = gl.glCreateProgramObjectARB();

			vertexShader = -1;

			fragmentShader = -1;

		}

		public void deleteShader() {

			gl.glUseProgramObjectARB(0);

			try {

				gl.glDetachObjectARB(programObject, vertexShader);

				gl.glDeleteObjectARB(vertexShader);

				gl.glDetachObjectARB(programObject, fragmentShader);

				gl.glDeleteObjectARB(fragmentShader);

				gl.glDeleteObjectARB(programObject);

			}

			catch (Exception e) {

			}

		}

		public void loadVertexShader(String file)

		{

			String shaderSource = join(loadStrings(file), "\n");

			vertexShader = gl.glCreateShaderObjectARB(GL.GL_VERTEX_SHADER_ARB);

			gl.glShaderSourceARB(vertexShader, 1, new String[] {

			shaderSource

			}

			, (int[]) null, 0);

			gl.glCompileShaderARB(vertexShader);

			checkLogInfo(gl, vertexShader);

			gl.glAttachObjectARB(programObject, vertexShader);

		}

		public void loadFragmentShader(String file)

		{

			String shaderSource = join(loadStrings(file), "\n");

			fragmentShader = gl
					.glCreateShaderObjectARB(GL.GL_FRAGMENT_SHADER_ARB);

			gl.glShaderSourceARB(fragmentShader, 1, new String[] {

			shaderSource

			}

			, (int[]) null, 0);

			gl.glCompileShaderARB(fragmentShader);

			checkLogInfo(gl, fragmentShader);

			gl.glAttachObjectARB(programObject, fragmentShader);

		}

		public int getAttribLocation(String name)

		{

			return (gl.glGetAttribLocationARB(programObject, name));

		}

		public int getUniformLocation(String name)

		{

			return (gl.glGetUniformLocationARB(programObject, name));

		}

		public void useShaders()

		{

			gl.glLinkProgramARB(programObject);

			gl.glValidateProgramARB(programObject);

			checkLogInfo(gl, programObject);

		}

		public void startShader()

		{

			gl.glUseProgramObjectARB(programObject);

		}

		public void endShader()

		{

			gl.glUseProgramObjectARB(0);

		}

		public void checkLogInfo(GL gl, int obj)

		{

			java.nio.IntBuffer iVal = BufferUtil.newIntBuffer(1);

			gl.glGetObjectParameterivARB(obj, GL.GL_OBJECT_INFO_LOG_LENGTH_ARB,
					iVal);

			int ilength = iVal.get();

			if (ilength <= 1)
				return;

			ByteBuffer infoLog = BufferUtil.newByteBuffer(ilength);

			iVal.flip();

			gl.glGetInfoLogARB(obj, ilength, iVal, infoLog);

			byte[] infoBytes = new byte[ilength];

			infoLog.get(infoBytes);

		}

	}

	float[][] vline_texCoord = { // [8][3]

	{

	1.0f, 0.0f, 0.0f

	}

	,

	{

	1.0f, 1.0f, 0.0f

	}

	,

	{

	0.5f, 0.0f, 0.0f

	}

	,

	{

	0.5f, 1.0f, 0.0f

	}

	,

	{

	0.5f, 0.0f, 0.0f

	}

	,

	{

	0.5f, 1.0f, 0.0f

	}

	,

	{

	0.0f, 0.0f, 0.0f

	}

	,

	{

	0.0f, 1.0f, 0.0f

	}

	};

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

	float[][] vline_vertexOffset = { // [8][2]

	{

	1.0f, 1.0f

	}

	, // ///

			{

			1.0f, -1.0f

			}

			, // par rapport

			{

			0.0f, 1.0f

			}

			, // au vertex A

			{

			0.0f, -1.0f

			}

			, // ///

			{

			0.0f, -1.0f

			}

			, // ///

			{

			0.0f, 1.0f

			}

			, // par rapport

			{

			1.0f, -1.0f

			}

			, // au vertex B

			{

			1.0f, 1.0f

			} // ///

	};

	float vline[][] =

	{

	{

	0.0f, 0.0f, 0.0f

	}

	, {

	100.0f, 0.0f, 0.0f

	}

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
