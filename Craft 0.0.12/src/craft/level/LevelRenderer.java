package craft.level;

import java.util.ArrayList;
import java.util.ListIterator;

import org.lwjgl.opengl.GL11;

import craft.HitResult;
import craft.Player;
import craft.level.tile.Face;
import craft.level.tile.Tile;
import craft.phys.AABB;
import craft.renderer.Frustum;
import craft.renderer.Tesselator;
import craft.renderer.Texture;

public class LevelRenderer implements LevelRendererListener {
	public static final int MAX_REBUILDS_PER_FRAME = 4;
	public static final int CHUNK_SIZE = 16;
	public static final int RENDER_DISTANCE = 32 * 32 * 16;
	private Level level;
	private RenderChunk[][][] renderChunks;
	private ArrayList<RenderChunk> renderChunkList = new ArrayList<RenderChunk>();
	private ArrayList<RenderChunk> dirtyRenderChunks;
	private int xChunks;
	private int yChunks;
	private int zChunks;
	private float cx, cy, cz, dx, dy, dz;

	public LevelRenderer(Level level) {
		this.level = level;
		level.setLevelRendererListener(this);

		xChunks = level.length / CHUNK_SIZE;
		yChunks = level.height / CHUNK_SIZE;
		zChunks = level.width / CHUNK_SIZE;

		renderChunks = new RenderChunk[xChunks][yChunks][zChunks];
		for (int x = 0; x < this.xChunks ; x++)
			for (int y = 0; y < this.yChunks; y++)
				for (int z = 0; z < this.zChunks; z++)
				{
					int x0 = x * 16;
					int y0 = y * 16;
					int z0 = z * 16;
					int x1 = (x + 1) * 16;
					int y1 = (y + 1) * 16;
					int z1 = (z + 1) * 16;

					x0 -= level.length / 2;
					x1 -= (level.length / 2 + 1);
					z0 -= level.width / 2;
					z1 -= (level.width / 2 + 1);
					y1 --;

					if (x1 > level.x1) x1 = level.x1;
					if (y1 > level.y1) y1 = level.y1;
					if (z1 > level.z1) z1 = level.z1;
					renderChunks[x][y][z] = new RenderChunk(level, x0, y0, z0, x1, y1, z1);
					renderChunkList.add(renderChunks[x][y][z]);
					//System.out.printf("%d,%d,%d,%d,%d,%d,%d,%d,%d\n", x, y, z, x0, y0, z0, x1, y1, z1);
				}
	}

	public void render(Player player, int layer) {
		float xd = player.x - cx;
		float yd = player.y - cy;
		float zd = player.z - cz;
		if (xd * xd + yd * yd + zd * zd > 64.0F) {
			cx = player.x;
			cy = player.y;
			cz = player.z;
			renderChunkList.sort(new DistanceChunkSorter(player));
		}
		ListIterator<RenderChunk> It = renderChunkList.listIterator();
		while(It.hasNext()) {
			RenderChunk c = It.next();
			if(c.visible && c.distanceToSqr(player) < RENDER_DISTANCE)
				c.render(layer);
		}
	}

	@SuppressWarnings("unused")
	private ArrayList<RenderChunk> getAllDirtyRenderChunks() {
		ArrayList<RenderChunk> dirtyRenderChunks = null;
		for (int x = 0; x < xChunks; x++)
			for (int y = 0; y < yChunks; y++)
				for (int z = 0; z < zChunks; z++) {
					RenderChunk chunk = renderChunks[x][y][z];
					if (chunk.isDirty() && level.isChunkLoaded(chunk.chunkx, chunk.chunkz)) {
						if (dirtyRenderChunks == null)
							dirtyRenderChunks = new ArrayList<RenderChunk>(xChunks * yChunks * zChunks);
						dirtyRenderChunks.add(chunk);
					}
				}
		return dirtyRenderChunks;
	}
	
	private ArrayList<RenderChunk> getNearByRenderChunks(Player player) {
		ArrayList<RenderChunk> dirtyRenderChunks = null;
		for (int x = 0; x < xChunks; x++)
			for (int y = 0; y < yChunks; y++)
				for (int z = 0; z < zChunks; z++) {
					RenderChunk chunk = renderChunks[x][y][z];
					if (level.isChunkLoaded(chunk.chunkx, chunk.chunkz) && chunk.distanceToSqr(player) < RENDER_DISTANCE * 2.0F) {
						if (dirtyRenderChunks == null)
							dirtyRenderChunks = new ArrayList<RenderChunk>(xChunks * yChunks * zChunks);
						dirtyRenderChunks.add(chunk);
					}
				}
		return dirtyRenderChunks;
	}

	public void updateDirtyChunks(Player player) {
		float xd = player.x - dx;
		float yd = player.y - dy;
		float zd = player.z - dz;
		if (xd * xd + yd * yd + zd * zd > 192.0F) {
			dx = player.x;
			dy = player.y;
			dz = player.z;
			dirtyRenderChunks = getNearByRenderChunks(player);
			if (dirtyRenderChunks == null)
				return;
			dirtyRenderChunks.sort(new DirtyRendererChunkSorter(player));
		} else {
			if (dirtyRenderChunks == null)
				return;
		}
		int rebuild = 0;
		for (int i = 0; (rebuild < MAX_REBUILDS_PER_FRAME) && (i < dirtyRenderChunks.size()); i++) {
			if (dirtyRenderChunks.get(i).isDirty()) {
				rebuild++;
				//if(dirtyRenderChunks.get(i).distanceToSqr(player) < RENDER_DISTANCE * 2.0F)
				dirtyRenderChunks.get(i).rebuild();
			}
		}
	}

	public void setDirty(int x0, int y0, int z0, int x1, int y1, int z1) {
		x0 += level.length / 2;
		z0 += level.width / 2;
		x1 += level.length / 2;
		z1 += level.width / 2;

		x0 /= CHUNK_SIZE;
		x1 /= CHUNK_SIZE;
		y0 /= CHUNK_SIZE;
		y1 /= CHUNK_SIZE;
		z0 /= CHUNK_SIZE;
		z1 /= CHUNK_SIZE;

		if (x0 < 0) x0 = 0;
		if (y0 < 0) y0 = 0;
		if (z0 < 0) z0 = 0;
		if (x1 >= this.xChunks) x1 = this.xChunks - 1;
		if (y1 >= this.yChunks) y1 = this.yChunks - 1;
		if (z1 >= this.zChunks) z1 = this.zChunks - 1;

		for (int x = x0; x <= x1; x++)
			for (int y = y0; y <= y1; y++)
				for (int z = z0; z <= z1; z++)
					this.renderChunks[x][y][z].setDirty();
	}

	public void pick(Player player) {
		Tesselator t = Tesselator.instance;
		AABB box = player.aabb.grow(Player.PLAYER_PICK_LENGTH, Player.PLAYER_PICK_LENGTH, Player.PLAYER_PICK_LENGTH);
		int x0 = (int)box.x0;
		int x1 = (int)(box.x1 + 1.0F);
		int y0 = (int)box.y0;
		int y1 = (int)(box.y1 + 1.0F);
		int z0 = (int)box.z0;
		int z1 = (int)(box.z1 + 1.0F);

		GL11.glInitNames();
		for (int x = x0; x < x1; x++) {
			GL11.glPushName(x);
			for (int y = y0; y < y1; y++) {
				GL11.glPushName(y);
				for (int z = z0; z < z1; z++) {
					GL11.glPushName(z);
					if (level.getBlock(x, y, z) != Tile.air.id) {
						GL11.glPushName(0);
						for(Face face : Face.values()) {
							GL11.glPushName(face.ordinal());
							t.begin();
							Tile.renderFaceNoTexture(t, x, y, z, face);
							t.end();
							GL11.glPopName();
						}
						GL11.glPopName();
					}
					GL11.glPopName();
				}
				GL11.glPopName();
			}
			GL11.glPopName();
		}
	}

	public void renderHit(HitResult h) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(0.1F, 0.1F, 0.1F, 0.6F);
		GL11.glLineWidth(1.3F);
		float x0 = h.x + 0.0F;
		float x1 = h.x + 1.0F;
		float y0 = h.y + 0.0F;
		float y1 = h.y + 1.0F;
		float z0 = h.z + 0.0F;
		float z1 = h.z + 1.0F;
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(x0, y1, z1);
		GL11.glVertex3f(x1, y1, z1);
		GL11.glVertex3f(x1, y0, z1);
		GL11.glVertex3f(x0, y0, z1);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(x0, y1, z0);
		GL11.glVertex3f(x0, y0, z0);
		GL11.glVertex3f(x1, y0, z0);
		GL11.glVertex3f(x1, y1, z0);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(x1, y1, z0);
		GL11.glVertex3f(x1, y1, z1);
		GL11.glVertex3f(x0, y1, z1);
		GL11.glVertex3f(x0, y1, z0);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(x0, y0, z0);
		GL11.glVertex3f(x0, y0, z1);
		GL11.glVertex3f(x1, y0, z1);
		GL11.glVertex3f(x1, y0, z0);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(x0, y1, z0);
		GL11.glVertex3f(x0, y1, z1);
		GL11.glVertex3f(x0, y0, z1);
		GL11.glVertex3f(x0, y0, z0);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(x1, y1, z1);
		GL11.glVertex3f(x1, y1, z0);
		GL11.glVertex3f(x1, y0, z0);
		GL11.glVertex3f(x1, y0, z1);
		GL11.glEnd();
		if (level.isIrregularBlock(h.x, h.y, h.z))
			return;
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		Tesselator t = Tesselator.instance;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, ((float)Math.sin(System.currentTimeMillis() / 100.0D) * 0.2F + 0.4F) * 0.5F);
		t.begin();
		Tile.renderFaceNoTexture(t, h.x, h.y, h.z, h.face);
		t.end();
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public void renderCrack(Level level, Player player) {
		Tesselator t = Tesselator.instance;
		int dx = player.dx;
		int dy = player.dy;
		int dz = player.dz;
		int time = player.destoryTime;
		Tile tile = Tile.tiles[level.getBlock(dx, dy, dz)];
		if(tile == Tile.air) return;
		int destroyTime = tile.destroyTime;
		if((double)time / destroyTime < 0.001D)
			return;
		Texture.bind(1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA);
		int tex = 240 + (int)(10 * time / destroyTime);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);
		t.begin();
		t.noColor();
		tile.renderTexture(t, level, dx, dy, dz, tex, 0);
		t.end();
	    GL11.glDisable(GL11.GL_BLEND);
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	@Override
	public void blockChanged(int x, int y, int z) {
		setDirty(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
	}

	@Override
	public void lightColumnChanged(int x0, int y0, int z0, int x1, int y1, int z1) {
		setDirty(x0 - 1, y0 - 1, z0 - 1, x1 + 1, y1 + 1, z1 + 1);
	}

	@Override
	public void allChanged() {
		setDirty(level.x0, level.y0, level.z0, level.x1, level.y1, level.z1);
	}

	public void cull(Player player, Frustum frustum) {
		for(int x = 0; x < xChunks; x++)
			for(int y = 0; y < yChunks; y++)
				for(int z = 0; z < zChunks; z++) {
					boolean visible = frustum.isVisible(renderChunks[x][y][z].aabb) && renderChunks[x][y][z].tiles > 0;
					//if((!chunks[x][y][z].isDirty()) && (!visible) && chunks[x][y][z].distanceToSqr(player) > RENDER_DISTANCE * 2.0F)
					//	chunks[x][y][z].reset();
					renderChunks[x][y][z].visible = visible;
				}
	}
}
