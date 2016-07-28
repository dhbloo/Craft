package craft.level.levelgen;

import java.util.Random;

import craft.level.tile.Tile;

public class LevelGen {
	public static final int SEA_HEIGHT = 64;
	private int length, height, width;
	private NoiseMap noiseMap;
	private Random random;
	private byte[][][] blocks;

	public LevelGen(int length, int height, int width, Random random) {
		this.length = length;
		this.height = height;
		this.width = width;
		this.noiseMap = new NoiseMap(random);
		this.random = random;
		this.blocks = new byte [length][height][width];
	}

	public byte[][][] generateNewLevel() {
		generateNewMap();
		generateCaves(20 + random.nextInt(30));
		generateSoil(30 + random.nextInt(20));
		addLiquid(10 + random.nextInt(5), 0.8F);
		//generateSandAndWater();
		return blocks;
	}

	private void generateNewMap() {
		float[][][] heightMap = new float[3][][];
		for(int i = 0; i < 3; i++)
			heightMap[i] = noiseMap.generatePerlinNoise(length, width, 6, 0.5F);
		float min = 0;
		float p = 0;
		for(int x = 0; x < length; x++)
			for(int z = 0; z < width; z++) {
				blocks[x][0][z] = (byte) Tile.bedrock.id;

				min = 40;
				p = (50 - min);
				int yh = (int) (min + heightMap[0][x][z] * p);
				if(yh < 0)  yh = 0;
				if(yh >= height)  yh = height  - 1;
				for(int y = 1; y < yh; y++)
					blocks[x][y][z] = (byte) Tile.stone.id;

				min = 45;
				p = (65 - min);
				yh = (int) (min + heightMap[1][x][z] * p);
				if(yh < 0)  yh = 0;
				if(yh >= height)  yh = height  - 1;
				for(int y = 1; y < yh; y++) {
					if(blocks[x][y][z] == Tile.air.id) {
						blocks[x][y][z] = (byte) Tile.soil.id;
						if(y == yh - 1)
							blocks[x][y][z] = (byte) Tile.grass.id;
					}
				}
				/*
				min = 45;
				p = (54 - min);
				yh = (int) (min + heightMap[2][x][z] * p);
				if(yh < 0)  yh = 0;
				if(yh >= height)  yh = height  - 1;
				for(int y = 40; y < yh; y++) {
					if(map[x][y][z] == Tile.air.id) {
						map[x][y][z] = (byte) Tile.sand.id;
						if(y == yh - 1) {
							map[x][y][z] = (byte) Tile.water.id;
							if(x + 1 < length && map[x + 1][y][z] == Tile.air.id) map[x + 1][y][z] = (byte) Tile.water.id;
							if(x - 1 >= 0 && map[x - 1][y][z] == Tile.air.id) map[x - 1][y][z] = (byte) Tile.water.id;
							if(z + 1 < width && map[x][y][z + 1] == Tile.air.id) map[x + 1][y][z + 1] = (byte) Tile.water.id;
							if(z - 1 >= 0 && map[x][y][z - 1] == Tile.air.id) map[x + 1][y][z - 1] = (byte) Tile.water.id;
						}
					}
				}*/
			}
	}

	private void generateCaves(int cavesCount) {
		for(int i = 0; i < cavesCount; i++) {
			int x = random.nextInt(length);
			int y = random.nextInt(40) + 5;
			int z = random.nextInt(width);
			setBlockAir(x, y, z, 5 + (int) (random.nextDouble() * 5) + (random.nextDouble() < 0.8 ? 0 : (int) (random.nextDouble() * 10)));
		}
	}

	private void generateSoil(int count) {
		for(int i = 0; i < count; i++) {
			int x = random.nextInt(length);
			int y = random.nextInt(40) + 5;
			int z = random.nextInt(width);
			setBlockSoil(x, y, z, 7 + (int) (random.nextDouble() * 5));
		}
	}
	
	private void addLiquid(int count, float alpha) {
		for(int i = 0; i < count; i++) {
			int x = random.nextInt(length);
			int y = random.nextInt(40) + 5;
			int z = random.nextInt(width);
			if(getBlock(x, y, z) == Tile.air) {
				while(--y > 0 && getBlock(x, y, z) == Tile.air);
				y++;
				if (random.nextFloat() <= alpha) {
					setBlock(x, y, z, Tile.water);
					Tile.water.add(x - length / 2, y, z - width / 2);
				} else {
					setBlock(x, y, z, Tile.lava);
					Tile.lava.add(x - length / 2, y, z - width / 2);
				}
			} else
				count++;
			if(i > 5000)	return;
		}
	}
	
	@SuppressWarnings("unused")
	private void generateSandAndWater() {
		float[][] heightMap = noiseMap.generatePerlinNoise(length, width, 6, 0.5F);
		for(int x = 0; x < length; x++)
			for(int z = 0; z < width; z++) {
				int min = 45;
				int p = (57 - min);
				int yh = (int) (min + heightMap[x][z] * p);
				if(yh < 0)  yh = 0;
				if(yh >= height)  yh = height  - 1;
				if(blocks[x][yh][z] == Tile.air.id)
					for(int y = randInt(5) + min; y < yh; y++) {
						if(blocks[x][y][z] == Tile.air.id || blocks[x][y][z] == Tile.grass.id) {
							blocks[x][y][z] = (byte) Tile.sand.id;
							if(y == yh - 1 && y < 52)
								blocks[x][y][z] = (byte) Tile.water.id;
						}
					}
			}
	}
	
 	private void setBlockAir(int x, int y, int z, int depth) {
		if(randInt(100) > 90)
			depth -= random.nextInt(2);
		//if(randInt(100) > 98)
		//	depth += random.nextInt(2);
		if(getBlock(x, y, z) == Tile.stone && depth >= 0) {
			setBlock(x, y, z,Tile.air);
			setBlockAir(x - 1, y, z, depth - 1);
			setBlockAir(x + 1, y, z, depth - 1);
			setBlockAir(x, y - 1, z, depth - randInt(2));
			setBlockAir(x, y + 1, z, depth - randInt(2));
			setBlockAir(x, y, z - 1, depth - 1);
			setBlockAir(x, y, z + 1, depth - 1);
			
			setBlockAir(x - 1, y - 1, z, depth - randInt(3));
			setBlockAir(x - 1, y - 1, z - 1, depth - randInt(4));
			setBlockAir(x - 1, y - 1, z + 1, depth - randInt(2));
			setBlockAir(x - 1, y + 1, z, depth - randInt(6));
			setBlockAir(x - 1, y + 1, z - 1, depth - randInt(3));
			setBlockAir(x - 1, y + 1, z + 1, depth - 2);
			
			setBlockAir(x + 1, y - 1, z, depth - randInt(5));
			setBlockAir(x + 1, y - 1, z - 1, depth - 1);
			setBlockAir(x + 1, y - 1, z + 1, depth - randInt(7));
			setBlockAir(x + 1, y + 1, z, depth - randInt(3));
			setBlockAir(x + 1, y + 1, z - 1, depth - randInt(4));
			setBlockAir(x + 1, y + 1, z + 1, depth - randInt(2));
			
			setBlockAir(x - 1, y, z - 1, depth - 1);
			setBlockAir(x - 1, y, z + 1, depth - 1);
			setBlockAir(x + 1, y, z - 1, depth - 1);
			setBlockAir(x + 1, y, z + 1, depth - 1);
			
			setBlockAir(x, y - 1, z - 1, depth - randInt(2));
			setBlockAir(x, y - 1, z + 1, depth - randInt(3));
			setBlockAir(x, y + 1, z - 1, depth - randInt(4));
			setBlockAir(x, y + 1, z + 1, depth - randInt(2));
		}
	}

	private void setBlockSoil(int x, int y, int z, int depth) {
		if((getBlock(x, y, z) == Tile.stone || getBlock(x, y, z) == Tile.air) && depth >= 0) {
			setBlock(x, y, z,Tile.soil);
			setBlockSoil(x - 1, y, z, depth - randInt(2));
			setBlockSoil(x + 1, y, z, depth - randInt(4));
			setBlockSoil(x, y - 1, z, depth - randInt(2));
			setBlockSoil(x, y + 1, z, depth - randInt(2));
			setBlockSoil(x, y, z - 1, depth - randInt(3));
			setBlockSoil(x, y, z + 1, depth - randInt(2));
			
			setBlockSoil(x - 1, y - 1, z, depth - randInt(4));
			setBlockSoil(x - 1, y - 1, z - 1, depth - randInt(2));
			setBlockSoil(x - 1, y - 1, z + 1, depth - randInt(3));
			setBlockSoil(x - 1, y + 1, z, depth - randInt(5));
			setBlockSoil(x - 1, y + 1, z - 1, depth - randInt(2));
			setBlockSoil(x - 1, y + 1, z + 1, depth - 2);
			
			setBlockSoil(x + 1, y - 1, z, depth - randInt(6));
			setBlockSoil(x + 1, y - 1, z - 1, depth - randInt(2));
			setBlockSoil(x + 1, y - 1, z + 1, depth - randInt(5));
			setBlockSoil(x + 1, y + 1, z, depth - randInt(2));
			setBlockSoil(x + 1, y + 1, z - 1, depth - randInt(3));
			setBlockSoil(x + 1, y + 1, z + 1, depth - randInt(2));
			
			setBlockSoil(x - 1, y, z - 1, depth - randInt(2));
			setBlockSoil(x - 1, y, z + 1, depth - randInt(2));
			setBlockSoil(x + 1, y, z - 1, depth - randInt(3));
			setBlockSoil(x + 1, y, z + 1, depth - randInt(2));
			
			setBlockSoil(x, y - 1, z - 1, depth - randInt(2));
			setBlockSoil(x, y - 1, z + 1, depth - randInt(3));
			setBlockSoil(x, y + 1, z - 1, depth - randInt(4));
			setBlockSoil(x, y + 1, z + 1, depth - randInt(2));
		}
	}
	
	private void setBlock(int x, int y, int z, Tile tile) {
		if(x < 0 || x >= length || y < 0 || y >= height || z < 0 || z >= width)
			return;
		blocks[x][y][z] = (byte) tile.id;
	}

	private Tile getBlock(int x, int y, int z) {
		if(x < 0 || x >= length || y < 0 || y >= height || z < 0 || z >= width)
			return null;
		return Tile.tiles[blocks[x][y][z]];
	}

	private int randInt(int bound) {
		return random.nextInt(bound - 1) + 1;
	}
}
