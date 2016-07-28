package craft.renderer;

import org.lwjgl.opengl.GL11;

import craft.world.tile.Face;

public class RenderHelper {

	public RenderHelper() {
		
	}
	
	/**画正方体的一面
	 * @param x 正方体的左x坐标
	 * @param y 正方体的下y坐标
	 * @param z 正方体的后z坐标
	 * @param length 正方体的棱长
	 * @param face 正方体的哪个面*/
	public static void drawCubeFace(float x, float y, float z, float length, Face face) {
		switch(face) {
	    case Front:
	    	GL11.glVertex3f(0.0F, length, length);
	        GL11.glVertex3f(0.0F, 0.0F, length);
	        GL11.glVertex3f(length, 0.0F, length);
	        GL11.glVertex3f(length, length, length);
	    	break;
	    case Back:
	    	GL11.glVertex3f(0.0F, length, 0.0F);
	        GL11.glVertex3f(length, length, 0.0F);
	        GL11.glVertex3f(length, 0.0F, 0.0F);
	        GL11.glVertex3f(0.0F, 0.0F, 0.0F);
	    	break;
	    case Top:
	    	GL11.glVertex3f(length, length, length);
	        GL11.glVertex3f(length, length, 0.0F);
	        GL11.glVertex3f(0.0F, length, 0.0F);
	        GL11.glVertex3f(0.0F, length, length);
	    	break;
	    case Bottom:
	    	GL11.glVertex3f(0.0F, 0.0F, length);
	        GL11.glVertex3f(0.0F, 0.0F, 0.0F);
	        GL11.glVertex3f(length, 0.0F, 0.0F);
	        GL11.glVertex3f(length, 0.0F, length);
	    	break;
	    case Left:
	    	GL11.glVertex3f(0.0F, length, length);
	        GL11.glVertex3f(0.0F, length, 0.0F);
	        GL11.glVertex3f(0.0F, 0.0F, 0.0F);
	        GL11.glVertex3f(0.0F, 0.0F, length);
	    	break;
	    case Right:
	    	GL11.glVertex3f(length, 0.0F, length);
	        GL11.glVertex3f(length, 0.0F, 0.0F);
	        GL11.glVertex3f(length, length, 0.0F);
	        GL11.glVertex3f(length, length, length);
	    	break;
	    }
	}
	
	/**画正方体的一面
	 * @param x 正方体的左x坐标
	 * @param y 正方体的下y坐标
	 * @param z 正方体的后z坐标
	 * @param length 正方体的棱长
	 * @param face 正方体的哪个面*/
	public static void drawCubeFaceWithTexture(float x, float y, float z, float length, Face face, float u0, float u1, float v0, float v1) {
		switch(face) {
	    case Front:
	    	vertexUV(0.0F, length, length, u0, v0);
	        vertexUV(0.0F, 0.0F, length, u0, v1);
	        vertexUV(length, 0.0F, length, u1, v1);
	        vertexUV(length, length, length, u1, v0);
	    	break;
	    case Back:
	    	vertexUV(0.0F, length, 0.0F, u1, v0);
	        vertexUV(length, length, 0.0F, u0, v0);
	        vertexUV(length, 0.0F, 0.0F, u0, v1);
	        vertexUV(0.0F, 0.0F, 0.0F, u1, v1);
	    	break;
	    case Top:
	    	vertexUV(length, length, length, u1, v1);
	        vertexUV(length, length, 0.0F, u1, v0);
	        vertexUV(0.0F, length, 0.0F, u0, v0);
	        vertexUV(0.0F, length, length, u0, v1);
	    	break;
	    case Bottom:
	    	vertexUV(0.0F, 0.0F, length, u0, v1);
	        vertexUV(0.0F, 0.0F, 0.0F, u0, v0);
	        vertexUV(length, 0.0F, 0.0F, u1, v0);
	        vertexUV(length, 0.0F, length, u1, v1);
	    	break;
	    case Left:
	    	vertexUV(0.0F, length, length, u1, v0);
	        vertexUV(0.0F, length, 0.0F, u0, v0);
	        vertexUV(0.0F, 0.0F, 0.0F, u0, v1);
	        vertexUV(0.0F, 0.0F, length, u1, v1);
	    	break;
	    case Right:
	    	vertexUV(length, 0.0F, length, u0, v1);
	        vertexUV(length, 0.0F, 0.0F, u1, v1);
	        vertexUV(length, length, 0.0F, u1, v0);
	        vertexUV(length, length, length, u0, v0);
	    	break;
	    }
	}
	
	/**画矩形
	 * @param x 矩形的左x坐标
	 * @param y 矩形的下y坐标
	 * @param w 矩形的宽
	 * @param h 矩形的高*/
	public static void drawRect(float x, float y, float w, float h) {
		
	}
	
	public static void drawRectWithTexture(float x, float y, float w, float h) {
		
	}
	
	public static void vertexUV(float x, float y, float z, float u, float v) {
		GL11.glTexCoord2f(u, v);
		GL11.glVertex3f(x, y, z);
	}

}
