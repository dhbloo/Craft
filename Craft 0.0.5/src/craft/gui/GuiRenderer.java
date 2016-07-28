package craft.gui;

import org.lwjgl.opengl.GL11;

import craft.Selector;
import craft.renderer.Texture;

public class GuiRenderer {
	private int width, height;
	private Selector selector;
	public GuiFont font;
	public GuiRenderer(int width, int height, Selector selector) {
		this.width = width * 240 / height;
		this.height = height * 240 / height;
		this.selector = selector;
		this.font = new GuiFont(width, height);
	}
	
	public void drawGui(float a) {
	    GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
	    GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, width, height, 0.0D, 1.0D, 200.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
	    GL11.glTranslatef(0.0F, 0.0F, -1.0F);

	    drawSelector();
	}
	
	private void drawSelector() {
		GL11.glPushMatrix();
	    GL11.glTranslatef(0.0F, height - 20.0F, 0.0F);
	    GL11.glScalef(20.0F, 20.0F, 20.0F);
	    
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    Texture.bind(23);
	    GL11.glBegin(GL11.GL_QUADS);
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    GL11.glTexCoord2f(0, 0);
    	GL11.glVertex3f(0.0F, 0.0F, 0.0F);
    	GL11.glTexCoord2f(1, 0);
    	GL11.glVertex3f(6.0F, 0.0F, 0.0F);
    	GL11.glTexCoord2f(1, 1);
    	GL11.glVertex3f(6.0F, 1.0F, 0.0F);
    	GL11.glTexCoord2f(0, 1);
    	GL11.glVertex3f(0.0F, 1.0F, 0.0F);
    	GL11.glEnd();
    	
    	for(int i = 0; i < Selector.SELECTOR_SIZE; i++) {
    		if(selector.getTileCount(i) == 0)
    			continue;
    		Texture.bind(selector.getTile(i).tex);
    		GL11.glPushMatrix();
        	GL11.glTranslatef(i + 0.125F, 0.12F, 0.0F);
        	GL11.glScalef(0.805F, 0.812F, 0.8F);
    	    GL11.glBegin(GL11.GL_QUADS);
    	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    	    GL11.glTexCoord2f(0, 0);
        	GL11.glVertex3f(0.0F, 0.0F, 0.0F);
        	GL11.glTexCoord2f(1, 0);
        	GL11.glVertex3f(1.0F, 0.0F, 0.0F);
        	GL11.glTexCoord2f(1, 1);
        	GL11.glVertex3f(1.0F, 1.0F, 0.0F);
        	GL11.glTexCoord2f(0, 1);
        	GL11.glVertex3f(0.0F, 1.0F, 0.0F);
        	GL11.glEnd();
        	GL11.glPopMatrix();
    	}
    	
    	Texture.bind(24);
    	GL11.glPushMatrix();
    	GL11.glTranslatef(selector.getPoint(), 0.0F, 0.0F);
	    GL11.glBegin(GL11.GL_QUADS);
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    GL11.glTexCoord2f(0, 0);
    	GL11.glVertex3f(0.0F, 0.0F, 0.0F);
    	GL11.glTexCoord2f(1, 0);
    	GL11.glVertex3f(1.0F, 0.0F, 0.0F);
    	GL11.glTexCoord2f(1, 1);
    	GL11.glVertex3f(1.0F, 1.0F, 0.0F);
    	GL11.glTexCoord2f(0, 1);
    	GL11.glVertex3f(0.0F, 1.0F, 0.0F);
    	GL11.glEnd();
    	GL11.glPopMatrix();
    	Texture.bind(-1);
    	
    	GL11.glDisable(GL11.GL_BLEND);
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	    GL11.glPopMatrix();
	    
	}
	
}
