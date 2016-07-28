package craft.environment;

import java.util.Random;

import craft.Player;
import craft.level.levelgen.NoiseMap;
import craft.renderer.Tesselator;

public class Clouds {
	public static final int CLOUD_HEIGHT = 128;
	public static final int RENDER_DISTANCE = 64 * 128 * 64;
	public int length, width;
	public boolean clouds[][];
	public int cloudSize;
	/**创建随机的云地图
	 * @param length 地图长度
	 * @param width 地图宽度
	 * @param cloudSize 云的宽度
	 * @param cloudCount 云的出现率*/
	public Clouds(int length, int width, int cloudSize, float cloudCount, int octaveCount, float persistance) {
		this.length = length;
		this.width = width;
		clouds = new boolean[length][width];
		float[][] noise = new NoiseMap(new Random()).generatePerlinNoise(length, width, octaveCount, persistance);
		for (int x = 0; x < length; x++)
			for (int y = 0; y < width; y++) {
				if (noise[x][y] < cloudCount)
					clouds[x][y] = true;
			}
		this.cloudSize = cloudSize;
	}
	
	/**渲染所有视野范围内的云*/
	public void render(Player player) {
		Tesselator t = Tesselator.instance;
		t.begin();
		t.color(1.0F, 1.0F, 1.0F);
		for (int x = 0; x < length; x++)
			for (int y = 0; y < width; y++) {
				int xa = (x - length / 2) * cloudSize;
				int ya = (y - width / 2) * cloudSize;
				if (clouds[x][y] && distanceToSqr(player, xa, ya) < RENDER_DISTANCE) {
					renderCloud(t, xa, ya, player.y > CLOUD_HEIGHT);
				}
			}
		t.end();
	}
	
	private void renderCloud(Tesselator t, int x, int y, boolean up) {
		int x0 = x;
		int x1 = x + cloudSize;
		int y0 = y;
		int y1 = y + cloudSize;
		if (up) {
			t.vertex(x1, CLOUD_HEIGHT, y0);
			t.vertex(x0, CLOUD_HEIGHT, y0);
			t.vertex(x0, CLOUD_HEIGHT, y1);
			t.vertex(x1, CLOUD_HEIGHT, y1);
		} else {
			t.vertex(x0, CLOUD_HEIGHT, y0);
			t.vertex(x1, CLOUD_HEIGHT, y0);
			t.vertex(x1, CLOUD_HEIGHT, y1);
			t.vertex(x0, CLOUD_HEIGHT, y1);
		}
	}
	
	private float distanceToSqr(Player player, int x, int z) {
		float xd = player.x - x + cloudSize / 2;
		float yd = player.y - CLOUD_HEIGHT;
		float zd = player.z - z + cloudSize / 2;
		return xd * xd + yd * yd + zd * zd;
	}
	

}
