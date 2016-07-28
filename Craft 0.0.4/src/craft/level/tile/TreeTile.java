package craft.level.tile;

public class TreeTile extends Tile {
	protected TreeTile(int id, int tex) {
		super(id, tex);
	}

	protected int getTexture(Face face) {
		if(face==Face.Top || face==Face.Bottom) return 8;
		return 9;
	}
	
}
