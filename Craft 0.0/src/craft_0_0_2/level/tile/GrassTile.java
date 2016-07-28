package craft_0_0_2.level.tile;

public class GrassTile extends Tile{
	protected GrassTile(int id) {
	    super(id);
	}
	protected GrassTile(TileConst tile) {
		super(tile);
	}
	@Override
	protected int getTexture(Face face) {
		if(face==Face.Top) return 1;
		if(face==Face.Bottom) return 3;
		return 2;
	}
}
