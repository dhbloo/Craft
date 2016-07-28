package craft.world.tile;

import java.util.Random;

import craft.phys.AABB;
import craft.renderer.Tesselator;
import craft.world.World;
import craft.world.tile.tileEntity.LiquidTileEntity;

public class LiquidTile extends Tile implements Cloneable{
	public static final int WATER_UPDATE_TIME = 15;
	public static final int LAVA_UPDATE_TIME = 50;
	public static final int WATER_SPREAD_SPEED = 2;
	public static final int LAVA_SPREAD_SPEED = 0;
	protected static final float H = -0.1F;
	protected final int cover;
	protected LiquidType type;
	
	public enum LiquidType{
		Water,Lava;
		public int getSpreadSpeed() {
			if(this == Water)
				return WATER_SPREAD_SPEED;
			else
				return LAVA_SPREAD_SPEED;
		}
		public int getUpdateTime() {
			if(this == Water)
				return WATER_UPDATE_TIME;
			else
				return LAVA_UPDATE_TIME;
		}
	}

	public LiquidTile(int id, int tex, int cover, LiquidType type) {
		super(id, tex);
		this.cover = cover;
		this.type = type;
		setHardness(15);
		setOpacity(0.2F);
		setAABB(new AABB(0.0F, 0.0F, 0.0F, 1.0F, 0.9F, 1.0F));
	}

	public void render(Tesselator t, World world, int x, int y, int z, int layer) {
		float c1 = 1.0F;
		float c2 = 0.9F;
		float c3 = 0.6F;
		float c4 = 0.8F;
		float b = 0.0F;
		float h = shouldRenderFace(world, x, y + 1, z, layer) ? H : 0.0F;
		if(shouldRenderFace(world, x - 1, y, z, layer)) {
			b =  getBrightness(world, x - 1, y, z) * c4;
			t.color(b, b, b);
			renderFace(t, x, y, z, h, Face.Left);
			renderCover(t, x, y, z, h, Face.Left);
		}
		if(shouldRenderFace(world, x + 1, y, z, layer)) {
			b =  getBrightness(world, x + 1, y, z) * c4;
			t.color(b, b, b);
			renderFace(t, x, y, z, h, Face.Right);
			renderCover(t, x, y, z, h, Face.Right);
		}
		if(shouldRenderFace(world, x, y - 1, z, layer)) {
			b =  getBrightness(world, x, y - 1, z) * c3;
			t.color(b, b, b);
			renderFace(t, x, y, z, h, Face.Bottom);
			renderCover(t, x, y, z, h, Face.Bottom);
		}
		if(shouldRenderFace(world, x, y + 1, z, layer) || !world.isLightBlock(x, y + 1, z)) {
			b =  getBrightness(world, x, y + 1, z) * c1;
			t.color(b, b, b);
			renderFace(t, x, y, z, h, Face.Top);
			renderCover(t, x, y, z, h, Face.Top);
		}
		if(shouldRenderFace(world, x, y, z - 1, layer)) {
			b =  getBrightness(world, x, y, z - 1) * c2;
			t.color(b, b, b);
			renderFace(t, x, y, z, h, Face.Back);
			renderCover(t, x, y, z, h, Face.Back);
		}
		if(shouldRenderFace(world, x, y, z + 1, layer)) {
			b =  getBrightness(world, x, y, z + 1) * c2;
			t.color(b, b, b);
			renderFace(t, x, y, z, h, Face.Front);
			renderCover(t, x, y, z, h, Face.Front);
		}
	}

	protected boolean shouldRenderFace(World world, int x, int y, int z, int layer) {
		if (type == LiquidType.Lava)
			return super.shouldRenderFace(world, x, y, z, 0);
		if (!world.isBlock(x, y, z))
			return false;
		if (world.isLightBlock(x, y, z) && (type == LiquidType.Water) && !(Tile.tiles[world.getBlock(x, y, z)] instanceof LiquidTile) && layer == 1)
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

	protected boolean shouldRender() {
		return true;
	}

	@Override
	public void update(World world, int x, int y, int z, Random random) {
		updateLiquid(world, x, y, z, 0);
	}

	private boolean updateLiquid(World world, int x, int y, int z, int depth) {
		boolean hasChanged = false;
		int fall = 0;
		while(!world.isSolidBlock(x, --y, z)) {
			boolean change = world.setBlock(x, y, z, id);
			if (change)
				hasChanged = true;
			if ((!change) || fall > type.getSpreadSpeed()) 
				break;
			fall++;
		}
		y++;
		if((type == LiquidType.Water || (!hasChanged)) && world.isSolidBlock(x, y - 1, z)) {
			hasChanged |= checkLiquid(world, x - 1, y, z, depth);
			hasChanged |= checkLiquid(world, x + 1, y, z, depth);
			hasChanged |= checkLiquid(world, x, y, z - 1, depth);
			hasChanged |= checkLiquid(world, x, y, z + 1, depth);
		}
		if(!hasChanged) {
			//world.setBlockDirectly(x, y, z, calmWater.id);
			world.removeTileEntity(x, y, z);
		}
		return hasChanged;
	}

	private boolean checkLiquid(World world, int x, int y, int z, int depth) {
		boolean hasChanged = false;
		if (!world.isSolidBlock(x, y, z) && !(tiles[world.getBlock(x, y, z)] instanceof LiquidTile)) {
			boolean changed = world.setBlock(x, y, z, id);
			if ((changed) && (depth < type.getSpreadSpeed())) {
				hasChanged |= updateLiquid(world, x, y, z, depth + 1);
			}
		}
		return hasChanged;
	}

	@Override
	public void neighborChanged(World world, int x, int y, int z, int block) {
		boolean hasAirNeighbor = false;
		if (world.getBlock(x - 1, y, z) == air.id) hasAirNeighbor = true;
		if (world.getBlock(x + 1, y, z) == air.id) hasAirNeighbor = true;
		if (world.getBlock(x, y - 1, z) == air.id) hasAirNeighbor = true;
		if (world.getBlock(x, y, z - 1) == air.id) hasAirNeighbor = true;
		if (world.getBlock(x, y, z + 1) == air.id) hasAirNeighbor = true;
		if (hasAirNeighbor)
			world.tileEntityList.add(new LiquidTileEntity(x, y, z, this));
		if (tiles[block] instanceof LiquidTile) {
			if (type == LiquidType.Water && ((LiquidTile)tiles[block]).getType() == LiquidType.Lava)
				world.setBlockDirectly(x, y, z, cobblestone.id);
			if (type == LiquidType.Lava && ((LiquidTile)tiles[block]).getType() == LiquidType.Water)
				world.setBlockDirectly(x, y, z, cobblestone.id);
		}

	}

	public LiquidType getType() {
		return this.type;
	}
	
	public boolean getFog() {
		return (type == LiquidType.Water);
	}
	
	@Override
	public boolean mayPick() {
		return true;
	}
	
	@Override
	public boolean shouldUpdate() {
		return false;
	}
	
	@Override
	public boolean isLightTile() {
		return true;
	}
	
	@Override
	public boolean isSoildTile() {
		return false;
	}
	
	@Override
	public void createDestoryParticle(World world, int x, int y, int z) {
		
	}
	
}


