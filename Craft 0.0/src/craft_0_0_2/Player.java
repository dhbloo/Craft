package craft_0_0_2;

import org.lwjgl.input.Keyboard;

import craft_0_0_2.level.Level;

public class Player extends Entity{
	public static final float PLAYER_WIDTH = 0.4F;		//��ҿ��
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
	public static final float PLAYER_PICK_LENGTH = 4.0F;
	public PlayerMode mode = PlayerMode.Normal;		//��ҵ�ǰģʽ
	
	public Player(Level level) {
		super(level);
		setSize(PLAYER_WIDTH,PLAYER_HEIGHT);
		this.heightOffset = PLAYER_EYE_HEIGHT;
		resetPos();
	}
	
	public void tick() {
		super.tick();
		float xa = 0.0F, za = 0.0F;
		if(Keyboard.isKeyDown(Keyboard.KEY_F))
			if(mode == PlayerMode.Normal)
				mode = PlayerMode.Flying;
			else
				mode = PlayerMode.Normal;
		if(Keyboard.isKeyDown(Keyboard.KEY_R))
			resetPos();
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) za -= 1.0F;
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) za += 1.0F;
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) xa -= 1.0F;
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) xa += 1.0F;
		
		if(mode == PlayerMode.Normal) {
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
		} else {
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
				yd += PLAYER_GRAVITY;
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
				yd -= PLAYER_GRAVITY;
			moveRelative(xa, za, PLAYER_WALK_SPEED);
		    move(xd, yd, zd);
		    yd *= 0.97F;
		}
		xd *= PLAYER_HORIZONTAL_SPEED_DECAY;
	    yd *= PLAYER_VERTICAL_SPEED_DECAY;
	    zd *= PLAYER_HORIZONTAL_SPEED_DECAY;
	    //System.out.println(x+","+y+","+z);
	}
	
}
