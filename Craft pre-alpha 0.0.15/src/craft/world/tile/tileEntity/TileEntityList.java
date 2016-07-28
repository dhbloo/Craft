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
	/**ʵ�巽���ϣ��*/
	private HashMap<Coord, TileEntity> tileEntitiyMap;
	/**Ҫ��ӵķ����ϣ��*/
	private HashMap<Coord, TileEntity> toAddTileEntitiyMap;
	
	public TileEntityList(World world, Random random) {
		this.world = world;
		this.random = random;
		//this.tileEntities = new ArrayList<TileEntity>();
		this.tileEntitiyMap = new HashMap<Coord, TileEntity>();
		this.toAddTileEntitiyMap = new HashMap<Coord, TileEntity>();
	}
	
	/**�����ʵ�巽�鵽����ӹ�ϣ��*/
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
	/**�����Ը���ȫ��ʵ�巽��*/
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

	/**��ȡʵ�巽�������*/
	public int getTileEntitiesCount() {
		return tileEntitiyMap.size();
	}
	
	/**ָ��ʵ���Ƿ��ڼ�������*/
	private boolean isTileEntityActive(TileEntity tileEntity) {
		return world.isBlockActive(tileEntity.x, tileEntity.z);
	}
	
	/**ָ��λ���Ƿ���ʵ�巽��*/
	public boolean hasTileEntity(int x, int y, int z) {
		return tileEntitiyMap.containsKey(new Coord(x, y, z));
	}
	
	/**�Ƴ�ָ��λ�õ�ʵ�巽��*/
	public void remove(int x, int y, int z) {
		TileEntity tileEntity = tileEntitiyMap.get(new Coord(x, y, z));
		if (tileEntity != null)
			tileEntity.remove();
	}
}
