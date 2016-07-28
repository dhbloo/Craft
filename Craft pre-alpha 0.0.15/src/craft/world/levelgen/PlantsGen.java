package craft.world.levelgen;

import craft.world.tile.Tile;

public class PlantsGen {
	private LevelGen levelGen;
	
	public PlantsGen(LevelGen levelGen) {
		this.levelGen = levelGen;
	}
	
	public void genPlants(int treesCount, int grassCount) {
		new TreeGen(levelGen).genTrees(treesCount);
		grassCount *= 0.2F;
		for (int i = 0; i < grassCount; i++) {
			int x = levelGen.random.nextInt(levelGen.world.length - 20) + 10; // ±ê¼Ç±ß½ç
			int z = levelGen.random.nextInt(levelGen.world.width - 20) + 10;
			genBushsIn(x, z, (int) (5 + levelGen.random.nextFloat() * 8));
		}
	}
	
	private void genBushsIn(int x, int z, int radius) {
		for (int xa = x - radius; xa < x + radius; xa++)
			for (int za = z - radius; za < z + radius; za++) {
				if (xa > 0 && za > 0 && xa < levelGen.world.length && za < levelGen.world.width)
					if (levelGen.random.nextFloat() > 0.95F) {
						genOneBushAt(xa, za);
					}
			}
	}
	
	private boolean genOneBushAt(int x, int z) {
		int y = levelGen.world.height - 1;
		while (y > 0 && levelGen.getBlock(x, y, z) == Tile.air)
			y--;
		if (levelGen.getBlock(x, y, z) != Tile.grass && levelGen.getBlock(x, y, z) != Tile.soil) {
			return false;
		}
		if (++y < levelGen.world.height)
			levelGen.setBlock(x, y, z, Tile.tallGrass);
		return true;
	}

}
