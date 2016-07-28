package craft.world;

import org.lwjgl.opengl.GL11;

import craft.Player;
import craft.phys.AABB;
import craft.renderer.Tesselator;
import craft.world.tile.Tile;

public class RenderChunk {
	public final World world;
	public final AABB aabb;
	public final int x0, y0, z0, x1, y1, z1;
	public final float x, y, z;
	public int chunkx, chunkz;
	private boolean dirty = true;
	public boolean visible = false;
	private int lists = -1;
	public static int updates = 0;
	public static long totalTime = 0L;
	public static int totalUpdates = 0;
	public int tiles = 0;
	
	private static Tesselator t = Tesselator.instance;

 	public RenderChunk(World world, int x0, int y0, int z0, int x1, int y1, int z1) {
		this.world = world;
		this.x0 = x0;
		this.y0 = y0;
		this.z0 = z0;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;

		this.x = (x0 + x1) / 2.0F;
		this.y = (y0 + y1) / 2.0F;
		this.z = (z0 + z1) / 2.0F;

		this.lists = GL11.glGenLists(2);
		this.aabb = new AABB(x0 - 1.0F, y0 - 1.0F, z0 - 1.0F, x1 + 1.0F, y1 + 1.0F, z1 + 1.0F);
		
		this.chunkx = (x0 + world.length / 2) / 16;
		this.chunkz = (z0 + world.width / 2) / 16;
	}

	public void setDirty() {
		if (!this.dirty)
			this.dirty =  true;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	private void rebuild(int layer) {
		GL11.glNewList(lists + layer, GL11.GL_COMPILE);
		t.begin();
		for (int x = x0; x <= x1; x++)
			for (int y = y0; y <= y1; y++)
				for (int z = z0; z <= z1; z++) {
					Tile tile = Tile.tiles[world.getBlock(x, y, z)];
					if (tile != Tile.air) {
						tile.render(t, world, x, y, z, layer);
						tiles++;
					}
				}
		t.end();
		GL11.glEndList();
	}

	public void rebuild() {
		long before = System.nanoTime();
		dirty = false;
		updates++;
		tiles = 0;
		rebuild(0);
		/**透明液体 混合绘制*/
		rebuild(1);
		long after = System.nanoTime();
		if(tiles > 0) {
			totalTime += after - before;
			totalUpdates += 1;
		}
	}

	public void render(int layer) {
		GL11.glCallList(this.lists + layer);
	}

	public float distanceToSqr(Player player) {
		float xd = player.x - this.x;
		float yd = player.y - this.y;
		float zd = player.z - this.z;
		return xd * xd + yd * yd + zd * zd;
	}
	
	public void reset() {
		GL11.glDeleteLists(lists, 2);
		lists = GL11.glGenLists(2);
		dirty =  true;
		tiles = 0;
		visible = false;
	}
}
