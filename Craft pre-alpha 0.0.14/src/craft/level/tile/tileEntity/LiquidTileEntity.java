package craft.level.tile.tileEntity;

import java.util.Random;

import craft.level.Level;
import craft.level.tile.LiquidTile;

public class LiquidTileEntity extends TileEntity {
	private int lastUpdate = 0;

	public LiquidTileEntity(int x, int y, int z, LiquidTile tileType) {
		super(x, y, z, tileType);
	}
	
	protected void update(Level level, Random random) {
		if(lastUpdate++ >= ((LiquidTile)tileType).getType().getUpdateTime()) {
			tileType.update(level, x, y, z, random);
			lastUpdate = 0;
		}
	}

}
