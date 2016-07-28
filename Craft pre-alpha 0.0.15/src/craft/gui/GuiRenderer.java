package craft.gui;

import org.lwjgl.opengl.GL11;

import craft.Selector;
import craft.renderer.Texture;
import craft.world.tile.Face;
import craft.world.tile.Tile;

public class GuiRenderer {
	private static final float CROSSHAIR_LENGTH = 10.0F;
	private int crosshairList = 0;
	private Selector selector;
	public Font font;
	public GuiRenderer(Selector selector) {
		this.selector = selector;
		this.font = new Font();
		this.crosshairList = drawCrosshair();
	}
	
	public void renderGui(float a) {
	    GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
	    GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, DisplayInfo.getWidth(), DisplayInfo.getHeight(), 0.0D, 0.0D, 200.0D);

		GL11.glTranslatef(DisplayInfo.getWidth() / 2, DisplayInfo.getHeight() / 2, 0.0F);
		GL11.glCallList(crosshairList);
		
	    GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, DisplayInfo.getScaledWidth(), DisplayInfo.getScaledHeight(), 0.0D, 0.0D, 200.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		
	    drawSelector();
	}
	
	private void drawSelector() {
		GL11.glLoadIdentity();
	    GL11.glTranslatef(0.0F, DisplayInfo.getScaledHeight() - 20.0F, -100.0F);
	    GL11.glScalef(20.0F, 20.0F, 20.0F);
    	
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    Texture.bind(Texture.GuiTexBase);
	    GL11.glBegin(GL11.GL_QUADS);
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    GL11.glTexCoord2f(1, 0);
    	GL11.glVertex3f(Selector.SELECTOR_SIZE, 0.0F, 0.0F);
	    GL11.glTexCoord2f(0, 0);
    	GL11.glVertex3f(0.0F, 0.0F, 0.0F);
    	GL11.glTexCoord2f(0, 1);
    	GL11.glVertex3f(0.0F, 1.0F, 0.0F);
    	GL11.glTexCoord2f(1, 1);
    	GL11.glVertex3f(Selector.SELECTOR_SIZE, 1.0F, 0.0F);
    	GL11.glEnd();
    	
    	for(int i = 0; i < Selector.SELECTOR_SIZE; i++) {
    		if(selector.getTileCount(i) == 0)
    			continue;
    		Texture.bind(1);
    		GL11.glPushMatrix();
        	GL11.glTranslatef(i + 0.18F, 0.32F, +3.0F);
        	GL11.glScalef(0.5F, 0.5F, 0.5F);
    	    draw3DTile(selector.getTile(i));
        	GL11.glPopMatrix();
        	GL11.glPushMatrix();
        	GL11.glTranslatef(i + 0.1F, 0.6F, +4.0F);
        	GL11.glScalef(0.02F, 0.02F, 0.02F);
        	font.printDirectly(String.valueOf(selector.getTileCount(i)), new RGBAColor(0.0F, 0.0F, 1.0F), 0);
        	GL11.glPopMatrix();
    	}
    	
    	Texture.bind(Texture.GuiTexBase + 1);
    	GL11.glPushMatrix();
    	GL11.glTranslatef(selector.getPoint() - 0.05F, -0.05F, +5.0F);
    	GL11.glScalef(1.1F, 1.1F, 1.1F);
	    GL11.glBegin(GL11.GL_QUADS);
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    GL11.glTexCoord2f(1, 0);
    	GL11.glVertex3f(1.0F, 0.0F, 0.0F);
	    GL11.glTexCoord2f(0, 0);
    	GL11.glVertex3f(0.0F, 0.0F, 0.0F);
    	GL11.glTexCoord2f(0, 1);
    	GL11.glVertex3f(0.0F, 1.0F, 0.0F);
    	GL11.glTexCoord2f(1, 1);
    	GL11.glVertex3f(1.0F, 1.0F, 0.0F);
    	GL11.glEnd();
    	GL11.glPopMatrix();
    	
    	GL11.glDisable(GL11.GL_BLEND);
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	}

	private void draw3DTile(Tile tile) {
		GL11.glRotatef(-30, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(45, 0.0F, 1.0F, 0.0F);
		GL11.glBegin(GL11.GL_QUADS);
		int xt = tile.getTexture(Face.Front) % 16 * 16;
	    int yt = tile.getTexture(Face.Front) / 16 * 16;
	    float u0 = xt / 256.0F;
	    float u1 = u0 + 0.0624375F;
	    float v0 = yt / 256.0F;
	    float v1 = v0 + 0.0624375F;
	    GL11.glColor4f(0.8F, 0.8F, 0.8F, 1.0F);
	    GL11.glTexCoord2f(u1, v0);
    	GL11.glVertex3f(1.0F, 0.0F, 1.0F);
    	GL11.glTexCoord2f(u0, v0);
    	GL11.glVertex3f(0.0F, 0.0F, 1.0F);
    	GL11.glTexCoord2f(u0, v1);
    	GL11.glVertex3f(0.0F, 1.0F, 1.0F);
    	GL11.glTexCoord2f(u1, v1);
    	GL11.glVertex3f(1.0F, 1.0F, 1.0F);
    	xt = tile.getTexture(Face.Right) % 16 * 16;
	    yt = tile.getTexture(Face.Right) / 16 * 16;
	     u0 = xt / 256.0F;
	     u1 = u0 + 0.0624375F;
	     v0 = yt / 256.0F;
	     v1 = v0 + 0.0624375F;
	     GL11.glColor4f(0.6F, 0.6F, 0.6F, 1.0F);
	    GL11.glTexCoord2f(u1, v0);
    	GL11.glVertex3f(0.0F, 1.0F, 0.0F);
    	GL11.glTexCoord2f(u0, v0);
    	GL11.glVertex3f(0.0F, 1.0F, 1.0F);
    	GL11.glTexCoord2f(u0, v1);
    	GL11.glVertex3f(0.0F, 0.0F, 1.0F);
    	GL11.glTexCoord2f(u1, v1);
    	GL11.glVertex3f(0.0F, 0.0F, 0.0F);
    	 xt = tile.getTexture(Face.Top) % 16 * 16;
	     yt = tile.getTexture(Face.Top) / 16 * 16;
	     u0 = xt / 256.0F;
	     u1 = u0 + 0.0624375F;
	     v0 = yt / 256.0F;
		 v1 = v0 + 0.0624375F;
	     GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    GL11.glTexCoord2f(u1, v0);
    	GL11.glVertex3f(1.0F, 0.0F, 0.0F);
    	GL11.glTexCoord2f(u0, v0);
    	GL11.glVertex3f(0.0F, 0.0F, 0.0F);
    	GL11.glTexCoord2f(u0, v1);
    	GL11.glVertex3f(0.0F, 0.0F, 1.0F);
    	GL11.glTexCoord2f(u1, v1);
    	GL11.glVertex3f(1.0F, 0.0F, 1.0F);
    	GL11.glEnd();
	}
	
	private int drawCrosshair() {
		int list = GL11.glGenLists(1);
		GL11.glNewList(list, GL11.GL_COMPILE);
		GL11.glLineWidth(1.25F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3f(CROSSHAIR_LENGTH, 0.0F, 0.0F);
		GL11.glVertex3f(-CROSSHAIR_LENGTH, 0.0F, 0.0F);
		GL11.glVertex3f(0.0F, CROSSHAIR_LENGTH, 0.0F);
		GL11.glVertex3f(0.0F, -CROSSHAIR_LENGTH, 0.0F);
		GL11.glEnd();
		GL11.glEndList();
		return list;
	}
	
	
}
