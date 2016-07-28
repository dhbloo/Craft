package craft.level.tile;

public class TreeTile extends Tile {
	protected TreeTile(int id, int tex, int destroyTime, boolean soildTile, boolean lightTile) {
		super(id, tex, destroyTime, soildTile, lightTile);
	}

	protected int getTexture(Face face) {
		if(face==Face.Top || face==Face.Bottom) return 8;
		return 9;
	}
	
}
