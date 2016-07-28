package craft.level.tile;

import craft.Player;
import craft.level.Level;
import craft.particle.ParticleEngine;
import craft.renderer.Tesselator;

public class LiquidTile extends EntityTile implements Cloneable{
	public static final int WATER_UPDATE_TIME = 25;
	public static final int LAVA_UPDATE_TIME = 50;
	public static final int WATER_SPREAD_SPEED = 2;
	public static final int LAVA_SPREAD_SPEED = 0;
	protected static final float H = -0.1F;
	protected final int cover;
	protected LiquidType type;
	private int lastUpdate = 0;
	public enum LiquidType{
		Water,Lava;
		int getSpreadSpeed() {
			if(this == Water)
				return WATER_SPREAD_SPEED;
			else
				return LAVA_SPREAD_SPEED;
		}
		int getUpdateTime() {
			if(this == Water)
				return WATER_UPDATE_TIME;
			else
				return LAVA_UPDATE_TIME;
		}
	}

	protected LiquidTile(int id, int tex, int cover, int destroyTime, boolean soildTile, boolean lightTile, int brightness, float brightnessDecay, LiquidType type) {
		super(id, tex, destroyTime, soildTile, lightTile, brightness, brightnessDecay);
		this.cover = cover;
		this.type = type;
		this.updating = true;
	}

	public void render(Tesselator t, Level level, int x, int y, int z, int layer) {
		float c1 = 1.0F;
		float c2 = 0.9F;
		float c3 = 0.6F;
		float c4 = 0.8F;
		float b = 0.0F;
		float h = shouldRenderFace(level, x, y + 1, z, layer) ? H : 0.0F;
		if(shouldRenderFace(level, x - 1, y, z, layer)) {
			b =  getBrightness(level, x - 1, y, z) * c4;
			t.color(b, b, b);
			renderFace(t, x, y, z, h, Face.Left);
			renderCover(t, x, y, z, h, Face.Left);
		}
		if(shouldRenderFace(level, x + 1, y, z, layer)) {
			b =  getBrightness(level, x + 1, y, z) * c4;
			t.color(b, b, b);
			renderFace(t, x, y, z, h, Face.Right);
			renderCover(t, x, y, z, h, Face.Right);
		}
		if(shouldRenderFace(level, x, y - 1, z, layer)) {
			b =  getBrightness(level, x, y - 1, z) * c3;
			t.color(b, b, b);
			renderFace(t, x, y, z, 1.0F, Face.Bottom);
			renderCover(t, x, y, z, h, Face.Bottom);
		}
		if(shouldRenderFace(level, x, y + 1, z, layer) || !level.isLightBlock(x, y + 1, z)) {
			b =  getBrightness(level, x, y + 1, z) * c1;
			t.color(b, b, b);
			renderFace(t, x, y, z, h, Face.Top);
			renderCover(t, x, y, z, h, Face.Top);
		}
		if(shouldRenderFace(level, x, y, z - 1, layer)) {
			b =  getBrightness(level, x, y, z - 1) * c2;
			t.color(b, b, b);
			renderFace(t, x, y, z, h, Face.Back);
			renderCover(t, x, y, z, h, Face.Back);
		}
		if(shouldRenderFace(level, x, y, z + 1, layer)) {
			b =  getBrightness(level, x, y, z + 1) * c2;
			t.color(b, b, b);
			renderFace(t, x, y, z, h, Face.Front);
			renderCover(t, x, y, z, h, Face.Front);
		}
	}

	protected void render(Tesselator t, Level level, float a) {
	}

	protected boolean shouldRenderFace(Level level, int x, int y, int z, int layer) {
		if (type == LiquidType.Lava)
			return super.shouldRenderFace(level, x, y, z, 0);
		if (!level.isBlock(x, y, z))
			return false;
		if (level.isLightBlock(x, y, z) && (type == LiquidType.Water) && !(Tile.tiles[level.getBlock(x, y, z)] instanceof LiquidTile) && layer == 1)
			return true;
		return false;
	}

	protected void renderFace(Tesselator t, int x, int y, int z, float h, Face face) {
		int tex = getTexture(face);
		
		int xt = tex % 16 * 16;
	    int yt = tex / 16 * 16;
	    float u0 = xt / 256.0F;
	    float u1 = (xt + 16.0F) / 256.0F;
	    float v0 = yt / 256.0F;
	    float v1 = (yt + 16.0F) / 256.0F;
	    
		float x0 = x + 0.0F;
		float x1 = x + 1.0F;
		float y0 = y + h;
		float y1 = y + h + 1.0F;
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

	protected void renderCover(Tesselator t, int x, int y, int z, float h, Face face) {
		int xt = cover % 16 * 16;
	    int yt = cover / 16 * 16;
	    float u0 = xt / 256.0F;
	    float u1 = (xt + 16.0F) / 256.0F;
	    float v0 = yt / 256.0F;
	    float v1 = (yt + 16.0F) / 256.0F;
		
		float x0 = x + 0.0F;
		float x1 = x + 1.0F;
		float y0 = y + h;
		float y1 = y + h + 1.0F;
		float z0 = z + 0.0F;
		float z1 = z + 1.0F;
		switch(face) {
	    case Front:
	    	t.vertexUV(x1, y1, z1, u1, v0);
	        t.vertexUV(x1, y0, z1, u1, v1);
	        t.vertexUV(x0, y0, z1, u0, v1);
	        t.vertexUV(x0, y1, z1, u0, v0);
	    	break;
	    case Back:
	    	t.vertexUV(x0, y0, z0, u1, v1);
	        t.vertexUV(x1, y0, z0, u0, v1);
	        t.vertexUV(x1, y1, z0, u0, v0);
	        t.vertexUV(x0, y1, z0, u1, v0);
	    	break;
	    case Top:
	    	t.vertexUV(x0, y1, z1, u0, v1);
	        t.vertexUV(x0, y1, z0, u0, v0);
	        t.vertexUV(x1, y1, z0, u1, v0);
	        t.vertexUV(x1, y1, z1, u1, v1);
	    	break;
	    case Bottom:
	    	t.vertexUV(x1, y0, z1, u1, v1);
	        t.vertexUV(x1, y0, z0, u1, v0);
	        t.vertexUV(x0, y0, z0, u0, v0);
	        t.vertexUV(x0, y0, z1, u0, v1);
	    	break;
	    case Left:
	    	t.vertexUV(x0, y0, z1, u1, v1);
	        t.vertexUV(x0, y0, z0, u0, v1);
	        t.vertexUV(x0, y1, z0, u0, v0);
	        t.vertexUV(x0, y1, z1, u1, v0);
	    	break;
	    case Right:
	    	t.vertexUV(x1, y1, z1, u0, v0);
	        t.vertexUV(x1, y1, z0, u1, v0);
	        t.vertexUV(x1, y0, z0, u1, v1);
	        t.vertexUV(x1, y0, z1, u0, v1);
	    	break;
	    }
	}

	public void destroy(Level level, int x, int y, int z, ParticleEngine particleEngine) {
		level.setBlock(x, y, z, air.id);
	}

	protected void createParticles(Level level, int x, int y, int z, ParticleEngine particleEngine) {

	}

	protected boolean shouldRender() {
		return true;
	}

	protected void update(Level level, Player player) {
		if(lastUpdate++ >= type.getUpdateTime()) {
			yo = y;
			updating = updateLiquid(level, x, (int)y, z, 0);
			lastUpdate = 0;
		}
	}

	private boolean updateLiquid(Level level, int x, int y, int z, int depth) {
		boolean hasChanged = false;
		int fall = 0;
		while(!level.isSolidBlock(x, --y, z)) {
			boolean change = level.setBlock(x, y, z, id);
			if (change)
				hasChanged = true;
			if ((!change) || fall > type.getSpreadSpeed()) 
				break;
			fall++;
		}
		y++;
		if((type == LiquidType.Water || (!hasChanged)) && level.isSolidBlock(x, y - 1, z)) {
			hasChanged |= checkLiquid(level, x - 1, y, z, depth);
			hasChanged |= checkLiquid(level, x + 1, y, z, depth);
			hasChanged |= checkLiquid(level, x, y, z - 1, depth);
			hasChanged |= checkLiquid(level, x, y, z + 1, depth);
		}
		if(!hasChanged) {
			//level.setBlockDirectly(x, y, z, calmWater.id);
			remove(this);
		}
		return hasChanged;
	}

	private boolean checkLiquid(Level level, int x, int y, int z, int depth) {
		boolean hasChanged = false;
		if (!level.isSolidBlock(x, y, z) && !(tiles[level.getBlock(x, y, z)] instanceof LiquidTile)) {
			boolean changed = level.setBlock(x, y, z, id);
			if ((changed) && (depth < type.getSpreadSpeed())) {
				hasChanged |= updateLiquid(level, x, y, z, depth + 1);
			}
		}
		return hasChanged;
	}

	@Override
	public void neighborChanged(Level level, int x, int y, int z, int block) {
		boolean hasAirNeighbor = false;
		if (level.getBlock(x - 1, y, z) == air.id) hasAirNeighbor = true;
		if (level.getBlock(x + 1, y, z) == air.id) hasAirNeighbor = true;
		if (level.getBlock(x, y - 1, z) == air.id) hasAirNeighbor = true;
		if (level.getBlock(x, y, z - 1) == air.id) hasAirNeighbor = true;
		if (level.getBlock(x, y, z + 1) == air.id) hasAirNeighbor = true;
		if (hasAirNeighbor)
			add(x, y, z);
		if (tiles[block] instanceof LiquidTile) {
			if (type == LiquidType.Water && ((LiquidTile)tiles[block]).getType() == LiquidType.Lava)
				level.setBlockDirectly(x, y, z, cobblestone.id);
			if (type == LiquidType.Lava && ((LiquidTile)tiles[block]).getType() == LiquidType.Water)
				level.setBlockDirectly(x, y, z, cobblestone.id);
		}

	}

	public LiquidType getType() {
		return this.type;
	}
	
	public boolean getFog() {
		return (type == LiquidType.Water);
	}
	
}

