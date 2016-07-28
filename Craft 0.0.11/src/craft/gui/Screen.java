package craft.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import craft.renderer.Tesselator;

public class Screen {
	protected int width;
	protected int height;

	public Screen(int width, int height) {
		this.width = width;
		this.height = height;
		init();
	}

	public void render(int xMouse, int yMouse) {
	}

	public void init() {
	}

	protected void fill(int x0, int y0, int x1, int y1, RGBAColor color) {
		Tesselator t = Tesselator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(color.r, color.g, color.b, color.a);
		t.begin();
		t.vertex(x0, y1, 0.0F);
		t.vertex(x1, y1, 0.0F);
		t.vertex(x1, y0, 0.0F);
		t.vertex(x0, y0, 0.0F);
		t.end();
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	protected void fillGradient(int x0, int y0, int x1, int y1, RGBAColor color1, RGBAColor color2) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4f(color1.r, color1.g, color1.b, color1.a);
		GL11.glVertex2f(x1, y0);
		GL11.glVertex2f(x0, y0);
		GL11.glColor4f(color2.r, color2.g, color2.b, color2.a);
		GL11.glVertex2f(x0, y1);
		GL11.glVertex2f(x1, y1);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void updateEvents() {
		while (Mouse.next()) {
			if (Mouse.getEventButtonState()) {
				int xm = Mouse.getEventX();
				int ym = this.height - Mouse.getEventY() - 1;
				mouseClicked(xm, ym, Mouse.getEventButton());
			}
		}

		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				keyPressed(Keyboard.getEventCharacter(), Keyboard.getEventKey());
			}
		}
	}

	protected void keyPressed(char eventCharacter, int eventKey) {
	}

	protected void mouseClicked(int x, int y, int button) {
	}

	public void tick() {
	}

}
