package craft.level.tile;

public class TreeTile extends Tile {
	protected TreeTile(int id, int tex, int destroyTime, boolean soildTile, boolean lightTile, int brightness, float brightnessDecay) {
		super(id, tex, destroyTime, soildTile, lightTile, brightness, brightnessDecay);
	}

	protected int getTexture(Face face) {
		if(face==Face.Top || face==Face.Bottom) return 9;
		return 8;
	}
	
}
