package craft.level.tile;

public class StoneTile extends Tile {

	public StoneTile(int id, int tex, int destroyTime, boolean soildTile, boolean lightTile, int brightness, float brightnessDecay) {
		super(id, tex, destroyTime, soildTile, lightTile, brightness, brightnessDecay);
	}
	
	public Tile getDroppedTile() {
		return cobblestone;
	}

}
