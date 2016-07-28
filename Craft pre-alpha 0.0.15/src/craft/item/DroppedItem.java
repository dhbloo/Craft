package craft.item;

import org.lwjgl.opengl.GL11;

import craft.Craft;
import craft.entity.Entity;
import craft.world.Brightness;
import craft.world.World;
import craft.world.tile.Face;
import craft.world.tile.Tile;

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
	private long timeOffset;

	public DroppedItem(World world, float x, float y, float z, float xa, float ya, float za, Tile tile) {
		super(world);
		this.tile = tile;
		setSize(0.6F, 0.6F);
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
		timeOffset = (long) (Craft.getTime() * Math.random());
	}

	public void tick() {
		super.tick();
		
		if (age++ >= lifetime || y < -16.0F) remove();
		
		yd -= ENTITY_GRAVITY;
		moveWithBorderary(xd, yd, zd);
		xd *= DROPPEDITEM_SPEED_DECAY;
		yd *= DROPPEDITEM_SPEED_DECAY;
		zd *= DROPPEDITEM_SPEED_DECAY;

		if (this.onGround) {
			this.xd *= DROPPEDITEM_ONGROUND_SPEED_DECAY;
			this.zd *= DROPPEDITEM_ONGROUND_SPEED_DECAY;
		}
	}
	
	public void render(float a) {
		long time = (long) (timeOffset + Craft.getTime() * 0.05);
		
	    float yd = (float)(Math.sin(time * 0.06F) + 1) * 0.05F;
	    float yRot = (float) (time % 360);
	    
	    float x = this.xo + (this.x - this.xo) * a;
		float y = this.yo + (this.y - this.yo) * a + yd;
		float z = this.zo + (this.z - this.zo) * a;
		
		float b = 0.2F + 0.8F * ((float)getBrightness() / Brightness.MAX);
	    
	    GL11.glTranslatef(x, y, z);
		GL11.glRotatef(yRot, 0.0F, 1.0F, 0.0F);
	    
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor3f(b * 0.8F, b * 0.8F, b * 0.8F);
		renderFace(Face.Front);
		renderFace(Face.Back);
		GL11.glColor3f(b * 1.0F, b * 1.0F, b * 1.0F);
		renderFace(Face.Top);
		GL11.glColor3f(b * 0.5F, b * 0.5F, b * 0.5F);
		renderFace(Face.Bottom);
		GL11.glColor3f(b * 0.6F, b * 0.6F, b * 0.6F);
		renderFace(Face.Right);
		renderFace(Face.Left);
		GL11.glEnd();
	}
	
	private void renderFace(Face face) {
		int tex = tile.getTexture(face);
		int xt = tex % 16 * 16;
	    int yt = tex / 16 * 16;
		float u0 = xt / 256.0F;
	    float u1 = (xt + 15.99F) / 256.0F;
	    float v0 = yt / 256.0F;
	    float v1 = (yt + 15.99F) / 256.0F;
	    
	    switch(face) {
	    case Front:
	    	vertexUV(-r, r, r, u0, v0);
	        vertexUV(-r, -r, r, u0, v1);
	        vertexUV(r, -r, r, u1, v1);
	        vertexUV(r, r, r, u1, v0);
	    	break;
	    case Back:
	    	vertexUV(-r, r, -r, u1, v0);
	        vertexUV(r, r, -r, u0, v0);
	        vertexUV(r, -r, -r, u0, v1);
	        vertexUV(-r, -r, -r, u1, v1);
	    	break;
	    case Top:
	    	vertexUV(r, r, r, u1, v1);
	        vertexUV(r, r, -r, u1, v0);
	        vertexUV(-r, r, -r, u0, v0);
	        vertexUV(-r, r, r, u0, v1);
	    	break;
	    case Bottom:
	    	vertexUV(-r, -r, r, u0, v1);
	        vertexUV(-r, -r, -r, u0, v0);
	        vertexUV(r, -r, -r, u1, v0);
	        vertexUV(r, -r, r, u1, v1);
	    	break;
	    case Left:
	    	vertexUV(-r, r, r, u1, v0);
	        vertexUV(-r, r, -r, u0, v0);
	        vertexUV(-r, -r, -r, u0, v1);
	        vertexUV(-r, -r, r, u1, v1);
	    	break;
	    case Right:
	    	vertexUV(r, -r, r, u0, v1);
	        vertexUV(r, -r, -r, u1, v1);
	        vertexUV(r, r, -r, u1, v0);
	        vertexUV(r, r, r, u0, v0);
	    	break;
	    }
	}
	
	private void vertexUV(float x, float y, float z, float u, float v) {
		GL11.glTexCoord2f(u, v);
		GL11.glVertex3f(x, y, z);
	}
	
}
