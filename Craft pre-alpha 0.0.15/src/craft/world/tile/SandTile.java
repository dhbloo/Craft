package craft.world.tile;

import craft.entity.EntityFallingTile;
import craft.world.World;

public class SandTile extends Tile{
	
	protected SandTile(int id, int tex) {
		super(id, tex);
		setHardness(20);
	}
	
	public void neighborChanged(World world, int x, int y, int z, int block) {
		tryToFall(world, x, y, z);
	}
	
	private void tryToFall(World world, int x, int y, int z) {
		if (world.isAirBlock(x, y - 1, z)) {
			world.setBlock(x, y, z, air.id);
			world.entityList.add(new EntityFallingTile(world, x, y, z, this));
		}
	}
	
	
}
