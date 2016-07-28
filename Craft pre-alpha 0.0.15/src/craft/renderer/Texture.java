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
	public static final int GuiTexBase = 5;
	public static final int FontTex = 7;
	private static int lastId = -1;
	private static int texCount = 0;
	/**纹理路径*/
	private static String terrainPath = "date\\terrain.png";
	/**人物纹理路径*/
	private static String charPath = "date\\char.png";
	/**Gui纹理路径*/
	private static String guiPath = "date\\gui\\";
	/**字体文件路径*/
	private static String fontFile = "date\\gui\\Font.bmp";
	/**Gui纹理文件名*/
	private static String guiTexNames[] = {
	"Inventory.png", "Select.png" };
	/**纹理文件路径*/
	private static String texPaths[] = {
	"date\\rock.png",
	"date\\water.png" };

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
		
		int id = 0;
		try {
			texid = BufferUtils.createIntBuffer(texPaths.length);
			GL11.glGenTextures(texid);
			for (; id < texPaths.length; id++) {
				File p = new File(texPaths[id]);
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
				System.out.println("Success loading Texture![id:" + (texid.get(id)) + "]:" + texPaths[id]);
				texCount++;
			}
			
		} catch (Exception e) {
			System.out.println("Fail to load Texture[" + (texCount + 1) + "]:" + texPaths[id]);
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
			texid = BufferUtils.createIntBuffer(guiTexNames.length);
			GL11.glGenTextures(texid);
			for (; id < guiTexNames.length; id++) {
				File p = new File(guiPath + guiTexNames[id]);
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
				System.out.println("Success loading GuiTexture[id:" + (texid.get(id)) + "]:" + guiPath + guiTexNames[id]);
				texCount++;
			}
		} catch (Exception e) {
			System.out.println("Fail to load GuiTexture[" + (texCount + 1) + "]:" + guiPath + guiTexNames[id]);
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
