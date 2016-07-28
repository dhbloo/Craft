package craft.renderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class Texture {
	private static int lastId = -1;
	private static int texCount = 0;
	private static String terrainPath = "date\\terrain.png"; // 纹理路径
	private static String charPath = "date\\char.png"; // 纹理路径
	private static String guiPath = "date\\gui\\"; // Gui纹理路径
	private static String fontFile = "date\\gui\\Font.bmp"; // 字体文件路径
	private static String guiTexnames[] = { // 纹理文件名
	"Inventory.png", "Select.png" };

	public static void loadTexture() {
		BufferedImage img;
		IntBuffer texid;
		try {
			texid = BufferUtils.createIntBuffer(1);
			GL11.glGenTextures(texid);
			File p = new File(terrainPath);
			img = ImageIO.read(p);
			bind(texid.get(0));
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
			System.out.println("Success loading Texture![id:" + (texid.get(0)) + "]:" + terrainPath);
			texCount++;
		} catch (Exception e) {
			System.out.println("Fail to load Texture!");
		}

		try {
			texid = BufferUtils.createIntBuffer(1);
			GL11.glGenTextures(texid);
			File p = new File(charPath);
			img = ImageIO.read(p);
			bind(texid.get(0));
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
			System.out.println("Success loading Char Texture![id:" + (texid.get(0)) + "]:" + charPath);
			texCount++;
		} catch (Exception e) {
			System.out.println("Fail to load Char Texture!");
		}
	}

	public static void loadGuiTexture() {
		int id = 0;
		BufferedImage img;
		IntBuffer texid;
		try {
			texid = BufferUtils.createIntBuffer(guiTexnames.length);
			GL11.glGenTextures(texid);
			for (; id < guiTexnames.length; id++) {
				File p = new File(guiPath + guiTexnames[id]);
				img = ImageIO.read(p);
				bind(texid.get(id));
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
				System.out.println("Success loading GuiTexture[id:" + (texid.get(id)) + "]:" + guiPath + guiTexnames[id]);
				texCount++;
			}
		} catch (Exception e) {
			System.out.println("Fail to load GuiTexture[" + (texCount + 1) + "]:" + guiPath + guiTexnames[id]);
		}
		
		try {
			texid = BufferUtils.createIntBuffer(1);
			GL11.glGenTextures(texid);
			img = ImageIO.read(new File(fontFile));
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texid.get(0));
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
				if (r == 0 && g == 0 && b == 0)
					a = 0;
				rawPixels[i] = (a << 24 | b << 16 | g << 8 | r);
			}
			pixels.asIntBuffer().put(rawPixels);
			GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D, GL11.GL_RGBA, w, h, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
			System.out.println("Success loading Font![id:" + texid.get(0) + "]:" + fontFile);
		} catch (Exception e) {
			System.out.println("Fail to load Font! :" + fontFile);
		}
	}
	
	public static void bind(int id) {
		if (id != lastId) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
			lastId = id;
		}
	}
}
