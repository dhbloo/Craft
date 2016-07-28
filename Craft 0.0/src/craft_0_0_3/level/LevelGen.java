package craft_0_0_3.level;

import java.util.Random;
import craft_0_0_3.level.tile.TileConst;

public class LevelGen {
	public static int minHeight = 55, maxHeight = 70;
	private int length, height, width;
	public LevelGen(int length, int height, int width) {
		this.length = length;
		this.height = height;
		this.width = width;
	}
	
	public byte[][][] generateNewLevel() {
		float[][] heightMap = generatePerlinNoise(length, width, 6, 0.5F);
		byte [][][] map = new byte [length][height][width];
		for(int x = 0; x < length; x++)
			for(int z = 0; z < width; z++) {
				int yh = (int) (minHeight + heightMap[x][z] * (maxHeight - minHeight));
				if(yh < 0)  yh = 0;
				if(yh >= height)  yh = height  - 1;
				for(int y = 0; y < yh; y++)
					map[x][y][z] = (byte) TileConst.STONE.value;
				map[x][0][z] = (byte) TileConst.BEDROCK.value;
				map[x][yh][z] = (byte) TileConst.GRASS.value;
				map[x][yh - 1][z] = (byte) TileConst.SOIL.value;
				map[x][yh - 2][z] = (byte) TileConst.SOIL.value;
				map[x][yh - 3][z] = (byte) TileConst.SOIL.value;
			}
		return map;
	}
	
	private float[][] generateWhiteNoise(int width, int height) {
		Random random = new Random();
		float[][] noise = new float[width][height];
		for (int i = 0; i < width; i++) {  
			for (int j = 0; j < height; j++) {  
				noise[i][j] = random.nextFloat();  
			}  
		}  
		return noise;  
	}
	
	private float[][] generateSmoothNoise(int width, int height, int octave)  
    {  
		float[][] baseNoise = generateWhiteNoise(width, height);

		float[][] smoothNoise = new float[width][height];  

		int samplePeriod = 1 << octave; // calculates 2 ^ k  
		float sampleFrequency = 1.0f / samplePeriod;  

		for (int i = 0; i < width; i++) {  
			//calculate the horizontal sampling indices  
			int sample_i0 = (i / samplePeriod) * samplePeriod;  
			int sample_i1 = (sample_i0 + samplePeriod) % width; //wrap around  
			float horizontal_blend = (i - sample_i0) * sampleFrequency;  

			for (int j = 0; j < height; j++) {  
				//calculate the vertical sampling indices  
				int sample_j0 = (j / samplePeriod) * samplePeriod;  
				int sample_j1 = (sample_j0 + samplePeriod) % height; //wrap around  
				float vertical_blend = (j - sample_j0) * sampleFrequency;  

				//blend the top two corners  
				float top = linearInterpolate(baseNoise[sample_i0][sample_j0],  
						baseNoise[sample_i1][sample_j0], horizontal_blend);  

				//blend the bottom two corners  
				float bottom = linearInterpolate(baseNoise[sample_i0][sample_j1],  
						baseNoise[sample_i1][sample_j1], horizontal_blend);  

				//final blend  
				smoothNoise[i][j] = linearInterpolate(top, bottom, vertical_blend);  
			}  
		}  
       
       return smoothNoise;  
    }
	
	private float linearInterpolate(float x0, float x1, float alpha) {  
       return x0 * (1 - alpha) + alpha * x1;  
    }
	
	private float[][] generatePerlinNoise(int width, int height, int octaveCount, float persistance) {

		float[][][] smoothNoise = new float[octaveCount][][]; //an array of 2D arrays containing  

		//generate smooth noise  
		for (int i = 0; i < octaveCount; i++)
			smoothNoise[i] = generateSmoothNoise(width, height, i);  

		float[][] perlinNoise = new float[width][height];  
		float amplitude = 1.0f;  
		float totalAmplitude = 0.0f;  

		//blend noise together  
		for (int octave = octaveCount - 1; octave >= 0; octave--) {  
			amplitude *= persistance;  
			totalAmplitude += amplitude;  

			for (int i = 0; i < width; i++)
				for (int j = 0; j < height; j++)
					perlinNoise[i][j] += smoothNoise[octave][i][j] * amplitude;
		}  

		//normalization  
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				perlinNoise[i][j] /= totalAmplitude;  

		return perlinNoise;  
	}
}
