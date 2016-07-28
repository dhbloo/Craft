package craft.level;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

import craft.Player;
import craft.entity.EntityList;
import craft.level.levelgen.LevelGen;
import craft.level.levelgen.PlantsGen;
import craft.level.tile.Face;
import craft.level.tile.LiquidTile;
import craft.level.tile.Bush;
import craft.level.tile.Tile;
import craft.level.tile.LiquidTile.LiquidType;
import craft.level.tile.tileEntity.TileEntityList;
import craft.particle.Particle;
import craft.particle.ParticleEngine;
import craft.phys.AABB;

public class Level {
	/**常量：Tick更新范围长宽高*/
	public static final int TICK_LENGTH = 128, TICK_HEIGHT = 64, TICK_WIDTH = 128;
	/**常量：Tick更新的方块数（取值范围0~256，建议不超过5）*/
	public static final float TICK_SPEED = 0.4F;
	/**常量：每帧更新区块数？*/
	public static final byte MAX_UPDATE_PER_FRAME = 4;
	/**常量：活跃区块最大距离*/
	public static final int CHUNK_ACTIVE_MAX_DISTANCE = Chunk.LENGTH * Chunk.WIDTH * 7 * 7;
	/**常量：边界地表高度*/
	public static final int GROUND_LEVEL = 30;
	/**常量：地表宽度*/
	public static final int GROUND_WIDTH = 256;
	public String name, createTime;
	/**长宽高*/
	public int length, width, height;
	/**长宽高边界值*/
	public int x0, y0, z0, x1, y1, z1;
	/**最大高度*/
	public int maxHeight = 0;
	/**X、Z区块数*/
	protected int xChunks, zChunks;
	/**区块数组*/
	protected Chunk[][] chunks;
	/**世界渲染监听器*/
	private LevelRendererListener levelRendererListener;
	/**随机数*/
	private Random random = new Random();
	/**Tick中未处理方块数*/
	private int unprocessed = 0;
	/**随机数*/
	public float globalBrightness = 0.99F;
	/**亮度更新管理*/
	private BrightnessChangedListener bListener = new BrightnessChangedListener(this);
	/**活动实体数组*/
	public EntityList entityList = new EntityList(this);
	/**实体方块数组*/
	public TileEntityList tileEntityList = new TileEntityList(this, this.random);
	/**粒子系统*/
	public ParticleEngine particleEngine = new ParticleEngine(this);

	public Level() {
		
	}

	/**创建随机地图
	 * @param 参数必须为16的倍数*/
	public void createNewMap(int length, int width, int height) {
		this.length = length;
		this.width = width;
		this.height = height;
		this.name = "New Map";
		this.createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		x0 = -length / 2;
		y0 = 0;
		z0 = -width / 2;
		x1 = length / 2 - 1;
		y1 = height - 1;
		z1 = width / 2 - 1;

		xChunks = length / Chunk.LENGTH;
		zChunks = width / Chunk.WIDTH;
		
		this.chunks = new Chunk[xChunks][zChunks];
		
		byte[][][] blocks = new LevelGen(length, height, width, random).generateNewLevel();
		blocks = new PlantsGen(blocks, random).generatePlants(length * width / 500, length * width / 50);
		for (int x = 0; x < xChunks; x++)
			for (int z = 0; z < zChunks; z++) {
				int xl0 = x0 + x * 16;
				int zl0 = z0 + z * 16;
				chunks[x][z] = new Chunk(this, xl0, y0, zl0);
				byte[][][] chunkBlocks = new byte[Chunk.LENGTH][Chunk.HEIGHT][Chunk.WIDTH];
				for (int xl = 0; xl < Chunk.LENGTH; xl++)
					for (int yl = 0; yl < Chunk.HEIGHT; yl++)
						for (int zl = 0; zl < Chunk.WIDTH; zl++) {
							chunkBlocks[xl][yl][zl] = blocks[xl0 + xl + length / 2][y0 + yl][zl0 + zl + width / 2];
							/*if (yl >= 0 && yl < 45)
								chunkBlocks[xl][yl][zl] = (byte) Tile.stone.id;
							if (yl >= 45 && yl < 60)
								chunkBlocks[xl][yl][zl] = (byte) Tile.soil.id;
							if (yl == 60)
								chunkBlocks[xl][yl][zl] = (byte) Tile.grass.id;
							if (yl == 61 && random.nextDouble() < 0.2)
								chunkBlocks[xl][yl][zl] = (byte) Tile.leaf.id;
							else if (yl == 61 && random.nextDouble() < 0.2)
								chunkBlocks[xl][yl][zl] = (byte) Tile.grassBush.id;
							else if (yl == 61 && random.nextDouble() < 0.2)
								chunkBlocks[xl][yl][zl] = (byte) Tile.water.id;*/
						}
				chunks[x][z].setDate(chunkBlocks);
			}
		 
		calcHeights(x0, z0, length, width);
		calcBrightness(x0, y0, z0, length, height, width);
		//setBrightnessAllUpdate();
	}

	/**创建空地图
	 * @param 参数必须为16的倍数*/
	protected void createEmptyMap(int length, int width, int height) {
		this.length = length;
		this.width = width;
		this.height = height;
		x0 = -length / 2;
		y0 = 0;
		z0 = -width / 2;
		x1 = length / 2 - 1;
		y1 = height - 1;
		z1 = width / 2 - 1;

		xChunks = length / Chunk.LENGTH;
		zChunks = width / Chunk.WIDTH;
		
		this.chunks = new Chunk[xChunks][zChunks];

		for (int x = 0; x < xChunks; x++)
			for (int z = 0; z < zChunks; z++) {
				int xl0 = x0 + x * 16;
				int zl0 = z0 + z * 16;
				chunks[x][z] = new Chunk(this, xl0, y0, zl0);
			}
	}

	/**获取方块ID*/
	public int getBlock(int x, int y, int z) {
		if (x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1)
			return Tile.air.id;
		int xl = x + length / 2;
		int zl = z + width / 2;
		int chunkx = xl >> Chunk.LENGTH_SHIFT_COUNT;
		int chunkz = zl >> Chunk.WIDTH_SHIFT_COUNT;
		Chunk chunk = chunks[chunkx][chunkz];
		if (!chunk.isLoaded())
			return Tile.air.id;
		xl -= chunkx << Chunk.LENGTH_SHIFT_COUNT;
		zl -= chunkz << Chunk.WIDTH_SHIFT_COUNT;
		return chunk.getBlock(xl, y, zl);
	}

	/**设置方块
	 * @param block 要设置的方块ID*/
	public boolean setBlock(int x, int y, int z, int block) {
		if (!setBlockDirectly(x, y, z, block)) return false;
			
		updateNeighborsAt(x, y, z, block);
		neighborChanged(x, y, z, block);
		blockChanged(x, y, z);
		return true;
	}

	/**直接设置方块（不检测更新）
	 * @param block 要设置的方块ID*/
	public boolean setBlockDirectly(int x, int y, int z, int block) {
		if (x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1)
			return false;
		int xl = x + length / 2;
		int zl = z + width / 2;
		int chunkx = xl >> Chunk.LENGTH_SHIFT_COUNT;
		int chunkz = zl >> Chunk.WIDTH_SHIFT_COUNT;
		Chunk chunk = chunks[chunkx][chunkz];
		if (!chunk.isLoaded())
			return false;
		
		xl -= chunkx << Chunk.LENGTH_SHIFT_COUNT;
		zl -= chunkz << Chunk.WIDTH_SHIFT_COUNT;
		if (hasTileEntity(x, y, z))
			tileEntityList.remove(x, y, z);
		return chunk.setBlock(xl, y, zl, (byte) block);
	}

	/**更新相邻方块*/
	public void updateNeighborsAt(int x, int y, int z, int block) {
		neighborChanged(x - 1, y, z, block);
		neighborChanged(x + 1, y, z, block);
		neighborChanged(x, y - 1, z, block);
		neighborChanged(x, y + 1, z, block);
		neighborChanged(x, y, z - 1, block);
		neighborChanged(x, y, z + 1, block);
	}
	
	/**相邻方块被改变*/
	private void neighborChanged(int x, int y, int z, int block) {
		Tile tile = Tile.tiles[getBlock(x, y, z)];
		if (tile != Tile.air)
			tile.neighborChanged(this, x, y, z, block);
	}

	/**是否在世界内*/
	public boolean isBlock(int x, int y, int z) {
		if (x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1)
			return false;
		return true;
	}

	/**是否为透明方块*/
	public boolean isLightBlock(int x, int y, int z) {
		Tile tile = Tile.tiles[getBlock(x, y, z)];
		return tile == Tile.air ? true : tile.isLightTile();
	}

	/**是否为固体方块（需要检测碰撞的方块）*/
	public boolean isSolidBlock(int x, int y, int z) {
		return Tile.tiles[getBlock(x, y, z)].soildTile;
	}

	/**是否为空气*/
	public boolean isAirBlock(int x, int y, int z) {
		return getBlock(x, y, z) == Tile.air.id;
	}
	
	/**设置世界更新监听器*/
	public void setLevelRendererListener(LevelRendererListener levelRendererListener) {
		this.levelRendererListener = levelRendererListener;
	}

	/**方块坐标得到区块*/
	public Chunk blockGetChunk(int x, int z) {
		if (x < x0 || x > x1 || z < z0 || z > z1)
			return null;
		x += length / 2;
		z += width / 2;
		return chunks[(int) Math.floor((float) x / Chunk.LENGTH)][(int) Math.floor((float) z / Chunk.WIDTH)];
	}

	/**计算高度表
	 * @param x0, y0, z0传入世界坐标
	 * @param x0 左范围边界
	 * @param z0 后范围边界
	 * @param x1 长度范围
	 * @param z1 宽度范围*/
	public void calcHeights(int x0, int z0, int x1, int z1) {
		if (x1 == length && z1 == width)
			maxHeight = 0;
		
		x0 += length / 2;
		z0 += width / 2;
		
		int xl0 = x0 >> Chunk.LENGTH_SHIFT_COUNT;
		int xl1 = (x1 - 1) >> Chunk.LENGTH_SHIFT_COUNT;
		int zl0 = z0 >> Chunk.WIDTH_SHIFT_COUNT;
		int zl1 = (z1 - 1) >> Chunk.WIDTH_SHIFT_COUNT;

		if (xl0 < 0) xl0 = 0;
		if (zl0 < 0) zl0 = 0;
		if (xl1 >= xChunks) xl1 = xChunks - 1;
		if (zl1 >= zChunks) zl1 = zChunks - 1;

		for (int x = xl0; x <= xl0 + xl1; x++)
			for (int z = zl0; z <= zl0 + zl1; z++) {
				chunks[x][z].calcAllHeights();
			}
	}

	/**获得AABB范围内的所有固体方块的AABB
	 * @param aabb 范围AABB
	 * @return 范围内所有固体方块的AABB*/
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
				for (int z = z0; z <= z1; z++) {
					Tile tile = Tile.tiles[getBlock(x, y, z)];
					if (tile != Tile.air) {
						AABB blockAABB = tile.getAABB(x, y, z);
						if (blockAABB != null)
							aABBs.add(blockAABB);
					}
				}

		return aABBs;
	}

	/**是否在高度表上（在Y轴上是否未被遮挡）*/
	public boolean isLit(int x, int y, int z) {
		if (x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1)
			return true;
		int ya = y1;
		while (ya > y0 && this.isLightBlock(x, ya, z))
			ya--;
		if (y >= ya)
			return true;
		else
			return false;
	}

	/**取x,y,z坐标的亮度*/
	public byte getBrightness(int x, int y, int z) {
		if (x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1)
			return Brightness.MAX;
		int xl = x + length / 2;
		int zl = z + width / 2;
		int chunkx = xl >> Chunk.LENGTH_SHIFT_COUNT;
		int chunkz = zl >> Chunk.WIDTH_SHIFT_COUNT;
		Chunk chunk = chunks[chunkx][chunkz];
		if (!chunk.isLoaded())
			return Brightness.MAX;
		xl -= chunkx << Chunk.LENGTH_SHIFT_COUNT;
		zl -= chunkz << Chunk.WIDTH_SHIFT_COUNT;
		return chunk.brightness[xl][y][zl];
	}
	
	/**设置x,y,z坐标的亮度
	 * @return 返回亮度是否改变*/
	boolean setBrightness(int x, int y, int z, byte brightness) {
		if (x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1)
			return false;
		int xl = x + length / 2;
		int zl = z + width / 2;
		int chunkx = xl >> Chunk.LENGTH_SHIFT_COUNT;
		int chunkz = zl >> Chunk.WIDTH_SHIFT_COUNT;
		Chunk chunk = chunks[chunkx][chunkz];
		if (!chunk.isLoaded())
			return false;
		if (brightness < 0 ) brightness = 0;
		if (brightness > Brightness.MAX) brightness = Brightness.MAX;
		xl -= chunkx << Chunk.LENGTH_SHIFT_COUNT;
		zl -= chunkz << Chunk.WIDTH_SHIFT_COUNT;
		if (chunk.brightness[xl][y][zl] == brightness)
			return false;
		chunk.brightness[xl][y][zl] = brightness;
		bListener.brightnessChanged(x, y, z, 0, 0, 0);
		return true;
	}

	/**取世界平均高度*/
	public float getAverageHeight() {
		long totalHeight = 0L;
		for (int x = x0; x <= x1; x++)
			for (int z = x0; z <= z1; z++)
				totalHeight += getHeights(x, z);
		return totalHeight / length / width;
	}

	/**世界更新*/
	public void tick(Player player) {
		unprocessed += TICK_LENGTH * TICK_HEIGHT * TICK_WIDTH;
		int ticks = (int) (unprocessed / (256 / TICK_SPEED));
		unprocessed -= ticks * (256 / TICK_SPEED);
		int xp = (int) (player.x - TICK_LENGTH / 2);
		int yp = (int) (player.y - TICK_HEIGHT / 2);
		int zp = (int) (player.z - TICK_WIDTH / 2);
		for (int i = 0; i < ticks; i++) {
			int x = xp + random.nextInt(TICK_LENGTH);
			int y = yp + random.nextInt(TICK_HEIGHT);
			int z = zp + random.nextInt(TICK_WIDTH);
			if (isBlockActive(x, z) && getBlock(x, y, z) != Tile.air.id)
				Tile.tiles[getBlock(x, y, z)].tick(this, x, y, z, random);
		}
		
		//globalBrightness = (float) (Math.sin(System.currentTimeMillis() / 1000.0D) + 1.0D) / 2.5F + 2.0F;
	}

	/**判断AABB范围内是否有任何液体
	 * @param aabb 范围AABB*/
	public boolean containsAnyLiquid(AABB box) {
		return containsLiquid(box, LiquidType.Water) ? true : containsLiquid(box, LiquidType.Lava) ? true : false;
	}

	/**判断AABB范围内是否有指定种类的液体
	 * @param aabb 范围AABB
	 * @param liquidType 液体种类*/
	public boolean containsLiquid(AABB box, LiquidType liquidType) {
		int x0 = (int) Math.floor(box.x0);
		int x1 = (int) Math.floor(box.x1 + 1.0F);
		int y0 = (int) Math.floor(box.y0);
		int y1 = (int) Math.floor(box.y1 + 1.0F);
		int z0 = (int) Math.floor(box.z0);
		int z1 = (int) Math.floor(box.z1 + 1.0F);

		if (x0 < this.x0)
			x0 = this.x0;
		if (y0 < this.y0)
			y0 = this.y0;
		if (z0 < this.z0)
			z0 = this.z0;
		if (x1 > this.x1)
			x1 = this.x1;
		if (y1 > this.y1)
			y1 = this.y1;
		if (z1 > this.z1)
			z1 = this.z1;

		for (int x = x0; x < x1; x++)
			for (int y = y0; y < y1; y++)
				for (int z = z0; z < z1; z++) {
					Tile tile = Tile.tiles[getBlock(x, y, z)];
					if ((tile instanceof LiquidTile) && ((LiquidTile) tile).getType() == liquidType)
						return true;
				}
		return false;
	}

	/**获得世界边缘的隐形碰撞箱*/
	public ArrayList<AABB> getWallCubes() {
		ArrayList<AABB> aABBs = new ArrayList<AABB>();
		aABBs.add(new AABB(x0 - 1, y0, z0, x0, y1, z1 + 1));
		aABBs.add(new AABB(x1 + 1, y0, z0, x1 + 2, y1, z1 + 1));
		aABBs.add(new AABB(x0, y0, z0 - 1, x1 + 1, y1, z0));
		aABBs.add(new AABB(x0, y0, z1 + 1, x1 + 1, y1, z1 + 2));
		aABBs.add(new AABB(x0 - GROUND_WIDTH, y0, z0 - GROUND_WIDTH, x0, y0 + GROUND_LEVEL, z1 + GROUND_WIDTH));
		aABBs.add(new AABB(x1 + 1, y0, z0 - GROUND_WIDTH, x1 + GROUND_WIDTH, y0 + GROUND_LEVEL, z1 + GROUND_WIDTH));
		aABBs.add(new AABB(x0 - GROUND_WIDTH, y0, z0 - GROUND_WIDTH, x1 + GROUND_WIDTH, y0 + GROUND_LEVEL, z0));
		aABBs.add(new AABB(x0 - GROUND_WIDTH, y0, z1 + 1, x1 + GROUND_WIDTH, y0 + GROUND_LEVEL, z1 + GROUND_WIDTH));
		return aABBs;
	}

	/**通知方块改变了*/
	protected void blockChanged(int x, int y, int z) {
		calcHeights(x, z, 1, 1);
		calcVerticalBrightness(x, z);
		//setBrightnessUpdate(x, y, z, 1, 1, 1);
		updateBrightness(x, y, z);
		if (levelRendererListener != null)
			levelRendererListener.blockChanged(x, y, z);
	}

	private void calcVerticalBrightness(int x, int z) {
		int chunkx = (int) Math.floor((x + length / 2.0F) / Chunk.LENGTH);
		int chunkz = (int) Math.floor((z + width / 2.0F) / Chunk.WIDTH);
		Chunk chunk = chunks[chunkx][chunkz];
		if (!chunk.isLoaded())
			return;
		chunk.calcVerticalBrightness(x - ((chunkx - xChunks / 2) * Chunk.LENGTH), z - ((chunkz - zChunks / 2) * Chunk.WIDTH), 1, 1);
	}

	/**通知亮度改变了*/
	protected void lightColumnChanged(int x0, int y0, int z0, int x1, int y1, int z1) {
		if (levelRendererListener != null)
			levelRendererListener.lightColumnChanged(x0, y0, z0, x1, y1, z1);
	}

	/**取得指定x,z的高度表高度*/
	public int getHeights(int x, int z) {
		if (x < x0 || x > x1 || z < z0 || z > z1)
			return 0;
		x += length / 2;
		z += width / 2;
		int chunkx = (int) Math.floor((float) x / Chunk.LENGTH);
		int chunkz = (int) Math.floor((float) z / Chunk.WIDTH);
		x -= chunkx * Chunk.LENGTH;
		z -= chunkz * Chunk.WIDTH;
		return chunks[chunkx][chunkz].getHeights(x, z);
	}
	
	/**判断区块是否已加载
	 * @param x 区块X坐标
	 * @param z 区块Z坐标*/
	public boolean isChunkLoaded(int x, int z) {
		if (x < 0 || x >= xChunks || z < 0 || z >= zChunks)
			return false;
		return chunks[x][z].isLoaded();
	}
	
	/**返回指定方块所在区块是否激活
	 * @param 方块全局坐标*/
	public boolean isBlockActive(int x, int z) {
		Chunk chunk = blockGetChunk(x, z);
		if (chunk != null && chunk.isLoaded() && chunk.active)
			return true;
		else
			return false;
	}
	
	/**是否为液体方块*/
	public boolean isLiquidBlock(int x, int y, int z) {
		return Tile.tiles[getBlock(x, y, z)] instanceof LiquidTile;
	}
	
	/**是否为不规则方块*/
	public boolean isIrregularBlock(int x, int y, int z) {
		return Tile.tiles[getBlock(x, y, z)] instanceof Bush;
	}
	
	/**更新区块状态(active)*/
	public void updateChunkState(Player player) {
		for (int x = 0; x < xChunks; x++)
			for (int z = 0; z < zChunks; z++) {
				if (chunks[x][z].distanceToSqr(player) < CHUNK_ACTIVE_MAX_DISTANCE)
					chunks[x][z].active = true;
				else
					chunks[x][z].active = false;
			}
	}
	
	/**计算指定范围的亮度变化
	 * @param x0, y0, z0传入世界坐标
	 * @param x0 左范围边界
	 * @param y0 低范围边界
	 * @param z0 后范围边界
	 * @param x1 长度范围
	 * @param y1 高度范围
	 * @param z1 宽度范围*/
	public void calcBrightness(int x0, int y0, int z0, int x1, int y1, int z1) {
		x0 += length / 2;
		z0 += width / 2;
		
		int xl0 = x0 / Chunk.LENGTH;
		int xl1 = x1 / Chunk.LENGTH;
		int zl0 = z0 / Chunk.WIDTH;
		int zl1 = z1 / Chunk.WIDTH;

		if (xl0 < 0) xl0 = 0;
		if (zl0 < 0) zl0 = 0;
		if (xl1 >= xChunks) xl1 = xChunks - 1;
		if (zl1 >= zChunks) zl1 = zChunks - 1;

		for (int x = xl0; x <= xl0 + xl1; x++)
			for (int z = zl0; z <= zl0 + zl1; z++)
				chunks[x][z].calcAllVerticalBrightness();
		
		x0 -= length / 2;
		z0 -= width / 2;
		
		for (int x = x0; x <= x0 + x1; x++)
			for (int z = z0; z <= z0 + z1; z++)
				for (int y = getHeights(x, z) + 1; y > 0; y--)
					if (isLightBlock(x, y, z))
						updateBrightness(x, y, z);
	}
	
	/**亮度更新（未定）*/
	public void update(Player player) {
		if (Chunk.numberOfDirtyChunks == 0) return;
		ArrayList<Chunk> dirtyChunks = new ArrayList<Chunk>(Chunk.numberOfDirtyChunks);
		for (int x = 0; x < xChunks; x++)
			for (int z = 0; z < zChunks; z++) {
				Chunk chunk = chunks[x][z];
				if (chunk.isDirty()) {
					dirtyChunks.add(chunk);
				}
			}
		//dirtyChunks.sort(new DirtyChunkSorter(player));
		for (int i = 0; (i < dirtyChunks.size()) && (i < MAX_UPDATE_PER_FRAME); i++) {
			dirtyChunks.get(i).updateBrightness(this);
		}
	}
	
	/**设置指定范围的亮度更新*/
	public void setBrightnessUpdate(int x0, int y0, int z0, int x1, int y1, int z1) {
		for (int x = x0; x <= x0 + x1; x++)
			for (int z = z0; z <= z0 + z1; z++) {
				for (int y = y0 + y1; y > y0; y--)
					if (isLightBlock(x, y, z)) {
						int chunkx = (int) Math.floor(( x + length / 2.0F) / Chunk.LENGTH);
						int chunkz = (int) Math.floor(( z + width / 2.0F) / Chunk.WIDTH);
						Chunk chunk = chunks[chunkx][chunkz];
						x -= (chunkx - xChunks / 2) * Chunk.LENGTH;
						z -= (chunkz - zChunks / 2) * Chunk.WIDTH;
						y %= Chunk.HEIGHT;
						chunk.setDirty(x, y, z, x, y, z);
					}
			}
		System.out.println("haha");
	}
	
	public void setBrightnessAllUpdate() {
		for (int x = 0; x < xChunks; x++)
			for (int z = 0; z < zChunks; z++) {
				int y1 = getHeights(x, z) + 1;
				int y0 = 0;
				chunks[x][z].setDirty(0, y0, 0, Chunk.LENGTH, y1, Chunk.WIDTH);
			}
	}
	
	/**指定坐标的方块改变，更新亮度*/
	protected void updateBrightness(int x, int y, int z) {
		if (!isBlock(x, y, z)) return;
		if (getBlock(x, y, z) == Tile.air.id) {
			cheakBrightness(x, y, z);
		} else {
			LinkedList<Coord> cleanList = new LinkedList<Coord>();
			LinkedList<Coord> updateList = new LinkedList<Coord>();
			if (isLightBlock(x, y, z))
				updateList.add(new Coord(x, y, z));
			cheakDark(cleanList, updateList, x, y, z);
			int i = 0;
			while (i < cleanList.size()) {
				Coord c = cleanList.get(i);
				cheakDark(cleanList, updateList, c.x, c.y, c.z);
				i++;
			}
			for (i = 0; i < cleanList.size(); i++) {
				Coord c = cleanList.get(i);
				setBrightness(c.x, c.y, c.z, (byte) 0);
			}
			for (i = 0; i < updateList.size(); i++) {
				Coord c = updateList.get(i);
				cheakBrightness(c.x, c.y, c.z);
			}
		}
		bListener.update();
	}

	/**当方块的透明度变化，检测新的亮度*/
	private void cheakBrightness(int x, int y, int z) {
		LinkedList<Coord> list = new LinkedList<Coord>();
		if (y <= getHeights(x, z))
			cheakBright(list, x, y, z);
		else
			cheakUpToDownPath(list, x, y, z);
		for (int i = 0; i < list.size(); i++) {
			Coord c = list.get(i);
			cheakBright(list, c.x, c.y, c.z);
		}
	}

	/**检测纵向亮度*/
	private void cheakUpToDownPath(LinkedList<Coord> list, int x, int y, int z) {
		Coord next;
		for (int hi = y; hi > getHeights(x, z); hi--) {
			int max = getBrightness(x, hi, z);
			for (Face f : Face.values()) {
				int xa = x, ya = hi, za = z;
				switch (f) {
				case Bottom: ya--; continue;
				case Top: ya++; continue;
				case Left: xa--; break;
				case Right: xa++; break;
				case Back: za--; break;
				case Front: za++; break;
				}
				if (!isLightBlock(xa, ya, za) || (ya > getHeights(xa, za)))
					continue;
				Tile tile = Tile.tiles[getBlock(xa, ya, za)];
				int b = (int) (getBrightness(xa, ya, za) - tile.opacity * Brightness.MAX + tile.brightness);
				next = new Coord(xa, ya, za);
				if (max > b) {
					if (!list.contains(next))
						list.add(next);
				}
			}

		}
		return;
		
	}
	
	/**遍历检测新的亮度*/
	private void cheakBright(LinkedList<Coord> updateList,int x, int y, int z) {
		int max = 0;
		LinkedList<Brightness> shouldUpdateList = new LinkedList<Brightness>();
		Brightness next;
		for (Face f : Face.values()) {
			int xa = x, ya = y, za = z;
			switch (f) {
			case Bottom: ya--; break;
			case Top: ya++; break;
			case Left: xa--; break;
			case Right: xa++; break;
			case Back: za--; break;
			case Front: za++; break;
			}
			if(!isLightBlock(xa, ya, za))
				continue;
			Tile tile = Tile.tiles[getBlock(xa, ya, za)];
			byte b = (byte) (getBrightness(xa, ya, za) - tile.opacity * Brightness.MAX + tile.brightness);
			next = new Brightness(xa, ya, za, b);
			shouldUpdateList.add(next);
			if (max < b) {
				max = b;
			}
		}

		for (ListIterator<Brightness> it = shouldUpdateList.listIterator(); it.hasNext(); ) {
			Brightness brightness = it.next();
			if (max == brightness.b)
				it.remove();
		}
		if (setBrightness(x, y, z, (byte) max))
			for (Brightness brightness : shouldUpdateList)
				//if (getBrightness(temp.x, temp.y, temp.z) > getBrightness(coord.x, coord.y, coord.z))
				if (max > brightness.b)
					if (!updateList.contains(brightness))
						updateList.add(brightness);
		return;
	}
	
	/**当方块的透明度变小，检测变暗后的亮度*/
	private boolean cheakDark(LinkedList<Coord> cleanList, LinkedList<Coord> updateList, int x, int y, int z) {
		int nb = getBrightness(x, y, z);
		for (Face f : Face.values()) {
			int xa = x, ya = y, za = z;
			switch (f) {
			case Bottom: ya--; break;
			case Top: ya++; break;
			case Left: xa--; break;
			case Right: xa++; break;
			case Back: za--; break;
			case Front: za++; break;
			}
			if (isLightBlock(xa, ya, za)) {
				Coord coord = new Coord(x, y, z);
				Coord b = new Coord(xa, ya, za);
				if (getBrightness(xa, ya, za) < nb) {
					if (!cleanList.contains(b))
						cleanList.add(b);
				} else if ((getBrightness(xa, ya, za) == nb)) {
					if (f == Face.Bottom) {
						if (!cleanList.contains(b))
							cleanList.add(b);
					} else {
						if (!updateList.contains(coord))
							updateList.add(coord);
					}
				} else if (f != Face.Top)
					if (!updateList.contains(coord))
						updateList.add(coord);
			}
		}
		//setBrightness(x, y, z, (byte) 0);
		return true;
	}
	
	/**指定位置是否有实体方块*/
	public boolean hasTileEntity(int x, int y, int z) {
		return tileEntityList.hasTileEntity(x, y, z);
	}
	
	/**删除指定坐标的实体方块*/
	public void removeTileEntity(int x, int y, int z) {
		tileEntityList.remove(x, y, z);
	}
	
	/**添加一个粒子到粒子系统*/
	public void addParticle(Particle p) {
		particleEngine.add(p);
	}
	
	
}
