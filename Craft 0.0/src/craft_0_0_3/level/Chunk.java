package craft_0_0_3.level;

import org.lwjgl.opengl.GL11;

import craft_0_0_3.Player;
import craft_0_0_3.level.tile.Tile;
import craft_0_0_3.level.tile.TileConst;
import craft_0_0_3.phys.AABB;
import craft_0_0_3.renderer.Texture;

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
	  private int list = -1;
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

	    this.list = GL11.glGenLists(1);
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
	  
	  public void rebuild() {
		  dirty = false;
		  updates++;
		  long before = System.nanoTime();
		  GL11.glNewList(this.list, GL11.GL_COMPILE);
		  int tiles = 0;
		  for (int x = x0; x <= x1; x++)
		      for (int y = y0; y <= y1; y++)
		    	  for (int z = z0; z <= z1; z++)
		    	  {
		    		  int tileId = level.getBlock(x, y, z);
		    		  if(tileId != TileConst.AIR.value) {
		    			  Tile.tiles[tileId].render(level, x, y, z);
		    			  tiles++;
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
	  
	  public void render() {
		  GL11.glCallList(this.list);
	  }

	public float distanceToSqr(Player player) {
		float xd = player.x - this.x;
	    float yd = player.y - this.y;
	    float zd = player.z - this.z;
	    return xd * xd + yd * yd + zd * zd;
	}
}
