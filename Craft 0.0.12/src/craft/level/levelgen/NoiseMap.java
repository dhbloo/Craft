package craft.level.levelgen;

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
}
