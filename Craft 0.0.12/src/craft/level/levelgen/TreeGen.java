package craft.level.levelgen;

import java.util.Random;

import craft.level.tile.Tile;

public class TreeGen {
	private int length, height, width;
	private byte[][][] blocks;
	private Random random;
	
	public TreeGen(byte[][][] blocks, Random random) {
		length = blocks.length;
		height = blocks[0].length;
		width = blocks[0][0].length;
		this.blocks = blocks;
		this.random = random;
	}
	
	public byte[][][] generateTrees(int treesCount) {
		for(int i = 0; i < treesCount; i++) {
			int x = random.nextInt(length - 2) + 1;		//±ê¼Ç±ß½ç
			int z = random.nextInt(width - 2) + 1;
			int y = height - 1;
			while (y > 0 && blocks[x][y][z] == Tile.air.id)
				y--;
			if(blocks[x][y][z] != Tile.grass.id && blocks[x][y][z] != Tile.soil.id) {
				i--;
				continue;
			}
			if(blocks[x][y][z] == Tile.grass.id)
				blocks[x][y][z] = (byte) Tile.soil.id;
			y++;
			int[][][] tb = TreeTemplate.getTree(random.nextInt(31));
			x -= tb.length / 2;
			z -= tb[0][0].length / 2;
			for(int xa = 0; xa < tb.length; xa++)
				for(int ya = 0; ya < tb[0].length; ya++)
					for(int za = 0; za < tb[0][0].length; za++) {
						if(tb[xa][ya][za] == Tile.air.id)
							continue;
						if(x + xa < 0 || y + ya < 0 || z + za < 0 || x + xa >= length || y + ya >= height || z + za >= width)
							continue;
						//if(blocks[x + xa][y + ya][z + za] != Tile.air.id)
						//	continue;
						blocks[x + xa][y + ya][z + za] = (byte) tb[xa][ya][za];
					}
		}
		return blocks;
	}
}
