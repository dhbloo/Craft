package craft.level.levelgen;

import java.util.Random;

import craft.level.tile.Tile;

public class LevelGen {
	public static final int SEA_HEIGHT = 64;
	private int length, height, width;
	private NoiseMap noiseMap;
	
	public LevelGen(int length, int height, int width, Random random) {
		this.length = length;
		this.height = height;
		this.width = width;
		this.noiseMap = new NoiseMap(random);
	}

	public byte[][][] generateNewLevel() {
		byte [][][] map = generateNewMap();
		/*float[][][] heightMap = new float[2][][];
		for(int i = 0; i < 2; i++)
			heightMap[i] = noiseMap.generatePerlinNoise(length, width, 6, 0.5F);
		int min = 0;
		int max = 0;
		for(int x = 0; x < length; x++)
			for(int z = 0; z < width; z++) {
				min = 50;
				max = 58;
				if(heightMap[0][x][z] < 0.25F) {
					heightMap[0][x][z] /= 0.25F;
				} else {
					heightMap[0][x][z] = 0;
				}
				int yh = (int) (min + heightMap[0][x][z] * (max - min));
				if(heightMap[0][x][z] != 0)
					System.out.println(yh);
				if(yh < min)  yh = min;
				if(yh > max)  yh = max;
				if(yh - min > 1)
					for(int y = min; y <= yh; y++)
						if(map[x][y][z] != Tile.air.id)
							map[x][y][z] = (byte) Tile.air.id;
			}*/
		return map;
	}

	private byte[][][] generateNewMap() {
		byte [][][] map = new byte [length][height][width];
		float[][][] heightMap = new float[3][][];
		for(int i = 0; i < 3; i++)
			heightMap[i] = noiseMap.generatePerlinNoise(length, width, 6, 0.5F);
		float min = 0;
		float p = 0;
		for(int x = 0; x < length; x++)
			for(int z = 0; z < width; z++) {
				map[x][0][z] = (byte) Tile.bedrock.id;

				min = 40;
				p = (50 - min);
				int yh = (int) (min + heightMap[0][x][z] * p);
				if(yh < 0)  yh = 0;
				if(yh >= height)  yh = height  - 1;
				for(int y = 1; y < yh; y++)
					map[x][y][z] = (byte) Tile.stone.id;

				min = 45;
				p = (65 - min);
				yh = (int) (min + heightMap[1][x][z] * p);
				if(yh < 0)  yh = 0;
				if(yh >= height)  yh = height  - 1;
				for(int y = 1; y < yh; y++) {
					if(map[x][y][z] == Tile.air.id) {
						map[x][y][z] = (byte) Tile.soil.id;
						if(y == yh - 1)
							map[x][y][z] = (byte) Tile.grass.id;
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
		return map;
	}
}
