package craft.level.levelgen;

import java.util.Random;

import craft.level.tile.Tile;

public class PlantsGen {
	private int length, height, width;
	private byte[][][] blocks;
	private Random random;
	
	public PlantsGen(byte[][][] blocks, Random random) {
		length = blocks.length;
		height = blocks[0].length;
		width = blocks[0][0].length;
		this.blocks = blocks;
		this.random = random;
	}
	
	public byte[][][] generatePlants(int treesCount, int grassCount) {
		blocks = new TreeGen(blocks, random).generateTrees(treesCount);
		for(int i = 0; i < grassCount; i++) {
			int x = random.nextInt(length - 2) + 1;		//±ê¼Ç±ß½ç
			int z = random.nextInt(width - 2) + 1;
			int y = height - 1;
			while (y > 0 && blocks[x][y][z] == Tile.air.id)
				y--;
			if(blocks[x][y][z] != Tile.grass.id && blocks[x][y][z] != Tile.soil.id) {
				i--;
				continue;
			}
			blocks[x][++y][z] = (byte) Tile.grassBush.id;
		}
		return blocks;
	}

}
