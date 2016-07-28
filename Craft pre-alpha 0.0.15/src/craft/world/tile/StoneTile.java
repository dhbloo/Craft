package craft.world.tile;

public class StoneTile extends Tile {

	public StoneTile(int id, int tex) {
		super(id, tex);
		setHardness(120);
	}
	
	public Tile getDroppedTile() {
		return cobblestone;
	}

}
