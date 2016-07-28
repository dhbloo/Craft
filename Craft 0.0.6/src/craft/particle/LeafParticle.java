package craft.particle;

import org.lwjgl.opengl.GL11;

import craft.level.Level;

public class LeafParticle extends Particle {
	public LeafParticle(Level level, float x, float y, float z, float xa, float ya, float za, int tex) {
		super(level, x, y, z, xa, ya, za, tex);
	}
	
	public void render(float a, float xa, float ya, float za, float xa2, float za2) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		super.render(a, xa, ya, za, xa2, za2);
		GL11.glDisable(GL11.GL_BLEND);
	}
}
