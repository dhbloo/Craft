package craft.level.tile;

import java.util.Random;

import craft.level.Level;

public class LeafTile extends Tile {
	
	protected LeafTile(int id, int tex) {
		super(id, tex);
		setHardness(10);
		setOpacity(0.05F);
	}

	protected float getBrightness(Level level, int x, int y, int z) {
		return super.getBrightness(level, x, y, z) * 0.7F;
	}
	
	public void tick(Level level, int x, int y, int z, Random random) {
		if(random.nextDouble() > 0.5)
			return;
		if (level.isLit(x, y, z)) {
			for (int xa = -2; xa <= 2; xa++)
				for (int ya = -1; ya <= 0; ya++)
					for (int za = -2; za <= 2; za++) {
						if(level.getBlock(x + xa, y + ya, z + za) == Tile.trunk.id) {
							int yb = -1;
							while(level.getBlock(x + xa, y + ya + yb, z + za) == Tile.trunk.id)
								yb--;
							if(level.getBlock(x + xa, y + ya + yb, z + za) == Tile.soil.id)
								return;
							else
								break;
						}
					}
			level.setBlock(x, y, z, Tile.air.id);
		}
		else
			if(random.nextDouble() > 0.3)
				level.setBlock(x, y, z, Tile.air.id);
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
}
