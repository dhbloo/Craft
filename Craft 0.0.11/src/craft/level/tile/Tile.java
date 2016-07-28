package craft.level.tile;

import java.util.Random;

import craft.level.Level;
import craft.level.Brightness.Brightness;
import craft.level.tile.LiquidTile.LiquidType;
import craft.particle.Particle;
import craft.particle.ParticleEngine;
import craft.phys.AABB;
import craft.renderer.Tesselator;

public class Tile {
	public static final Tile[] tiles = new Tile[256];
	public static final Tile air = new Tile(0, 0, 0, false, true, 0, 0.00F);
	public static final Tile stone = new Tile(1, 3, 100, true, false, 0, 1.0F);
	public static final GrassTile grass = new GrassTile(2, 1, 20, true, false, 0, 1.0F);
	public static final Tile soil = new Tile(3, 2, 20, true, false, 0, 1.0F);
	public static final Tile cobblestone = new Tile(4, 4, 100, true, false, 0, 1.0F);
	public static final Tile bedrock = new Tile(5, 5, 100000, true, false, 0, 1.0F);
	public static final Tile planks = new Tile(6, 6, 70, true, false, 0, 1.0F);
	public static final Tile oak_planks = new Tile(7, 7, 70, true, false, 0, 1.0F);
	public static final TreeTile trunk = new TreeTile(8, 9, 70, true, false, 0, 1.0F);
	public static final LeafTile leaf = new LeafTile(9, 10, 10, true, true, 0, 0.05F);
	public static final EntityTile sand = new EntityTile(10, 11, 15, true, true, 0, 1.0F);
	public static final LiquidTile water = new LiquidTile(11, 12, 13, 15, false, true, 0, 0.5F, LiquidType.Water);
	public static final CalmLiquidTile calmWater = new CalmLiquidTile(12, 12, 13, 15, false, true, 0, 0.5F, LiquidType.Water);
	public static final LiquidTile lava = new LiquidTile(13, 14, 14, 15, false, true, 0, 0.5F, LiquidType.Lava);
	public static final CalmLiquidTile calmLava = new CalmLiquidTile(14, 14, 14, 15, false, true, 0, 0.5F, LiquidType.Lava);
	public static final Tile grassBush = new Grass(15, 15, 2, false, true, 0, 0.05F);
	
	public static final int PARTICLE_COUNT = 4;
	public static final int TICK_COUNT = 4;
	public final int tex;
	public final int id;
	public final int destroyTime;
	public final boolean soildTile, lightTile;
	public final byte brightness;
	public final float brightnessDecay;
	
	protected Tile(int id, int tex, int destroyTime, boolean soildTile, boolean lightTile, int brightness, float brightnessDecay) {
		this.tex=tex;
		this.id=id;
		this.destroyTime = destroyTime;
		this.soildTile = soildTile;
		this.lightTile = lightTile;
		this.brightness = (byte) brightness;
		this.brightnessDecay = brightnessDecay;
		tiles[id] = this;
	}
	
	protected int getTexture(Face face) {
	    return this.tex;
	}
	
	public void render(Tesselator t, Level level, int x, int y, int z, int layer) {
		float c1 = 1.0F;
	    float c2 = 0.8F;
	    float c3 = 0.5F;
	    float c4 = 0.6F;
	    float b = 0.0F;
	    if(shouldRenderFace(level, x - 1, y, z, layer)) {
	    	b =  getBrightness(level, x - 1, y, z) * c4;
	    	t.color(b, b, b);
	    	renderFace(t, x, y, z, Face.Left);
	    }
	    if(shouldRenderFace(level, x + 1, y, z, layer)) {
	    	b =  getBrightness(level, x + 1, y, z) * c4;
	    	t.color(b, b, b);
	    	renderFace(t, x, y, z, Face.Right);
	    }
	    if(shouldRenderFace(level, x, y - 1, z, layer)) {
	    	b =  getBrightness(level, x, y - 1, z) * c3;
	    	t.color(b, b, b);
	    	renderFace(t, x, y, z, Face.Bottom);
	    }
	    if(shouldRenderFace(level, x, y + 1, z, layer)) {
	    	b =  getBrightness(level, x, y + 1, z) * c1;
	    	t.color(b, b, b);
	    	renderFace(t, x, y, z, Face.Top);
	    }
	    if(shouldRenderFace(level, x, y, z - 1, layer)) {
	    	b =  getBrightness(level, x, y, z - 1) * c2;
	    	t.color(b, b, b);
	    	renderFace(t, x, y, z, Face.Back);
	    }
	    if(shouldRenderFace(level, x, y, z + 1, layer)) {
	    	b =  getBrightness(level, x, y, z + 1) * c2;
	    	t.color(b, b, b);
	    	renderFace(t, x, y, z, Face.Front);
	    }
	}
	
	protected boolean shouldRenderFace(Level level, int x, int y, int z, int layer) {
	    if (level.isLightBlock(x, y, z) && layer == 0)
	    	return true; 
	    return false;
	  }
	
	protected float getBrightness(Level level, int x, int y, int z) {
		return (float)level.getBrightness(x, y, z) / Brightness.MAX;
	}
	
	protected void renderFace(Tesselator t, int x, int y, int z, Face face) {
		int tex = getTexture(face);
		
		int xt = tex % 16 * 16;
	    int yt = tex / 16 * 16;
	    float u0 = xt / 256.0F;
	    float u1 = (xt + 15.99F) / 256.0F;
	    float v0 = yt / 256.0F;
	    float v1 = (yt + 15.99F) / 256.0F;
	    
		float x0 = x + 0.0F;
	    float x1 = x + 1.0F;
	    float y0 = y + 0.0F;
	    float y1 = y + 1.0F;
	    float z0 = z + 0.0F;
	    float z1 = z + 1.0F;
	    switch(face) {
	    case Front:
	    	t.vertexUV(x0, y1, z1, u0, v0);
	        t.vertexUV(x0, y0, z1, u0, v1);
	        t.vertexUV(x1, y0, z1, u1, v1);
	        t.vertexUV(x1, y1, z1, u1, v0);
	    	break;
	    case Back:
	    	t.vertexUV(x0, y1, z0, u1, v0);
	        t.vertexUV(x1, y1, z0, u0, v0);
	        t.vertexUV(x1, y0, z0, u0, v1);
	        t.vertexUV(x0, y0, z0, u1, v1);
	    	break;
	    case Top:
	    	t.vertexUV(x1, y1, z1, u1, v1);
	        t.vertexUV(x1, y1, z0, u1, v0);
	        t.vertexUV(x0, y1, z0, u0, v0);
	        t.vertexUV(x0, y1, z1, u0, v1);
	    	break;
	    case Bottom:
	    	t.vertexUV(x0, y0, z1, u0, v1);
	        t.vertexUV(x0, y0, z0, u0, v0);
	        t.vertexUV(x1, y0, z0, u1, v0);
	        t.vertexUV(x1, y0, z1, u1, v1);
	    	break;
	    case Left:
	    	t.vertexUV(x0, y1, z1, u1, v0);
	        t.vertexUV(x0, y1, z0, u0, v0);
	        t.vertexUV(x0, y0, z0, u0, v1);
	        t.vertexUV(x0, y0, z1, u1, v1);
	    	break;
	    case Right:
	    	t.vertexUV(x1, y0, z1, u0, v1);
	        t.vertexUV(x1, y0, z0, u1, v1);
	        t.vertexUV(x1, y1, z0, u1, v0);
	        t.vertexUV(x1, y1, z1, u0, v0);
	    	break;
	    }
	}
	
	public static void renderFaceNoTexture(Tesselator t, int x, int y, int z, Face face) {
		float x0 = x + 0.0F;
	    float x1 = x + 1.0F;
	    float y0 = y + 0.0F;
	    float y1 = y + 1.0F;
	    float z0 = z + 0.0F;
	    float z1 = z + 1.0F;
	    switch(face) {
	    case Front:
	    	t.vertex(x0, y1, z1);
	        t.vertex(x0, y0, z1);
	        t.vertex(x1, y0, z1);
	        t.vertex(x1, y1, z1);
	    	break;
	    case Back:
	    	t.vertex(x0, y1, z0);
	        t.vertex(x1, y1, z0);
	        t.vertex(x1, y0, z0);
	        t.vertex(x0, y0, z0);
	    	break;
	    case Top:
	    	t.vertex(x1, y1, z1);
	        t.vertex(x1, y1, z0);
	        t.vertex(x0, y1, z0);
	        t.vertex(x0, y1, z1);
	    	break;
	    case Bottom:
	    	t.vertex(x0, y0, z1);
	        t.vertex(x0, y0, z0);
	        t.vertex(x1, y0, z0);
	        t.vertex(x1, y0, z1);
	    	break;
	    case Left:
	    	t.vertex(x0, y1, z1);
	        t.vertex(x0, y1, z0);
	        t.vertex(x0, y0, z0);
	        t.vertex(x0, y0, z1);
	    	break;
	    case Right:
	    	t.vertex(x1, y0, z1);
	        t.vertex(x1, y0, z0);
	        t.vertex(x1, y1, z0);
	        t.vertex(x1, y1, z1);
	    	break;
	    }
	}

	public static void renderFaceWithTexture(Tesselator t, int x, int y, int z, Face face, int tex) {
		int xt = tex % 16 * 16;
	    int yt = tex / 16 * 16;
	    float u0 = xt / 256.0F;
	    float u1 = (xt + 15.99F) / 256.0F;
	    float v0 = yt / 256.0F;
	    float v1 = (yt + 15.99F) / 256.0F;
	    
		float x0 = x + 0.0F;
	    float x1 = x + 1.0F;
	    float y0 = y + 0.0F;
	    float y1 = y + 1.0F;
	    float z0 = z + 0.0F;
	    float z1 = z + 1.0F;
	    switch(face) {
	    case Front:
	    	t.vertexUV(x0, y1, z1, u0, v0);
	        t.vertexUV(x0, y0, z1, u0, v1);
	        t.vertexUV(x1, y0, z1, u1, v1);
	        t.vertexUV(x1, y1, z1, u1, v0);
	    	break;
	    case Back:
	    	t.vertexUV(x0, y1, z0, u1, v0);
	        t.vertexUV(x1, y1, z0, u0, v0);
	        t.vertexUV(x1, y0, z0, u0, v1);
	        t.vertexUV(x0, y0, z0, u1, v1);
	    	break;
	    case Top:
	    	t.vertexUV(x1, y1, z1, u1, v1);
	        t.vertexUV(x1, y1, z0, u1, v0);
	        t.vertexUV(x0, y1, z0, u0, v0);
	        t.vertexUV(x0, y1, z1, u0, v1);
	    	break;
	    case Bottom:
	    	t.vertexUV(x0, y0, z1, u0, v1);
	        t.vertexUV(x0, y0, z0, u0, v0);
	        t.vertexUV(x1, y0, z0, u1, v0);
	        t.vertexUV(x1, y0, z1, u1, v1);
	    	break;
	    case Left:
	    	t.vertexUV(x0, y1, z1, u1, v0);
	        t.vertexUV(x0, y1, z0, u0, v0);
	        t.vertexUV(x0, y0, z0, u0, v1);
	        t.vertexUV(x0, y0, z1, u1, v1);
	    	break;
	    case Right:
	    	t.vertexUV(x1, y0, z1, u0, v1);
	        t.vertexUV(x1, y0, z0, u1, v1);
	        t.vertexUV(x1, y1, z0, u1, v0);
	        t.vertexUV(x1, y1, z1, u0, v0);
	    	break;
	    }
	}

	public void renderTexture(Tesselator t, Level level, int x, int y, int z, int tex, int layer) {
		float c1 = 1.0F;
	    float c2 = 0.8F;
	    float c3 = 0.6F;
	    if(shouldRenderFace(level, x - 1, y, z, layer)) {
	    	t.color(c2, c2, c2);
	    	renderFaceWithTexture(t, x, y, z, Face.Left, tex);
	    }
	    if(shouldRenderFace(level, x + 1, y, z, layer)) {
	    	t.color(c2, c2, c2);
	    	renderFaceWithTexture(t, x, y, z, Face.Right, tex);
	    }
	    if(shouldRenderFace(level, x, y - 1, z, layer)) {
	    	t.color(c3, c3, c3);
	    	renderFaceWithTexture(t, x, y, z, Face.Bottom, tex);
	    }
	    if(shouldRenderFace(level, x, y + 1, z, layer)) {
	    	t.color(c1, c1, c1);
	    	renderFaceWithTexture(t, x, y, z, Face.Top, tex);
	    }
	    if(shouldRenderFace(level, x, y, z - 1, layer)) {
	    	t.color(c2, c2, c2);
	    	renderFaceWithTexture(t, x, y, z, Face.Back, tex);
	    }
	    if(shouldRenderFace(level, x, y, z + 1, layer)) {
	    	t.color(c2, c2, c2);
	    	renderFaceWithTexture(t, x, y, z, Face.Front, tex);
	    }
	}
	
	public static AABB getAABB(int x, int y, int z) {
		return new AABB(x, y, z, x + 1, y + 1, z + 1);
	}
	
	public void tick(Level level, int x, int y, int z, Random random) {
		
	}
	
	public void destroy(Level level, int x, int y, int z, ParticleEngine particleEngine) {
		level.setBlock(x, y, z, air.id);
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
	
	public void neighborChanged(Level level, int x, int y, int z, int block) {
		
	}
	
}
