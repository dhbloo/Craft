package craft.world.levelgen;

import java.util.Random;

import craft.world.World;
import craft.world.tile.Tile;

public class LevelGen {
	public static final int SEA_HEIGHT = 64;
	public World world;
	private NoiseMap noiseMap;
	public Random random;

	public LevelGen(World world, Random random) {
		this.world = world;
		this.noiseMap = new NoiseMap(random);
		this.random = random;
	}

	public void generateNewLevel() {
		generateNewMap();
		generateCaves(20 + random.nextInt(20));
		carveTunnels((int) (world.length * world.height * world.width / 512 / 256 * (random.nextDouble() * 0.5 + 0.75)));
		generateSoil(40 + random.nextInt(30));
		addLiquid(15 + random.nextInt(15), 0.8F);
		//generateSandAndWater();
		setGrass();
	}

	private void generateNewMap() {
		float[][][] heightMap = new float[4][][];
		/**地基高度*/
		heightMap[0] = noiseMap.generatePerlinNoise(world.length, world.width, 7, 0.25F);
		/**表面粗糙度（细节层）*/
		heightMap[1] = noiseMap.generatePerlinNoise(world.length, world.width, 4, 0.35F);
		/**小山丘*/
		heightMap[2] = noiseMap.generatePerlinNoise(world.length, world.width, 6, 0.2F);
		/**高原*/
		heightMap[3] = noiseMap.generatePerlinNoise(world.length, world.width, 8, 0.2F);
		float[][] map = new float[world.length][world.width];
		
		noiseMap.add(heightMap[2], -0.68F);
		noiseMap.peak(heightMap[2], 0.0F);
		noiseMap.scale(heightMap[2], 2.0F);
		
		noiseMap.add(heightMap[3], -0.75F);
		noiseMap.peak(heightMap[3], 0.01F);
		noiseMap.scale(heightMap[3], 3.0F);
		
		noiseMap.scale(heightMap[0], 1.2F);
		noiseMap.scale(heightMap[1], 0.2F);
		map = noiseMap.addNoise(noiseMap.addNoise(noiseMap.addNoise(heightMap[0], heightMap[1]), heightMap[2]), heightMap[3]);
		
		int min = 45;
		int p = 17;
		for(int x = 0; x < world.length; x++)
			for(int z = 0; z < world.width; z++) {
				int yh = (int) (min + map[x][z] * p);
				if(yh < 0)  yh = 0;
				if(yh >= world.height)  yh = world.height  - 1;
				/*for(int y = 45; y < 60; y++)
					blocks[x][y][z] = (byte) Tile.calmWater.id;*/
				for(int y = 1; y < yh - 4; y++)
					setBlock(x, y, z, Tile.stone);
				for(int y = yh - 4; y < yh; y++)
					setBlock(x, y, z, Tile.soil);
				setBlock(x, 0, z, Tile.bedrock);
			}
	}

	private void generateCaves(int cavesCount) {
		for(int i = 0; i < cavesCount; i++) {
			int x = random.nextInt(world.length);
			int y = random.nextInt(50) + 5;
			int z = random.nextInt(world.width);
			setBlockAir(x, y, z, 5 + (int) (random.nextDouble() * 5) + (random.nextDouble() < 0.8 ? 0 : (int) (random.nextDouble() * 10)));
		}
	}

	private void carveTunnels(int count) {
		for (int i = 0; i < count; i++) {
			float x = this.random.nextFloat() * world.length;
			float y = this.random.nextFloat() * 40 + 5;
			float z = this.random.nextFloat() * world.width;
			/**隧道长度*/
			int length = (int) (this.random.nextFloat() + this.random.nextFloat() * 150.0F);
			/**曲折程度*/
			float dir1 = (float) (this.random.nextFloat() * Math.PI * 2.0D);
			float dira1 = 0.0F;
			float dir2 = (float) (this.random.nextFloat() * Math.PI * 2.0D);
			float dira2 = 0.0F;

			for (int l = 0; l < length; l++) {
				x = (float) (x + Math.sin(dir1) * Math.cos(dir2));
				z = (float) (z + Math.cos(dir1) * Math.cos(dir2));
				y = (float) (y + Math.sin(dir2));

				dir1 += dira1 * 0.2F;
				dira1 *= 0.9F;
				dira1 += this.random.nextFloat() - this.random.nextFloat();

				dir2 += dira2 * 0.5F;
				dir2 *= 0.5F;
				dira2 *= 0.9F;
				dira2 += this.random.nextFloat() - this.random.nextFloat();

				float size = (float) (Math.sin(l * Math.PI / length) * 2.5D + 1.0D);

				for (int xx = (int) (x - size); xx <= (int) (x + size); xx++)
					for (int yy = (int) (y - size); yy <= (int) (y + size); yy++)
						for (int zz = (int) (z - size); zz <= (int) (z + size); zz++) {
							float xd = xx - x;
							float yd = yy - y;
							float zd = zz - z;
							float dd = xd * xd + yd * yd * 2.0F + zd * zd;
							if ((dd < size * size) && (xx >= 1) && (yy >= 1) && (zz >= 1) && (xx < world.length - 1) && (yy < world.height - 1) && (zz < world.width - 1)) {
								if (getBlock(xx, yy, zz) == Tile.stone)
									setBlock(xx, yy, zz, Tile.air);
							}
						}
			}
		}
	}
	
	private void generateSoil(int count) {
		for(int i = 0; i < count; i++) {
			int x = random.nextInt(world.length);
			int y = random.nextInt(40) + 5;
			int z = random.nextInt(world.width);
			setBlockSoil(x, y, z, 7 + (int) (random.nextDouble() * 5));
		}
	}
	
	private void addLiquid(int count, float alpha) {
		for(int i = 0; i < count; i++) {
			int x = random.nextInt(world.length);
			int y = random.nextInt(40) + 5;
			int z = random.nextInt(world.width);
			if(getBlock(x, y, z) == Tile.air) {
				while(--y > 0 && getBlock(x, y, z) == Tile.air);
				y++;
				if (random.nextFloat() <= alpha) {
					setBlock(x, y, z, Tile.water);
					/*Tile.water.add(x - length / 2, y, z - width / 2);*/
				} else {
					setBlock(x, y, z, Tile.lava);
					/*Tile.lava.add(x - length / 2, y, z - width / 2);*/
				}
			} else
				count++;
			if(i > 5000)	return;
		}
	}
	
	@SuppressWarnings("unused")
	private void generateSandAndWater() {
		float[][] heightMap = noiseMap.generatePerlinNoise(world.length, world.width, 6, 0.5F);
		for(int x = 0; x < world.length; x++)
			for(int z = 0; z < world.width; z++) {
				int min = 45;
				int p = (57 - min);
				int yh = (int) (min + heightMap[x][z] * p);
				if(yh < 0)  yh = 0;
				if(yh >= world.height)  yh = world.height  - 1;
				if(getBlock(x, yh, z) == Tile.air)
					for(int y = randInt(5) + min; y < yh; y++) {
						if(getBlock(x, y, z) == Tile.air || getBlock(x, y, z) == Tile.grass) {
							setBlock(x, y, z, Tile.sand);
							if(y == yh - 1 && y < 52)
								setBlock(x, y, z, Tile.water);
						}
					}
			}
	}
	
 	private void setBlockAir(int x, int y, int z, int depth) {
		if(randInt(100) > 90)
			depth -= random.nextInt(2);
		Tile tile = getBlock(x, y, z);
		if((tile == Tile.stone || tile == Tile.soil || tile == Tile.grass) && depth >= 0) {
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
			
			setBlockAir(x + 1, y - 1, z, depth - randInt(2));
			setBlockAir(x + 1, y - 1, z - 1, depth - randInt(2));
			setBlockAir(x + 1, y - 1, z + 1, depth - randInt(6));
			setBlockAir(x + 1, y + 1, z, depth - randInt(3));
			setBlockAir(x + 1, y + 1, z - 1, depth - randInt(4));
			setBlockAir(x + 1, y + 1, z + 1, depth - randInt(3));
			
			setBlockAir(x - 1, y, z - 1, depth - 1);
			setBlockAir(x - 1, y, z + 1, depth - 1);
			setBlockAir(x + 1, y, z - 1, depth - 1);
			setBlockAir(x + 1, y, z + 1, depth - 1);
			
			setBlockAir(x, y - 1, z - 1, depth - randInt(4));
			setBlockAir(x, y - 1, z + 1, depth - randInt(3));
			setBlockAir(x, y + 1, z - 1, depth - randInt(2));
			setBlockAir(x, y + 1, z + 1, depth - randInt(5));
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
	
	public void setBlock(int x, int y, int z, Tile tile) {
		if(x < 0 || x >= world.length || y < 0 || y >= world.height || z < 0 || z >= world.width)
			return;
		x -= world.length >> 1;
		z -= world.width >> 1;
		world.setBlockDirectly(x, y, z, tile.id);
	}

	public Tile getBlock(int x, int y, int z) {
		if(x < 0 || x >= world.length || y < 0 || y >= world.height || z < 0 || z >= world.width)
			return null;
		x -= world.length >> 1;
		z -= world.width >> 1;
		return Tile.tiles[world.getBlock(x, y, z)];
	}
	
	public int randInt(int bound) {
		return random.nextInt(bound - 1) + 1;
	}
	
	public boolean isLightBlock(int x, int y, int z) {
		Tile tile = Tile.tiles[world.getBlock(x - (world.length >> 1), y, z - (world.width >> 1))];
		return tile == Tile.air ? true : tile.isLightTile();
	}
	
	private void setGrass() {
		for (int x = 0; x < world.length; x++)
			for (int z = 0; z < world.width; z++) {
				int y = world.height - 1;
				while (isLightBlock(x, y, z))
					y--;
				if (getBlock(x, y, z) == Tile.soil)
					setBlock(x, y, z, Tile.grass);
			}
	}
	
}
