package craft.renderer;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class Tesselator {
	@SuppressWarnings("unused")
	private static final int MAX_MEMORY_USE = 4194304;
	private static final int MAX_FLOATS = 524288;
	private FloatBuffer buffer = BufferUtils.createFloatBuffer(MAX_FLOATS);

	private float[] array = new float[MAX_FLOATS];

	private int vertices = 0;
	private float u;
	private float v;
	private float r;
	private float g;
	private float b;
	private boolean hasColor = false;
	private boolean hasTexture = false;
	private int len = 3;
	private int p = 0;
	private boolean noColor = false;

	public static Tesselator instance = new Tesselator();

	public void end() {
		if (vertices > 0) {
			buffer.clear();
			buffer.put(array, 0, p);
			buffer.flip();

			if ((hasTexture) && (hasColor))
				GL11.glInterleavedArrays(GL11.GL_T2F_C3F_V3F, 0, buffer);
			else if (hasTexture)
				GL11.glInterleavedArrays(GL11.GL_T2F_V3F, 0, buffer);
			else if (hasColor)
				GL11.glInterleavedArrays(GL11.GL_C3F_V3F, 0, buffer);
			else
				GL11.glInterleavedArrays(GL11.GL_V3F, 0, buffer);
			GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
			if (hasTexture) GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			if (hasColor) GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);

			GL11.glDrawArrays(7, 0, vertices);

			GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
			if (hasTexture) GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			if (hasColor) GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		}

		clear();
	}

	private void clear() {
		vertices = 0;

		buffer.clear();
		p = 0;
	}

	public void begin() {
		clear();
		hasColor = false;
		hasTexture = false;
		noColor = false;
	}

	public void tex(float u, float v) {
		if (!hasTexture) len += 2;

		hasTexture = true;
		this.u = u;
		this.v = v;
	}

	public void color(float r, float g, float b) {
		if (noColor) return;
		if (!hasColor) len += 3;

		hasColor = true;
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public void color(int r, int g, int b) {
		color((byte)r, (byte)g, (byte)b);
	}

	public void color(byte r, byte g, byte b) {
		if (noColor) return;
		if (!hasColor) len += 3;

		hasColor = true;
		this.r = ((r & 0xFF) / 255.0F);
		this.g = ((g & 0xFF) / 255.0F);
		this.b = ((b & 0xFF) / 255.0F);
	}

	public void vertexUV(float x, float y, float z, float u, float v) {
		tex(u, v);
		vertex(x, y, z);
	}

	public void vertex(float x, float y, float z) {
		if (hasTexture) {
			array[(p++)] = u;
			array[(p++)] = v;
		}
		if (hasColor) {
			array[(p++)] = r;
			array[(p++)] = g;
			array[(p++)] = b;
		}
		array[(p++)] = x;
		array[(p++)] = y;
		array[(p++)] = z;

		vertices += 1;
		if ((vertices % 4 == 0) && (p >= 524288 - len * 4))
			end();
	}

	public void color(int c) {
		int r = c >> 16 & 0xFF;
		int g = c >> 8 & 0xFF;
		int b = c & 0xFF;
		color(r, g, b);
	}

	public void noColor() {
		noColor = true;
	}
}
