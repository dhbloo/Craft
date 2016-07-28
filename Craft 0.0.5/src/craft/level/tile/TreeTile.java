package craft.level.tile;

public class TreeTile extends Tile {
	protected TreeTile(int id, int tex, int destroyTime) {
		super(id, tex, destroyTime);
	}

	protected int getTexture(Face face) {
		if(face==Face.Top || face==Face.Bottom) return 8;
		return 9;
	}
	
}
