package craft.environment;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import craft.Player;
import craft.renderer.Tesselator;
import craft.world.levelgen.NoiseMap;

public class Clouds {
	public static final int CLOUD_HEIGHT = 128;
	public static final int CLOUD_THICKNESS = 5;
	public static final int RENDER_DISTANCE = 64 * 128 * 64;
	public static final float CLOUD_MOVE_SPEED = 0.02F;
	public int length, width;
	public int cloudSize;
	private int cloudListBase;
	private float xOffsetO = 0.0F;
	private float xOffset = 0.0F;
	
	/**创建随机的云地图
	 * @param length 地图长度
	 * @param width 地图宽度
	 * @param cloudSize 云的宽度
	 * @param cloudCount 云的出现率*/
	public Clouds(int length, int width, int cloudSize, float cloudCount, int octaveCount, float persistance) {
		this.length = length;
		this.width = width;
		boolean[][] clouds = new boolean[length][width];
		float[][] noise = new NoiseMap(new Random()).generatePerlinNoise(length, width, octaveCount, persistance);
		for (int x = 0; x < length; x++)
			for (int y = 0; y < width; y++) {
				if (noise[x][y] < cloudCount)
					clouds[x][y] = true;
			}
		this.cloudSize = cloudSize;
		compileCloudList(clouds);
		clouds = null;
		xOffset = -length / 2;
	}
	
	/**渲染所有视野范围内的云*/
	public void compileCloudList(boolean[][] clouds) {
		Tesselator t = Tesselator.instance;
		cloudListBase = GL11.glGenLists(1);
		GL11.glNewList(cloudListBase, GL11.GL_COMPILE);
		GL11.glColor4f(0.9F, 0.9F, 0.88F, 0.6F);
		t.begin();
		t.noColor();
		for (int x = 0; x < length; x++)
			for (int y = 0; y < width; y++) {
				int xa = (x - length / 2) * cloudSize;
				int ya = (y - width / 2) * cloudSize;
				if (clouds[x][y]) {
					renderCloud(t, xa, ya);
				}
			}
		t.end();
		GL11.glEndList();
	}
	
	private void renderCloud(Tesselator t, int x, int z) {
		int x0 = x;
		int x1 = x + cloudSize;
		int y0 = CLOUD_HEIGHT;
		//int y1 = CLOUD_HEIGHT + CLOUD_THICKNESS;
		int z0 = z;
		int z1 = z + cloudSize;
		t.color(0.8F, 0.8F, 0.8F);
		t.vertex(x1, y0, z0);
		t.vertex(x0, y0, z0);
		t.vertex(x0, y0, z1);
		t.vertex(x1, y0, z1);
		
		/*t.vertex(x1, y1, z0);
		t.vertex(x0, y1, z0);
		t.vertex(x0, y1, z1);
		t.vertex(x1, y1, z1);
		
		t.color(0.9F, 0.9F, 0.9F);
		t.vertex(x1, y1, z0);
		t.vertex(x1, y0, z0);
		t.vertex(x1, y0, z1);
		t.vertex(x1, y1, z1);
		
		t.vertex(x0, y1, z0);
		t.vertex(x0, y0, z0);
		t.vertex(x0, y0, z1);
		t.vertex(x0, y1, z1);
		
		t.color(0.95F, 0.95F, 0.95F);
		t.vertex(x1, y0, z0);
		t.vertex(x0, y0, z0);
		t.vertex(x0, y1, z0);
		t.vertex(x1, y1, z0);
		
		t.vertex(x1, y1, z1);
		t.vertex(x0, y1, z1);
		t.vertex(x0, y0, z1);
		t.vertex(x1, y0, z1);*/
	}
	
	@SuppressWarnings("unused")
	private float distanceToSqr(Player player, int x, int z) {
		float xd = player.x - x + cloudSize / 2;
		float yd = player.y - CLOUD_HEIGHT;
		float zd = player.z - z + cloudSize / 2;
		return xd * xd + yd * yd + zd * zd;
	}
	
	public void render(float a) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glPushMatrix();
		GL11.glColor4f(0.9F, 0.9F, 0.88F, 0.9F);
		GL11.glTranslatef(xOffset + (xOffset - xOffsetO) * a, 0.0F, 0.0F);
		GL11.glCallList(cloudListBase);
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
	
	public void tick() {
		xOffsetO = xOffset;
		xOffset += CLOUD_MOVE_SPEED;
	}
	
}
