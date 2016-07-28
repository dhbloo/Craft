package craft.gui;

import org.lwjgl.opengl.GL11;

public class RGBAColor {
	public static final RGBAColor white = new RGBAColor(1.0F, 1.0F, 1.0F);
	public float r, g, b ,a;

	public RGBAColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public RGBAColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = 1.0F;
	}
	
	public void bind() {
		GL11.glColor4f(r, g, b, a);
	}
	
}
