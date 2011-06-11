package spaceinvaders.opengl;
import java.io.File;

import javax.media.opengl.GL;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;

class VolumeLineRenderer {

	int textureID, gpuProgVolumeLine_lineTex;

	// int gpuProgVolumeLine_other;

	int gpuProgVolumeLine_other;

	float lWidth;

	int gpuProgVolumeLine_lineWidth;

	boolean isRendering;

	VolumeLineRenderer volumeLineRenderer = null;

	GLSLShader gpuProgVolumeLine = null;

	PGraphicsOpenGL pgl = null;
	GL gl;
	PApplet applet;

	public VolumeLineRenderer(PApplet applet) {

		this.applet = applet;
		this.pgl = (PGraphicsOpenGL) applet.g;

		this.textureID = 0;

		this.lWidth = 1.0f;

		this.isRendering = false;

		gl = pgl.gl;

		this.gpuProgVolumeLine = new GLSLShader(applet, gl);

		File lvs = new File(applet.dataPath("vshader.vert"));

		File lfs = new File(applet.dataPath("fshader.frag"));

		if (lvs.exists() && lfs.exists()) {

			try {

				gpuProgVolumeLine.loadVertexShader(lvs.toString());

				gpuProgVolumeLine.loadFragmentShader(lfs.toString());

				gpuProgVolumeLine.useShaders();

			}

			catch (Exception e) {

				applet.println(e);
				applet.exit();

			}

		}

		else {

			applet.exit();

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

			volumeLineRenderer = new VolumeLineRenderer(applet);

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

	float[][] vline_texCoord = { // [8][3]
	{ 1.0f, 0.0f, 0.0f }, { 1.0f, 1.0f, 0.0f }, { 0.5f, 0.0f, 0.0f },
			{ 0.5f, 1.0f, 0.0f

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

	public void renderLine(float[] p1, float[] p2) {

		gl.glBegin(gl.GL_TRIANGLE_STRIP);

		gl.glNormal3fv(vline_vertexOffset[0], 0);

		gl.glTexCoord2f(vline_texCoord[0][0], vline_texCoord[0][1]);

		gl.glVertexAttrib4f(this.gpuProgVolumeLine_other, p2[0], p2[1], p2[2],
				1.0f);

		gl.glVertex3f(p1[0], p1[1], p1[2]);

		gl.glNormal3fv(vline_vertexOffset[1], 0);

		gl.glTexCoord2f(vline_texCoord[1][0], vline_texCoord[1][1]);

		gl.glVertexAttrib4f(this.gpuProgVolumeLine_other, p2[0], p2[1], p2[2],
				1.0f);

		gl.glVertex3fv(p1, 0);

		gl.glNormal3fv(vline_vertexOffset[2], 0);

		gl.glTexCoord2fv(vline_texCoord[2], 0);

		gl.glVertexAttrib4f(this.gpuProgVolumeLine_other, p2[0], p2[1], p2[2],
				1.0f);

		gl.glVertex3fv(p1, 0);

		gl.glNormal3fv(vline_vertexOffset[3], 0);

		gl.glTexCoord2fv(vline_texCoord[3], 0);

		gl.glVertexAttrib4f(this.gpuProgVolumeLine_other, p2[0], p2[1], p2[2],
				1.0f);

		gl.glVertex3fv(p1, 0);

		gl.glNormal3fv(vline_vertexOffset[4], 0);

		gl.glTexCoord2fv(vline_texCoord[4], 0);

		gl.glVertexAttrib4f(this.gpuProgVolumeLine_other, p1[0], p1[1], p1[2],
				1.0f);

		gl.glVertex3fv(p2, 0);

		gl.glNormal3fv(vline_vertexOffset[5], 0);

		gl.glTexCoord2fv(vline_texCoord[5], 0);

		gl.glVertexAttrib4f(this.gpuProgVolumeLine_other, p1[0], p1[1], p1[2],
				1.0f);

		gl.glVertex3fv(p2, 0);

		gl.glNormal3fv(vline_vertexOffset[6], 0);

		gl.glTexCoord2fv(vline_texCoord[6], 0);

		gl.glVertexAttrib4f(this.gpuProgVolumeLine_other, p1[0], p1[1], p1[2],
				1.0f);

		gl.glVertex3fv(p2, 0);

		gl.glNormal3fv(vline_vertexOffset[7], 0);

		gl.glTexCoord2fv(vline_texCoord[7], 0);

		gl.glVertexAttrib4f(this.gpuProgVolumeLine_other, p1[0], p1[1], p1[2],
				1.0f);

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
