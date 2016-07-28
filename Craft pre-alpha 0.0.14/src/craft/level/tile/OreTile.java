package craft.level.tile;

public class OreTile extends Tile {

	public OreTile(int id, int tex) {
		super(id, tex);
	}
	
	@Override
	public Tile getDroppedTile() {
		return null;
	}

}
