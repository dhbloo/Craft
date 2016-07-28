package craft.level;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

import craft.Player;
import craft.level.levelgen.LevelGen;
import craft.level.levelgen.PlantsGen;
import craft.level.tile.EntityTile;
import craft.level.tile.Face;
import craft.level.tile.LiquidTile;
import craft.level.tile.LiquidTile.LiquidType;
import craft.level.tile.Tile;
import craft.phys.AABB;

public class Level {
	/**������Tick���·�Χ�����*/
	public static final int TICK_LENGTH = 128, TICK_HEIGHT = 64, TICK_WIDTH = 128;
	/**������Tick���µķ�������ȡֵ��Χ0~256�����鲻����5��*/
	public static final float TICK_SPEED = 0.4F;
	/**������ÿ֡������������*/
	public static final byte MAX_UPDATE_PER_FRAME = 4;
	/**��������Ծ����������*/
	public static final int CHUNK_ACTIVE_MAX_DISTANCE = Chunk.LENGTH * Chunk.LENGTH * 6 * 6;
	public String name, createTime;
	/**�����*/
	public int length, width, height;
	/**����߽߱�ֵ*/
	public int x0, y0, z0, x1, y1, z1;
	/**���߶�*/
	public int maxHeight = 0;
	/**X��Z������*/
	protected int xChunks, zChunks;
	/**��������*/
	protected Chunk[][] chunks;
	/**������Ⱦ������*/
	private LevelRendererListener levelRendererListener;
	/**�����*/
	private Random random = new Random();
	/**Tick��δ��������*/
	private int unprocessed = 0;
	/**�����*/
	public float globalBrightness = 0.99F;
	/**δ֪*/
	private int bx0, bx1, by0, by1, bz0, bz1;

	public Level() {

	}

	/**���������ͼ*/
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
						}
				chunks[x][z].setDate(chunkBlocks);
			}
		 
		calcHeights(x0, z0, length, width);
		calcBrightness(x0, y0, z0, length, height, width);
	}

	/**�����յ�ͼ*/
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

	/**��ȡ����ID*/
	public int getBlock(int x, int y, int z) {
		if (x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1)
			return Tile.air.id;
		int chunkx = (int) Math.floor(( x + length / 2.0F) / Chunk.LENGTH);
		int chunkz = (int) Math.floor(( z + width / 2.0F) / Chunk.WIDTH);
		Chunk chunk = chunks[chunkx][chunkz];
		if (!chunk.isLoaded())
			return Tile.air.id;
		x -= (chunkx - xChunks / 2) * Chunk.LENGTH;
		z -= (chunkz - zChunks / 2) * Chunk.WIDTH;
		y %= Chunk.HEIGHT;
		return chunk.getBlock(x, y, z);
	}

	/**���÷���
	 * @param block Ҫ���õķ���ID*/
	public boolean setBlock(int x, int y, int z, int block) {
		if (x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1)
			return false;
		int chunkx = (int) Math.floor(( x + length / 2.0F) / Chunk.LENGTH);
		int chunkz = (int) Math.floor(( z + width / 2.0F) / Chunk.WIDTH);
		Chunk chunk = chunks[chunkx][chunkz];
		if (!chunk.isLoaded())
			return false;
		
		int xl = x - (chunkx - xChunks / 2) * Chunk.LENGTH;
		int zl = z - (chunkz - zChunks / 2) * Chunk.WIDTH;

		Tile oldTile = Tile.tiles[chunk.getBlock(xl, y, zl)];
		Tile newTile = Tile.tiles[block];
		if (!chunk.setBlock(xl, y, zl, (byte) block))
			return false;

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

		blockChanged(x, y, z);
		return true;
	}

	/**ֱ�����÷��飨�������£�
	 * @param block Ҫ���õķ���ID*/
	public boolean setBlockDirectly(int x, int y, int z, int block) {
		if (x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1)
			return false;
		int chunkx = (int) Math.floor(( x + length / 2.0F) / Chunk.LENGTH);
		int chunkz = (int) Math.floor(( z + width / 2.0F) / Chunk.WIDTH);
		Chunk chunk = chunks[chunkx][chunkz];
		if (!chunk.isLoaded())
			return false;
		return chunk.setBlock(x - ((chunkx - xChunks / 2) * Chunk.LENGTH), y % Chunk.HEIGHT, z - ((chunkz - zChunks / 2) * Chunk.WIDTH), (byte) block);
	}

	/**���ڷ��鱻�ı�*/
	private void neighborChanged(int x, int y, int z, int block) {
		Tile tile = Tile.tiles[getBlock(x, y, z)];
		if (tile != Tile.air)
			tile.neighborChanged(this, x, y, z, block);
	}

	/**�Ƿ���������*/
	public boolean isBlock(int x, int y, int z) {
		if (x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1)
			return false;
		return true;
	}

	/**�Ƿ�Ϊ͸������*/
	public boolean isLightBlock(int x, int y, int z) {
		return Tile.tiles[getBlock(x, y, z)].lightTile;
	}

	/**�Ƿ�Ϊ���巽�飨��Ҫ�����ײ�ķ��飩*/
	public boolean isSolidBlock(int x, int y, int z) {
		return Tile.tiles[getBlock(x, y, z)].soildTile;
	}

	protected void setLevelRendererListener(LevelRendererListener levelRendererListener) {
		this.levelRendererListener = levelRendererListener;
	}

	/**��������õ�����*/
	public Chunk blockGetChunk(int x, int z) {
		if (x < x0 || x > x1 || z < z0 || z > z1)
			return null;
		x += length / 2;
		z += width / 2;
		return chunks[(int) Math.floor((float) x / Chunk.LENGTH)][(int) Math.floor((float) z / Chunk.WIDTH)];
	}

	/**����߶ȱ�
	 * @param x0, y0, z0������������
	 * @param x0 ��Χ�߽�
	 * @param z0 ��Χ�߽�
	 * @param x1 ���ȷ�Χ
	 * @param z1 ��ȷ�Χ*/
	public void calcHeights(int x0, int z0, int x1, int z1) {
		if (x1 == length && z1 == width)
			maxHeight = 0;
		
		x0 += length / 2;
		z0 += width / 2;
		
		int xl0 = x0 / Chunk.LENGTH;
		int xl1 = (x1 - 1) / Chunk.LENGTH;
		int zl0 = z0 / Chunk.WIDTH;
		int zl1 = (z1 - 1) / Chunk.WIDTH;

		if (xl0 < 0) xl0 = 0;
		if (zl0 < 0) zl0 = 0;
		if (xl1 >= xChunks) xl1 = xChunks - 1;
		if (zl1 >= zChunks) zl1 = zChunks - 1;

		for (int x = xl0; x <= xl0 + xl1; x++)
			for (int z = zl0; z <= zl0 + zl1; z++) {
				chunks[x][z].calcAllHeights();
			}
	}

	/**���AABB��Χ�ڵ����й��巽���AABB
	 * @param aabb ��ΧAABB
	 * @return ��Χ�����й��巽���AABB*/
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
					if (isSolidBlock(x, y, z))
						aABBs.add(Tile.getAABB(x, y, z));

		return aABBs;
	}

	/**�Ƿ��ڸ߶ȱ��ϣ���Y�����Ƿ�δ���ڵ���*/
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

	/**ȡx,y,z���������*/
	public byte getBrightness(int x, int y, int z) {
		if (x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1)
			return Brightness.MAX;
		int chunkx = (int) Math.floor(( x + length / 2.0F) / Chunk.LENGTH);
		int chunkz = (int) Math.floor(( z + width / 2.0F) / Chunk.WIDTH);
		Chunk chunk = chunks[chunkx][chunkz];
		if (!chunk.isLoaded())
			return Brightness.MAX;
		return chunk.brightness[x - ((chunkx - xChunks / 2) * Chunk.LENGTH)][y % Chunk.HEIGHT][z - ((chunkz - zChunks / 2) * Chunk.WIDTH)];
	}
	
	/**����x,y,z���������
	 * @return ���������Ƿ�ı�*/
	boolean setBrightness(int x, int y, int z, byte brightness) {
		if (x < x0 || x > x1 || y < y0 || y > y1 || z < z0 || z > z1)
			return false;
		int chunkx = (int) Math.floor(( x + length / 2.0F) / Chunk.LENGTH);
		int chunkz = (int) Math.floor(( z + width / 2.0F) / Chunk.WIDTH);
		Chunk chunk = chunks[chunkx][chunkz];
		if (!chunk.isLoaded())
			return false;
		if (brightness < 0 ) brightness = 0;
		if (brightness > Brightness.MAX) brightness = Brightness.MAX;
		if (chunk.brightness[x - ((chunkx - xChunks / 2) * Chunk.LENGTH)][y % Chunk.HEIGHT][z - ((chunkz - zChunks / 2) * Chunk.WIDTH)] == brightness)
			return false;
		chunk.brightness[x - ((chunkx - xChunks / 2) * Chunk.LENGTH)][y % Chunk.HEIGHT][z - ((chunkz - zChunks / 2) * Chunk.WIDTH)] = brightness;
		if (x < bx0) bx0 = x;
		if (x > bx1) bx1 = x;
		if (y < by0) by0 = y;
		if (y > by1) by1 = y;
		if (z < bz0) bz0 = z;
		if (z > bz1) bz1 = z;
		return true;
	}

	/**ȡ����ƽ���߶�*/
	public float getAverageHeight() {
		long totalHeight = 0L;
		for (int x = x0; x <= x1; x++)
			for (int z = x0; z <= z1; z++)
				totalHeight += getHeights(x, z);
		return totalHeight / length / width;
	}

	/**�������*/
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
	}

	/**�ж�AABB��Χ���Ƿ����κ�Һ��
	 * @param aabb ��ΧAABB*/
	public boolean containsAnyLiquid(AABB box) {
		return containsLiquid(box, LiquidType.Water) ? true : containsLiquid(box, LiquidType.Lava) ? true : false;
	}

	/**�ж�AABB��Χ���Ƿ���ָ�������Һ��
	 * @param aabb ��ΧAABB
	 * @param liquidType Һ������*/
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

	/**��������Ե��������ײ��*/
	public ArrayList<AABB> getWallCubes() {
		ArrayList<AABB> aABBs = new ArrayList<AABB>();
		aABBs.add(new AABB(x0 - 1, y0, z0, x0, y1, z1 + 1));
		aABBs.add(new AABB(x1 + 1, y0, z0, x1 + 2, y1, z1 + 1));
		aABBs.add(new AABB(x0, y0, z0 - 1, x1 + 1, y1, z0));
		aABBs.add(new AABB(x0, y0, z1 + 1, x1 + 1, y1, z1 + 2));
		return aABBs;
	}

	/**֪ͨ����ı���*/
	protected void blockChanged(int x, int y, int z) {
		calcHeights(x, z, 1, 1);
		calcVerticalBrightness(x, z);
		updateBrightness(x, y, z);
		if (levelRendererListener != null)
			levelRendererListener.blockChanged(x, y, z);
	}

	/**֪ͨ���ȸı���*/
	protected void lightColumnChanged(int x0, int y0, int z0, int x1, int y1, int z1) {
		if (levelRendererListener != null)
			levelRendererListener.lightColumnChanged(x0, y0, z0, x1, y1, z1);
	}

	/**ȡ��ָ��x,z�ĸ߶ȱ�߶�*/
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
	
	/**�ж������Ƿ��Ѽ���
	 * @param x ����X����
	 * @param z ����Z����*/
	public boolean isChunkLoaded(int x, int z) {
		if (x < 0 || x >= xChunks || z < 0 || z >= zChunks)
			return false;
		return chunks[x][z].isLoaded();
	}
	
	/**δ��*/
	public boolean isBlockActive(int x, int z) {
		Chunk chunk = blockGetChunk(x, z);
		if (chunk != null && chunk.isLoaded() && chunk.active)
			return true;
		else
			return false;
	}
	
	/**�Ƿ�ΪҺ�巽��*/
	public boolean isLiquidBlock(int x, int y, int z) {
		return Tile.tiles[getBlock(x, y, z)] instanceof LiquidTile;
	}
	
	/**�Ƿ�Ϊ�����򷽿�*/
	public boolean isIrregularBlock(int x, int y, int z) {
		return getBlock(x, y, z) == Tile.grassBush.id;
	}
	
	/**��������״̬(active)*/
	public void updateChunkState(Player player) {
		for (int x = 0; x < xChunks; x++)
			for (int z = 0; z < zChunks; z++) {
				if (chunks[x][z].distanceToSqr(player) < CHUNK_ACTIVE_MAX_DISTANCE)
					chunks[x][z].active = true;
				else
					chunks[x][z].active = false;
			}
	}
	
	/**����ָ����Χ�����ȱ仯
	 * @param x0, y0, z0������������
	 * @param x0 ��Χ�߽�
	 * @param y0 �ͷ�Χ�߽�
	 * @param z0 ��Χ�߽�
	 * @param x1 ���ȷ�Χ
	 * @param y1 �߶ȷ�Χ
	 * @param z1 ��ȷ�Χ*/
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

	private void calcVerticalBrightness(int x, int z) {
		if (x < x0 || x > x1 || z < z0 || z > z1)
			return;
		int chunkx = (int) Math.floor(( x + length / 2.0F) / Chunk.LENGTH);
		int chunkz = (int) Math.floor(( z + width / 2.0F) / Chunk.WIDTH);
		Chunk chunk = chunks[chunkx][chunkz];
		if (!chunk.isLoaded())
			return;
		chunk.calcVerticalBrightness(x - ((chunkx - xChunks / 2) * Chunk.LENGTH), z - ((chunkz - zChunks / 2) * Chunk.WIDTH), 1, 1);
	}
	
	/**���ȸ��£�δ����*/
	public void update(Player player) {
		ArrayList<Chunk> dirtyChunks = null;
		for (int x = 0; x < xChunks; x++)
			for (int z = 0; z < zChunks; z++) {
				Chunk chunk = chunks[x][z];
				if (chunk.isDirty()) {
					if (dirtyChunks == null)
						dirtyChunks = new ArrayList<Chunk>(Chunk.numberOfDirtyChunks);
					dirtyChunks.add(chunk);
				}
			}
		if (dirtyChunks == null) return;
		dirtyChunks.sort(new DirtyChunkSorter(player));
		for (int i = 0; (i < dirtyChunks.size()); i++) {
			dirtyChunks.get(i).updateVertical();
		}
	}
	
	/**����ָ����Χ�����ȱ仯
	 * @param x0, y0, z0������������
	 * @param x0 ��Χ�߽�
	 * @param y0 �ͷ�Χ�߽�
	 * @param z0 ��Χ�߽�
	 * @param x1 ���ȷ�Χ
	 * @param y1 �߶ȷ�Χ
	 * @param z1 ��ȷ�Χ*/
	@SuppressWarnings("unused")
	private void calcScatterBrightness(int x0, int y0, int z0, int x1, int y1, int z1) {
		int xa0 = this.x1, xa1 = this.x0, ya0 = this.y1, ya1 = this.y0, za0 = this.z1, za1 = this.z0;
		for (int x = x0; x < x0 + x1; x++)
			for (int z = z0; z < z0 + z1; z++) {
				for (int y = this.y1; y >= this.y0; y--) {
					int nb = getBrightness(x, y, z);
					if (nb <= 1) continue;
					int b0 = nb / -2, b1 = nb / 2;
					for (int xb = b0 + 1; xb < b1; xb++)
						for (int zb = b0 + 1; zb < b1; zb++) {
							if (xb == 0 && zb == 0) continue;
							double dist =  Math.sqrt((b1 - Math.abs(xb)) * (b1 - Math.abs(xb) + (b1 - Math.abs(zb)) * (b1 - Math.abs(zb)) / 2.0));
							byte ab = (byte) (dist * nb / b1);
							if (ab > Brightness.MAX)
								ab = Brightness.MAX;
							if (ab < 0)
								ab = 0;
							int lx = this.x0 + x + xb, lz = this.z0 + z + zb;
							int oldBrightness = 0;
							oldBrightness = getBrightness(lx, y, lz);
							if (oldBrightness < ab)
								setBrightness(lx, y, lz, (byte) ab);
							if (oldBrightness != ab) {
								if (lx < xa0) xa0 = lx;
								if (lx > xa1) xa1 = lx;
								if (y < ya0) ya0 = y;
								if (y > ya1) ya1 = y;
								if (lz < za0) za0 = lz;
								if (lz > za1) za1 = lz;
							}
						}
					
				}
			}
		if (xa1 > xa0 || ya1 > ya0 || za1 > za0)
			lightColumnChanged(xa0, ya0, za0, xa1, ya1, za1);
	}
	
	/**ָ������ķ���ı䣬��������*/
	private void updateBrightness(int x, int y, int z) {
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

		lightColumnChanged(bx0, by0, bz0, bx1, by1, bz1);
		bx0 = x1;
		by0 = y1;
		bz0 = z1;
		bx1 = x0;
		by1 = y0;
		bz1 = z0;
	}

	/**�������͸���ȱ仯������µ�����*/
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

	/**�����������*/
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
				int b = (int) (getBrightness(xa, ya, za) - tile.brightnessDecay * Brightness.MAX + tile.brightness);
				next = new Coord(xa, ya, za);
				if (max > b) {
					if (!list.contains(next))
						list.add(next);
				}
			}

		}
		return;
		
	}
	
	/**��������µ�����*/
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
			byte b = (byte) (getBrightness(xa, ya, za) - tile.brightnessDecay * Brightness.MAX + tile.brightness);
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
	
	/**�������͸���ȱ�С�����䰵�������*/
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
	
	/*private void updateBrightnesso(int x, int y, int z) {
		if (!isBlock(x, y, z)) return;
		boolean changed = false;
		if (isLightBlock(x, y, z)) {
			for (Face f : Face.values()) {
				/*Face fn;
				int xa = x, ya = y, za = z;
				do {
					fn = cheakBrightness(xa, ya, za, f);
					switch (fn) {
						case Bottom: ya--; break;
						case Top: ya++; break;
						case Left: xa--; break;
						case Right: xa++; break;
						case Back: za--; break;
						case Front: za++; break;
					}
				} while (fn != null);
				changed |= cheakBrightness(x, y, z, f);
			}
		} else {
			for (Face f : Face.values()) {  
				cheakDark(x, y, z, (byte) (getBrightness(x, y, z) + 1), f);
			}
			changed = true;
		}
		if (changed) {
			lightColumnChanged(bx0, by0, bz0, bx1, by1, bz1);
			bx0 = x1;
			by0 = y1;
			bz0 = z1;
			bx1 = x0;
			by1 = y0;
			bz1 = z0;
		}
	}
	
	private boolean cheakBrightnesso(int x, int y, int z, Face face) {
		if (!isBlock(x, y, z)) return false;
		byte oldBrightness = getBrightness(x, y, z);
		switch(face) {
		case Bottom: y--; break;
		case Top: y++; break;
		case Left: x--; break;
		case Right: x++; break;
		case Back: z--; break;
		case Front: z++; break;
		}
		Face comeFrom = null;
		// ����y���������
		byte nb = 0;
		// �Ϸ���
		byte b1 = getBrightness(x, y + 1, z);
		if (b1 > nb && isLightBlock(x, y + 1, z)) {
			// �Ϸ���Ĺⷽ��
			byte by = (byte) (b1 - Tile.tiles[getBlock(x, y + 1, z)].brightnessDecay * Brightness.MAX);
			if (by < 0) by = 0;
			if (by > nb) {
				nb = by;
				comeFrom = Face.Top;
			}
			/*if (isLightBlock(x, y + 2, z)) {
				byte b2 = getBrightness(x, y + 2, z);
				int b = b2 - b1;
				if (b > 0) { // ����
				} else if (b < 0) {// ����
				} else {// �޹�
				}
			}*//*
		}
		// �·��� 
		b1 = getBrightness(x, y - 1, z);
		if (b1 > nb && isLightBlock(x, y - 1, z)) {
			// �·���Ĺⷽ�� 
			byte by = (byte) (b1 - Tile.tiles[getBlock(x, y - 1, z)].brightnessDecay * Brightness.MAX);
			if (by < 0) by = 0;
			if (by > nb) {
				nb = by;
				comeFrom = Face.Bottom;
			}
		}
		
		b1 = getBrightness(x - 1, y, z);
		if (b1 > nb && isLightBlock(x - 1, y, z)) {
			byte by = (byte) (b1 - Tile.tiles[getBlock(x - 1, y, z)].brightnessDecay * Brightness.MAX);
			if (by < 0) by = 0;
			if (by > nb) {
				nb = by;
				comeFrom = Face.Left;
			}
		}
		
		b1 = getBrightness(x + 1, y, z);
		if (b1 > nb && isLightBlock(x + 1, y, z)) {
			byte by = (byte) (b1 - Tile.tiles[getBlock(x + 1, y, z)].brightnessDecay * Brightness.MAX);
			if (by < 0) by = 0;
			if (by > nb) {
				nb = by;
				comeFrom = Face.Right;
			}
		}
		
		b1 = getBrightness(x, y, z - 1);
		if (b1 > nb && isLightBlock(x, y, z - 1)) {
			byte by = (byte) (b1 - Tile.tiles[getBlock(x, y, z - 1)].brightnessDecay * Brightness.MAX);
			if (by < 0) by = 0;
			if (by > nb) {
				nb = by;
				comeFrom = Face.Back;
			}
		}
		
		b1 = getBrightness(x, y, z + 1);
		if (b1 > nb && isLightBlock(x, y, z + 1)) {
			byte by = (byte) (b1 - Tile.tiles[getBlock(x, y, z + 1)].brightnessDecay * Brightness.MAX);
			if (by < 0) by = 0;
			if (by > nb) {
				nb = by;
				comeFrom = Face.Front;
			}
		}
		
		if	(oldBrightness == nb || comeFrom == null) return false;
		
		setBrightness(x, y, z, nb);
		
		/*if (comeFrom != Face.Bottom)
			cheakBrightness(x, y, z, Face.Bottom);
		if (comeFrom != Face.Top)
			cheakBrightness(x, y, z, Face.Top);
		if (comeFrom != Face.Left)
			cheakBrightness(x, y, z, Face.Left);
		if (comeFrom != Face.Right)
			cheakBrightness(x, y, z, Face.Right);
		if (comeFrom != Face.Back)
			cheakBrightness(x, y, z, Face.Back);
		if (comeFrom != Face.Front)
			cheakBrightness(x, y, z, Face.Front);
		return true;
	}
	
	private void cheakDarko(int x, int y, int z, byte brightness, Face face) {
		switch(face) {
		case Bottom: y--; break;
		case Top: y++; break;
		case Left: x--; break;
		case Right: x++; break;
		case Back: z--; break;
		case Front: z++; break;
		}
		if (isBlock(x, y, z) && isLightBlock(x, y, z))  {
			int b = getBrightness(x, y, z);
			if (b < brightness) {
				setBrightness(x, y, z, (byte) (b - Tile.tiles[getBlock(x, y, z)].brightnessDecay * Brightness.MAX));
				byte nb = (byte) (brightness - Tile.tiles[getBlock(x, y, z)].brightnessDecay * Brightness.MAX);
				for (Face f : Face.values()) {  
					cheakDark(x, y, z, nb, f);
				}
			} else if (b == brightness && face == Face.Bottom) {
				setBrightness(x, y, z, (byte) (b - Tile.tiles[getBlock(x, y, z)].brightnessDecay * Brightness.MAX));
				byte nb = (byte) (brightness - Tile.tiles[getBlock(x, y, z)].brightnessDecay * Brightness.MAX);
				cheakDark(x, y, z, nb, Face.Bottom);
			}
		}
	}*/
	
}
