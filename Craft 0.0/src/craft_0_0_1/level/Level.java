package craft_0_0_1.level;

import java.util.ArrayList;

import craft_0_0_1.level.tile.Tile;
import craft_0_0_1.level.tile.TileConst;
import craft_0_0_1.phys.AABB;

public class Level {
	public final int length,width,height;		//长宽高
	public final int x0,y0,z0,x1,y1,z1;		//边界值
	public int maxHeight = 0;		//最大高度
	private byte[][][] blocks;		//x,z,y方块ID数组
	private int[][] lightDepths;		//x,z高度数组
	private LevelListener levelListener;
	
	public Level(int length, int width, int height) {
		this.length = length;
		this.width = width;
		this.height = height;
		this.blocks = new byte [length][height][width];
		this.lightDepths = new int [length][width];
		x0 = -length / 2;
		y0 = 0;
		z0 = -width / 2;
		x1 = length / 2 - 1;
		y1 = height - 1;
		z1 = width / 2 - 1;
		
		createFlatTerrain(100);
		/*blocks[32][101][33] = (byte) TileConst.STONE.getValue();
		blocks[32][101][32] = (byte) TileConst.STONE.getValue();
		blocks[32][102][32] = (byte) TileConst.STONE.getValue();
		blocks[32][102][31] = (byte) TileConst.STONE.getValue();
		blocks[32][103][30] = (byte) TileConst.STONE.getValue();*/
		calcLightDepths(x0, z0, x1, z1);
	}
	
	private void createFlatTerrain(int h) {
		for(int y = y0; y <= h; y++) {
			if(y <= h * 2 / 3)
				for(int x = 0; x < length; x++)
					for(int z = 0; z < width; z++)
						blocks[x][y][z] = (byte) TileConst.STONE.getValue();
			if(y == 0)
				for(int x = 0; x < length; x++)
					for(int z = 0; z < width; z++)
						blocks[x][y][z] = (byte) TileConst.BEDROCK.getValue();
			if(y > h * 2 / 3 && y < h)
				for(int x = 0; x < length; x++)
					for(int z = 0; z < width; z++)
						blocks[x][y][z] = (byte) TileConst.SOIL.getValue();
			if(y == h)
				for(int x = 0; x < length; x++)
					for(int z = 0; z < width; z++)
						blocks[x][y][z] = (byte) TileConst.GRASS.getValue();
		}
	}
	
	public int getBlock(int x, int y, int z) {
		if(x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1)
			return TileConst.AIR.getValue();
		x += length / 2;
		z += length / 2;
		return blocks[x][y][z];
	}
	
	public void setBlock(int x, int y, int z, int block) {
		if(x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1)
			return;
		x += length / 2;
		z += length / 2;
		blocks[x][y][z] = (byte) block;
		calcLightDepths(x, z, 1, 1);
		if(levelListener != null)
			levelListener.blockChanged(x, y, z);
	}
	
	public boolean isBlock(int x, int y, int z) {
		if(x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1)
			return false;
		return true;
	}
	
	public boolean isLightBlock(int x, int y, int z) {
		return !isSolidBlock(x,y,z);
	}
	
	public boolean isSolidBlock(int x, int y, int z) {
		int block = getBlock(x,y,z);
		if(block == TileConst.AIR.getValue())
			return false;
		else
			return true;
	}
	
	public void setLevelListener(LevelListener levelListener) {
		this.levelListener = levelListener;
	}
	
	public void calcLightDepths(int x0, int y0, int x1, int y1) {
		for(int x = x0; x < x1; x++)
			for(int z = y0; z < y1; z++)
			{
				int oldDepth = lightDepths[x + length / 2][z + width / 2];
				int y = height;
				while(y > y0 && isLightBlock(x,y,z))
					y--;
				lightDepths[x + length / 2][z + width / 2] = y;
				if (y > maxHeight)
					maxHeight = y;
				if (oldDepth != y) {
		          int yl0 = oldDepth < y ? oldDepth : y;
		          int yl1 = oldDepth > y ? oldDepth : y;
		          if(levelListener != null)
		        	  levelListener.lightColumnChanged(x, z, yl0, yl1);
				}
			}
	}
	
	public ArrayList<AABB> getCubes(AABB aabb) {
	    ArrayList<AABB> aABBs = new ArrayList<AABB>();
	    /*int x0 = (int)aabb.x0;
	    int x1 = (int)(aabb.x1 + 1.0F);
	    int y0 = (int)aabb.y0;
	    int y1 = (int)(aabb.y1 + 1.0F);
	    int z0 = (int)aabb.z0;
	    int z1 = (int)(aabb.z1 + 1.0F);*/
	    
	    int x0 = (int) Math.floor(aabb.x0);
	    int x1 = (int) Math.floor(aabb.x1 + 1.0F);
	    int y0 = (int) Math.floor(aabb.y0);
	    int y1 = (int) Math.floor(aabb.y1 + 1.0F);
	    int z0 = (int) Math.floor(aabb.z0);
	    int z1 = (int) Math.floor(aabb.z1 + 1.0F);

	    if (x0 < this.x0) x0 = this.x0;
	    if (y0 < this.y0) y0 = this.y0;
	    if (z0 < this.z0) z0 = this.z0;
	    if (x1 > this.x1) x1 = this.x1;
	    if (y1 > this.y1) y1 = this.y1;
	    if (z1 > this.z1) z1 = this.z1;

	    for (int x = x0; x <= x1; x++)
	      for (int y = y0; y <= y1; y++)
	    	  for (int z = z0; z <= z1; z++)
	    		  if (isSolidBlock(x,y,z))
	    			  aABBs.add(Tile.tiles[getBlock(x, y, z)].getAABB(x, y, z));
	    
	    return aABBs;
	}
	
	public void tick() {
		
	}
}
