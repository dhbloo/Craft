package craft.world.tile;

import java.util.Random;

import craft.item.DroppedItem;
import craft.item.PickupEngine;
import craft.particle.Particle;
import craft.particle.SmallParticle;
import craft.phys.AABB;
import craft.renderer.Tesselator;
import craft.world.Brightness;
import craft.world.World;
import craft.world.tile.LiquidTile.LiquidType;

public class Tile {
	/**普通方块的共享AABB*/
	public static final AABB normalAABB = new AABB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	
	public static final Tile[] tiles = new Tile[256];
	public static final Tile air = (new Tile(0, 0)).setOpacity(0.04F);
	public static final Tile stone = new StoneTile(1, 3);
	public static final GrassTile grass = new GrassTile(2, 1);
	public static final Tile soil = (new Tile(3, 2)).setHardness(30);
	public static final Tile cobblestone = (new Tile(4, 4)).setHardness(120);
	public static final Tile bedrock = (new Tile(5, 5)).setUnbreakable();
	public static final Tile planks = new Tile(6, 6).setHardness(80);
	public static final Tile oakPlanks = (new Tile(7, 7)).setHardness(80);
	public static final TreeTile trunk = new TreeTile(8, 9);
	public static final LeafTile leaf = new LeafTile(9, 10);
	public static final SandTile sand = new SandTile(10, 11);
	public static final LiquidTile water = new LiquidTile(11, 12, 13, LiquidType.Water);
	public static final CalmLiquidTile calmWater = new CalmLiquidTile(12, 12, 13, LiquidType.Water);
	public static final LiquidTile lava = new LiquidTile(13, 14, 14, LiquidType.Lava);
	public static final CalmLiquidTile calmLava = new CalmLiquidTile(14, 14, 14, LiquidType.Lava);
	public static final Tile tallGrass = new Bush(15, 15);
	public static final Tile oakSapling = new Bush(16, 18);
	public static final Tile dandelion = new Bush(17, 16);
	public static final Tile poppy = new Bush(18, 17);
	public static final Tile gravel = new SandTile(19, 19);
	public static final Tile coalOre = new OreTile(20, 20).setHardness(100);
	public static final Tile ironOre = new OreTile(21, 21).setHardness(130);
	public static final Tile goldOre = new OreTile(22, 22).setHardness(180);
	
	public static final int PARTICLE_COUNT = 4;
	public static final int TICK_COUNT = 4;
	public final int tex;
	public final int id;
	/**方块硬度*/
	public int hardness;
	/**方块的本身亮度*/
	public byte brightness;
	/**不透明度（亮度衰减度）*/
	public float opacity;
	/**方块碰撞箱AABB，要在(0, 0, 0)到(1, 1, 1)之间*/
	protected AABB aabb;
	
	protected Tile(int id, int tex) {
		this.tex = tex;
		this.id = id;
		this.brightness = 0;
		this.opacity = 1.0F;
		this.aabb = normalAABB;
		tiles[id] = this;
	}
	
	/**获取方块对应面的纹理序号*/
	public int getTexture(Face face) {
	    return this.tex;
	}
	
	/**区块渲染中的方块渲染*/
	public void render(Tesselator t, World world, int x, int y, int z, int layer) {
		float c1 = 1.0F;
	    float c2 = 0.8F;
	    float c3 = 0.5F;
	    float c4 = 0.6F;
	    float b = 0.0F;
	    if(shouldRenderFace(world, x - 1, y, z, layer)) {
	    	b =  getBrightness(world, x - 1, y, z) * c4;
	    	t.color(b, b, b);
	    	renderFace(t, x, y, z, Face.Left);
	    }
	    if(shouldRenderFace(world, x + 1, y, z, layer)) {
	    	b =  getBrightness(world, x + 1, y, z) * c4;
	    	t.color(b, b, b);
	    	renderFace(t, x, y, z, Face.Right);
	    }
	    if(shouldRenderFace(world, x, y - 1, z, layer)) {
	    	b =  getBrightness(world, x, y - 1, z) * c3;
	    	t.color(b, b, b);
	    	renderFace(t, x, y, z, Face.Bottom);
	    }
	    if(shouldRenderFace(world, x, y + 1, z, layer)) {
	    	b =  getBrightness(world, x, y + 1, z) * c1;
	    	t.color(b, b, b);
	    	renderFace(t, x, y, z, Face.Top);
	    }
	    if(shouldRenderFace(world, x, y, z - 1, layer)) {
	    	b =  getBrightness(world, x, y, z - 1) * c2;
	    	t.color(b, b, b);
	    	renderFace(t, x, y, z, Face.Back);
	    }
	    if(shouldRenderFace(world, x, y, z + 1, layer)) {
	    	b =  getBrightness(world, x, y, z + 1) * c2;
	    	t.color(b, b, b);
	    	renderFace(t, x, y, z, Face.Front);
	    }
	}
	
	protected boolean shouldRenderFace(World world, int x, int y, int z, int layer) {
	    if (world.isLightBlock(x, y, z) && layer == 0)
	    	return true; 
	    return false;
	  }
	
	protected float getBrightness(World world, int x, int y, int z) {
		return (float)world.getBrightness(x, y, z) / Brightness.MAX * 0.9F + 0.1F;
	}
	
	protected void renderFace(Tesselator t, int x, int y, int z, Face face) {
		int tex = getTexture(face);
		
		int xt = tex % 16 * 16;
	    int yt = tex / 16 * 16;
	    float u0 = xt / 256.0F;
	    float u1 = (xt + 15.99F) / 256.0F;
	    float v0 = yt / 256.0F;
	    float v1 = (yt + 15.99F) / 256.0F;
	    
		float x0 = x + 0.0F;
	    float x1 = x + 1.0F;
	    float y0 = y + 0.0F;
	    float y1 = y + 1.0F;
	    float z0 = z + 0.0F;
	    float z1 = z + 1.0F;
	    switch(face) {
	    case Front:
	    	t.vertexUV(x0, y1, z1, u0, v0);
	        t.vertexUV(x0, y0, z1, u0, v1);
	        t.vertexUV(x1, y0, z1, u1, v1);
	        t.vertexUV(x1, y1, z1, u1, v0);
	    	break;
	    case Back:
	    	t.vertexUV(x0, y1, z0, u1, v0);
	        t.vertexUV(x1, y1, z0, u0, v0);
	        t.vertexUV(x1, y0, z0, u0, v1);
	        t.vertexUV(x0, y0, z0, u1, v1);
	    	break;
	    case Top:
	    	t.vertexUV(x1, y1, z1, u1, v1);
	        t.vertexUV(x1, y1, z0, u1, v0);
	        t.vertexUV(x0, y1, z0, u0, v0);
	        t.vertexUV(x0, y1, z1, u0, v1);
	    	break;
	    case Bottom:
	    	t.vertexUV(x0, y0, z1, u0, v1);
	        t.vertexUV(x0, y0, z0, u0, v0);
	        t.vertexUV(x1, y0, z0, u1, v0);
	        t.vertexUV(x1, y0, z1, u1, v1);
	    	break;
	    case Left:
	    	t.vertexUV(x0, y1, z1, u1, v0);
	        t.vertexUV(x0, y1, z0, u0, v0);
	        t.vertexUV(x0, y0, z0, u0, v1);
	        t.vertexUV(x0, y0, z1, u1, v1);
	    	break;
	    case Right:
	    	t.vertexUV(x1, y0, z1, u0, v1);
	        t.vertexUV(x1, y0, z0, u1, v1);
	        t.vertexUV(x1, y1, z0, u1, v0);
	        t.vertexUV(x1, y1, z1, u0, v0);
	    	break;
	    }
	}
	
	public static void renderFaceNoTexture(Tesselator t, int x, int y, int z, Face face) {
		float x0 = x + 0.0F;
	    float x1 = x + 1.0F;
	    float y0 = y + 0.0F;
	    float y1 = y + 1.0F;
	    float z0 = z + 0.0F;
	    float z1 = z + 1.0F;
	    switch(face) {
	    case Front:
	    	t.vertex(x0, y1, z1);
	        t.vertex(x0, y0, z1);
	        t.vertex(x1, y0, z1);
	        t.vertex(x1, y1, z1);
	    	break;
	    case Back:
	    	t.vertex(x0, y1, z0);
	        t.vertex(x1, y1, z0);
	        t.vertex(x1, y0, z0);
	        t.vertex(x0, y0, z0);
	    	break;
	    case Top:
	    	t.vertex(x1, y1, z1);
	        t.vertex(x1, y1, z0);
	        t.vertex(x0, y1, z0);
	        t.vertex(x0, y1, z1);
	    	break;
	    case Bottom:
	    	t.vertex(x0, y0, z1);
	        t.vertex(x0, y0, z0);
	        t.vertex(x1, y0, z0);
	        t.vertex(x1, y0, z1);
	    	break;
	    case Left:
	    	t.vertex(x0, y1, z1);
	        t.vertex(x0, y1, z0);
	        t.vertex(x0, y0, z0);
	        t.vertex(x0, y0, z1);
	    	break;
	    case Right:
	    	t.vertex(x1, y0, z1);
	        t.vertex(x1, y0, z0);
	        t.vertex(x1, y1, z0);
	        t.vertex(x1, y1, z1);
	    	break;
	    }
	}

	public static void renderFaceWithTexture(Tesselator t, int x, int y, int z, Face face, int tex) {
		int xt = tex % 16 * 16;
	    int yt = tex / 16 * 16;
	    float u0 = xt / 256.0F;
	    float u1 = (xt + 15.99F) / 256.0F;
	    float v0 = yt / 256.0F;
	    float v1 = (yt + 15.99F) / 256.0F;
	    
		float x0 = x + 0.0F;
	    float x1 = x + 1.0F;
	    float y0 = y + 0.0F;
	    float y1 = y + 1.0F;
	    float z0 = z + 0.0F;
	    float z1 = z + 1.0F;
	    switch(face) {
	    case Front:
	    	t.vertexUV(x0, y1, z1, u0, v0);
	        t.vertexUV(x0, y0, z1, u0, v1);
	        t.vertexUV(x1, y0, z1, u1, v1);
	        t.vertexUV(x1, y1, z1, u1, v0);
	    	break;
	    case Back:
	    	t.vertexUV(x0, y1, z0, u1, v0);
	        t.vertexUV(x1, y1, z0, u0, v0);
	        t.vertexUV(x1, y0, z0, u0, v1);
	        t.vertexUV(x0, y0, z0, u1, v1);
	    	break;
	    case Top:
	    	t.vertexUV(x1, y1, z1, u1, v1);
	        t.vertexUV(x1, y1, z0, u1, v0);
	        t.vertexUV(x0, y1, z0, u0, v0);
	        t.vertexUV(x0, y1, z1, u0, v1);
	    	break;
	    case Bottom:
	    	t.vertexUV(x0, y0, z1, u0, v1);
	        t.vertexUV(x0, y0, z0, u0, v0);
	        t.vertexUV(x1, y0, z0, u1, v0);
	        t.vertexUV(x1, y0, z1, u1, v1);
	    	break;
	    case Left:
	    	t.vertexUV(x0, y1, z1, u1, v0);
	        t.vertexUV(x0, y1, z0, u0, v0);
	        t.vertexUV(x0, y0, z0, u0, v1);
	        t.vertexUV(x0, y0, z1, u1, v1);
	    	break;
	    case Right:
	    	t.vertexUV(x1, y0, z1, u0, v1);
	        t.vertexUV(x1, y0, z0, u1, v1);
	        t.vertexUV(x1, y1, z0, u1, v0);
	        t.vertexUV(x1, y1, z1, u0, v0);
	    	break;
	    }
	}

	public void renderTexture(Tesselator t, World world, int x, int y, int z, int tex, int layer) {
		float c1 = 1.0F;
	    float c2 = 0.8F;
	    float c3 = 0.6F;
	    if(shouldRenderFace(world, x - 1, y, z, layer)) {
	    	t.color(c2, c2, c2);
	    	renderFaceWithTexture(t, x, y, z, Face.Left, tex);
	    }
	    if(shouldRenderFace(world, x + 1, y, z, layer)) {
	    	t.color(c2, c2, c2);
	    	renderFaceWithTexture(t, x, y, z, Face.Right, tex);
	    }
	    if(shouldRenderFace(world, x, y - 1, z, layer)) {
	    	t.color(c3, c3, c3);
	    	renderFaceWithTexture(t, x, y, z, Face.Bottom, tex);
	    }
	    if(shouldRenderFace(world, x, y + 1, z, layer)) {
	    	t.color(c1, c1, c1);
	    	renderFaceWithTexture(t, x, y, z, Face.Top, tex);
	    }
	    if(shouldRenderFace(world, x, y, z - 1, layer)) {
	    	t.color(c2, c2, c2);
	    	renderFaceWithTexture(t, x, y, z, Face.Back, tex);
	    }
	    if(shouldRenderFace(world, x, y, z + 1, layer)) {
	    	t.color(c2, c2, c2);
	    	renderFaceWithTexture(t, x, y, z, Face.Front, tex);
	    }
	}
	
	public AABB getAABB(int x, int y, int z) {
		return aabb.cloneMove(x, y, z);
	}
	
	/**随机性更新*/
	public void tick(World world, int x, int y, int z, Random random) {
		
	}
	
	/**方块被破坏了*/
	public void destroyed(World world, int x, int y, int z, PickupEngine pickupEngine) {
		world.setBlock(x, y, z, air.id);
		createDestoryParticle(world, x, y, z);
		Tile tile = getDroppedTile();
		if (tile != null)
			pickupEngine.add(new DroppedItem(world, x, y, z, 0.0F, 0.0F, 0.0F, tile));
	}
	
	public void destroying(World world, int x, int y, int z) {
		world.addParticle(new SmallParticle(world, x + 0.5F, y + 0.5F, z + 0.5F, this.tex));
	}
	
	/**创建破坏方块的粒子效果*/
	public void createDestoryParticle(World world, int x, int y, int z) {
		int SD = PARTICLE_COUNT;
		for (int xx = 0; xx < SD; xx++)
			for (int yy = 0; yy < SD; yy++)
				for (int zz = 0; zz < SD; zz++) {
					float xp = x + (xx + 0.5F) / SD;
					float yp = y + (yy + 0.5F) / SD;
					float zp = z + (zz + 0.5F) / SD;
					world.addParticle(new Particle(world, xp, yp, zp, xp - x - 0.5F, yp - y - 0.5F, zp - z - 0.5F, this.tex));
				}
	}
	
	public void neighborChanged(World world, int x, int y, int z, int block) {
		
	}
	
	/**获取方块的掉落物品*/
	public Tile getDroppedTile() {
		return this;
	}
	
	/**是否为透明方块*/
	public boolean isLightTile() {
		return false;
	}
	
	/**是否为固体方块（是否会有碰撞效果）*/
	public boolean isSoildTile() {
		return true;
	}
	
	/**是否可拾取*/
	public boolean mayPick() {
		return true;
	}
	
	/**是否需要随机更新*/
	public boolean shouldTick() {
		return false;
	}
	
	/**是否需要周期性更新*/
	public boolean shouldUpdate() {
		return false;
	}

	/**设置方块硬度（破坏时间）*/
	protected Tile setHardness(int hardness) {
		this.hardness = hardness;
		return this;
	}
	
	/**设置方块自身亮度*/
	protected Tile setBrightness(byte brightness) {
		this.brightness = brightness;
		return this;
	}
	
	/**设置方块亮度衰减*/
	protected Tile setOpacity(float opacity) {
		this.opacity = opacity;
		return this;
	}
	
	/**设置方块碰撞AABB*/
	protected Tile setAABB(AABB aabb) {
		this.aabb = aabb;
		return this;
	}
	
	/**设置方块不可破坏*/
	protected Tile setUnbreakable() {
		this.hardness = -1;
		return this;
	}

	/**周期性更新*/
	public void update(World world, int x, int y, int z, Random random) {
	}
	
}
