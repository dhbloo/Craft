package craft_0_0_2.level;

import java.util.ArrayList;
import craft_0_0_2.level.tile.Tile;
import craft_0_0_2.level.tile.TileConst;
import craft_0_0_2.phys.AABB;

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
	}
	
	private void createFlatTerrain(int h) {
		for(int x = 0; x < length; x++)
			for(int z = 0; z < width; z++)
					for(int y = y0; y <= h; y++) {
						if(y <= h * 2 / 3)
							blocks[x][y][z] = (byte) TileConst.STONE.value;
						if(y == 0)
							blocks[x][y][z] = (byte) TileConst.BEDROCK.value;
						if(y > h * 2 / 3 && y < h)
							blocks[x][y][z] = (byte) TileConst.SOIL.value;
						if(y == h)
							blocks[x][y][z] = (byte) TileConst.GRASS.value;
		}
	}
	
	public int getBlock(int x, int y, int z) {
		if(x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1)
			return TileConst.AIR.value;
		x += length / 2;
		z += width / 2;
		return blocks[x][y][z];
	}
	
	public boolean setBlock(int x, int y, int z, int block) {
		if(x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1)
			return false;
		if(blocks[x + length / 2][y][z + width / 2] ==  block)
			return false;
		blocks[x + length / 2][y][z + width / 2] = (byte) block;
		calcLightDepths(x, z, 1, 1);
		if(levelListener != null)
			levelListener.blockChanged(x, y, z);
		return true;
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
		if(block == TileConst.AIR.value)
			return false;
		else
			return true;
	}
	
	public void setLevelListener(LevelListener levelListener) {
		this.levelListener = levelListener;
	}
	
	public void calcLightDepths(int x0, int y0, int x1, int y1) {
		for(int x = x0; x < x0 + x1; x++)
			for(int z = y0; z < y0 + y1; z++)
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
	    			  aABBs.add(Tile.getAABB(x, y, z));
	    
	    return aABBs;
	}
	
	public boolean isLit(int x, int y, int z) {
		if(x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1) 
			return true;
	    return y >= lightDepths[x + length / 2][z + width / 2];
	}
	
	public float getBrightness(int x, int y, int z) {
		if(isLit(x, y, z))
			return 1.0F;
		if(y >= getAverageHeight()) {
			return 0.7F * (1 - (lightDepths[x + length / 2][z + width / 2] - y) / (height / 0.5F));
		}
		else
			return 0.7F * (1 + (lightDepths[x + length / 2][z + width / 2] - y) / (height / 0.5F));
	}
	
	public float getAverageHeight() {
		long totalHeight = 0L;
		for(int x = 0; x < length; x++)
			for(int z = 0; z < width; z++)
				totalHeight += lightDepths[x][z];
		return totalHeight / length / width;
	}
	
	public void tick() {
		
	}
}
