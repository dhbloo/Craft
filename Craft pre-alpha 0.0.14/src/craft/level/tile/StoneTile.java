package craft.level.tile;

public class StoneTile extends Tile {

	public StoneTile(int id, int tex) {
		super(id, tex);
		setHardness(100);
	}
	
	public Tile getDroppedTile() {
		return cobblestone;
	}

}
