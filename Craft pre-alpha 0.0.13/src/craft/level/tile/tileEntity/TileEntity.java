package craft.level.tile.tileEntity;

import java.util.Random;

import craft.level.Coord;
import craft.level.Level;
import craft.level.tile.Tile;

public abstract class TileEntity extends Tile implements Cloneable{
	protected int x, y, z;
	protected boolean removed = false;

 	protected TileEntity(int id, int tex) {
		super(id, tex);
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	/**获得此种方块的一个新对象*/
	protected TileEntity getNewTile(int x, int y, int z) {
		try {
			TileEntity tileEntity = (TileEntity) this.clone();
			tileEntity.x = x;
			tileEntity.y = y;
			tileEntity.z = z;
			return tileEntity;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
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
