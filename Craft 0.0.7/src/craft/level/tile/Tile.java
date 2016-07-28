package craft.level.tile;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import craft.level.Level;
import craft.level.tile.LiquidTile.LiquidType;
import craft.particle.Particle;
import craft.particle.ParticleEngine;
import craft.phys.AABB;
import craft.renderer.Texture;

public class Tile {
	public static final Tile[] tiles = new Tile[256];
	public static final Tile air = new Tile(0, 0, 0, false, true);
	public static final Tile stone = new Tile(1, 4, 100, true, false);
	public static final GrassTile grass = new GrassTile(2, 2, 20, true, false);
	public static final SoilTile soil = new SoilTile(3, 3, 20, true, false);
	public static final Tile cobblestone = new Tile(4, 5, 100, true, false);
	public static final Tile bedrock = new Tile(5, 6, 100000, true, false);
	public static final Tile planks = new Tile(6, 7, 70, true, false);
	public static final Tile oak_planks = new Tile(7, 10, 70, true, false);
	public static final TreeTile trunk = new TreeTile(8, 9, 70, true, false);
	public static final LeafTile leaf = new LeafTile(9, 11, 10, true, true);
	public static final EntityTile sand = new EntityTile(10, 12, 15, true, true);
	public static final LiquidTile water = new LiquidTile(11, 13, 14, 15, false, true, LiquidType.Water);
	public static final ClamLiquidTile calmWater = new ClamLiquidTile(12, 13, 14, 15, false, true, LiquidType.Water);
	public static final LiquidTile lava = new LiquidTile(13, 15, 15, 15, false, true, LiquidType.Lava);
	public static final ClamLiquidTile clamLava = new ClamLiquidTile(14, 15, 15, 15, false, true, LiquidType.Lava);
	public static final int PARTICLE_COUNT = 4;
	public static final int TICK_COUNT = 4;
	public final int tex;
	public final int id;
	public final int destroyTime;
	public final boolean soildTile, lightTile;
	protected static int dx, dy, dz;
	protected static int time = 0;
	
	protected Tile(int id, int tex, int destroyTime, boolean soildTile, boolean lightTile) {
		this.tex=tex;
		this.id=id;
		this.destroyTime = destroyTime;
		this.soildTile = soildTile;
		this.lightTile = lightTile;
		tiles[id] = this;
	}
	
	protected int getTexture(Face face) {
	    return this.tex;
	}
	
	public void render(Level level, int x, int y, int z) {
		float c1 = 1.0F;
	    float c2 = 0.8F;
	    float c3 = 0.5F;
	    float c4 = 0.6F;
	    float b = 0.0F;
	    if(shouldRenderFace(level, x - 1, y, z)) {
	    	b =  getBrightness(level, x - 1, y, z) * c4;
	    	GL11.glColor4f(b, b, b, 1.0F);
	    	renderFace(x, y, z, Face.Left);
	    }
	    if(shouldRenderFace(level, x + 1, y, z)) {
	    	b =  getBrightness(level, x + 1, y, z) * c4;
	    	GL11.glColor4f(b, b, b, 1.0F);
	    	renderFace(x, y, z, Face.Right);
	    }
	    if(shouldRenderFace(level, x, y - 1, z)) {
	    	b =  getBrightness(level, x, y - 1, z) * c3;
	    	GL11.glColor4f(b, b, b, 1.0F);
	    	renderFace(x, y, z, Face.Bottom);
	    }
	    if(shouldRenderFace(level, x, y + 1, z)) {
	    	b =  getBrightness(level, x, y + 1, z) * c1;
	    	GL11.glColor4f(b, b, b, 1.0F);
	    	renderFace(x, y, z, Face.Top);
	    }
	    if(shouldRenderFace(level, x, y, z - 1)) {
	    	b =  getBrightness(level, x, y, z - 1) * c2;
	    	GL11.glColor4f(b, b, b, 1.0F);
	    	renderFace(x, y, z, Face.Back);
	    }
	    if(shouldRenderFace(level, x, y, z + 1)) {
	    	b =  getBrightness(level, x, y, z + 1) * c2;
	    	GL11.glColor4f(b, b, b, 1.0F);
	    	renderFace(x, y, z, Face.Front);
	    }
	}
	
	protected boolean shouldRenderFace(Level level, int x, int y, int z) {
	    if (level.isLightBlock(x, y, z)) 
	    	return true; 
	    return false;
	  }
	
	protected float getBrightness(Level level, int x, int y, int z) {
		return level.getBrightness(x, y, z);
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
	
	public boolean destroy(Level level, int x, int y, int z, ParticleEngine particleEngine, boolean needTime) {
		if(x != dx || y != dy || z != dz) {
			dx = x;
			dy = y;
			dz = z;
			time = 0;
		}
		time++;
		//System.out.println("Destorying Tile: X=" + dx + " Y=" + dy + " Z=" + dz + " Percentage="+(int)(time* 100.0F / destroyTime)  + "%");
		if(time < destroyTime && needTime)
			return false;
		time = 0;
		level.setBlock(x, y, z, air.id);
		createParticles(level, x, y, z, particleEngine);
		return true;
	}
	
	protected void createParticles(Level level, int x, int y, int z, ParticleEngine particleEngine) {
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
	
	public static void cancelDestroy() {
		time = 0;
	}
	
	public void renderCrack(Level level) {
		if((double)time / destroyTime < 0.001D)
			return;
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.5F);
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Texture.bind(16 + (int)(10 * (time - 1) / destroyTime));
		float c1 = 1.0F;
	    float c2 = 1.0F;
	    float c3 = 1.0F;
	    float a = 0.8F;
	    if(shouldRenderFace(level, dx - 1, dy, dz)) {
	    	GL11.glColor4f(c2, c2, c2, a);
	    	renderFaceNoTexture(dx, dy, dz, Face.Left);
	    }
	    if(shouldRenderFace(level, dx + 1, dy, dz)) {
	    	GL11.glColor4f(c2, c2, c2, a);
	    	renderFaceNoTexture(dx, dy, dz, Face.Right);
	    }
	    if(shouldRenderFace(level, dx, dy - 1, dz)) {
	    	GL11.glColor4f(c3, c3, c3, a);
	    	renderFaceNoTexture(dx, dy, dz, Face.Bottom);
	    }
	    if(shouldRenderFace(level, dx, dy + 1, dz)) {
	    	GL11.glColor4f(c1, c1, c1, a);
	    	renderFaceNoTexture(dx, dy, dz, Face.Top);
	    }
	    if(shouldRenderFace(level, dx, dy, dz - 1)) {
	    	GL11.glColor4f(c2, c2, c2, a);
	    	renderFaceNoTexture(dx, dy, dz, Face.Back);
	    }
	    if(shouldRenderFace(level, dx, dy, dz + 1)) {
	    	GL11.glColor4f(c2, c2, c2, a);
	    	renderFaceNoTexture(dx, dy, dz, Face.Front);
	    }
	    Texture.bind(-1);
	    GL11.glDisable(GL11.GL_ALPHA_TEST);
	    GL11.glDisable(GL11.GL_BLEND);
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	public void neighborChanged(Level level, int x, int y, int z, int block) {
		
	}
	
}
