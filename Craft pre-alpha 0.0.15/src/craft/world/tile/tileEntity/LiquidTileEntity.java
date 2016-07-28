package craft.world.tile.tileEntity;

import java.util.Random;

import craft.world.World;
import craft.world.tile.LiquidTile;

public class LiquidTileEntity extends TileEntity {
	private int lastUpdate = 0;

	public LiquidTileEntity(int x, int y, int z, LiquidTile tileType) {
		super(x, y, z, tileType);
	}
	
	protected void update(World world, Random random) {
		if(lastUpdate++ >= ((LiquidTile)tileType).getType().getUpdateTime()) {
			tileType.update(world, x, y, z, random);
			lastUpdate = 0;
		}
	}

}
