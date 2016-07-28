package craft;

import org.lwjgl.input.Keyboard;

import craft.level.Level;

public class Player extends Entity{
	public static final float PLAYER_WIDTH = 0.4F;		//��ҿ��
	public static final float PLAYER_HEIGHT = 1.75F;		//��Ҹ߶�
	public static final float PLAYER_EYE_HEIGHT = 1.65F;		//����۸�
	public static final float PLAYER_FIELD_OF_VIEW_X = 90.0F;		//���������Ұ��Χ
	public static final float PLAYER_FIELD_OF_VIEW_MOVE_SPEED = 0.15F;		//�����Ұ��ת�ٶ�
	public static final float PLAYER_JUMP_HEIGHT = 0.24F;		//�����Ծ�߶�
	public static final float PLAYER_WALK_SPEED = 0.075F;		//����ƶ��ٶ�
	public static final float PLAYER_GRAVITY = 0.02F;		//��ҵ����ٶ� ����ֵ��Խ������Խ��
	public static final float PLAYER_IN_LIQUID_GRAVITY = 0.01F;		//�����Һ���еĵ����ٶ�
	public static final float PLAYER_IN_LIQUID_SPEED = 0.025F;		//�����Һ���е��ƶ��ٶ�
	public static final float PLAYER_IN_LIQUID_JUMP_SPEED = 0.02F;		//�����Һ���е���Ծ�ٶ�
	public static final float PLAYER_IN_WATER_SPEED_DECAY = 0.8F;		//�����ˮ�е��ٶ�˥��
	public static final float PLAYER_IN_LAVA_SPEED_DECAY = 0.5F;		//������ҽ��е��ٶ�˥��
	public static final float PLAYER_IN_LIQUID_JUMP_HEIGHT = 0.2F;		//�����Һ���е��������ߵ���Ծ�߶�
	public static final float PLAYER_PICK_LENGTH = 4.0F;
	public PlayerMode mode = PlayerMode.Normal;		//��ҵ�ǰģʽ
	public enum PlayerMode {
		Normal,		//��ͨģʽ
		Flying		//����ģʽ
	}

	public Player(Level level) {
		super(level);
		setSize(PLAYER_WIDTH,PLAYER_HEIGHT);
		this.heightOffset = PLAYER_EYE_HEIGHT;
		resetPos();
	}

	public void tick() {
		super.tick();
		float xa = 0.0F, za = 0.0F;
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) za -= 1.0F;
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) za += 1.0F;
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) xa -= 1.0F;
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) xa += 1.0F;

		boolean inWater = isInWater();
		boolean inLava = isInLava();

		if(mode == PlayerMode.Normal) {
			if ((inWater || inLava) && Keyboard.isKeyDown(Keyboard.KEY_SPACE))
				yd += PLAYER_IN_LIQUID_JUMP_SPEED;
			else if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && onGround == true)
				yd = PLAYER_JUMP_HEIGHT;

			if (inWater) {
				moveRelative(xa, za, PLAYER_IN_LIQUID_SPEED);
				move(xd, yd, zd);
				xd *= PLAYER_IN_WATER_SPEED_DECAY;
				yd *= (PLAYER_IN_WATER_SPEED_DECAY * 1.1F);
				zd *= PLAYER_IN_WATER_SPEED_DECAY;
				yd -= PLAYER_IN_LIQUID_GRAVITY;
				
				if (horizontalCollision && isFree(xd, yd + 0.5F/* - y + yo*/, zd))
			        yd = PLAYER_IN_LIQUID_JUMP_HEIGHT;
			} else if (inLava) {
				moveRelative(xa, za, PLAYER_IN_LIQUID_SPEED);
				move(xd, yd, zd);
				xd *= PLAYER_IN_LAVA_SPEED_DECAY;
				yd *= (PLAYER_IN_LAVA_SPEED_DECAY * 1.5F);
				zd *= PLAYER_IN_LAVA_SPEED_DECAY;
				yd -= PLAYER_IN_LIQUID_GRAVITY;
				if (horizontalCollision && isFree(xd, yd + 0.5F /*- y + yo*/, zd))
			        yd = PLAYER_IN_LIQUID_JUMP_HEIGHT;
			} else {
				moveRelative(xa, za, onGround ? PLAYER_WALK_SPEED : PLAYER_WALK_SPEED * 0.25F);
				
				move(xd, yd, zd);
				yd -= PLAYER_GRAVITY;

				if (onGround) {
					xd *= ENTITY_ONGROUND_SPEED_DECAY;
					zd *= ENTITY_ONGROUND_SPEED_DECAY;
				}
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
		xd *= ENTITY_HORIZONTAL_SPEED_DECAY;
		yd *= ENTITY_VERTICAL_SPEED_DECAY;
		zd *= ENTITY_HORIZONTAL_SPEED_DECAY;
		//System.out.println(x+","+y+","+z);
	}

}
