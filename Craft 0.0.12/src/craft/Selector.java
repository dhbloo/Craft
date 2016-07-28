package craft;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import craft.level.tile.Tile;

public class Selector {
	public static final int SELECTOR_SIZE = 8;
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
		return addTile(tile, 1);
	}

	public boolean addTile(Tile tile, int count) {
		if (tile == null) return false;
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
			tileCount[p] = count;
		} else {
			tileCount[p] += count;
		}
		return true;
	}
	
	public boolean removeOneTile() {
		if (tileCount[point] == 0) return false;
		tileCount[point]--;
		if(tileCount[point] == 0) {
			tiles[point] = null;
		}
		return true;
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

	public void saveDate(DataOutputStream out) throws IOException {
		for(int i = 0; i < SELECTOR_SIZE; i++) {
			if(tiles[i] == null) {
				out.writeInt(Tile.air.id);
				out.writeInt(0);
			} else {
				out.writeInt(tiles[i].id);
				out.writeInt(tileCount[i]);
			}
		}
	}

	public void readDate(DataInputStream in) throws IOException {
		for(int i = 0; i < SELECTOR_SIZE; i++) {
			tiles[i] = Tile.tiles[in.readInt()];
			if(tiles[i] == Tile.air)
				tiles[i] = null;
			tileCount[i] = in.readInt();
		}
	}

}
