package craft.level.tile.tileEntity;

import java.util.Random;

import craft.level.Coord;
import craft.level.Level;
import craft.level.tile.Tile;

public abstract class TileEntity implements Cloneable{
	protected int x, y, z;
	protected boolean removed = false;
	/**此方块实体的方块类型*/
	public Tile tileType;

 	protected TileEntity(int x, int y, int z, Tile tileType) {
 		this.x = x;
 		this.y = y;
 		this.z = z;
		this.tileType = tileType;
	}

	/**周期性更新*/
	protected void update(Level level, Random random) {
		
	}
	
	/**将方块从实体方块列表中移除*/
	public void remove() {
		this.removed = true;
	}

	public Coord getCoord() {
		return new Coord(x, y, z);
	}
	
	
}
