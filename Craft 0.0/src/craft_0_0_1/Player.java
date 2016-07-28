package craft_0_0_1;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import craft_0_0_1.level.Level;
import craft_0_0_1.phys.AABB;

public class Player {
	public static final float PLAYER_RADIUS = 0.2F;		//��Ұ뾶
	public static final float PLAYER_HEIGHT = 1.75F;		//��Ҹ߶�
	public static final float PLAYER_EYE_HEIGHT = 1.62F;		//����۸�
	public static final float PLAYER_FIELD_OF_VIEW_X = 90.0F;		//���������Ұ��Χ
	public static final float PLAYER_FIELD_OF_VIEW_MOVE_SPEED = 0.15F;		//�����Ұ��ת�ٶ�
	public static final float PLAYER_JUMP_HEIGHT = 0.15F;		//�����Ծ�߶�
	public static final float PLAYER_WALK_SPEED = 0.02F;		//����ƶ��ٶ�
	public static final float PLAYER_GRAVITY = 0.008F;		//��ҵ����ٶ� ����ֵ��Խ������Խ��
	public static final float PLAYER_HORIZONTAL_SPEED_DECAY = 0.91F;		//ˮƽ�����ٶ�˥�� ����ֵ��(0~1)ԽС˥��Խ��
	public static final float PLAYER_VERTICAL_SPEED_DECAY = 0.98F;		//��ֱ�����ٶ�˥�� ����ֵ��(0~1)ԽС˥��Խ��
	public static final float PLAYER_ONGROUND_SPEED_DECAY = 0.8F;		//�ŵ��ٶ�˥�� ����ֵ��(0~1)ԽС˥��Խ��
	private Level level;
	public float x, y, z, xd, yd, zd;
	public float xRot, yRot;
	public AABB aabb;
	public boolean onGround = false;		//����ŵ�
	public PlayerMode mode = PlayerMode.Normal;		//��ҵ�ǰģʽ
	
	public Player(Level level) {
		this.level = level;
		aabb = new AABB(-PLAYER_RADIUS, 0, -PLAYER_RADIUS, PLAYER_RADIUS, PLAYER_HEIGHT, PLAYER_RADIUS);
		resetPos();
	}
	
	public void resetPos() {
		float x = level.x0 + (float)Math.random() * level.length;
	    float y = level.maxHeight + 10;
	    float z = level.z0 + (float)Math.random() * level.width;
	    onGround = false;
	    aabb.moveTo(x, y, z);
	}
	
	public void turn(float xo, float yo) {
	    this.yRot = (float)(this.yRot + xo * PLAYER_FIELD_OF_VIEW_MOVE_SPEED);
	    this.xRot = (float)(this.xRot - yo * PLAYER_FIELD_OF_VIEW_MOVE_SPEED);
	    if (this.yRot >= 180) this.yRot -= 360.0F;
	    if (this.yRot <= -180) this.yRot += 360.0F;
	    if (this.xRot < -PLAYER_FIELD_OF_VIEW_X) this.xRot = -PLAYER_FIELD_OF_VIEW_X;
	    if (this.xRot > PLAYER_FIELD_OF_VIEW_X) this.xRot = PLAYER_FIELD_OF_VIEW_X;
	}
	
	public void tick() {
		float xa = 0.0F, za = 0.0F;
		if(Keyboard.isKeyDown(Keyboard.KEY_R))
			resetPos();
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) za -= 1.0F;
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) za += 1.0F;
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) xa -= 1.0F;
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) xa += 1.0F;
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && onGround == true)
			yd = PLAYER_JUMP_HEIGHT;
		
		moveRelative(xa, za, onGround ? PLAYER_WALK_SPEED : PLAYER_WALK_SPEED / 4.0F);

	    yd -= PLAYER_GRAVITY;
	    move(xd, yd, zd);
	    xd *= PLAYER_HORIZONTAL_SPEED_DECAY;
	    yd *= PLAYER_VERTICAL_SPEED_DECAY;
	    zd *= PLAYER_HORIZONTAL_SPEED_DECAY;

	    if (onGround) {
	      xd *= PLAYER_ONGROUND_SPEED_DECAY;
	      zd *= PLAYER_ONGROUND_SPEED_DECAY;
	    }
	    //System.out.println(x+","+y+","+z);
	}
	
	public void move(float xa, float ya, float za) {
	    float xaOrg = xa;
	    float yaOrg = ya;
	    float zaOrg = za;

	    ArrayList<AABB> aABBs = level.getCubes(aabb.expand(xa, ya, za));

	    for (int i = 0; i < aABBs.size(); i++)
	      ya = ((AABB)aABBs.get(i)).clipYCollide(aabb, ya);
	    aabb.move(0.0F, ya, 0.0F);

	    for (int i = 0; i < aABBs.size(); i++)
	      xa = ((AABB)aABBs.get(i)).clipXCollide(aabb, xa);
	    aabb.move(xa, 0.0F, 0.0F);

	    for (int i = 0; i < aABBs.size(); i++)
	      za = ((AABB)aABBs.get(i)).clipZCollide(aabb, za);
	    aabb.move(0.0F, 0.0F, za);

	    onGround = ((yaOrg != ya) && (yaOrg < 0.0F));

	    if (xaOrg != xa) xd = 0.0F;
	    if (yaOrg != ya) yd = 0.0F;
	    if (zaOrg != za) zd = 0.0F;

	    x = ((aabb.x0 + aabb.x1) / 2.0F);
	    y = (aabb.y0 + PLAYER_EYE_HEIGHT);
	    z = ((aabb.z0 + aabb.z1) / 2.0F);
	}
	
	public void moveRelative(float xa, float za, float speed) {
	    float dist = xa * xa + za * za;
	    if (dist < 0.01F) return;

	    dist = speed / (float)Math.sqrt(dist);
	    xa *= dist;
	    za *= dist;
	    
	    float sin = (float)Math.sin(yRot * Math.PI / 180.0D);
	    float cos = (float)Math.cos(yRot * Math.PI / 180.0D);
	    
	    xd += xa * cos - za * sin;
	    zd += za * cos + xa * sin;
	}
	
	
}
