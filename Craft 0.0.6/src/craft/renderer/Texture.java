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
	private static String path = "date\\textures\\";		//纹理路径
	private static String guiPath = "date\\gui\\";		//Gui纹理路径
	private static String crackPath = "date\\textures\\crack\\crack ";		//裂纹纹理路径
	private static String texnames[] = {			//纹理文件名
		"grass.png",
		"grasssoil.png",
		"soil.png",
		"stone.png",
		"cobblestone.png",
		"bedrock.png",
		"planks.png",
		"trunk.png",
		"bark.png",
		"oak planks.png",
		"leaf.png",
		"sand.png",
		"water.png",
		"watercover.png"
	};
	private static String guiTexnames[] = {			//纹理文件名
		"Inventory.png",
		"Select.png"
	};
	private static String crackTexnames[] = {
		"1x.png", "2x.png", "3x.png", "4x.png", "5x.png", "6x.png", "7x.png", "8x.png", "9x.png", "10x.png"
	};
	public static void loadTexture() {
		int id = 0;
		try {
			BufferedImage img;
			IntBuffer texid = BufferUtils.createIntBuffer(texnames.length);
			GL11.glGenTextures(texid);
			for( ; id <  texnames.length ; id++) {
				File p=new File(path+texnames[id]);
				img=ImageIO.read(p);
				bind(texid.get(id));
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
				int w = img.getWidth();
			    int h = img.getHeight();
			    ByteBuffer pixels = BufferUtils.createByteBuffer(w * h * 4);
			    int[] rawPixels = new int[w * h];
			    img.getRGB(0, 0, w, h, rawPixels, 0, w);
			    for (int i = 0; i < rawPixels.length; i++)
			      {
			      int a = rawPixels[i] >> 24 & 0xFF;
			      int r = rawPixels[i] >> 16 & 0xFF;
			      int g = rawPixels[i] >> 8 & 0xFF;
			      int b = rawPixels[i] & 0xFF;
			      rawPixels[i] = (a << 24 | b << 16 | g << 8 | r);
			    }
			    pixels.asIntBuffer().put(rawPixels);
			    GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D, GL11.GL_RGBA, w, h, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
			    System.out.println("Success loading Texture[id:"+texid.get(id)+"]:"+texnames[id]);
			    texCount++;
			}
		} catch (Exception e) {
			System.out.println("Fail to load Texture["+(id + 1)+"]:"+path+texnames[id]);
		}
		
		try {
			BufferedImage img;
			IntBuffer texid = BufferUtils.createIntBuffer(crackTexnames.length);
			GL11.glGenTextures(texid);
			for(id = 0 ; id < crackTexnames.length ; id++) {
				File p = new File(crackPath+crackTexnames[id]);
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
			    System.out.println("Success loading CrackTexture[id:"+(texid.get(id))+"]:"+crackTexnames[id]);
				texCount++;
			}
		} catch (Exception e) {
			System.out.println("Fail to load CrackTexture["+(texCount+1)+"]:"+crackPath+crackTexnames[id]);
		}
	}
	
	public static void loadGuiTexture() {
		int id =  0;
		try {
			BufferedImage img;
			IntBuffer texid = BufferUtils.createIntBuffer(guiTexnames.length);
			GL11.glGenTextures(texid);
			for( ; id < guiTexnames.length ; id++) {
				File p = new File(guiPath+guiTexnames[id]);
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
			    System.out.println("Success loading GuiTexture[id:"+(texid.get(id))+"]:"+guiTexnames[id]);
				texCount++;
			}
		} catch (Exception e) {
			System.out.println("Fail to load GuiTexture["+(texCount+1)+"]:"+guiPath+guiTexnames[id]);
		}
	}
	
	public static void bind(int id) {
		if(id != lastId) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
			lastId = id;
		}
	}
}
