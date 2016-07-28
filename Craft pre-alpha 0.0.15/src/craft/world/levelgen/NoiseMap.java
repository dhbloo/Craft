package craft.world.levelgen;

import java.util.Random;

public class NoiseMap {
	private Random random;
	public NoiseMap(Random random) {
		this.random = random;
	}
	
	private float[][] generateWhiteNoise(int width, int height) {
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
				float top = cosInterpolate(baseNoise[sample_i0][sample_j0],  
						baseNoise[sample_i1][sample_j0], horizontal_blend);  

				//blend the bottom two corners  
				float bottom = cosInterpolate(baseNoise[sample_i0][sample_j1],  
						baseNoise[sample_i1][sample_j1], horizontal_blend);  

				//final blend  
				smoothNoise[i][j] = cosInterpolate(top, bottom, vertical_blend);  
			}  
		}  
       
       return smoothNoise;  
    }
	
	@SuppressWarnings("unused")
	private float linearInterpolate(float x0, float x1, float alpha) {  
       return x0 * (1 - alpha) + alpha * x1;  
    }
	
	private float cosInterpolate(float x0, float x1, float alpha) {  
			float f = (float) (1 - Math.cos(alpha * Math.PI)) * 0.5F;
			return x0 * (1 - f) + f * x1;
	    }
	
	public float[][] generatePerlinNoise(int width, int height, int octaveCount, float persistance) {

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
	
	/**噪声增加*/
	public void add(float[][] noise, float a) {
		for (int x = 0; x < noise.length; x++)
			for (int y = 0; y < noise[0].length; y++) {
				noise[x][y] += a;
			}
	}
	
	/**缩放噪声*/
	public void scale(float[][] noise, float a) {
		for (int x = 0; x < noise.length; x++)
			for (int y = 0; y < noise[0].length; y++) {
				noise[x][y] *= a;
			}
	}
	
	/**将两个噪声相加*/
	public float[][] addNoise(float[][] noise1, float[][] noise2) {
		int length = noise1.length;
		int width = noise1[0].length;
		/**判断两个噪声的大小是否相等*/
		if (noise2.length != length || noise2[0].length != width) return null;
		float[][] newNoise = new float[length][width];
		for (int x = 0; x < length; x++)
			for (int y = 0; y < width; y++) {
				newNoise[x][y] = noise1[x][y] + noise2[x][y];
			}
		return newNoise;
	}
	
	/**将两个噪声按指定比例混合
	 * @param a 混合系数（范围：0~1）*/
	public float[][] mixNoise(float[][] noise1, float[][] noise2, float a) {
		int length = noise1.length;
		int width = noise1[0].length;
		/**判断两个噪声的大小是否相等*/
		if (noise2.length != length || noise2[0].length != width) return null;
		float[][] newNoise = new float[length][width];
		for (int x = 0; x < length; x++)
			for (int y = 0; y < width; y++) {
				newNoise[x][y] = noise1[x][y] + (noise2[x][y] - noise1[x][y]) * a;
			}
		return newNoise;
	}
	
	/**噪声极值化，用于添加山峰*/
	public void peak(float[][] noise, float least) {
		for (int x = 0; x < noise.length; x++)
			for (int y = 0; y < noise[0].length; y++) {
				if (noise[x][y] < least)
					noise[x][y] = 0.0F;
			}
	}
	
	
}
