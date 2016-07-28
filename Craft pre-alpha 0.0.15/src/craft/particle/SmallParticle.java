package craft.particle;

import craft.world.World;

public class SmallParticle extends Particle {
	public static final float SMALLPARTICLE_SPEED = 0.16F;			//粒子分散速度
	public static final float SMALLPARTICLE_DISPERSED = 0.4F;		//粒子分散程度
	public static final float SMALLPARTICLE_LIFE = 3.0F;		//粒子生命

	public SmallParticle(World world, float x, float y, float z, int tex) {
		super(world, x, y, z, 0.0F, 0.0F, 0.0F, tex);
		setSize(0.1F, 0.1F);
		heightOffset = (aabbHeight / 2.0F);

		xd = (float)(Math.random() * 2.0D - 1.0D) * 0.4F;
		yd = (float)(Math.random() * 2.0D - 1.0D) * 0.4F + 0.4F;
		zd = (float)(Math.random() * 2.0D - 1.0D) * 0.4F;
		float speed = (float)(Math.random() + Math.random() + 1.0D) * SMALLPARTICLE_SPEED;
		float dd = (float)Math.sqrt(xd * xd + yd * yd + zd * zd);
		xd = (this.xd / dd * speed * SMALLPARTICLE_DISPERSED);
		yd = (this.yd / dd * speed * SMALLPARTICLE_DISPERSED + 0.03F);
		zd = (this.zd / dd * speed * SMALLPARTICLE_DISPERSED);

		size = (float)(Math.random() * 0.2D + 0.3D);
		lifetime = (int)(SMALLPARTICLE_LIFE / (Math.random() * 0.9D + 0.1D));
	}

}
