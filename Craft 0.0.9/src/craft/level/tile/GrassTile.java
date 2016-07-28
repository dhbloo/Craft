package craft.level.tile;

import java.util.Random;

import craft.level.Level;

public class GrassTile extends Tile {
	protected GrassTile(int id, int tex, int destroyTime, boolean soildTile, boolean lightTile) {
		super(id, tex, destroyTime, soildTile, lightTile);
	}
	
	protected int getTexture(Face face) {
		if(face==Face.Top) return 0;
		if(face==Face.Bottom) return 2;
		return 1;
	}
	
	public void tick(Level level, int x, int y, int z, Random random) {
		if (level.isLit(x, y, z))
			for (int i = 0; i < TICK_COUNT; i++) {
				int xt = x + random.nextInt(8) - 4;
				int yt = y + random.nextInt(6) - 3;
				int zt = z + random.nextInt(8) - 4;
				if ((level.getBlock(xt, yt, zt) == Tile.soil.id) && (level.isLit(xt, yt, zt)))
					level.setBlock(xt, yt, zt, Tile.grass.id);
			}
		else
			level.setBlock(x, y, z, Tile.soil.id);
	}
}
