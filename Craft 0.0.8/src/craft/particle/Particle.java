package craft.particle;

import org.lwjgl.opengl.GL11;

import craft.Entity;
import craft.level.Level;
import craft.renderer.Texture;

public class Particle extends Entity{
	public static final float PARTICLE_GRAVITY = 0.02F;		//粒子掉落速度 重力值，越大，引力越大
	public static final float PARTICLE_SPEED_DECAY = 0.96F;		//速度衰减 惯性值：(0~1)越小衰减越快
	public static final float PARTICLE_ONGROUND_SPEED_DECAY = 0.75F;		//着地速度衰减 惯性值：(0~1)越小衰减越快
	public static final float PARTICLE_SPEED = 0.15F;			//粒子分散速度
	public static final float PARTICLE_DISPERSED = 0.3F;		//粒子分散程度
	public static final float PARTICLE_LIFE = 5.0F;		//粒子生命
	private int tex;
	private float u0, v0;
	private int age = 0;
	private int lifetime = 0;
	private float size;
	
	public Particle(Level level, float x, float y, float z, float xa, float ya, float za, int tex) {
		super(level);
		this.tex = tex;
		setSize(0.2F, 0.2F);
		heightOffset = (aabbHeight / 2.0F);
		setPos(x, y, z);

		xd = (xa + (float)(Math.random() * 2.0D - 1.0D) * 0.4F);
		yd = (ya + (float)(Math.random() * 2.0D - 1.0D) * 0.4F);
		zd = (za + (float)(Math.random() * 2.0D - 1.0D) * 0.4F);
		float speed = (float)(Math.random() + Math.random() + 1.0D) * PARTICLE_SPEED;
		float dd = (float)Math.sqrt(xd * xd + yd * yd + zd * zd);
		xd = (this.xd / dd * speed * PARTICLE_DISPERSED);
		yd = (this.yd / dd * speed * PARTICLE_DISPERSED + 0.03F);
		zd = (this.zd / dd * speed * PARTICLE_DISPERSED);
		u0 = (float)Math.random() * (1 - 0.125F);
		v0 = (float)Math.random() * (1 - 0.125F);

		size = (float)(Math.random() * 0.5D + 0.5D);
		lifetime = (int)(PARTICLE_LIFE / (Math.random() * 0.9D + 0.1D));
	}
	
	public void tick() {
		super.tick();

		if (age++ >= lifetime) remove();

		yd -= PARTICLE_GRAVITY;
		move(xd, yd, zd);
		xd *= PARTICLE_SPEED_DECAY;
		yd *= PARTICLE_SPEED_DECAY;
		zd *= PARTICLE_SPEED_DECAY;

		if (this.onGround) {
			this.xd *= PARTICLE_ONGROUND_SPEED_DECAY;
			this.zd *= PARTICLE_ONGROUND_SPEED_DECAY;
		}
	}
	
	public void render(float a, float xa, float ya, float za, float xa2, float za2) {
	    float u1 = u0 + 0.125F;
	    float v1 = v0 + 0.125F;
	    float r = 0.1F * size;

	    float x = xo + (this.x - xo) * a;
	    float y = yo + (this.y - yo) * a;
	    float z = zo + (this.z - zo) * a;
	    Texture.bind(tex);
	    GL11.glBegin(GL11.GL_QUADS);
	    GL11.glTexCoord2f(u0, v0);
	    GL11.glVertex3f(x - xa * r + xa2 * r, y + ya * r, z - za * r + za2 * r);
	    GL11.glTexCoord2f(u0, v1);
	    GL11.glVertex3f(x - xa * r - xa2 * r, y - ya * r, z - za * r - za2 * r);
	    GL11.glTexCoord2f(u1, v1);
	    GL11.glVertex3f(x + xa * r - xa2 * r, y - ya * r, z + za * r - za2 * r);
	    GL11.glTexCoord2f(u1, v0);
	    GL11.glVertex3f(x + xa * r + xa2 * r, y + ya * r, z + za * r + za2 * r);
	    GL11.glEnd();
	  }
}
