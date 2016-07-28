package craft.world;

import craft.Player;
import craft.phys.AABB;
import craft.world.tile.Tile;

public class Chunk {
	/**长*/
	public static final int LENGTH = 16;
	/**长右移位*/
	public static final int LENGTH_SHIFT_COUNT = LENGTH >> 2;
	/**长与位*/
	public static final int LENGTH_AND_COUNT = LENGTH - 1;
	/**高*/
	public static int HEIGHT;
	/**高右移位*/
	public static int HEIGHT_SHIFT_COUNT;
	/**方块储存单位高与位*/
	public static final int STORAGE_AND_COUNT = BlockStorage.SIZE - 1;
	/**方块储存单位总数*/
	public static int STORAGE_SIZE;
	public final World world;
	public final AABB aabb;
	public final int x0, y0, z0, x1, y1, z1;
	public final float x, y, z;
	/**方块设置储存*/
	public BlockStorage[] blockStorage;
	/**x,z高度数组*/
	private int[][] heights;
	/**区块最大高度*/
	public int maxHeight = 0;
	/**区块是否已加载*/
	private boolean loaded = false;
	/**区块是否激活*/
	public boolean active = false;
	/**是否需要更新*/
	private boolean dirty = false;
	/**区块内需要更新的范围*/
	private int dirtyX0, dirtyLength, dirtyY0, dirtyHeight, dirtyZ0, dirtyLENGTH;
	/**需要更新区块数*/
	protected static int numberOfDirtyChunks = 0;
	
	/**创建空区块
	 * @param x0 区块左边界的世界坐标
	 * @param y0 区块低边界的世界坐标
	 * @param z0 区块后边界的世界坐标*/
	public Chunk(World world, int x0, int y0, int z0) {
		this.world = world;
		HEIGHT = world.height;
		HEIGHT_SHIFT_COUNT = HEIGHT >> 2;
		STORAGE_SIZE = HEIGHT >> (BlockStorage.STORAGE_SHIFT_COUNT);
		this.x0 = x0;
		this.y0 = y0;
		this.z0 = z0;
		this.x1 = x0 + LENGTH - 1;
		this.y1 = y0 + HEIGHT - 1;
		this.z1 = z0 + LENGTH - 1;

		this.x = (x0 + x1) / 2.0F;
		this.y = (y0 + y1) / 2.0F;
		this.z = (z0 + z1) / 2.0F;

		this.aabb = new AABB(x0 - 1.0F, y0 - 1.0F, z0 - 1.0F, x1 + 1.0F, y1 + 1.0F, z1 + 1.0F);
	}

	/**设置区块数据*/
	public void setDate(BlockStorage[] storage) {
		blockStorage = storage;
		heights = new int[LENGTH][LENGTH];
		this.loaded = true;
	}
	
	/**创建平坦区块*/
	protected void createFlatTerrain(int h) {
		blockStorage = new BlockStorage[STORAGE_SIZE];
		this.heights = new int [LENGTH][LENGTH];
		
		for(int x = 0; x < LENGTH; x++)
			for(int z = 0; z < LENGTH; z++)
				for(int y = y0; y <= h; y++) {
					BlockStorage storage = blockStorage[y >> BlockStorage.STORAGE_SHIFT_COUNT];
					int yl = y & STORAGE_AND_COUNT;
					if(y <= h * 2 / 3)
						storage.setBlockID(x, yl, z, (byte) Tile.stone.id);
					if(y == 0)
						storage.setBlockID(x, yl, z, (byte) Tile.bedrock.id);
					if(y > h * 2 / 3 && y < h)
						storage.setBlockID(x, yl, z, (byte) Tile.soil.id);
					if(y == h)
						storage.setBlockID(x, yl, z, (byte) Tile.grass.id);
				}
		
		this.loaded = true;
	}
	
	/**取区块内方块
	 * @param 传入区块局部坐标*/
	public int getBlock(int x, int y, int z) {
		/*if (x < 0 || x >= LENGTH || y < 0 || y >= HEIGHT || z < 0 || z >= LENGTH)
			return Tile.air.id;*/
		return blockStorage[y >> BlockStorage.STORAGE_SHIFT_COUNT].getBlockID(x, y & STORAGE_AND_COUNT, z);
	}
	
	/**设置区块内方块
	 * @param 传入区块局部坐标
	 * @param block 方块ID*/
	public void setBlock(int x, int y, int z, byte block) {
		/*if (x < 0 || x >= LENGTH || y < 0 || y >= HEIGHT || z < 0 || z >= LENGTH)
			return false;*/
		blockStorage[y >> BlockStorage.STORAGE_SHIFT_COUNT].setBlockID(x, y & STORAGE_AND_COUNT, z, block);
	}
	
	/**取区块内方块亮度
	 * @param 传入区块局部坐标*/
	public byte getBlockBrightness(int x, int y, int z) {
		return blockStorage[y >> BlockStorage.STORAGE_SHIFT_COUNT].getBlockBrightness(x, y & STORAGE_AND_COUNT, z);
	}
	
	/**设置区块内方块亮度
	 * @param 传入区块局部坐标
	 * @param brightness 方块亮度*/
	public void setBlockBrightness(int x, int y, int z, byte brightness) {
		blockStorage[y >> BlockStorage.STORAGE_SHIFT_COUNT].setBlockBrightness(x, y & STORAGE_AND_COUNT, z, brightness);
	}
	
	/**计算高度表
	 * @param x0, y0, z0传入区块局部坐标
	 * @param x0 左范围边界
	 * @param z0 后范围边界
	 * @param x1 长度范围
	 * @param z1 宽度范围*/
	public void calcHeights(int x0, int z0, int x1, int z1) {
		if (x1 == LENGTH && z1 == LENGTH)
			maxHeight = 0;
		for (int x = x0; x < x0 + x1; x++)
			for (int z = z0; z < z0 + z1; z++) {
				int y = this.y1;
				while (y > this.y0 && getBlock(x, y, z) == Tile.air.id)
					y--;
				heights[x][z] = y;
				if (y > maxHeight)
					maxHeight = y;
			}
		if (maxHeight > world.maxHeight)
			world.maxHeight = maxHeight;
	}
	
	/**计算全部高度*/
	public void calcAllHeights() {
		calcHeights(0, 0, LENGTH, LENGTH);
	}
	
	/**计算纵向亮度
	 * @param x0, y0, z0传入区块局部坐标
	 * @param x0 左范围边界
	 * @param z0 后范围边界
	 * @param x1 长度范围
	 * @param z1 宽度范围*/
	public void calcVerticalBrightness(int x0, int z0, int x1, int z1) {
		int xa0 = world.x1, xa1 = world.x0, ya0 = world.y1, ya1 = world.y0, za0 = world.z1, za1 = world.z0;
		for (int x = x0; x < x0 + x1; x++)
			for (int z = z0; z < z0 + z1; z++) {
				byte oldBrightness = 0;
				for (int y = this.y1; y > this.heights[x][z]; y--) {
					BlockStorage storage = blockStorage[y >> BlockStorage.STORAGE_SHIFT_COUNT];
					int yl = y & STORAGE_AND_COUNT;
					oldBrightness = storage.getBlockBrightness(x, yl, z);
					storage.setBlockBrightness(x, yl, z, Brightness.MAX);
					if (oldBrightness != Brightness.MAX) {
						int xa = this.x0 + x;
						int ya = this.y0 + y;
						int za = this.z0 + z;
						if (xa < xa0) xa0 = xa;
						if (xa > xa1) xa1 = xa;
						if (ya < ya0) ya0 = ya;
						if (ya > ya1) ya1 = ya;
						if (za < za0) za0 = za;
						if (za > za1) za1 = za;
					}
				}
			}
		if (xa1 > xa0 || ya1 > ya0 || za1 > za0)
			world.lightColumnChanged(xa0, ya0, za0, xa1, ya1, za1);
	}
	
	public void calcAllVerticalBrightness() {
		calcVerticalBrightness(0, 0, LENGTH, LENGTH);
	}
	
	/**是否为透明方块
	 * @param 传入区块局部坐标*/
	public boolean isLightBlock(int x, int y, int z) {
		Tile tile = Tile.tiles[getBlock(x, y, z)];
		return tile == Tile.air ? true : tile.isLightTile();
	}
	
	/**清除区块内数据，释放内存*/
	public void clearDate() {
		loaded = false;
		blockStorage = null;
		heights = null;
	}
	
	/**返回区块是否被加载*/
	public boolean isLoaded() {
		return this.loaded;
	}
	
	/**获得区块指定x,z高度
	 * @param 传入区块局部坐标*/
	public int getHeights(int x, int z) {
		return heights[x][z];
	}
	
	/**获得距离玩家的横向距离（未开方）*/
	public float distanceToSqr(Player player) {
		float xd = player.x - this.x;
		//float yd = player.y - this.y;
		float zd = player.z - this.z;
		return xd * xd + zd * zd;
	}
	
	private void updateVertical() {
		calcVerticalBrightness(dirtyX0, dirtyZ0, dirtyLength, dirtyLENGTH);
	}
	
	/**设置区块指定范围需要更新*/
	public void setDirty(int x0, int y0, int z0, int x1, int y1, int z1) {
		if (!dirty) {
			dirty = true;
			numberOfDirtyChunks++;
		}
		if (x0 < dirtyX0) dirtyX0 = x0;
		if (y0 < dirtyY0) dirtyY0 = y0;
		if (z0 < dirtyZ0) dirtyZ0 = z0;
		if (x1 > dirtyLength) dirtyLength = x1;
		if (y1 > dirtyHeight) dirtyHeight = y1;
		if (z1 > dirtyLENGTH) dirtyLENGTH = z1;
	}
	
	/**返回区块是否需要更新*/
	public boolean isDirty() {
		return this.dirty;
	}
	
	public void updateBrightness(World world) {
		dirty = false;
		numberOfDirtyChunks--;
		updateVertical();
		for (int x = dirtyX0; x < dirtyX0 + dirtyLength; x++)
			for (int y = dirtyY0; y < dirtyY0 + dirtyHeight; y++)
				for (int z = dirtyZ0; z < dirtyZ0 + dirtyLENGTH; z++)
					world.updateBrightness(x + x0, y + y0, z + z0);
	}
	
	/*public void save(DataOutputStream out) throws IOException {
		for(int x = 0; x < LENGTH; x++)
			for(int y = 0; y < HEIGHT; y++)
				out.write(blocks[x][y]);
	}
	
	public void read(DataInputStream in) throws IOException {
		this.blocks = new byte [LENGTH][HEIGHT][LENGTH];
		this.heights = new int [LENGTH][LENGTH];
		//this.brightness = new byte [LENGTH][HEIGHT][LENGTH];
		
		this.loaded = true;
		for(int x = 0; x < LENGTH; x++)
			for(int y = 0; y < HEIGHT; y++)
				in.readFully(blocks[x][y]);
	}*/
	
}
