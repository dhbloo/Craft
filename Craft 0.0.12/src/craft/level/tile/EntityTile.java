package craft.level.tile;

import java.util.ArrayList;

import craft.Player;
import craft.level.Level;
import craft.phys.AABB;
import craft.renderer.Frustum;
import craft.renderer.Tesselator;

public class EntityTile extends Tile implements Cloneable{
	public static final float FALL_SPEED = 0.2F;
	protected int x, z;
	protected float y, yo;
	private static ArrayList<EntityTile> entityTiles = new ArrayList<EntityTile>();

	protected EntityTile(int id, int tex, int destroyTime, boolean soildTile, boolean lightTile, int brightness, float brightnessDecay) {
		super(id, tex, destroyTime, soildTile, lightTile, brightness, brightnessDecay);
	}
	
	public static void ticks(Level level, Player player) {
		for (int i = 0; i < entityTiles.size(); i++) {
			EntityTile entityTile = entityTiles.get(i);
			if (level.isBlockActive(entityTile.x, entityTile.z))
				entityTile.update(level, player);
		}
	}
	
	protected void update(Level level, Player player) {		
		yo = y;
		int ya = (int) Math.floor(y);
		/**是否正在更新*/
		boolean updating = !move(level, -FALL_SPEED);
		if(updating) {
			if(level.getBlock(x, ya, z) == id)
				level.setBlock(x, ya, z, air.id);
		} else {
			if(level.getBlock(x, ya, z) != id)
				level.setBlock(x, ya, z, id);
			entityTiles.remove(this);
		}
		if(y < -128.0F)
			entityTiles.remove(this);
	}
	
	public boolean move(Level level, float ya) {		//返回是否着地
		float yaOrg = ya;
		AABB aabb = getAABB();
		ArrayList<AABB> aABBs = level.getCubes(aabb.expand(0.0F, ya, 0.0F));
		aABBs.addAll(getTilesAABB());

		for (int i = 0; i < aABBs.size(); i++)
			ya = aABBs.get(i).clipYCollide(aabb, ya);
		aabb.move(0.0F, ya, 0.0F);

		y = aabb.y0;
		return (yaOrg != ya);
	}
	
	public void add(int x, int y, int z) {
		try {
			EntityTile newTile = (EntityTile) this.clone();
			newTile.x = x;
			newTile.y = y;
			newTile.yo = y;
			newTile.z = z;
			entityTiles.add(newTile);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	public void remove(int x, int y, int z) {
		for(int i = 0 ; i < entityTiles.size() ; i++){
			EntityTile t = entityTiles.get(i);
			if(t.x == x && t.y == y && t.z == z) {
				entityTiles.remove(i);
				break;
			}
		}
	}
	
	protected void remove(EntityTile tile) {
		entityTiles.remove(tile);
	}
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	public static void renderEntityTiles(Tesselator t, Level level, Frustum frustum, float a) {
		t.begin();
		for(int i = 0 ; i < entityTiles.size() ; i++){
			EntityTile tile = entityTiles.get(i);
			if(frustum.isVisible(tile.getAABB()))
				tile.render(t, level, a);
		}
		t.end();
	}
	
	protected void render(Tesselator t, Level level, float a) {
		int xt = tex % 16 * 16;
	    int yt = tex / 16 * 16;
	    float u0 = xt / 256.0F;
	    float u1 = (xt + 16.0F) / 256.0F;
	    float v0 = yt / 256.0F;
	    float v1 = (yt + 16.0F) / 256.0F;
		
		float x0 = x + 0.0F;
		float x1 = x + 1.0F;
		float y0 = y + (y - yo) * a;
		float y1 = y0 + 1.0F;
		float z0 = z + 0.0F;
		float z1 = z + 1.0F;
		
		float c1 = 1.0F;
	    float c2 = 0.8F;
	    float c3 = 0.5F;
	    float c4 = 0.6F;
	    float b = 0.0F;
	    
		b =  getBrightness(level, x, y, z + 1) * c2;
		t.color(b, b, b);
		t.vertexUV(x0, y1, z1, u0, v0);
        t.vertexUV(x0, y0, z1, u0, v1);
        t.vertexUV(x1, y0, z1, u1, v1);
        t.vertexUV(x1, y1, z1, u1, v0);
		b =  getBrightness(level, x, y, z - 1) * c2;
		t.color(b, b, b);
		t.vertexUV(x0, y1, z0, u1, v0);
        t.vertexUV(x1, y1, z0, u0, v0);
        t.vertexUV(x1, y0, z0, u0, v1);
        t.vertexUV(x0, y0, z0, u1, v1);
		b =  getBrightness(level, x, y + 1, z) * c1;
		t.color(b, b, b);
		t.vertexUV(x1, y1, z1, u1, v1);
        t.vertexUV(x1, y1, z0, u1, v0);
        t.vertexUV(x0, y1, z0, u0, v0);
        t.vertexUV(x0, y1, z1, u0, v1);
		b =  getBrightness(level, x, y - 1, z) * c3;
		t.color(b, b, b);
		t.vertexUV(x0, y0, z1, u0, v1);
        t.vertexUV(x0, y0, z0, u0, v0);
        t.vertexUV(x1, y0, z0, u1, v0);
        t.vertexUV(x1, y0, z1, u1, v1);
		b =  getBrightness(level, x - 1, y, z) * c4;
		t.color(b, b, b);
		t.vertexUV(x0, y1, z1, u1, v0);
        t.vertexUV(x0, y1, z0, u0, v0);
        t.vertexUV(x0, y0, z0, u0, v1);
        t.vertexUV(x0, y0, z1, u1, v1);
		b =  getBrightness(level, x + 1, y, z) * c4;
		t.color(b, b, b);
    	t.vertexUV(x1, y0, z1, u0, v1);
        t.vertexUV(x1, y0, z0, u1, v1);
        t.vertexUV(x1, y1, z0, u1, v0);
        t.vertexUV(x1, y1, z1, u0, v0);
	}
	
	protected float getBrightness(Level level, int x, float y, int z) {
		return super.getBrightness(level, x, (int)Math.floor(y), z);
	}
	
	public AABB getAABB() {
		return new AABB(x, y, z, x + 1, y + 1, z + 1);
	}
	
	public static ArrayList<AABB> getTilesAABB() {
		ArrayList<AABB> aabb = new ArrayList<AABB>();
		for(int i = 0 ; i < entityTiles.size() ; i++){
			EntityTile t = entityTiles.get(i);
			if (t.soildTile)
				aabb.add(t.getAABB());
		}
		return aabb;
	}

	protected boolean shouldRender() {
		return true;
	}
	
	@Override
	public void neighborChanged(Level level, int x, int y, int z, int block) {
		if(!level.isSolidBlock(x, y - 1, z))
			add(x, y, z);
	}
	
	public static int getEntityTilesCount() {
		return entityTiles.size();
	}
	
}
