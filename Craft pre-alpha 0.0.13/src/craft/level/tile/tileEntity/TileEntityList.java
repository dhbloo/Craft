package craft.level.tile.tileEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

import craft.level.Coord;
import craft.level.Level;

public class TileEntityList {
	private Level level;
	private Random random;
	/*private ArrayList<TileEntity> tileEntities;*/
	/**实体方块哈希表*/
	private HashMap<Coord, TileEntity> tileEntitiyMap;
	/**要添加的方块哈希表*/
	private HashMap<Coord, TileEntity> toAddTileEntitiyMap;
	
	public TileEntityList(Level level, Random random) {
		this.level = level;
		this.random = random;
		//this.tileEntities = new ArrayList<TileEntity>();
		this.tileEntitiyMap = new HashMap<Coord, TileEntity>();
		this.toAddTileEntitiyMap = new HashMap<Coord, TileEntity>();
	}
	
	/**添加新实体方块到待添加哈希表*/
	public void add(TileEntity tileEntity) {
		//tileEntities.add(tileEntity);
		//tileEntitiyMap.put(tileEntity.getCoord(), tileEntity);
		toAddTileEntitiyMap.put(tileEntity.getCoord(), tileEntity);
	}

	
	/*
	public void remove(TileEntity tileEntity) {
		//tileEntities.remove(tileEntity);
		tileEntitiyMap.remove(tileEntity.getCoord());
	}
	
	public void remove(int index) {
		//tileEntities.remove(index);
	}
	*/
	/**周期性更新全部实体方块*/
	public void updateAll() {
		Iterator<Entry<Coord, TileEntity>> iterator = tileEntitiyMap.entrySet().iterator();
		while (iterator.hasNext()) {
			TileEntity tileEntity = iterator.next().getValue();
			if (!isTileEntityActive(tileEntity))
				continue;
			tileEntity.update(level, random);
			if (tileEntity.removed) {
				iterator.remove();
			}
		}
		tileEntitiyMap.putAll(toAddTileEntitiyMap);
		toAddTileEntitiyMap.clear();
		/*for (int i = 0; i < tileEntities.size(); i++) {
			TileEntity tileEntity = tileEntities.get(i);
			if (!isTileEntityActive(tileEntity))
				continue;
			tileEntity.update(level, random);
			if (tileEntities.get(i).removed)
				tileEntities.remove(i--);
		}*/
	}

	/**获取实体方块的数量*/
	public int getTileEntitiesCount() {
		return tileEntitiyMap.size();
	}
	
	/**指定实体是否在激活区块*/
	private boolean isTileEntityActive(TileEntity tileEntity) {
		return level.isBlockActive(tileEntity.x, tileEntity.z);
	}
	
	/**指定位置是否有实体方块*/
	public boolean hasTileEntity(int x, int y, int z) {
		return tileEntitiyMap.containsKey(new Coord(x, y, z));
	}
	
	/**移除指定位置的实体方块*/
	public boolean remove(int x, int y, int z) {
		return tileEntitiyMap.remove(new Coord(x, y, z)) != null;
	}
}
