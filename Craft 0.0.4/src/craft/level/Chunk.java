package craft.level;

import org.lwjgl.opengl.GL11;

import craft.Player;
import craft.level.tile.Tile;
import craft.phys.AABB;
import craft.renderer.Texture;

public class Chunk {
	public final Level level;
	public final AABB aabb;
	public final int x0;
	public final int y0;
	public final int z0;
	public final int x1;
	public final int y1;
	public final int z1;
	public final float x;
	public final float y;
	public final float z;
	private boolean dirty = true;
	private int lists = -1;
	public static int updates = 0;
	public static long totalTime = 0L;
	public static int totalUpdates = 0;
	public long dirtiedTime = 0L;

	public Chunk(Level level, int x0, int y0, int z0, int x1, int y1, int z1) {
		this.level = level;
		this.x0 = x0;
		this.y0 = y0;
		this.z0 = z0;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;

		this.x = (x0 + x1 / 2.0F);
		this.y = (y0 + y1 / 2.0F);
		this.z = (z0 + z1 / 2.0F);

		this.lists = GL11.glGenLists(2);
		this.aabb = new AABB(x0 - 1.0F, y0 - 1.0F, z0 - 1.0F, x1 + 1.0F, y1 + 1.0F, z1 + 1.0F);
	}

	public void setDirty() {
		if (!this.dirty) {
			this.dirtiedTime = System.currentTimeMillis();
			this.dirty =  true;
		}
	}

	public boolean isDirty() {
		return this.dirty;
	}

	private void rebuild(int layer) {
		dirty = false;
		updates++;
		long before = System.nanoTime();
		GL11.glNewList(lists + layer, GL11.GL_COMPILE);
		int tiles = 0;
		for (int x = x0; x <= x1; x++)
			for (int y = y0; y <= y1; y++)
				for (int z = z0; z <= z1; z++)
				{
					if(layer == 0) {
						if(level.isLightBlock(x, y, z))
							continue;
						Tile tile = Tile.tiles[level.getBlock(x, y, z)];
						if(tile != Tile.air) {
							tile.render(level, x, y, z);
							tiles++;
						}
					} else {
						if(!level.isLightBlock(x, y, z))
							continue;
						Tile tile = Tile.tiles[level.getBlock(x, y, z)];
						if(tile != Tile.air) {
							tile.render(level, x, y, z);
							tiles++;
						}
					}
				}
		GL11.glEndList();
		Texture.bind(-1);
		long after = System.nanoTime();
		if(tiles > 0) {
			totalTime += after - before;
			totalUpdates += 1;
		}
	}

	public void rebuild() {
		rebuild(0);
		GL11.glDepthMask(false);
		rebuild(1);		//°ëÍ¸Ã÷»æÖÆ
		GL11.glDepthMask(true);
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
}
