package craft.gui;

import org.lwjgl.opengl.GL11;

import craft.renderer.Texture;

public class Font {
	private static int lines = 0;
	private int fontList;

	protected Font() {
		fontList = GL11.glGenLists(256);
		Texture.bind(Texture.FontTex);
		for (int i = 0; i < 256; i++) {
			float cx = (i % 16) / 16.0F;
			float cy = 1 - (i / 16) / 16.0F;
			GL11.glNewList(fontList + i, GL11.GL_COMPILE);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(cx + 0.0625F, 1 - cy);
			GL11.glVertex3f(16.0F, 0.0F, 0.0F);
			GL11.glTexCoord2f(cx, 1 - cy);
			GL11.glVertex3f(0.0F, 0.0F, 0.0F);
			GL11.glTexCoord2f(cx, 1 - (cy - 0.0625F));
			GL11.glVertex3f(0.0F, 16.0F, 0.0F);
			GL11.glTexCoord2f(cx + 0.0625F, 1 - (cy - 0.0625F));
			GL11.glVertex3f(16.0F, 16.0F, 0.0F);
			GL11.glEnd();
			GL11.glTranslatef(10.0F, 0.0F, 0.0F);
			GL11.glEndList();
		}
	}

	public void print(String string, int x, int y, RGBAColor color, int set, float scale) {
		if (set > 1)
			set = 1;
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0, DisplayInfo.getWidth(), DisplayInfo.getHeight(), 0.0, -1.0, 1.0);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();

		GL11.glTranslated(x, y, 0.0F);
		GL11.glScalef(scale, scale, scale);

		color.bind();
		Texture.bind(Texture.FontTex);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		for (int i = 0; i < string.length(); i++)
			GL11.glCallList(string.charAt(i) + fontList - 32 + (128 * set));
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
	}
	
	public void printDirectly(String string, RGBAColor color,int set) {
		if (set > 1)
			set = 1;
		color.bind();
		Texture.bind(Texture.FontTex);

		for (int i = 0; i < string.length(); i++)
			GL11.glCallList(string.charAt(i) + fontList - 32 + (128 * set));
	}

	public void print(String string, RGBAColor color) {
		print(string, 0, lines * 15, color, 0, 1.0F);
	}
	
	public void printIn(String string, RGBAColor color) {
		print(string, color);
		lines++;
	}
	
	public void clean() {
		lines = 0;
	}
	
}
