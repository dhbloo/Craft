package craft.entity;

import java.util.ArrayList;

import craft.phys.AABB;
import craft.renderer.Tesselator;
import craft.world.Brightness;
import craft.world.World;
import craft.world.tile.Tile;

public class EntityFallingTile extends Entity {
	public static final float FALL_SPEED = 0.2F;
	private Tile tileType;

	public EntityFallingTile(World world, int x, int y, int z, Tile tileType) {
		super(world);
		setSize(0.98F, 0.98F);
		setPos(x + 0.5F, y + 0.5F, z + 0.5F);
		this.tileType = tileType;
	}

	@Override
	public void tick() {
		super.tick();
		int xa = (int) Math.floor(x);
		int ya = (int) Math.floor(y);
		int za = (int) Math.floor(z);
		/** 是否正在更新 */
		move(-FALL_SPEED);
		if (onGround) {
			if (world.getBlock(xa, ya, za) != tileType.id)
				world.setBlock(xa, ya, za, tileType.id);
			remove();
		}
		if (y < -128.0F)
			remove();
	}

	public void move(float ya) { // 返回是否着地
		float yaOrg = ya;
		AABB rangeAabb = aabb.expand(0.0F, ya, 0.0F);
		ArrayList<AABB> aABBs = world.getCubes(rangeAabb);
		aABBs.addAll(world.entityList.getEntitiesAABB(rangeAabb));

		for (int i = 0; i < aABBs.size(); i++)
			ya = aABBs.get(i).clipYCollide(aabb, ya);
		aabb.move(0.0F, ya, 0.0F);

		onGround = ((yaOrg != ya) && (yaOrg < 0.0F));
		y = aabb.y0 + heightOffset;
	}

	@Override
	public void render(Tesselator t, float a) {
		int xt = tileType.tex % 16 * 16;
		int yt = tileType.tex / 16 * 16;
		float u0 = xt / 256.0F;
		float u1 = (xt + 16.0F) / 256.0F;
		float v0 = yt / 256.0F;
		float v1 = (yt + 16.0F) / 256.0F;

		float x0 = x - 0.5F;
		float x1 = x + 0.5F;
		float y0 = y + (y - yo) * a - 0.5F;
		float y1 = y0 + 1.0F;
		float z0 = z - 0.5F;
		float z1 = z + 0.5F;

		float b = 0.0F;
		float c1 = 1.0F;
		float c2 = 0.8F;
		float c3 = 0.5F;
		float c4 = 0.6F;
		
		int x = (int)Math.floor(this.x);
		int y = (int)Math.floor(y0);
		int z = (int)Math.floor(this.z);
		
		b = world.getBrightness(x, y, z + 1) / Brightness.MAX * c2;
		t.color(b, b, b);
		t.vertexUV(x0, y1, z1, u0, v0);
		t.vertexUV(x0, y0, z1, u0, v1);
		t.vertexUV(x1, y0, z1, u1, v1);
		t.vertexUV(x1, y1, z1, u1, v0);

		b = world.getBrightness(x, y, z - 1) / Brightness.MAX * c2;
		t.color(b, b, b);
		t.vertexUV(x0, y1, z0, u1, v0);
		t.vertexUV(x1, y1, z0, u0, v0);
		t.vertexUV(x1, y0, z0, u0, v1);
		t.vertexUV(x0, y0, z0, u1, v1);

		b = world.getBrightness(x, y + 1, z) / Brightness.MAX * c1;
		t.color(b, b, b);
		t.vertexUV(x1, y1, z1, u1, v1);
		t.vertexUV(x1, y1, z0, u1, v0);
		t.vertexUV(x0, y1, z0, u0, v0);
		t.vertexUV(x0, y1, z1, u0, v1);

		b = world.getBrightness(x, y - 1, z) / Brightness.MAX * c3;
		t.color(b, b, b);
		t.vertexUV(x0, y0, z1, u0, v1);
		t.vertexUV(x0, y0, z0, u0, v0);
		t.vertexUV(x1, y0, z0, u1, v0);
		t.vertexUV(x1, y0, z1, u1, v1);

		b = world.getBrightness(x - 1, y, z) / Brightness.MAX * c4;
		t.color(b, b, b);
		t.vertexUV(x0, y1, z1, u1, v0);
		t.vertexUV(x0, y1, z0, u0, v0);
		t.vertexUV(x0, y0, z0, u0, v1);
		t.vertexUV(x0, y0, z1, u1, v1);

		b = world.getBrightness(x + 1, y, z) / Brightness.MAX * c4;
		t.color(b, b, b);
		t.vertexUV(x1, y0, z1, u0, v1);
		t.vertexUV(x1, y0, z0, u1, v1);
		t.vertexUV(x1, y1, z0, u1, v0);
		t.vertexUV(x1, y1, z1, u0, v0);
	}

}
