package craft;

import craft.level.tile.Tile;

public class Selector {
	public static final int SELECTOR_SIZE = 6;
	private Tile[] tiles = new Tile[SELECTOR_SIZE];
	private int[] tileCount = new int[SELECTOR_SIZE];
	private int point = 0;
	
	public Tile getSelectTile() {
		return tiles[point];
	}
	
	public int getPoint() {
		return point;
	}
	
	public boolean addTile(Tile tile) {
		int p = -1;
		for(int i = 0; i < tiles.length; i++) {
			if(tiles[i] == tile) {
				p = i;
				break;
			}
			if(tileCount[i] == 0 && p == -1)
				p = i;
		}
		if(p == -1)
			return false;
		if(tileCount[p] == 0) {
			tiles[p] = tile;
			tileCount[p] = 1;
		} else {
			tileCount[p]++;
		}
		return true;
	}
	
	public void removeOneTile() {
		tileCount[point]--;
		if(tileCount[point] == 0) {
			tiles[point] = null;
		}
	}
	
	public void forward() {
		if(++point >= SELECTOR_SIZE)
			point = 0;
	}
	
	public void backward() {
		if(--point < 0)
			point = SELECTOR_SIZE - 1;
	}
	
	public Tile getTile(int p) {
		return tiles[p];
	}
	
	public int getTileCount(int p) {
		return tileCount[p];
	}
	
}
