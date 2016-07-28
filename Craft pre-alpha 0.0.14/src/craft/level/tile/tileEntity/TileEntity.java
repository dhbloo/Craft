package craft.level.tile.tileEntity;

import java.util.Random;

import craft.level.Coord;
import craft.level.Level;
import craft.level.tile.Tile;

public abstract class TileEntity implements Cloneable{
	protected int x, y, z;
	protected boolean removed = false;
	/**�˷���ʵ��ķ�������*/
	public Tile tileType;

 	protected TileEntity(int x, int y, int z, Tile tileType) {
 		this.x = x;
 		this.y = y;
 		this.z = z;
		this.tileType = tileType;
	}

	/**�����Ը���*/
	protected void update(Level level, Random random) {
		
	}
	
	/**�������ʵ�巽���б����Ƴ�*/
	public void remove() {
		this.removed = true;
	}

	public Coord getCoord() {
		return new Coord(x, y, z);
	}
	
	
}
