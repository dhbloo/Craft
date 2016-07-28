package craft.world.levelgen;

import craft.world.tile.Tile;

public class TreeGen {
	private LevelGen levelGen;
	
	public TreeGen(LevelGen levelGen) {
		this.levelGen = levelGen;
	}
	
	public void genTrees(int treesCount) {
		for (int i = 0; i < treesCount; i++) {
			int x = levelGen.random.nextInt(levelGen.world.length - 2) + 1; // 标记边界
			int z = levelGen.random.nextInt(levelGen.world.width - 2) + 1;
			if(!genTreeWithTemplate(x, z, levelGen.random.nextInt(TreeTemplate.Count + 1)))
				i--;
		}
	}
	
	/**在指定位置的泥土上生成一颗树
	 * @return 生成是否成功*/
	public boolean genTreeWithTemplate(int x, int z, int templateIndex) {
		int y = levelGen.world.height - 1;
		while (y > 0 && levelGen.getBlock(x, y, z) == Tile.air)
			y--;
		if (levelGen.getBlock(x, y, z) != Tile.grass && levelGen.getBlock(x, y, z) != Tile.soil) {
			return false;
		}
		if (levelGen.getBlock(x, y, z) == Tile.grass)
			levelGen.setBlock(x, y, z, Tile.soil);
		y++;
		int[][][] tb = TreeTemplate.getTree(levelGen.random.nextInt(31));
		x -= tb.length / 2;
		z -= tb[0][0].length / 2;
		for (int xa = 0; xa < tb.length; xa++)
			for (int ya = 0; ya < tb[0].length; ya++)
				for (int za = 0; za < tb[0][0].length; za++) {
					if (tb[xa][ya][za] == Tile.air.id)
						continue;
					if (x + xa < 0 || y + ya < 0 || z + za < 0 || x + xa >= levelGen.world.length || y + ya >= levelGen.world.height || z + za >= levelGen.world.width)
						continue;
					levelGen.setBlock(x + xa, y + ya, z + za, Tile.tiles[tb[xa][ya][za]]);
				}
		return true;
	}
}
