package craft.gui;

import org.lwjgl.opengl.GL11;

import craft.Selector;
import craft.renderer.Texture;

public class GuiRenderer {
	private int width, height;
	private Selector selector;
	public Font font;
	public GuiRenderer(int width, int height, Selector selector) {
		reshape(width, height);
		this.selector = selector;
		this.font = new Font(width, height);
	}
	
	public void reshape(int width, int height) {
		this.width = width * 240 / height;
		this.height = 240;
	}
	
	public void drawGui(float a) {
	    GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
	    GL11.glLoadIdentity();
	    GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, width, height, 0.0D, 0.0D, 200.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

	    drawSelector();
	}
	
	private void drawSelector() {
		GL11.glLoadIdentity();
	    GL11.glTranslatef(0.0F, height - 20.0F, 0.0F);
	    GL11.glScalef(20.0F, 20.0F, 20.0F);
    	
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    Texture.bind(3);
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
    		int xt = selector.getTile(i).tex % 16 * 16;
    	    int yt = selector.getTile(i).tex / 16 * 16;
    	    float u0 = xt / 256.0F;
    	    float u1 = (xt + 16.0F) / 256.0F;
    	    float v0 = yt / 256.0F;
    	    float v1 = (yt + 16.0F) / 256.0F;
    		GL11.glPushMatrix();
        	GL11.glTranslatef(i + 0.125F, 0.12F, 0.0F);
        	GL11.glScalef(0.805F, 0.812F, 0.8F);
    	    GL11.glBegin(GL11.GL_QUADS);
    	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        	GL11.glTexCoord2f(u1, v0);
        	GL11.glVertex3f(1.0F, 0.0F, 0.0F);
        	GL11.glTexCoord2f(u0, v0);
        	GL11.glVertex3f(0.0F, 0.0F, 0.0F);
        	GL11.glTexCoord2f(u0, v1);
        	GL11.glVertex3f(0.0F, 1.0F, 0.0F);
        	GL11.glTexCoord2f(u1, v1);
        	GL11.glVertex3f(1.0F, 1.0F, 0.0F);
        	GL11.glEnd();
        	GL11.glPopMatrix();
        	GL11.glPushMatrix();
        	GL11.glTranslatef(i + 0.1F, 0.6F, 0.0F);
        	GL11.glScalef(0.02F, 0.02F, 0.02F);
        	font.printDirectly(String.valueOf(selector.getTileCount(i)), new RGBAColor(0.0F, 0.0F, 1.0F), 0);
        	GL11.glPopMatrix();
    	}
    	
    	Texture.bind(4);
    	GL11.glPushMatrix();
    	GL11.glTranslatef(selector.getPoint(), 0.0F, 0.0F);
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


	/*private void drawSelector2() {
		GL11.glPushMatrix();
	    GL11.glTranslatef(0.0F, height - 20.0F, 0.0F);
	    GL11.glScalef(20.0F, 20.0F, 20.0F);
    	
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    Texture.bind(5);
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
    	
    	Texture.bind(1);
    	for(int i = 0; i < Selector.SELECTOR_SIZE; i++) {
    		if(selector.getTileCount(i) == 0)
    			continue;
    		int xt = selector.getTile(i).tex % 16 * 16;
    	    int yt = selector.getTile(i).tex / 16 * 16;
    	    float u0 = xt / 256.0F;
    	    float u1 = (xt + 16.0F) / 256.0F;
    	    float v0 = yt / 256.0F;
    	    float v1 = (yt + 16.0F) / 256.0F;
    		GL11.glPushMatrix();
        	GL11.glTranslatef(i + 0.07692F, 0.07692F, 0.0F);
        	GL11.glScalef(0.805F, 0.812F, 0.8F);
    	    GL11.glBegin(GL11.GL_QUADS);
    	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        	GL11.glTexCoord2f(u1, v0);
        	GL11.glVertex3f(1.0F, 0.0F, 0.0F);
        	GL11.glTexCoord2f(u0, v0);
        	GL11.glVertex3f(0.0F, 0.0F, 0.0F);
        	GL11.glTexCoord2f(u0, v1);
        	GL11.glVertex3f(0.0F, 1.0F, 0.0F);
        	GL11.glTexCoord2f(u1, v1);
        	GL11.glVertex3f(1.0F, 1.0F, 0.0F);
        	GL11.glEnd();
        	GL11.glPopMatrix();
    	}
    	
    	Texture.bind(6);
    	GL11.glPushMatrix();
    	GL11.glTranslatef(selector.getPoint() + 0.07692F, 0.0F, 0.0F);
	    GL11.glBegin(GL11.GL_QUADS);
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    GL11.glTexCoord2f(1, 0);
    	GL11.glVertex3f(0.8076F, 0.9166F, 0.0F);
	    GL11.glTexCoord2f(0, 0);
    	GL11.glVertex3f(0.0F, 0.9166F, 0.0F);
    	GL11.glTexCoord2f(0, 1);
    	GL11.glVertex3f(0.0F, 0.9615F, 0.0F);
    	GL11.glTexCoord2f(1, 1);
    	GL11.glVertex3f(0.8076F, 0.9615F, 0.0F);
    	GL11.glEnd();
    	GL11.glPopMatrix();
    	
    	GL11.glDisable(GL11.GL_BLEND);
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	    GL11.glPopMatrix();
	    
	}*/
	
	
}
