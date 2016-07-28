package craft;

import org.lwjgl.input.Keyboard;

import craft.item.DroppedItem;
import craft.item.PickupEngine;
import craft.level.Level;
import craft.level.tile.Face;
import craft.level.tile.Tile;
import craft.particle.ParticleEngine;

public class Player extends Entity{
	public static final float PLAYER_WIDTH = 0.4F;		//玩家宽度
	public static final float PLAYER_HEIGHT = 1.75F;		//玩家高度
	public static final float PLAYER_EYE_HEIGHT = 1.65F;		//玩家眼高
	
	public static final float PLAYER_PICK_LENGTH = 4.0F;
	public PlayerMode mode = PlayerMode.Normal;		//玩家当前模式
	public int dx, dy, dz;
	public int destoryTime = 0;
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
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
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
				moveRelative(xa, za, onGround ? ENTITY_WALK_SPEED : ENTITY_WALK_SPEED * 0.25F);
				
				moveWithWall(xd, yd, zd);
				yd -= ENTITY_GRAVITY;

				if (onGround) {
					xd *= ENTITY_ONGROUND_SPEED_DECAY;
					zd *= ENTITY_ONGROUND_SPEED_DECAY;
				}
			}
		} else {
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
				yd += ENTITY_GRAVITY;
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
				yd -= ENTITY_GRAVITY;
			moveRelative(xa, za, ENTITY_WALK_SPEED);
			move(xd, yd, zd);
			yd *= 0.97F;
		}
		xd *= ENTITY_HORIZONTAL_SPEED_DECAY;
		yd *= ENTITY_VERTICAL_SPEED_DECAY;
		zd *= ENTITY_HORIZONTAL_SPEED_DECAY;
		//System.out.println(x+","+y+","+z);
	}

	public boolean destroyTile(Tile tile, int x, int y, int z, ParticleEngine particleEngine, PickupEngine pickupEngine) {
		if(x != dx || y != dy || z != dz) {
			dx = x;
			dy = y;
			dz = z;
			destoryTime = 0;
		}
		destoryTime++;
		if(destoryTime < tile.destroyTime && mode == PlayerMode.Normal)
			return false;
		destoryTime = 0;
		tile.destroy(level, dx, dy, dz, particleEngine);
		tile = tile.getDroppedTile();
		if (tile != null)
			pickupEngine.add(new DroppedItem(level, dx, dy, dz, 0.0F, 0.0F, 0.0F, tile));
		return true;
	}
	
	public void cancelDestroy() {
		destoryTime = 0;
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
	
}
