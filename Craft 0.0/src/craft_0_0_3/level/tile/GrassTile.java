package craft_0_0_3.level.tile;

import java.util.Random;
import craft_0_0_3.level.Level;

public class GrassTile extends Tile {
	protected GrassTile(TileConst tile) {
		super(tile);
	}
	
	@Override
	protected int getTexture(Face face) {
		if(face==Face.Top) return 1;
		if(face==Face.Bottom) return 3;
		return 2;
	}
	
	public void tick(Level level, int x, int y, int z, Random random) {
		if (level.isLit(x, y, z))
			for (int i = 0; i < TICK_COUNT; i++) {
				int xt = x + random.nextInt(8) - 4;
				int yt = y + random.nextInt(6) - 3;
				int zt = z + random.nextInt(8) - 4;
				if ((level.getBlock(xt, yt, zt) == TileConst.SOIL.value) && (level.isLit(xt, yt, zt)))
					level.setBlock(xt, yt, zt, TileConst.GRASS.value);
			}
		else
			level.setBlock(x, y, z, TileConst.SOIL.value);
	}
}
