package craft.level;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import craft.Player;
import craft.level.Brightness.Brightness;
import craft.level.tile.Tile;
import craft.phys.AABB;

public class Chunk {
	public static final int LENGTH = 16, WIDTH = 16;		//����
	public static int HEIGHT;
	public final Level level;
	public final AABB aabb;
	public final int x0, y0, z0, x1, y1, z1;
	public final float x, y, z;
	/**x,z,y����ID����*/
	private byte[][][] blocks;
	/**x,z�߶�����*/
	private int[][] heights;
	//protected byte[][][] brightness;		//��ά��������
	/**������Ϣ��ά����*/
	protected byte[][][] brightness;
	/**�������߶�*/
	public int maxHeight = 0;
	/**�����Ƿ��Ѽ���*/
	private boolean loaded = false;
	/**�����Ƿ񼤻�*/
	public boolean active = false;
	/**��������Ҫ���µķ�ΧAABB*/
	private AABB dirtyAabb = null;
	/**��Ҫ����������*/
	public static int numberOfDirtyChunks = 0;
	
	/**����������
	 * @param x0 ������߽����������
	 * @param y0 ����ͱ߽����������
	 * @param z0 �����߽����������*/
	public Chunk(Level level, int x0, int y0, int z0) {
		this.level = level;
		Chunk.HEIGHT = level.height;
		this.x0 = x0;
		this.y0 = y0;
		this.z0 = z0;
		this.x1 = x0 + LENGTH - 1;
		this.y1 = y0 + HEIGHT - 1;
		this.z1 = z0 + WIDTH - 1;

		this.x = (x0 + x1 / 2.0F);
		this.y = (y0 + y1 / 2.0F);
		this.z = (z0 + z1 / 2.0F);

		this.aabb = new AABB(x0 - 1.0F, y0 - 1.0F, z0 - 1.0F, x1 + 1.0F, y1 + 1.0F, z1 + 1.0F);
	}

	/**������������*/
	public void setDate(byte[][][] blocks) {
		this.blocks = blocks;
		this.heights = new int [LENGTH][WIDTH];
		this.brightness = new byte [LENGTH][HEIGHT][WIDTH];
		
		this.loaded = true;
	}
	
	/**����ƽ̹����*/
	protected void createFlatTerrain(int h) {
		this.blocks = new byte[LENGTH][HEIGHT][WIDTH];
		this.heights = new int [LENGTH][WIDTH];
		this.brightness = new byte [LENGTH][HEIGHT][WIDTH];
		
		for(int x = 0; x < LENGTH; x++)
			for(int z = 0; z < WIDTH; z++)
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
		
		this.loaded = true;
	}
	
	/**ȡ�����ڷ���
	 * @param ��������ֲ�����*/
	public int getBlock(int x, int y, int z) {
		if (x < 0 || x >= LENGTH || y < 0 || y >= HEIGHT || z < 0 || z >= WIDTH)
			return Tile.air.id;
		return blocks[x][y][z];
	}
	
	/**���������ڷ���
	 * @param ��������ֲ�����
	 * @param block ����ID*/
	public boolean setBlock(int x, int y, int z, int block) {
		if (x < 0 || x >= LENGTH || y < 0 || y >= HEIGHT || z < 0 || z >= WIDTH)
			return false;
		if (blocks[x][y][z] == block)
			return false;
		blocks[x][y][z] = (byte) block;
		return true;
	}
	
	/**����߶ȱ�
	 * @param x0, y0, z0��������ֲ�����
	 * @param x0 ��Χ�߽�
	 * @param z0 ��Χ�߽�
	 * @param x1 ���ȷ�Χ
	 * @param z1 ��ȷ�Χ*/
	public void calcHeights(int x0, int z0, int x1, int z1) {
		if (x1 == LENGTH && z1 == WIDTH)
			maxHeight = 0;
		for (int x = x0; x < x0 + x1; x++)
			for (int z = z0; z < z0 + z1; z++) {
				//int oldDepth = heights[x][z];
				int y = this.y1;
				while (y > this.y0 && this.getBlock(x, y, z) == Tile.air.id)
					y--;
				heights[x][z] = y;
				if (y > maxHeight)
					maxHeight = y;
				/*if (oldDepth != y) {
					int yl0 = oldDepth < y ? oldDepth : y;
					int yl1 = oldDepth > y ? oldDepth : y;
					level.lightColumnChanged(x0 + x, z0 + z, yl0, yl1);
				}*/
			}
		if (maxHeight > level.maxHeight)
			level.maxHeight = maxHeight;
	}
	
	/**����ȫ���߶�*/
	public void calcAllHeights() {
		calcHeights(0, 0, LENGTH, WIDTH);
	}
	
	/**������������
	 * @param x0, y0, z0��������ֲ�����
	 * @param x0 ��Χ�߽�
	 * @param z0 ��Χ�߽�
	 * @param x1 ���ȷ�Χ
	 * @param z1 ��ȷ�Χ*/
	public void calcVerticalBrightness(int x0, int z0, int x1, int z1) {
		int xa0 = level.x1, xa1 = level.x0, ya0 = level.y1, ya1 = level.y0, za0 = level.z1, za1 = level.z0;
		for (int x = x0; x < x0 + x1; x++)
			for (int z = z0; z < z0 + z1; z++) {
				byte oldBrightness = 0;
				for (int y = this.y1; y > this.heights[x][z]; y--) {
					oldBrightness = brightness[x][y][z];
					brightness[x][y][z] = Brightness.MAX;
					if (oldBrightness != brightness[x][y][z]) {
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
				for (int y = this.heights[x][z]; y >= this.y0; y--) {
					oldBrightness = brightness[x][y][z];
					Tile t = Tile.tiles[getBlock(x, y + 1, z)];
					byte bn = Brightness.MAX;
					if (y + 1 < HEIGHT)
						bn = brightness[x][y + 1][z];
					brightness[x][y][z] = (byte) (bn + t.brightness - Brightness.MAX * t.brightnessDecay);
					if (brightness[x][y][z] > Brightness.MAX)
						brightness[x][y][z] = Brightness.MAX;
					if (brightness[x][y][z] < 0) {
						brightness[x][y][z] = 0;
						//break;
					}
					if (oldBrightness != brightness[x][y][z]) {
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
			level.lightColumnChanged(xa0, ya0, za0, xa1, ya1, za1);
	}
	
	/**����ȫ����������*/
	public void calcAllVerticalBrightness() {
		calcVerticalBrightness(0, 0, LENGTH, WIDTH);
	}
	
	/*public void calcHorizontalBrightness(int x0, int z0, int x1, int z1) {
		int xa0 = level.x1, xa1 = level.x0, ya0 = HEIGHT, ya1 = 0, za0 = level.z1, za1 = level.z0;
		for (int x = x0; x < x0 + x1; x++)
			for (int z = z0; z < z0 + z1; z++) {
				for (int y = this.y1; y >= this.y0; y--) {
					int oldBrightness = brightness[x][y][z];
					int ab = 0, b0 = Level.MAX_BRIGHTNESS / -2, b1 = Level.MAX_BRIGHTNESS / 2;
					for (int xb = b0; xb <= b1; xb++)
						for (int zb = b0; zb <= b1; zb++) {
							if (x + xb < 0 || x + xb > 15 || z + zb < 0 || z + zb > 15)
								ab += level.getBrightness(this.x0 + x + xb, y, this.z0 + z + zb) * (b1 - Math.abs(xb) + 1);
							else
								ab += brightness[x + xb][y][z + zb] * (b1 - Math.abs(xb) + 1);
						}
					brightness[x][y][z] = (byte) ( (int) (ab / (Level.MAX_BRIGHTNESS + 1.0F) / (Level.MAX_BRIGHTNESS + 1.0F)  / 169));
					if (brightness[x][y][z] > Level.MAX_BRIGHTNESS)
						brightness[x][y][z] = Level.MAX_BRIGHTNESS;
					if (brightness[x][y][z] < 1)
						brightness[x][y][z] = 1;
					if (oldBrightness != brightness[x][y][z]) {
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
			level.lightColumnChanged(xa0, ya0, za0, xa1, ya1, za1);
	}*/

	/**�Ƿ�Ϊ͸������
	 * @param ��������ֲ�����*/
	public boolean isLightBlock(int x, int y, int z) {
		return Tile.tiles[this.getBlock(x,y,z)].lightTile;
	}
	
	/**������������ݣ��ͷ��ڴ�*/
	public void clearDate() {
		loaded = false;
		blocks = null;
		heights = null;
		brightness = null;
	}
	
	/**���������Ƿ񱻼���*/
	public boolean isLoaded() {
		return this.loaded;
	}
	
	/**�������ָ��x,z�߶�
	 * @param ��������ֲ�����*/
	public int getHeights(int x, int z) {
		return heights[x][z];
	}
	
	/**��þ�����ҵľ��루δ������*/
	public float distanceToSqr(Player player) {
		float xd = player.x - this.x;
		float yd = player.y - this.y;
		float zd = player.z - this.z;
		return xd * xd + yd * yd + zd * zd;
	}
	
	public void updateVertical() {
		int x0 = (int) dirtyAabb.x0, x1 = (int) dirtyAabb.x1, z0 = (int) dirtyAabb.z0, z1 = (int) dirtyAabb.z1;
		calcVerticalBrightness(x0, z0, x1 - x0, z1 - z0);
	}
	
	/*public void updateHorizontal() {
		int x0 = (int) dirtyAabb.x0, x1 = (int) dirtyAabb.x1, z0 = (int) dirtyAabb.z0, z1 = (int) dirtyAabb.z1;
		calcScatterBrightness(x0, z0, x1 - x0, z1 - z0);
		dirtyAabb = null;
		numberOfDirtyChunks--;
	}*/
	
	/**��������ָ����Χ��Ҫ����*/
	public void setDirty(int x0, int y0, int z0, int x1, int y1, int z1) {
		if (x0 < 0) x0 = 0;
		if (y0 < 0) y0 = 0;
		if (z0 < 0) z0 = 0;
		if (x1 >= LENGTH) x1 = LENGTH - 1;
		if (y1 >= HEIGHT) y1 = HEIGHT - 1;
		if (z1 >= WIDTH) z1 = WIDTH - 1;
		if (dirtyAabb == null) {
			dirtyAabb = new AABB(x0, y0, z0, x1, y1, z1);
			numberOfDirtyChunks++;
			return;
		}
		if (x0 < dirtyAabb.x0) dirtyAabb.x0 = x0;
		if (y0 < dirtyAabb.y0) dirtyAabb.y0 = y0;
		if (z0 < dirtyAabb.z0) dirtyAabb.z0 = z0;
		if (x1 > dirtyAabb.x1) dirtyAabb.x1 = x1;
		if (y1 > dirtyAabb.y1) dirtyAabb.y1 = y1;
		if (z1 > dirtyAabb.z1) dirtyAabb.z1 = z1;
	}
	
	/**���������Ƿ���Ҫ����*/
	public boolean isDirty() {
		return dirtyAabb != null;
	}
	
	public void save(DataOutputStream out) throws IOException {
		for(int x = 0; x < LENGTH; x++)
			for(int y = 0; y < HEIGHT; y++)
				out.write(blocks[x][y]);
	}
	
	public void read(DataInputStream in) throws IOException {
		this.blocks = new byte [LENGTH][HEIGHT][WIDTH];
		this.heights = new int [LENGTH][WIDTH];
		//this.brightness = new byte [LENGTH][HEIGHT][WIDTH];
		
		this.loaded = true;
		for(int x = 0; x < LENGTH; x++)
			for(int y = 0; y < HEIGHT; y++)
				in.readFully(blocks[x][y]);
	}
	
}
