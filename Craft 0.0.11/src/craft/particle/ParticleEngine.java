package craft.particle;

import java.util.ArrayList;
import java.util.List;

import craft.Player;
import craft.level.Level;
import craft.level.Brightness.Brightness;
import craft.renderer.Tesselator;

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

	    Tesselator t = Tesselator.instance;
	    t.begin();
		for (int i = 0; i < this.particles.size(); i++) {
			float b = 0.85F * particles.get(i).getBrightness() / Brightness.MAX;
			t.color(b, b, b);
			particles.get(i).render(t, a, xa, ya, za, xa2, za2);
		}
		t.end();
	}
}
