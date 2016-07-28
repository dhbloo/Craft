package craft.character;

import org.lwjgl.opengl.GL11;

import craft.Entity;
import craft.level.Level;
import craft.renderer.Texture;

public class Human extends Entity {
	public static final float HUMAN_WIDTH = 0.4F; // ¿í¶È
	public static final float HUMAN_HEIGHT = 1.75F; // ¸ß¶È
	public float rot;
	public float timeOffs;
	public float speed;
	public float rotA;
	private static CharModel charModel = new CharModel();

	public Human(Level level, float x, float y, float z) {
		super(level);
		setSize(HUMAN_WIDTH, HUMAN_HEIGHT);
		rotA = ((float) (Math.random() + 1.0D) * 0.01F);
		setPos(x, y, z);
		timeOffs = ((float) Math.random() * 1239813.0F);
		rot = (float) (Math.random() * Math.PI * 2.0D);
		speed = 1.0F;
	}

	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		float xa = 0.0F;
		float za = 0.0F;

		if (y < -128.0F)
			remove();

		rot += rotA;
		rotA *= 0.99F;
		rotA = (float) (rotA + (Math.random() - Math.random()) * Math.random() * Math.random() * 0.07999999821186066D);
		xa = (float) Math.sin(rot);
		za = (float) Math.cos(rot);

		boolean inWater = isInWater();
		boolean inLava = isInLava();

		if ((inWater || inLava) && Math.random() < 0.48D)
			yd += ENTITY_IN_LIQUID_JUMP_SPEED;
		else if (onGround == true && Math.random() < 0.08D)
			yd = ENTITY_JUMP_HEIGHT;

		if (inWater) {
			float yo = y;
			moveRelative(xa, za, ENTITY_IN_LIQUID_SPEED);
			moveWithWall(xd, yd, zd);
			xd *= ENTITY_IN_WATER_SPEED_DECAY;
			yd *= (ENTITY_IN_WATER_SPEED_DECAY * 1.1F);
			zd *= ENTITY_IN_WATER_SPEED_DECAY;
			yd -= ENTITY_IN_LIQUID_GRAVITY;

			if (horizontalCollision && isFree(xd, yd + 0.5F - y + yo, zd))
				yd = ENTITY_IN_LIQUID_JUMP_HEIGHT;
		} else if (inLava) {
			float yo = y;
			moveRelative(xa, za, ENTITY_IN_LIQUID_SPEED);
			moveWithWall(xd, yd, zd);
			xd *= ENTITY_IN_LAVA_SPEED_DECAY;
			yd *= (ENTITY_IN_LAVA_SPEED_DECAY * 1.5F);
			zd *= ENTITY_IN_LAVA_SPEED_DECAY;
			yd -= ENTITY_IN_LIQUID_GRAVITY;
			if (horizontalCollision && isFree(xd, yd + 0.5F - y + yo, zd))
				yd = ENTITY_IN_LIQUID_JUMP_HEIGHT;
		} else {
			moveRelative(xa, za, onGround ? ENTITY_WALK_SPEED : ENTITY_WALK_SPEED * 0.2F);

			yd -= ENTITY_GRAVITY;
			moveWithWall(xd, yd, zd);
			xd *= ENTITY_HORIZONTAL_SPEED_DECAY;
			yd *= ENTITY_VERTICAL_SPEED_DECAY;
			zd *= ENTITY_HORIZONTAL_SPEED_DECAY;

			if (onGround) {
				xd *= ENTITY_ONGROUND_SPEED_DECAY;
				zd *= ENTITY_ONGROUND_SPEED_DECAY;
			}
		}
	}

	public void render(float a) {
		Texture.bind(2);

		GL11.glPushMatrix();
		double time = System.nanoTime() / 1.0e9D * 10.0D * this.speed + this.timeOffs;

		float size = 0.05833333F;
		float yy = (float) (-Math.abs(Math.sin(time * 0.6662D)) * 5.0D - 23.0D);
		GL11.glTranslatef(this.xo + (this.x - this.xo) * a, this.yo + (this.y - this.yo) * a, this.zo + (this.z - this.zo) * a);
		GL11.glScalef(1.0F, -1.0F, 1.0F);
		GL11.glScalef(size, size, size);
		GL11.glTranslatef(0.0F, yy, 0.0F);
		float c = 57.29578F;
		GL11.glRotatef(this.rot * c + 180.0F, 0.0F, 1.0F, 0.0F);

		charModel.render((float) time);
		GL11.glPopMatrix();
	}

}
