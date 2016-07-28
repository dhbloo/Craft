package craft.world.tile;

import java.util.Random;

import craft.world.World;

public class GrassTile extends Tile {
	protected GrassTile(int id, int tex) {
		super(id, tex);
		setHardness(30);
	}
	
	public int getTexture(Face face) {
		if(face==Face.Top) return 0;
		if(face==Face.Bottom) return 2;
		return 1;
	}
	
	public void tick(World world, int x, int y, int z, Random random) {
		if (world.isLit(x, y, z))
			if (world.isLiquidBlock(x, y + 1, z) && random.nextFloat() > 0.95F)
				world.setBlock(x, y, z, Tile.soil.id);
			else
				for (int i = 0; i < TICK_COUNT; i++) {
					int xt = x + random.nextInt(8) - 4;
					int yt = y + random.nextInt(6) - 3;
					int zt = z + random.nextInt(8) - 4;
					if ((world.getBlock(xt, yt, zt) == Tile.soil.id) && (world.isLit(xt, yt, zt)))
						world.setBlock(xt, yt, zt, Tile.grass.id);
				}
		else
			world.setBlock(x, y, z, Tile.soil.id);
	}
	
	public Tile getDroppedTile() {
		return soil;
	}
	
	@Override
	public boolean shouldTick() {
		return true;
	}
}
