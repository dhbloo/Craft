package craft.level;

import java.util.ArrayList;
import java.util.Random;

import craft.level.levelgen.LevelGen;
import craft.level.levelgen.TreeGen;
import craft.level.tile.EntityTile;
import craft.level.tile.LiquidTile;
import craft.level.tile.LiquidTile.LiquidType;
import craft.level.tile.Tile;
import craft.phys.AABB;

public class Level {
	public static final int TICK_LENGTH = 128, TICK_HEIGHT = 64, TICK_WIDTH = 128;
	public static final float TICK_SPEED = 3.0F;
	public String name, createTime;
	public int length,width,height;		//长宽高
	public int x0,y0,z0,x1,y1,z1;		//边界值
	public int maxHeight = 0;		//最大高度
	private byte[][][] blocks;		//x,z,y方块ID数组
	private int[][] lightDepths;		//x,z高度数组
	private LevelListener levelListener;
	private Random random = new Random();
	private int unprocessed = 0;

	public Level() {
		
	}
	
	public Level(int length, int width, int height) {
		this.length = length;
		this.width = width;
		this.height = height;
		this.lightDepths = new int [length][width];
		x0 = -length / 2;
		y0 = 0;
		z0 = -width / 2;
		x1 = length / 2 - 1;
		y1 = height - 1;
		z1 = width / 2 - 1;
		
		this.blocks = new LevelGen(length, height, width, random).generateNewLevel();
		calcLightDepths(x0, z0, length, width);
		this.blocks = new TreeGen(blocks, lightDepths, random).generateTrees(100);
		calcLightDepths(x0, z0, length, width);
	}

	public void setDate(int length, int width, int height, byte[][][] blocks) {
		this.length = length;
		this.width = width;
		this.height = height;
		this.blocks = blocks;
		this.lightDepths = new int [length][width];
		x0 = -length / 2;
		y0 = 0;
		z0 = -width / 2;
		x1 = length / 2 - 1;
		y1 = height - 1;
		z1 = width / 2 - 1;
		calcLightDepths(x0, z0, length, width);
	}
	
	public byte[][][] getDate() {
		return this.blocks;
	}
	
	/*public void generateNewMap(int length, int width, int height) {
		this.length = length;
		this.width = width;
		this.height = height;
		this.lightDepths = new int [length][width];
		x0 = -length / 2;
		y0 = 0;
		z0 = -width / 2;
		x1 = length / 2 - 1;
		y1 = height - 1;
		z1 = width / 2 - 1;
		
		this.blocks = new LevelGen(length, height, width, random).generateNewLevel();
		calcLightDepths(x0, z0, length, width);
		this.blocks = new TreeGen(blocks, lightDepths, random).generateTrees(100);
		calcLightDepths(x0, z0, length, width);
	}*/
	
	@SuppressWarnings("unused")
	private void createFlatTerrain(int h) {
		for(int x = 0; x < length; x++)
			for(int z = 0; z < width; z++)
				for(int y = y0; y <= h; y++) {
					if(y <= h * 2 / 3)
						blocks[x][y][z] = (byte) Tile.stone.id;
					if(y == 0)
						blocks[x][y][z] = (byte) Tile.bedrock.id;
					if(y > h * 2 / 3 && y < h)
						blocks[x][y][z] = (byte) Tile.soil.id;
					if(y == h)
						blocks[x][y][z] = (byte) Tile.grass.id;
				}
	}

	public int getBlock(int x, int y, int z) {
		if(x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1)
			return Tile.air.id;
		x += length / 2;
		z += width / 2;
		return blocks[x][y][z];
	}

	public boolean setBlock(int x, int y, int z, int block) {
		if(x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1)
			return false;
		if(blocks[x + length / 2][y][z + width / 2] ==  block)
			return false;
		Tile oldTile = Tile.tiles[blocks[x + length / 2][y][z + width / 2]];
		Tile newTile = Tile.tiles[block];
		blocks[x + length / 2][y][z + width / 2] = (byte) block;

		if (oldTile instanceof EntityTile)
			((EntityTile) oldTile).remove(x, y, z);

		if (oldTile instanceof EntityTile)
			((EntityTile) oldTile).remove(x, y, z);

		if (newTile instanceof EntityTile)
			((EntityTile) newTile).add(x, y, z);

		neighborChanged(x - 1, y, z, block);
		neighborChanged(x + 1, y, z, block);
		neighborChanged(x, y - 1, z, block);
		neighborChanged(x, y + 1, z, block);
		neighborChanged(x, y, z - 1, block);
		neighborChanged(x, y, z + 1, block);

		calcLightDepths(x, z, 1, 1);
		if(levelListener != null)
			levelListener.blockChanged(x, y, z);
		return true;
	}

	public boolean setBlockDirectly(int x, int y, int z, int block) {
		if(x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1)
			return false;
		if(blocks[x + length / 2][y][z + width / 2] ==  block)
			return false;
		blocks[x + length / 2][y][z + width / 2] = (byte) block;
		return true;
	}

	private void neighborChanged(int x, int y, int z, int block) {
		if(x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1)
			return;
		Tile tile = Tile.tiles[blocks[x + length / 2][y][z + width / 2]];
		if (tile != Tile.air)
			tile.neighborChanged(this, x, y, z, block);
	}

	public boolean isBlock(int x, int y, int z) {
		if(x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1)
			return false;
		return true;
	}

	public boolean isLightBlock(int x, int y, int z) {
		return Tile.tiles[getBlock(x,y,z)].lightTile;
	}

	public boolean isSolidBlock(int x, int y, int z) {
		return Tile.tiles[getBlock(x,y,z)].soildTile;
	}

	public void setLevelListener(LevelListener levelListener) {
		this.levelListener = levelListener;
	}

	public void calcLightDepths(int x0, int y0, int x1, int y1) {
		if(x1 == length && z1 == width)
			maxHeight = 0;
		for(int x = x0; x < x0 + x1; x++)
			for(int z = y0; z < y0 + y1; z++)
			{
				int oldDepth = lightDepths[x + length / 2][z + width / 2];
				int y = this.y0 + height;
				while(y > this.y0 && isLightBlock(x,y,z))
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
		if(y >= lightDepths[x + length / 2][z + width / 2])
			return true;
		else
			/*for(int i = lightDepths[x + length / 2][z + width / 2]; i >= y; i--)
				if(!isLightBlock(x, i, z))
					return false;*/
			return false;
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
		unprocessed += length * height * width;
		int ticks = (int) (unprocessed / ((length + height + width) * 16 / TICK_SPEED));
		unprocessed -= ticks * ((length + height + width) * 16 / TICK_SPEED);
		int xp = x0; //(int) (player.x - TICK_LENGTH / 2);
		int yp = y0; //(int) (player.y - TICK_HEIGHT / 2);
		int zp = z0; //(int) (player.z - TICK_WIDTH / 2);
		for (int i = 0; i < ticks; i++) {
			int x = xp + random.nextInt(length);
			int y = yp + random.nextInt(height);
			int z = zp + random.nextInt(width);
			if (getBlock(x, y, z) != Tile.air.id)
				Tile.tiles[getBlock(x, y, z)].tick(this, x, y, z, random);
		}
	}

	public boolean containsAnyLiquid(AABB box) {
		return containsLiquid(box, LiquidType.Water) ? true : containsLiquid(box, LiquidType.Lava) ? true : false;
	}
	
	public boolean containsLiquid(AABB box, LiquidType liquidType) {
		int x0 = (int)Math.floor(box.x0);
		int x1 = (int)Math.floor(box.x1 + 1.0F);
		int y0 = (int)Math.floor(box.y0);
		int y1 = (int)Math.floor(box.y1 + 1.0F);
		int z0 = (int)Math.floor(box.z0);
		int z1 = (int)Math.floor(box.z1 + 1.0F);

		if (x0 < this.x0) x0 = this.x0;
		if (y0 < this.y0) y0 = this.y0;
		if (z0 < this.z0) z0 = this.z0;
		if (x1 > this.x1) x1 = this.x1;
		if (y1 > this.y1) y1 = this.y1;
		if (z1 > this.z1) z1 = this.z1;

		for (int x = x0; x < x1; x++)
			for (int y = y0; y < y1; y++)
				for (int z = z0; z < z1; z++) {
					Tile tile = Tile.tiles[getBlock(x, y, z)];
					if ((tile instanceof LiquidTile) && ((LiquidTile)tile).getType() == liquidType)
						return true;
				}
		return false;
	}
	
	public ArrayList<AABB> getWallCubes() {
		ArrayList<AABB> aABBs = new ArrayList<AABB>();
		aABBs.add(new AABB(x0 - 1, y0, z0, x0, y1, z1 + 1));
		aABBs.add(new AABB(x1 + 1, y0, z0, x1 + 2, y1, z1 + 1));
		aABBs.add(new AABB(x0, y0, z0 - 1, x1 + 1, y1, z0));
		aABBs.add(new AABB(x0, y0, z1 + 1, x1 + 1, y1, z1 + 2));
		return aABBs;
	}
}
