package craft.level.tile;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import craft.Player;
import craft.level.Level;
import craft.particle.ParticleEngine;
import craft.phys.AABB;
import craft.renderer.Frustum;
import craft.renderer.Texture;

public class EntityTile extends Tile implements Cloneable{
	public static final float FALL_SPEED = 0.1F;
	protected int x, z;
	protected float y;
	protected boolean onGround = false;
	private static ArrayList<EntityTile> entityTiles = new ArrayList<EntityTile>();

	protected EntityTile(int id, int tex, int destroyTime, boolean soildTile, boolean lightTile) {
		super(id, tex, destroyTime, soildTile, lightTile);
	}
	
	public static void ticks(Level level, Player player) {
		for(int i = 0 ; i < entityTiles.size() ; i++){
			EntityTile t = entityTiles.get(i);
		    t.update(level, player);
		}
	}
	
	protected void update(Level level, Player player) {
		int ya = (int) Math.floor(y);
		move(level, -FALL_SPEED);
		if(onGround) {
			if(level.getBlock(x, ya, z) != id)
				level.setBlock(x, ya, z, id);
		} else {
			if(level.getBlock(x, ya, z) != air.id)
				level.setBlock(x, ya, z, air.id);
		}
		if(y < -128.0F)
			entityTiles.remove(this);
	}
	
	public void move(Level level, float ya) {		//返回是否着地
		float yaOrg = ya;
		AABB aabb = getAABB();
		ArrayList<AABB> aABBs = level.getCubes(aabb.expand(0.0F, ya, 0.0F));

		for (int i = 0; i < aABBs.size(); i++)
			ya = aABBs.get(i).clipYCollide(aabb, ya);
		aabb.move(0.0F, ya, 0.0F);

		onGround =  (yaOrg != ya);
		y = aabb.y0;
	}
	
	public void add(int x, int y, int z) {
		try {
			EntityTile newTile = (EntityTile) this.clone();
			newTile.x = x;
			newTile.y = y;
			newTile.z = z;
			entityTiles.add(newTile);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	public boolean destroy(Level level, int x, int y, int z, ParticleEngine particleEngine, boolean needTime) {
		if(!super.destroy(level, x, y, z, particleEngine, needTime))
			return false;
		for(int i = 0 ; i < entityTiles.size() ; i++){
			EntityTile t = entityTiles.get(i);
			if(t.x == x && t.y == y && t.z == z) {
				entityTiles.remove(i);
				break;
			}
		}
		return true;
	}
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	public static void renderEntityTiles(Level level, Frustum fusturm) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		for(int i = 0 ; i < entityTiles.size() ; i++){
			EntityTile t = entityTiles.get(i);
			if(!t.onGround && fusturm.isVisible(t.getAABB()))
				t.render(level);
		}
		Texture.bind(-1);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	protected void render(Level level) {
		int tex = getTexture(Face.Front);
		float x0 = x + 0.0F;
		float x1 = x + 1.0F;
		float y0 = y + 0.0F;
		float y1 = y + 1.0F;
		float z0 = z + 0.0F;
		float z1 = z + 1.0F;
		float c1 = 1.0F;
	    float c2 = 0.8F;
	    float c3 = 0.5F;
	    float c4 = 0.6F;
	    float b = 0.0F;
		Texture.bind(tex);
		GL11.glBegin(GL11.GL_QUADS);
		b =  getBrightness(level, x, y, z + 1) * c2;
		GL11.glColor4f(b, b, b, 1.0F);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(x0, y1, z1);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(x1, y1, z1);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(x1, y0, z1);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(x0, y0, z1);
		b =  getBrightness(level, x, y, z - 1) * c2;
		GL11.glColor4f(b, b, b, 1.0F);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(x0, y1, z0);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(x0, y0, z0);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(x1, y0, z0);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(x1, y1, z0);
		b =  getBrightness(level, x, y + 1, z) * c1;
		GL11.glColor4f(b, b, b, 1.0F);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(x1, y1, z0);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(x1, y1, z1);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(x0, y1, z1);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(x0, y1, z0);
		b =  getBrightness(level, x, y - 1, z) * c3;
		GL11.glColor4f(b, b, b, 1.0F);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(x0, y0, z0);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(x0, y0, z1);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(x1, y0, z1);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(x1, y0, z0);
		b =  getBrightness(level, x, y, z - 1) * c4;
		GL11.glColor4f(b, b, b, 1.0F);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(x0, y1, z0);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(x0, y1, z1);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(x0, y0, z1);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(x0, y0, z0);
		b =  getBrightness(level, x, y, z + 1) * c4;
		GL11.glColor4f(b, b, b, 1.0F);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(x1, y1, z1);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(x1, y1, z0);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(x1, y0, z0);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(x1, y0, z1);
		GL11.glEnd();
	}
	
	protected float getBrightness(Level level, int x, float y, int z) {
		return super.getBrightness(level, x, (int)y, z);
	}
	
	public AABB getAABB() {
		return new AABB(x, y, z, x + 1, y + 1, z + 1);
	}
	
	public static ArrayList<AABB> getTilesAABB() {
		ArrayList<AABB> aabb = new ArrayList<AABB>();
		for(int i = 0 ; i < entityTiles.size() ; i++){
			EntityTile t = entityTiles.get(i);
			aabb.add(t.getAABB());
		}
		return aabb;
	}

}
