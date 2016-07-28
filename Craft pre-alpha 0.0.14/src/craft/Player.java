package craft;

import org.lwjgl.input.Keyboard;

import craft.entity.Entity;
import craft.item.DroppedItem;
import craft.item.PickupEngine;
import craft.level.Level;
import craft.level.tile.Face;
import craft.level.tile.Tile;

public class Player extends Entity{
	/**玩家宽度*/
	public static final float PLAYER_WIDTH = 0.4F;	
	/**玩家高度*/
	public static final float PLAYER_HEIGHT = 1.75F;
	/**玩家眼高*/
	public static final float PLAYER_EYE_HEIGHT = 1.65F;
	/**玩家行走速度*/
	public static final float PLAYER_WALK_SPEED = 0.06F;
	/**玩家拾取长度*/
	public static final float PLAYER_PICK_LENGTH = 4.0F;
	/**玩家视野偏移最大值*/
	public static final float PLAYER_FOV_OFFSET_MAX = 5.0F;
	/**玩家视野偏移变化大小*/
	public static final float PLAYER_FOV_OFFSET_CHANGE_STEP = 0.5F;
	/**玩家破坏确认停留时间*/
	public static final int PLAYER_DESTORY_WAIT_TIME = 5;
	
	/**玩家当前模式*/
	public PlayerMode mode = PlayerMode.Normal;
	public int dx, dy, dz;
	public int destoryTime = 0;
	/**上次上下摆动*/
	public float oBob;
	/**上下摆动*/
	public float bob;
	/**上次Y轴掉落摇晃*/
	public float oTilt;
	/**Y轴掉落摇晃*/
	public float tilt;
	/**上次的视野偏移值*/
	private float fovOffsetO;
	/**视野偏移值*/
	private float fovOffset;
	/**出生点*/
	private int spawnX, spawnY, spawnZ;
	
	public enum PlayerMode {
		/**普通模式*/
		Normal,
		/**飞行模式*/
		Flying
	}

	public Player(Level level) {
		super(level);
		setSize(PLAYER_WIDTH,PLAYER_HEIGHT);
		this.heightOffset = PLAYER_EYE_HEIGHT;
		setRandomSpawnPos();
		resetPos();
	}

	public void tick() {
		super.tick();
		this.oBob = this.bob;
		this.oTilt = this.tilt;
		this.fovOffsetO = this.fovOffset;
		float xa = 0.0F, za = 0.0F;
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) za -= 1.0F;
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) za += 1.0F;
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) xa -= 1.0F;
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) xa += 1.0F;

		boolean inWater = isInWater();
		boolean inLava = isInLava();

		if(mode == PlayerMode.Normal) {
			if ((inWater || inLava) && Keyboard.isKeyDown(Keyboard.KEY_SPACE))
				yd += ENTITY_IN_LIQUID_JUMP_SPEED;
			else if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && onGround == true)
				yd = ENTITY_JUMP_HEIGHT;

			if (inWater) {
				float yo = y;
				moveRelative(xa, za, ENTITY_IN_LIQUID_SPEED);
				moveWithBorderary(xd, yd, zd);
				xd *= ENTITY_IN_WATER_SPEED_DECAY;
				yd *= (ENTITY_IN_WATER_SPEED_DECAY * 1.1F);
				zd *= ENTITY_IN_WATER_SPEED_DECAY;
				yd -= ENTITY_IN_LIQUID_GRAVITY;
				
				if (horizontalCollision && isFree(xd, yd + 0.6F - y + yo, zd))
			        yd = ENTITY_IN_LIQUID_JUMP_HEIGHT;
			} else if (inLava) {
				float yo = y;
				moveRelative(xa, za, ENTITY_IN_LIQUID_SPEED);
				moveWithBorderary(xd, yd, zd);
				xd *= ENTITY_IN_LAVA_SPEED_DECAY;
				yd *= (ENTITY_IN_LAVA_SPEED_DECAY * 1.5F);
				zd *= ENTITY_IN_LAVA_SPEED_DECAY;
				yd -= ENTITY_IN_LIQUID_GRAVITY;
				if (horizontalCollision && isFree(xd, yd + 0.6F - y + yo, zd))
			        yd = ENTITY_IN_LIQUID_JUMP_HEIGHT;
			} else {
				moveRelative(xa, za, onGround ? PLAYER_WALK_SPEED : PLAYER_WALK_SPEED * 0.25F);
				
				moveWithBorderary(xd, yd, zd);
				yd -= ENTITY_GRAVITY;

				if (onGround) {
					xd *= ENTITY_ONGROUND_SPEED_DECAY;
					zd *= ENTITY_ONGROUND_SPEED_DECAY;
				} else {
					walkDistO = 0.0F;
					walkDist = 0.0F;
				}
				
				float dist = (float) Math.sqrt(xd * xd + zd * zd);
				float yc = (float) Math.atan(-yd * 0.2F) * 15.0F;
				bob += (dist - bob) * 0.4F;
				tilt += (yc - tilt) * 0.8F;
				
			}
		} else {
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
				yd += ENTITY_GRAVITY * 2.0F;
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
				yd -= ENTITY_GRAVITY * 2.0F;
			moveRelative(xa, za, PLAYER_WALK_SPEED * 1.5F);
			moveWithBorderary(xd, yd, zd);
			yd *= 0.90F;
		}
		
		xd *= ENTITY_HORIZONTAL_SPEED_DECAY;
		yd *= ENTITY_VERTICAL_SPEED_DECAY;
		zd *= ENTITY_HORIZONTAL_SPEED_DECAY;
		
		if (mode == PlayerMode.Flying && fovOffset < PLAYER_FOV_OFFSET_MAX) {
			fovOffset += PLAYER_FOV_OFFSET_CHANGE_STEP;
		} else if (mode == PlayerMode.Normal && fovOffset > 0) {
			fovOffset -= PLAYER_FOV_OFFSET_CHANGE_STEP;
		}
	}

	public boolean destroyTile(Tile tile, int x, int y, int z, PickupEngine pickupEngine) {
		if(x != dx || y != dy || z != dz) {
			dx = x;
			dy = y;
			dz = z;
			destoryTime = -PLAYER_DESTORY_WAIT_TIME;
		}
		destoryTime++;
		if(destoryTime < tile.hardness && mode == PlayerMode.Normal || tile.hardness == -1)
			return false;
		destoryTime = -PLAYER_DESTORY_WAIT_TIME;
		tile.destroy(level, dx, dy, dz);
		tile = tile.getDroppedTile();
		if (tile != null)
			pickupEngine.add(new DroppedItem(level, dx, dy, dz, 0.0F, 0.0F, 0.0F, tile));
		return true;
	}
	
	public void cancelDestroy() {
		destoryTime = -PLAYER_DESTORY_WAIT_TIME;
	}
	
	public Face getYRotFace() {
		if (Math.abs(yRot) < 45.0F)
			return Face.Front;
		else if (yRot > 45.0F && yRot < 135.0F)
			return Face.Right;
		else if (yRot < -45.0F && yRot > -135.0F)
			return Face.Left;
		else
			return Face.Back;
	}
	
	@Override
	public void resetPos() {
		int y = spawnY;
		if (!level.isAirBlock(spawnX, y, spawnZ)) {
			y = level.maxHeight + 2;
			while (level.isAirBlock(spawnX, y - 1, spawnZ)) y--;
			y += 2;
		}
		setPos(spawnX, y, spawnZ);
		bob = 0.0F;
		oBob = 0.0F;
		tilt = 0.0F;
		oTilt = 0.0F;
	}
	
	/**设置随机出生点*/
	public void setRandomSpawnPos() {
		spawnX = (int) (level.x0 + Math.random() * level.length);
		spawnY = level.maxHeight + 2;
		spawnZ = (int) (level.z0 + Math.random() * level.width);
		while (level.isAirBlock(spawnX, spawnY - 1, spawnZ)) spawnY--;
		spawnY += 2;
	}
	
	/**切换游戏模式*/
	public void toggleMode() {
		if (mode == PlayerMode.Normal) {
			mode = PlayerMode.Flying;
		}
		else
			mode = PlayerMode.Normal;
	}
	
	/**是否应该有视角摇晃*/
	public boolean shouldShowBob() {
		return onGround && mode == PlayerMode.Normal;
	}
	
	/**获得视角摇晃偏移值*/
	public float getFovOffset(float a) {
		return fovOffsetO + (fovOffset - fovOffsetO) * a;
	}
	
	/**掉落伤害*/
	@Override
	protected void causeFallDamage(float fallDistant) {
	}
	
	
}
