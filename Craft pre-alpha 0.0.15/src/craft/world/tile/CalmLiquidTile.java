package craft.world.tile;

import craft.world.World;

public class CalmLiquidTile extends LiquidTile {

	public CalmLiquidTile(int id, int tex, int cover, LiquidType type) {
		super(id, tex, cover, type);
	}

	protected boolean shouldRender() {
		return false;
	}
	
	@Override
	public void neighborChanged(World world, int x, int y, int z, int block) {
		/*boolean hasAirNeighbor = false;
	    if (world.getBlock(x - 1, y, z) == air.id) hasAirNeighbor = true;
	    if (world.getBlock(x + 1, y, z) == air.id) hasAirNeighbor = true;
	    if (world.getBlock(x, y - 1, z) == air.id) hasAirNeighbor = true;
	    if (world.getBlock(x, y, z - 1) == air.id) hasAirNeighbor = true;
	    if (world.getBlock(x, y, z + 1) == air.id) hasAirNeighbor = true;
	    if(hasAirNeighbor) {
	    	world.setBlockDirectly(x, y, z, water.id);
	    	add(x, y, z);
	    }*/
	}
	
}
