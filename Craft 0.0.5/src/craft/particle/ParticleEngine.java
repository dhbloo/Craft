package craft.particle;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import craft.Player;
import craft.level.Level;
import craft.renderer.Texture;

public class ParticleEngine {
	protected Level level;
	private List<Particle> particles = new ArrayList<Particle>();

	public ParticleEngine(Level level) {
		this.level = level;
	}

	public void add(Particle p) {
		particles.add(p);
	}

	public void tick() {
		for (int i = 0; i < particles.size(); i++) {
			Particle p = particles.get(i);
			p.tick();
			if (p.removed)
				particles.remove(i--);
		}
	}

	public void render(Player player, float a)
	{
		if (particles.size() == 0) return;

		float xa = -(float)Math.cos(player.yRot * Math.PI / 180.0D);
		float za = -(float)Math.sin(player.yRot * Math.PI / 180.0D);

		float xa2 = -za * (float)Math.sin(player.xRot * Math.PI / 180.0D);
		float za2 = xa * (float)Math.sin(player.xRot * Math.PI / 180.0D);
		float ya = (float)Math.cos(player.xRot * Math.PI / 180.0D);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(0.8F, 0.8F, 0.8F, 1.0F);
		for (int i = 0; i < this.particles.size(); i++)
			particles.get(i).render(a, xa, ya, za, xa2, za2);
		Texture.bind(-1);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
}
