package craft.level.tile;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import craft.level.Level;
import craft.particle.Particle;
import craft.particle.ParticleEngine;
import craft.phys.AABB;
import craft.renderer.Texture;

public class Tile {
	public static final Tile[] tiles = new Tile[256];
	public static final Tile air = new Tile(0, 0);
	public static final Tile stone = new Tile(1, 4);
	public static final Tile grass = new GrassTile(2, 2);
	public static final Tile soil = new SoilTile(3, 3);
	public static final Tile cobblestone = new Tile(4, 5);
	public static final Tile bedrock = new Tile(5, 6);
	public static final Tile planks = new Tile(6, 7);
	public static final Tile oak_planks = new Tile(7, 10);
	public static final Tile trunk = new TreeTile(8, 9);
	public static final Tile leaf = new LeafTile(9, 11);
	public static final Tile sand = new Tile(10, 12);
	public static final int PARTICLE_COUNT = 4;
	public static final int TICK_COUNT = 4;
	public int tex;
	public final int id;
	
	protected Tile(int id, int tex) {
		this.tex=tex;
		this.id=id;
		tiles[id] = this;
	}
	
	protected int getTexture(Face face) {
	    return this.tex;
	}
	
	public void render(Level level, int x, int y, int z) {
		float c1 = 1.0F;
	    float c2 = 0.8F;
	    float c3 = 0.6F;
	    float b = 0.0F;
	    if(shouldRenderFace(level, x - 1, y, z)) {
	    	b =  level.getBrightness(x - 1, y, z);
	    	GL11.glColor3f(c2 * b, c2 * b, c2 * b);
	    	renderFace(x, y, z, Face.Left);
	    }
	    if(shouldRenderFace(level, x + 1, y, z)) {
	    	b =  level.getBrightness(x + 1, y, z);
	    	GL11.glColor3f(c2 * b, c2 * b, c2 * b);
	    	renderFace(x, y, z, Face.Right);
	    }
	    if(shouldRenderFace(level, x, y - 1, z)) {
	    	b =  level.getBrightness(x, y - 1, z);
	    	GL11.glColor3f(c3 * b, c3 * b, c3 * b);
	    	renderFace(x, y, z, Face.Bottom);
	    }
	    if(shouldRenderFace(level, x, y + 1, z)) {
	    	b =  level.getBrightness(x, y + 1, z);
	    	GL11.glColor3f(c1 * b, c1 * b, c1 * b);
	    	renderFace(x, y, z, Face.Top);
	    }
	    if(shouldRenderFace(level, x, y, z - 1)) {
	    	b =  level.getBrightness(x, y, z - 1);
	    	GL11.glColor3f(c2 * b, c2 * b, c2 * b);
	    	renderFace(x, y, z, Face.Back);
	    }
	    if(shouldRenderFace(level, x, y, z + 1)) {
	    	b =  level.getBrightness(x, y, z + 1);
	    	GL11.glColor3f(c2 * b, c2 * b, c2 * b);
	    	renderFace(x, y, z, Face.Front);
	    }
	}
	
	private boolean shouldRenderFace(Level level, int x, int y, int z) {
	    if (level.isLightBlock(x, y, z)) 
	    	return true; 
	    return false;
	  }
	
	protected void renderFace(int x, int y, int z, Face face) {
		int tex = getTexture(face);
		float x0 = x + 0.0F;
	    float x1 = x + 1.0F;
	    float y0 = y + 0.0F;
	    float y1 = y + 1.0F;
	    float z0 = z + 0.0F;
	    float z1 = z + 1.0F;
	    Texture.bind(tex);
	    GL11.glBegin(GL11.GL_QUADS);
	    switch(face) {
	    case Front:
	    	GL11.glTexCoord2f(0, 0);
	    	GL11.glVertex3f(x0, y1, z1);
	    	GL11.glTexCoord2f(1, 0);
	    	GL11.glVertex3f(x1, y1, z1);
	    	GL11.glTexCoord2f(1, 1);
	    	GL11.glVertex3f(x1, y0, z1);
	    	GL11.glTexCoord2f(0, 1);
	    	GL11.glVertex3f(x0, y0, z1);
	    	break;
	    case Back:
	    	GL11.glTexCoord2f(1, 0);
	    	GL11.glVertex3f(x0, y1, z0);
	    	GL11.glTexCoord2f(1, 1);
	    	GL11.glVertex3f(x0, y0, z0);
	    	GL11.glTexCoord2f(0, 1);
	    	GL11.glVertex3f(x1, y0, z0);
	    	GL11.glTexCoord2f(0, 0);
	    	GL11.glVertex3f(x1, y1, z0);
	    	break;
	    case Top:
	    	GL11.glTexCoord2f(1, 0);
	    	GL11.glVertex3f(x1, y1, z0);
	    	GL11.glTexCoord2f(1, 1);
	    	GL11.glVertex3f(x1, y1, z1);
	    	GL11.glTexCoord2f(0, 1);
	    	GL11.glVertex3f(x0, y1, z1);
	    	GL11.glTexCoord2f(0, 0);
	    	GL11.glVertex3f(x0, y1, z0);
	    	break;
	    case Bottom:
	    	GL11.glTexCoord2f(0, 1);
	    	GL11.glVertex3f(x0, y0, z0);
	    	GL11.glTexCoord2f(0, 0);
	    	GL11.glVertex3f(x0, y0, z1);
	    	GL11.glTexCoord2f(1, 0);
	    	GL11.glVertex3f(x1, y0, z1);
	    	GL11.glTexCoord2f(1, 1);
	    	GL11.glVertex3f(x1, y0, z0);
	    	break;
	    case Left:
	    	GL11.glTexCoord2f(0, 0);
	    	GL11.glVertex3f(x0, y1, z0);
	    	GL11.glTexCoord2f(1, 0);
	    	GL11.glVertex3f(x0, y1, z1);
	    	GL11.glTexCoord2f(1, 1);
	    	GL11.glVertex3f(x0, y0, z1);
	    	GL11.glTexCoord2f(0, 1);
	    	GL11.glVertex3f(x0, y0, z0);
	    	break;
	    case Right:
	    	GL11.glTexCoord2f(0, 0);
	    	GL11.glVertex3f(x1, y1, z1);
	    	GL11.glTexCoord2f(1, 0);
	    	GL11.glVertex3f(x1, y1, z0);
	    	GL11.glTexCoord2f(1, 1);
	    	GL11.glVertex3f(x1, y0, z0);
	    	GL11.glTexCoord2f(0, 1);
	    	GL11.glVertex3f(x1, y0, z1);
	    	break;
	    }
	    GL11.glEnd();
	}
	
	public static void renderFaceNoTexture(int x, int y, int z, Face face) {
		float x0 = x + 0.0F;
	    float x1 = x + 1.0F;
	    float y0 = y + 0.0F;
	    float y1 = y + 1.0F;
	    float z0 = z + 0.0F;
	    float z1 = z + 1.0F;
	    GL11.glBegin(GL11.GL_QUADS);
	    switch(face) {
	    case Front:
	    	GL11.glTexCoord2f(0, 0);
	    	GL11.glVertex3f(x0, y1, z1);
	    	GL11.glTexCoord2f(1, 0);
	    	GL11.glVertex3f(x1, y1, z1);
	    	GL11.glTexCoord2f(1, 1);
	    	GL11.glVertex3f(x1, y0, z1);
	    	GL11.glTexCoord2f(0, 1);
	    	GL11.glVertex3f(x0, y0, z1);
	    	break;
	    case Back:
	    	GL11.glTexCoord2f(1, 0);
	    	GL11.glVertex3f(x0, y1, z0);
	    	GL11.glTexCoord2f(1, 1);
	    	GL11.glVertex3f(x0, y0, z0);
	    	GL11.glTexCoord2f(0, 1);
	    	GL11.glVertex3f(x1, y0, z0);
	    	GL11.glTexCoord2f(0, 0);
	    	GL11.glVertex3f(x1, y1, z0);
	    	break;
	    case Top:
	    	GL11.glTexCoord2f(1, 0);
	    	GL11.glVertex3f(x1, y1, z0);
	    	GL11.glTexCoord2f(1, 1);
	    	GL11.glVertex3f(x1, y1, z1);
	    	GL11.glTexCoord2f(0, 1);
	    	GL11.glVertex3f(x0, y1, z1);
	    	GL11.glTexCoord2f(0, 0);
	    	GL11.glVertex3f(x0, y1, z0);
	    	break;
	    case Bottom:
	    	GL11.glTexCoord2f(0, 1);
	    	GL11.glVertex3f(x0, y0, z0);
	    	GL11.glTexCoord2f(0, 0);
	    	GL11.glVertex3f(x0, y0, z1);
	    	GL11.glTexCoord2f(1, 0);
	    	GL11.glVertex3f(x1, y0, z1);
	    	GL11.glTexCoord2f(1, 1);
	    	GL11.glVertex3f(x1, y0, z0);
	    	break;
	    case Left:
	    	GL11.glTexCoord2f(0, 0);
	    	GL11.glVertex3f(x0, y1, z0);
	    	GL11.glTexCoord2f(1, 0);
	    	GL11.glVertex3f(x0, y1, z1);
	    	GL11.glTexCoord2f(1, 1);
	    	GL11.glVertex3f(x0, y0, z1);
	    	GL11.glTexCoord2f(0, 1);
	    	GL11.glVertex3f(x0, y0, z0);
	    	break;
	    case Right:
	    	GL11.glTexCoord2f(0, 0);
	    	GL11.glVertex3f(x1, y1, z1);
	    	GL11.glTexCoord2f(1, 0);
	    	GL11.glVertex3f(x1, y1, z0);
	    	GL11.glTexCoord2f(1, 1);
	    	GL11.glVertex3f(x1, y0, z0);
	    	GL11.glTexCoord2f(0, 1);
	    	GL11.glVertex3f(x1, y0, z1);
	    	break;
	    }
	    GL11.glEnd();
	}
	
	public static void renderLine(int x, int y, int z) {
		float x0 = x + 0.0F;
		float x1 = x + 1.0F;
		float y0 = y + 0.0F;
		float y1 = y + 1.0F;
		float z0 = z + 0.0F;
		float z1 = z + 1.0F;
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(x0, y1, z1);
		GL11.glVertex3f(x1, y1, z1);
		GL11.glVertex3f(x1, y0, z1);
		GL11.glVertex3f(x0, y0, z1);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(x0, y1, z0);
		GL11.glVertex3f(x0, y0, z0);
		GL11.glVertex3f(x1, y0, z0);
		GL11.glVertex3f(x1, y1, z0);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(x1, y1, z0);
		GL11.glVertex3f(x1, y1, z1);
		GL11.glVertex3f(x0, y1, z1);
		GL11.glVertex3f(x0, y1, z0);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(x0, y0, z0);
		GL11.glVertex3f(x0, y0, z1);
		GL11.glVertex3f(x1, y0, z1);
		GL11.glVertex3f(x1, y0, z0);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(x0, y1, z0);
		GL11.glVertex3f(x0, y1, z1);
		GL11.glVertex3f(x0, y0, z1);
		GL11.glVertex3f(x0, y0, z0);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(x1, y1, z1);
		GL11.glVertex3f(x1, y1, z0);
		GL11.glVertex3f(x1, y0, z0);
		GL11.glVertex3f(x1, y0, z1);
		GL11.glEnd();
	}
	
	public static AABB getAABB(int x, int y, int z) {
		return new AABB(x, y, z, x + 1, y + 1, z + 1);
	}
	
	public void tick(Level level, int x, int y, int z, Random random) {
		
	}
	
	public void destroy(Level level, int x, int y, int z, ParticleEngine particleEngine) {
		int SD = PARTICLE_COUNT;
		for (int xx = 0; xx < SD; xx++)
			for (int yy = 0; yy < SD; yy++)
				for (int zz = 0; zz < SD; zz++) {
					float xp = x + (xx + 0.5F) / SD;
					float yp = y + (yy + 0.5F) / SD;
					float zp = z + (zz + 0.5F) / SD;
					particleEngine.add(new Particle(level, xp, yp, zp, xp - x - 0.5F, yp - y - 0.5F, zp - z - 0.5F, this.tex));
				}
	}
}
