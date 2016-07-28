package craft.level.tile;

import java.util.Random;

import craft.level.Level;
import craft.phys.AABB;
import craft.renderer.Tesselator;

public class Bush extends Tile {

	public Bush(int id, int tex) {
		super(id, tex);
		setHardness(2);
		setOpacity(0.05F);
		setNoSoildTile();
	}
	
	@Override
	public void render(Tesselator t, Level level, int x, int y, int z, int layer)
	  {
	    if ((level.isLit(x, y, z) ^ layer != 1)) return;

	    int tex = getTexture(null);
	    float u0 = tex % 16 / 16.0F;
	    float u1 = u0 + 0.0624375F;
	    float v0 = tex / 16 / 16.0F;
	    float v1 = v0 + 0.0624375F;

	    int rots = 2;
	    float b = getBrightness(level, x, y, z);
	    t.color(b, b, b);
		for (int r = 0; r < rots; r++) {
			float xa = (float) (Math.sin(r * Math.PI / rots + Math.PI / 4) * 0.6);
			float za = (float) (Math.cos(r * Math.PI / rots + Math.PI / 4) * 0.6);
			float x0 = x + 0.5F - xa;
			float x1 = x + 0.5F + xa;
			float y0 = y + 0.0F;
			float y1 = y + 1.0F;
			float z0 = z + 0.5F - za;
			float z1 = z + 0.5F + za;

			t.vertexUV(x0, y1, z0, u1, v0);
			t.vertexUV(x1, y1, z1, u0, v0);
			t.vertexUV(x1, y0, z1, u0, v1);
			t.vertexUV(x0, y0, z0, u1, v1);

			t.vertexUV(x1, y1, z1, u1, v0);
			t.vertexUV(x0, y1, z0, u0, v0);
			t.vertexUV(x0, y0, z0, u0, v1);
			t.vertexUV(x1, y0, z1, u1, v1);
		}
	}

	public void tick(Level level, int x, int y, int z, Random random) {
	    int below = level.getBlock(x, y - 1, z);
		if ((!level.isLit(x, y, z)) || ((below != Tile.soil.id) && (below != Tile.grass.id))) {
			level.setBlock(x, y, z, Tile.air.id);
		}
	}
	
	public Tile getDroppedTile() {
		return null;
	}
	
	@Override
	public boolean isLightTile() {
		return true;
	}
	
	@Override
	public boolean shouldTick() {
		return true;
	}
	
	@Override
	public AABB getAABB(int x, int y, int z) {
		return null;
	}
	
	@Override
	public void neighborChanged(Level level, int x, int y, int z, int block) {
		if (level.getBlock(x, y - 1, z) != grass.id) {
			level.setBlock(x, y, z, air.id);
			createDestoryParticle(level, x, y, z);
		}
	}
	
}
