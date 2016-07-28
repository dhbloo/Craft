package craft.item;

import craft.Entity;
import craft.level.Level;
import craft.level.tile.Tile;
import craft.renderer.Tesselator;

public class DroppedItem extends Entity{
	public static final float DROPPEDITEM_SPEED_DECAY = 0.97F;		//速度衰减 惯性值：(0~1)越小衰减越快
	public static final float DROPPEDITEM_ONGROUND_SPEED_DECAY = 0.7F;		//着地速度衰减 惯性值：(0~1)越小衰减越快
	public static final float DROPPEDITEM_SPEED = 0.15F;			//掉落物分散速度
	public static final float DROPPEDITEM_DISPERSED = 0.3F;		//掉落物分散程度
	public static final int DROPPEDITEM_LIFETIME = 2 * 60 * 30;		//掉落物生命值
	public static final float r = 0.2F	;		//贴图大小
	protected Tile tile;
	private int age = 0;
	private int lifetime = 0;

	public DroppedItem(Level level, float x, float y, float z, float xa, float ya, float za, Tile tile) {
		super(level);
		this.tile = tile;
		setSize(0.4F, 0.4F);
		heightOffset = (aabbHeight / 2.0F);
		setPos(x + 0.5F, y + 0.5F, z + 0.5F);
		
		xd = (xa + (float)(Math.random() * 2.0D - 1.0D) * 0.1F);
		yd = (ya + (float)(Math.random() * 2.0D - 1.0D) * 0.1F);
		zd = (za + (float)(Math.random() * 2.0D - 1.0D) * 0.1F);
		float speed = (float)(Math.random() + Math.random() + 1.0D) * DROPPEDITEM_SPEED;
		float dd = (float)Math.sqrt(xd * xd + yd * yd + zd * zd);
		xd = (this.xd / dd * speed * DROPPEDITEM_DISPERSED);
		yd = (this.yd / dd * speed * DROPPEDITEM_DISPERSED + 0.03F);
		zd = (this.zd / dd * speed * DROPPEDITEM_DISPERSED);
		
		lifetime = DROPPEDITEM_LIFETIME;
	}

	public void tick() {
		super.tick();
		
		if (age++ >= lifetime || y < -16.0F) remove();
		
		yd -= ENTITY_GRAVITY;
		moveWithWall(xd, yd, zd);
		xd *= DROPPEDITEM_SPEED_DECAY;
		yd *= DROPPEDITEM_SPEED_DECAY;
		zd *= DROPPEDITEM_SPEED_DECAY;

		if (this.onGround) {
			this.xd *= DROPPEDITEM_ONGROUND_SPEED_DECAY;
			this.zd *= DROPPEDITEM_ONGROUND_SPEED_DECAY;
		}
	}
	
	public void render(Tesselator t, float a, float xa, float ya, float za, float yd) {
		int tex = tile.tex;
		int xt = tex % 16 * 16;
	    int yt = tex / 16 * 16;
	    float u0 = xt / 256.0F;
	    float u1 = (xt + 15.99F) / 256.0F;
	    float v0 = yt / 256.0F;
	    float v1 = (yt + 15.99F) / 256.0F;

		float x = this.xo + (this.x - this.xo) * a;
		float y = this.yo + (this.y - this.yo) * a + yd;
		float z = this.zo + (this.z - this.zo) * a;
		/**正面*/
		t.vertexUV(x - xa * r, y - ya * r, z - za * r, u0, v1);
		t.vertexUV(x - xa * r, y + ya * r, z - za * r, u0, v0);
		t.vertexUV(x + xa * r, y + ya * r, z + za * r, u1, v0);
		t.vertexUV(x + xa * r, y - ya * r, z + za * r, u1, v1);
		
		/**背面*/
		t.vertexUV(x - xa * r, y + ya * r, z - za * r, u0, v0);
		t.vertexUV(x - xa * r, y - ya * r, z - za * r, u0, v1);
		t.vertexUV(x + xa * r, y - ya * r, z + za * r, u1, v1);
		t.vertexUV(x + xa * r, y + ya * r, z + za * r, u1, v0);
	}
	
}
