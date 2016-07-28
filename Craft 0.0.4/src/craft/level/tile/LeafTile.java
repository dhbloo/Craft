package craft.level.tile;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import craft.level.Level;
import craft.particle.LeafParticle;
import craft.particle.ParticleEngine;

public class LeafTile extends Tile {
	protected LeafTile(int id, int tex) {
		super(id, tex);
	}

	public void render(Level level,int x,int y,int z) {
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		super.render(level, x, y, z);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
	
	public void tick(Level level, int x, int y, int z, Random random) {
		if (level.isLit(x, y, z)) {
			for (int xa = -2; xa <= 2; xa++)
				for (int ya = -1; ya <= 0; ya++)
					for (int za = -2; za <= 2; za++) {
						if(level.getBlock(x + xa, y + ya, z + za) == Tile.trunk.id) {
							int yb = -1;
							while(level.getBlock(x + xa, y + ya + yb, z + za) == Tile.trunk.id)
								yb--;
							if(level.getBlock(x + xa, y + ya + yb, z + za) == Tile.soil.id)
								return;
							else
								break;
						}
					}
			level.setBlock(x, y, z, Tile.air.id);
		}
		else
			if(random.nextDouble() > 0.3)
				level.setBlock(x, y, z, Tile.air.id);
	}
	
	public void destroy(Level level, int x, int y, int z, ParticleEngine particleEngine) {
		int SD = PARTICLE_COUNT;
		for (int xx = 0; xx < SD; xx++)
			for (int yy = 0; yy < SD; yy++)
				for (int zz = 0; zz < SD; zz++) {
					float xp = x + (xx + 0.5F) / SD;
					float yp = y + (yy + 0.5F) / SD;
					float zp = z + (zz + 0.5F) / SD;
					particleEngine.add(new LeafParticle(level, xp, yp, zp, xp - x - 0.5F, yp - y - 0.5F, zp - z - 0.5F, this.tex));
				}
	}
}
