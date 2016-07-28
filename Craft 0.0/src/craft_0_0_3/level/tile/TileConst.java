package craft_0_0_3.level.tile;

public enum TileConst {
	AIR(0,0),
	STONE(1,4),
	GRASS(2,2),
	SOIL(3,3),
	COBBLESTONE(4,5),
	BEDROCK(5,6),
	OAK_PLANKS(6,7);
	public final int value;
	private final int tex;
	
	TileConst(int value, int tex) {
		this.tex=tex;
		this.value=value;
	}
	public final int getTex() {
		return tex;
	}

}
