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
	
	/**��ô��ַ����һ���¶���*/
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
