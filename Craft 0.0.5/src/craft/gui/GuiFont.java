package craft.gui;

import java.awt.Font;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

public class GuiFont {
	private int width, height;
	public TrueTypeFont font;

	public GuiFont(int width, int height) {
		this.width = width;
		this.height = height;
		Font awtFont = new Font("Times New Roman", Font.BOLD, 30);
		font = new TrueTypeFont(awtFont, true);
	}

	public void print(String string, int x, int y) {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, width, height, 0.0D, -1.0D, 100.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -10.0F);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		//GL11.glEnable(GL11.GL_BLEND);
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTranslatef(50, 50, -2.0F);
		GL11.glScalef(30.0F, 30.0F, 30.0F);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.5F);
		GL11.glVertex3f(0.0F, 0.0F, 0.0F);
		GL11.glVertex3f(1.0F, 0.0F, 0.0F);
		GL11.glVertex3f(1.0F, 1.0F, 0.0F);
		GL11.glVertex3f(0.0F, 1.0F, 0.0F);
		GL11.glEnd();
		Color.white.bind();
		font.drawString(x, y, string, Color.darkGray);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		//GL11.glDisable(GL11.GL_BLEND);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
	}
}
