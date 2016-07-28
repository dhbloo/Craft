package craft.level.tile;

import craft.level.Level;

public class CalmLiquidTile extends LiquidTile {

	public CalmLiquidTile(int id, int tex, int cover, int destroyTime, boolean soildTile, boolean lightTile, int brightness, float brightnessDecay, LiquidType type) {
		super(id, tex, cover, destroyTime, soildTile, lightTile, brightness, brightnessDecay, type);
	}

	protected boolean shouldRender() {
		return false;
	}
	
	@Override
	public void neighborChanged(Level level, int x, int y, int z, int block) {
		/*boolean hasAirNeighbor = false;
	    if (level.getBlock(x - 1, y, z) == air.id) hasAirNeighbor = true;
	    if (level.getBlock(x + 1, y, z) == air.id) hasAirNeighbor = true;
	    if (level.getBlock(x, y - 1, z) == air.id) hasAirNeighbor = true;
	    if (level.getBlock(x, y, z - 1) == air.id) hasAirNeighbor = true;
	    if (level.getBlock(x, y, z + 1) == air.id) hasAirNeighbor = true;
	    if(hasAirNeighbor) {
	    	level.setBlockDirectly(x, y, z, water.id);
	    	add(x, y, z);
	    }*/
	}
	
}
