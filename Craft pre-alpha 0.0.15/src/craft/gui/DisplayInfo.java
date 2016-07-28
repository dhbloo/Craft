package craft.gui;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class DisplayInfo {
	private static int width, height;
	private static int scaledWidth, scaledHeight;

	public DisplayInfo() {
		
	}
	
	public static void reSizeGLScene() {
		DisplayInfo.width = Display.getWidth();
		DisplayInfo.height = Display.getHeight();
		GL11.glViewport(0, 0, DisplayInfo.width, DisplayInfo.height);
		scaledWidth = width * 240 / height;
		scaledHeight = 240;
	}
	
	public static int getWidth() {
		return width;
	}
	
	public static int getHeight() {
		return height;
	}

	public static int getScaledWidth() {
		return scaledWidth;
	}
	
	public static int getScaledHeight() {
		return scaledHeight;
	}
	
	
}
