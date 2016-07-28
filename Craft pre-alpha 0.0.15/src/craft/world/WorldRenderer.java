package craft.world;

import java.util.ArrayList;
import java.util.ListIterator;

import org.lwjgl.opengl.GL11;

import craft.HitResult;
import craft.Player;
import craft.phys.AABB;
import craft.renderer.Frustum;
import craft.renderer.Tesselator;
import craft.renderer.Texture;
import craft.setting.DisplayOption;
import craft.setting.GameSetting;
import craft.world.tile.Face;
import craft.world.tile.Tile;

public class WorldRenderer implements WorldAccess {
	public static final int MAX_REBUILDS_PER_FRAME = 4;
	public static final int CHUNK_SIZE = 16;
	public static final int CHUNK_SHIFT_COUNT = CHUNK_SIZE >> 2;
	private World world;
	private RenderChunk[][][] renderChunks;
	private ArrayList<RenderChunk> renderChunkList = new ArrayList<RenderChunk>();
	private ArrayList<RenderChunk> dirtyRenderChunks;
	private int xChunks;
	private int yChunks;
	private int zChunks;
	private float cx, cy, cz, dx, dy, dz;
	/**边界地表显示列表*/
	private int surroundingLists;

	public WorldRenderer(World world) {
		this.world = world;
		world.addWorldAccess(this);

		xChunks = world.length >> CHUNK_SHIFT_COUNT;
		yChunks = world.height >> CHUNK_SHIFT_COUNT;
		zChunks = world.width >> CHUNK_SHIFT_COUNT;

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

					x0 -= world.length / 2;
					x1 -= (world.length / 2 + 1);
					z0 -= world.width / 2;
					z1 -= (world.width / 2 + 1);
					y1 --;

					if (x1 > world.x1) x1 = world.x1;
					if (y1 > world.y1) y1 = world.y1;
					if (z1 > world.z1) z1 = world.z1;
					RenderChunk chunk = new RenderChunk(world, x0, y0, z0, x1, y1, z1);
					renderChunks[x][y][z] = chunk;
					renderChunkList.add(chunk);
					//System.out.printf("%d,%d,%d,%d,%d,%d,%d,%d,%d\n", x, y, z, x0, y0, z0, x1, y1, z1);
				}
		
		surroundingLists = GL11.glGenLists(2);
		complieSurroundingGroundList();
		complieSurroundingWaterList();
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
			if(c.visible && c.distanceToSqr(player) < GameSetting.instance.getFloat(DisplayOption.RenderDistance))
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
					if (chunk.isDirty() && world.isChunkLoaded(chunk.chunkx, chunk.chunkz)) {
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
					if (world.isChunkLoaded(chunk.chunkx, chunk.chunkz) && chunk.distanceToSqr(player) < GameSetting.instance.getFloat(DisplayOption.RenderDistance) * 1.5F) {
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
				dirtyRenderChunks.get(i).rebuild();
			}
		}
	}

	public void setDirty(int x0, int y0, int z0, int x1, int y1, int z1) {
		x0 += world.length / 2;
		z0 += world.width / 2;
		x1 += world.length / 2;
		z1 += world.width / 2;

		x0 >>= CHUNK_SHIFT_COUNT;
		x1 >>= CHUNK_SHIFT_COUNT;
		y0 >>= CHUNK_SHIFT_COUNT;
		y1 >>= CHUNK_SHIFT_COUNT;
		z0 >>= CHUNK_SHIFT_COUNT;
		z1 >>= CHUNK_SHIFT_COUNT;

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
					if (world.getBlock(x, y, z) != Tile.air.id) {
						GL11.glPushName(0);
						for(Face face : Face.values()) {
							GL11.glPushName(face.ordinal());
							t.begin();
							if (Tile.tiles[world.getBlock(x, y, z)].mayPick())
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
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glColor4f(0.1F, 0.1F, 0.1F, 0.6F);
		GL11.glLineWidth(1.5F);
		Tile tile = Tile.tiles[world.getBlock(h.x, h.y, h.z)];
		AABB aabb = tile.getAABB(h.x, h.y, h.z);
		float x0 = aabb.x0;
		float x1 = aabb.x1;
		float y0 = aabb.y0;
		float y1 = aabb.y1;
		float z0 = aabb.z0;
		float z1 = aabb.z1;
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
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		if (!world.isIrregularBlock(h.x, h.y, h.z)) {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			Tesselator t = Tesselator.instance;
			GL11.glColor4f(1.0F, 1.0F, 1.0F, ((float)Math.sin(System.currentTimeMillis() / 100.0D) * 0.2F + 0.2F) * 0.3F);
			t.begin();
			t.noColor();
			Tile.renderFaceNoTexture(t, h.x, h.y, h.z, h.face);
			t.end();
		}
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public void renderCrack(World world, Player player) {
		Tesselator t = Tesselator.instance;
		int dx = player.dx;
		int dy = player.dy;
		int dz = player.dz;
		int time = player.destoryTime;
		Tile tile = Tile.tiles[world.getBlock(dx, dy, dz)];
		if(tile == Tile.air) return;
		if(tile == Tile.bedrock) return;
		int destroyTime = tile.hardness;
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
		tile.renderTexture(t, world, dx, dy, dz, tex, 0);
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
		setDirty(world.x0, world.y0, world.z0, world.x1, world.y1, world.z1);
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
	
	/**编译边界地表显示列表*/
	public void complieSurroundingGroundList() {
		GL11.glNewList(surroundingLists, GL11.GL_COMPILE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Tesselator t = Tesselator.instance;
		float y = World.GROUND_LEVEL;
		int s = World.GROUND_WIDTH;
		if (s > world.length)	s = world.length;
		if (s > world.width)	s = world.width;
		/**边界地表宽度系数*/
		int d = 3;
		t.begin();
		for (int xx = world.x0 - s * d; xx < world.x1 + s * d; xx += s) {
			for (int zz = world.z0 - s * d; zz < world.z1 + s * d; zz += s) {
				float yy = y;
				if ((xx >= world.x0) && (zz >= world.z0) && (xx < world.x1) && (zz < world.z1))
					continue;
				t.vertexUV(xx + 0, yy, zz + s, 0.0F, s);
				t.vertexUV(xx + s, yy, zz + s, s, s);
				t.vertexUV(xx + s, yy, zz + 0, s, 0.0F);
				t.vertexUV(xx + 0, yy, zz + 0, 0.0F, 0.0F);
			}
		}
		t.end();
		GL11.glColor3f(0.8F, 0.8F, 0.8F);
		t.begin();
		for (int xx = world.x0; xx < world.x1; xx += s) {
			t.vertexUV(xx + 0, 0.0F, world.z0, 0.0F, 0.0F);
			t.vertexUV(xx + s, 0.0F, world.z0, s, 0.0F);
			t.vertexUV(xx + s, y, world.z0, s, y);
			t.vertexUV(xx + 0, y, world.z0, 0.0F, y);

			t.vertexUV(xx + 0, y, world.z1 + 1.0F, 0.0F, y);
			t.vertexUV(xx + s, y, world.z1 + 1.0F, s, y);
			t.vertexUV(xx + s, 0.0F, world.z1 + 1.0F, s, 0.0F);
			t.vertexUV(xx + 0, 0.0F, world.z1 + 1.0F, 0.0F, 0.0F);
		}
		GL11.glColor3f(0.6F, 0.6F, 0.6F);
		for (int zz = world.z0; zz <= world.z1; zz += s) {
			t.vertexUV(world.x0, y, zz + 0, 0.0F, 0.0F);
			t.vertexUV(world.x0, y, zz + s, s, 0.0F);
			t.vertexUV(world.x0, 0.0F, zz + s, s, y);
			t.vertexUV(world.x0, 0.0F, zz + 0, 0.0F, y);

			t.vertexUV(world.x1 + 1.0F, 0.0F, zz + 0, 0.0F, y);
			t.vertexUV(world.x1 + 1.0F, 0.0F, zz + s, s, y);
			t.vertexUV(world.x1 + 1.0F, y, zz + s, s, 0.0F);
			t.vertexUV(world.x1 + 1.0F, y, zz + 0, 0.0F, 0.0F);
		}
		t.end();
		GL11.glEndList();
	}
	
	/**编译边界地表水显示列表*/
	public void complieSurroundingWaterList() {
		GL11.glNewList(surroundingLists + 1, GL11.GL_COMPILE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Tesselator t = Tesselator.instance;
		float y = World.GROUND_LEVEL + 2.0F;
		int s = World.GROUND_WIDTH;
		if (s > world.length)	s = world.length;
		if (s > world.width)	s = world.width;
		/**边界地表宽度系数*/
		int d = 3;
		t.begin();
		for (int xx = world.x0 - s * d; xx < world.x1 + s * d; xx += s) {
			for (int zz = world.z0 - s * d; zz < world.z1 + s * d; zz += s) {
				float yy = y - 0.1F;
				if ((xx >= world.x0) && (zz >= world.z0) && (xx < world.x1) && (zz < world.z1))
					continue;
				t.vertexUV(xx + 0, yy, zz + s, 0.0F, s);
				t.vertexUV(xx + s, yy, zz + s, s, s);
				t.vertexUV(xx + s, yy, zz + 0, s, 0.0F);
				t.vertexUV(xx + 0, yy, zz + 0, 0.0F, 0.0F);

				t.vertexUV(xx + 0, yy, zz + 0, 0.0F, 0.0F);
				t.vertexUV(xx + s, yy, zz + 0, s, 0.0F);
				t.vertexUV(xx + s, yy, zz + s, s, s);
				t.vertexUV(xx + 0, yy, zz + s, 0.0F, s);
			}
		}
		t.end();
		GL11.glEndList();
	}
	
	/**渲染边界地表*/
	public void renderSurroundingGround() {
		Texture.bind(2);
		GL11.glCallList(surroundingLists);
		Texture.bind(3);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glCallList(surroundingLists + 1);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
}
