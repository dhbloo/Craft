package craft;

import craft.world.tile.Face;

public class HitResult {
  public int x;
  public int y;
  public int z;
  public int o;
  public Face face;

  public HitResult(int x, int y, int z, int o, Face face) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.o = o;
    this.face = face;
  }
  
}