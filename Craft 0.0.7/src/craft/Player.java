package craft;

import org.lwjgl.input.Keyboard;

import craft.level.Level;

public class Player extends Entity{
	public static final float PLAYER_WIDTH = 0.4F;		//玩家宽度
	public static final float PLAYER_HEIGHT = 1.75F;		//玩家高度
	public static final float PLAYER_EYE_HEIGHT = 1.65F;		//玩家眼高
	public static final float PLAYER_FIELD_OF_VIEW_X = 90.0F;		//玩家上下视野范围
	public static final float PLAYER_FIELD_OF_VIEW_MOVE_SPEED = 0.15F;		//玩家视野旋转速度
	public static final float PLAYER_JUMP_HEIGHT = 0.24F;		//玩家跳跃高度
	public static final float PLAYER_WALK_SPEED = 0.075F;		//玩家移动速度
	public static final float PLAYER_GRAVITY = 0.02F;		//玩家掉落速度 重力值，越大，引力越大
	public static final float PLAYER_IN_LIQUID_GRAVITY = 0.01F;		//玩家在液体中的掉落速度
	public static final float PLAYER_IN_LIQUID_SPEED = 0.025F;		//玩家在液体中的移动速度
	public static final float PLAYER_IN_LIQUID_JUMP_SPEED = 0.02F;		//玩家在液体中的跳跃速度
	public static final float PLAYER_IN_WATER_SPEED_DECAY = 0.8F;		//玩家在水中的速度衰减
	public static final float PLAYER_IN_LAVA_SPEED_DECAY = 0.5F;		//玩家在岩浆中的速度衰减
	public static final float PLAYER_IN_LIQUID_JUMP_HEIGHT = 0.2F;		//玩家在液体中的碰到岸边的跳跃高度
	public static final float PLAYER_PICK_LENGTH = 4.0F;
	public PlayerMode mode = PlayerMode.Normal;		//玩家当前模式
	public enum PlayerMode {
		Normal,		//普通模式
		Flying		//飞行模式
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
