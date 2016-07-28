package craft_0_0_1.level.tile;

public enum TileConst {
	AIR(0,0),
	STONE(1,4),
	GRASS(2,0),
	SOIL(3,3),
	COBBLESTONE(4,5),
	BEDROCK(5,6),
	OAK_PLANKS(6,7);
	
	private int tex,value;
	TileConst(int value, int tex) {
		this.tex=tex;
		this.value=value;
	}
	public final int getTex() {
		return tex;
	}
	public final int getValue() {
		return value;
	}
}
