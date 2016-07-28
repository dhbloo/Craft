package craft_0_0_1.level;

import org.lwjgl.opengl.GL11;

import craft_0_0_1.level.tile.Tile;
import craft_0_0_1.level.tile.TileConst;
import craft_0_0_1.phys.AABB;

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
	  @SuppressWarnings("unused")
	private static long totalTime = 0L;
	  @SuppressWarnings("unused")
	private static int totalUpdates = 0;
	  
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
	    this.aabb = new AABB(x0, y0, z0, x1, y1, z1);
	  }
	  
	  public void setDirty() {
		  this.dirty =  true;
	  }
	  
	  public boolean isDirty() {
		  return this.dirty;
	  }
	  
	  public void rebuild() {
		  dirty = false;
		  updates++;
		  long before = System.nanoTime();
		  GL11.glNewList(list, GL11.GL_COMPILE);
		  int tiles = 0;
		  for (int x = x0; x <= x1; x++)
		      for (int y = y0; y <= y1; y++)
		    	  for (int z = z0; z <= z1; z++)
		    	  {
		    		  int tileId = level.getBlock(x, y, z);
		    		  if(tileId != TileConst.AIR.getValue()) {
		    			  Tile.tiles[tileId].render(level, x, y, z);
		    			  tiles++;
		    		  }
		    	  }
		  GL11.glEndList();
		  long after = System.nanoTime();
		  if(tiles > 0) {
			  totalTime += after - before;
		      totalUpdates += 1;
		  }
	  }
	  
	  public void render() {
		  GL11.glCallList(list);
	  }
}
