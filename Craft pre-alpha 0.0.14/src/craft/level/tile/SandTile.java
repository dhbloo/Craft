package craft.level.tile;

import craft.entity.EntityFallingTile;
import craft.level.Level;

public class SandTile extends Tile{
	
	protected SandTile(int id, int tex) {
		super(id, tex);
		setHardness(15);
	}
	
	public void neighborChanged(Level level, int x, int y, int z, int block) {
		tryToFall(level, x, y, z);
	}
	
	private void tryToFall(Level level, int x, int y, int z) {
		if (level.isAirBlock(x, y - 1, z)) {
			level.setBlock(x, y, z, air.id);
			level.entityList.add(new EntityFallingTile(level, x, y, z, this));
		}
	}
	
	
}
