package craft.world.tile.tileEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

import craft.world.Coord;
import craft.world.World;

public class TileEntityList {
	private World world;
	private Random random;
	/*private ArrayList<TileEntity> tileEntities;*/
	/**实体方块哈希表*/
	private HashMap<Coord, TileEntity> tileEntitiyMap;
	/**要添加的方块哈希表*/
	private HashMap<Coord, TileEntity> toAddTileEntitiyMap;
	
	public TileEntityList(World world, Random random) {
		this.world = world;
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
			if (tileEntity.removed) {
				iterator.remove();
				continue;
			}
			if (!isTileEntityActive(tileEntity))
				continue;
			tileEntity.update(world, random);
		}
		tileEntitiyMap.putAll(toAddTileEntitiyMap);
		toAddTileEntitiyMap.clear();
	}

	/**获取实体方块的数量*/
	public int getTileEntitiesCount() {
		return tileEntitiyMap.size();
	}
	
	/**指定实体是否在激活区块*/
	private boolean isTileEntityActive(TileEntity tileEntity) {
		return world.isBlockActive(tileEntity.x, tileEntity.z);
	}
	
	/**指定位置是否有实体方块*/
	public boolean hasTileEntity(int x, int y, int z) {
		return tileEntitiyMap.containsKey(new Coord(x, y, z));
	}
	
	/**移除指定位置的实体方块*/
	public void remove(int x, int y, int z) {
		TileEntity tileEntity = tileEntitiyMap.get(new Coord(x, y, z));
		if (tileEntity != null)
			tileEntity.remove();
	}
}
