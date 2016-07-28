package craft.world.tile;

public class TreeTile extends Tile {
	
	protected TreeTile(int id, int tex) {
		super(id, tex);
		setHardness(70);
	}

	public int getTexture(Face face) {
		if(face==Face.Top || face==Face.Bottom) return 9;
		return 8;
	}
	
}
