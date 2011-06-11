package spaceinvaders.opengl;
import java.nio.ByteBuffer;

import javax.media.opengl.GL;

import processing.core.PApplet;

import com.sun.opengl.util.BufferUtil;

public class GLSLShader {

	GL gl;

	int programObject;

	int vertexShader;

	int fragmentShader;

	PApplet pApplet;

	GLSLShader(PApplet pApplet, GL gl0) {
		this.pApplet = pApplet;

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

		String shaderSource = pApplet.join(pApplet.loadStrings(file), "\n");

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

		String shaderSource = pApplet.join(pApplet.loadStrings(file), "\n");

		fragmentShader = gl.glCreateShaderObjectARB(GL.GL_FRAGMENT_SHADER_ARB);

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
