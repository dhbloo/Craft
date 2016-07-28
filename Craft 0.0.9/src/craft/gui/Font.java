package craft.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class Font {
	private int width, height;
	private int fontList;
	private IntBuffer fontTex = BufferUtils.createIntBuffer(1);
	private static String fontFile = "date\\gui\\Font.bmp";		//字体文件路径
	protected Font(int width, int height) {
		loadFont();
		fontList = GL11.glGenLists(256);
		for(int i = 0; i < 256; i++) {
			int cx = i % 16 / 16;
			int cy = (int)Math.floor(i / 16) / 16;
			GL11.glNewList(fontList + i, GL11.GL_COMPILE);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontTex.get(0));
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(cx, 1 - cy - 0.0625F);
			GL11.glVertex3f(0.0F, 0.0F, 0.0F);
			GL11.glTexCoord2f(cx + 0.0625F, 1 - cy - 0.0625F);
			GL11.glVertex3f(16.0F, 0.0F, 0.0F);
			GL11.glTexCoord2f(cx + 0.0625F, 1 - cy);
			GL11.glVertex3f(16.0F, 16.0F, 0.0F);
			GL11.glTexCoord2f(cx, 1 - cy);
			GL11.glVertex3f(0.0F, 16.0F, 0.0F);
			GL11.glEnd();
			GL11.glTranslatef(10.0F, 0.0F, 0.0F);
			GL11.glEndList();
		}
	}

	private boolean loadFont() {
		try {
			BufferedImage img;
			GL11.glGenTextures(fontTex);
			img = ImageIO.read(new File(fontFile));
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontTex.get(0));
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			int w = img.getWidth();
			int h = img.getHeight();
			ByteBuffer pixels = BufferUtils.createByteBuffer(w * h * 4);
			int[] rawPixels = new int[w * h];
			img.getRGB(0, 0, w, h, rawPixels, 0, w);
			for (int i = 0; i < rawPixels.length; i++) {
				int a = rawPixels[i] >> 24 & 0xFF;
		      	int r = rawPixels[i] >> 16 & 0xFF;
		      	int g = rawPixels[i] >> 8 & 0xFF;
		      	int b = rawPixels[i] & 0xFF;
		      	rawPixels[i] = (a << 24 | b << 16 | g << 8 | r);
			}
			pixels.asIntBuffer().put(rawPixels);
			GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D, GL11.GL_RGBA, w, h, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
			System.out.println("Success loading Font!");
			return true;
		} catch (Exception e) {
			System.out.println("Fail to load Font! :" + fontFile);
			return false;
		}
	}
	
	public void print(String string, int x, int y, int set) {
		if(set > 1)
			set = 1;
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glOrtho(0.0D, width, height, 0.0D, 1.0D, 200.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
	    GL11.glTranslated(x, y, -1.0F);
	    GL11.glScalef(20, 20, 20);
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontTex.get(0));
		/*GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(0.0F, 0.0F, 0.0F);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(1.0F, 0.0F, 0.0F);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(1.0F, 1.0F, 0.0F);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(0.0F, 1.0F, 0.0F);
		GL11.glEnd();*/
	    GL11.glListBase(fontList - 32 + (128 * set));
	    ByteBuffer text = BufferUtils.createByteBuffer(string.length());
	    GL11.glCallLists(text.put(string.getBytes()));
	    GL11.glCallList(35);
	    GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
}
