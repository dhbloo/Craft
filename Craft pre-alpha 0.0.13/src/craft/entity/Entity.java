package craft.entity;

import java.util.ArrayList;

import craft.Player;
import craft.level.Level;
import craft.level.tile.tileEntity.LiquidTile.LiquidType;
import craft.phys.AABB;
import craft.renderer.Tesselator;

public class Entity {
	public static final float ENTITY_FIELD_OF_VIEW_X = 90.0F;		//ʵ��������Ұ��Χ
	public static final float ENTITY_FIELD_OF_VIEW_MOVE_SPEED = 0.15F;		//ʵ����Ұ��ת�ٶ�
	public static final float ENTITY_JUMP_HEIGHT = 0.24F;		//ʵ����Ծ�߶�
	public static final float ENTITY_WALK_SPEED = 0.075F;		//ʵ���ƶ��ٶ�
	public static final float ENTITY_GRAVITY = 0.02F;		//ʵ������ٶ� ����ֵ��Խ������Խ��
	public static final float ENTITY_HORIZONTAL_SPEED_DECAY = 0.82F;		//ˮƽ�����ٶ�˥�� ����ֵ��(0~1)ԽС˥��Խ��
	public static final float ENTITY_VERTICAL_SPEED_DECAY = 0.96F;		//��ֱ�����ٶ�˥�� ����ֵ��(0~1)ԽС˥��Խ��
	public static final float ENTITY_ONGROUND_SPEED_DECAY = 0.7F;		//�ŵ��ٶ�˥�� ����ֵ��(0~1)ԽС˥��Խ��
	public static final float ENTITY_IN_LIQUID_GRAVITY = 0.01F;		//�����Һ���еĵ����ٶ�
	public static final float ENTITY_IN_LIQUID_SPEED = 0.025F;		//�����Һ���е��ƶ��ٶ�
	public static final float ENTITY_IN_LIQUID_JUMP_SPEED = 0.02F;		//�����Һ���е���Ծ�ٶ�
	public static final float ENTITY_IN_WATER_SPEED_DECAY = 0.8F;		//�����ˮ�е��ٶ�˥��
	public static final float ENTITY_IN_LAVA_SPEED_DECAY = 0.5F;		//������ҽ��е��ٶ�˥��
	public static final float ENTITY_IN_LIQUID_JUMP_HEIGHT = 0.2F;		//�����Һ���е��������ߵ���Ծ�߶�
	protected Level level;
	public float xo, yo, zo, x, y, z, xd, yd, zd;
	public float xRot, yRot;
	public AABB aabb;
	public boolean onGround = false;		//ʵ���ŵ�
	public boolean horizontalCollision = false;		//ʵ�������ײ

	public boolean removed = false;			//ʵ�屻�Ƴ�

	protected float heightOffset = 0.0F;		//	�߶�ƫ��
	protected float aabbWidth = 0.0F;			//	AABB���
	protected float aabbHeight = 0.0F;		//AABB�߶�

	public Entity(Level level) {
		this.level = level;
		this.aabb = new AABB();
		resetPos();
	}

	protected void resetPos() {
		float x = level.x0 + (float)Math.random() * level.length;
		float y = level.maxHeight + 5;
		float z = level.z0 + (float)Math.random() * level.width;
		setPos(x, y, z);
	}

	public void remove() {
		this.removed = true;
	}

	protected void setSize(float width, float height) {
		this.aabbWidth = width;
		this.aabbHeight = height;
		this.heightOffset = height / 2;
	}

	protected void setPos(float x, float y, float z) {
		this.xo = x;
		this.yo = y;
		this.zo = z;
		this.x = x;
		this.y = y;
		this.z = z;
		float w = aabbWidth / 2.0F;
		float h = aabbHeight / 2.0F;
		aabb.setBounds(x - w, y - h, z - w, x + w, y + h, z + w);
	}
	
	protected void setPosWithMoving(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		float w = aabbWidth / 2.0F;
		float h = aabbHeight / 2.0F;
		aabb.setBounds(x - w, y - h, z - w, x + w, y + h, z + w);
	}

	public void turn(float xo, float yo) {
		this.yRot = (float)(this.yRot + xo * ENTITY_FIELD_OF_VIEW_MOVE_SPEED);
		this.xRot = (float)(this.xRot - yo * ENTITY_FIELD_OF_VIEW_MOVE_SPEED);
		if (this.yRot >= 180) this.yRot -= 360.0F;
		if (this.yRot <= -180) this.yRot += 360.0F;
		if (this.xRot < -ENTITY_FIELD_OF_VIEW_X) this.xRot = -ENTITY_FIELD_OF_VIEW_X;
		if (this.xRot > ENTITY_FIELD_OF_VIEW_X) this.xRot = ENTITY_FIELD_OF_VIEW_X;
	}

	public void tick() {
		this.xo = x;
		this.yo = y;
		this.zo = z;
	}

	protected void move(float xa, float ya, float za) {
		float xaOrg = xa;
		float yaOrg = ya;
		float zaOrg = za;

		ArrayList<AABB> aABBs = level.getCubes(aabb.expand(xa, ya, za));

		for (int i = 0; i < aABBs.size(); i++)
			ya = aABBs.get(i).clipYCollide(aabb, ya);
		aabb.move(0.0F, ya, 0.0F);

		for (int i = 0; i < aABBs.size(); i++)
			xa = aABBs.get(i).clipXCollide(aabb, xa);
		aabb.move(xa, 0.0F, 0.0F);

		for (int i = 0; i < aABBs.size(); i++)
			za = aABBs.get(i).clipZCollide(aabb, za);
		aabb.move(0.0F, 0.0F, za);

		horizontalCollision = ((xaOrg != xa) || (zaOrg != za));
		onGround = ((yaOrg != ya) && (yaOrg < 0.0F));

		if (xaOrg != xa) this.xd = 0.0F;
		if (yaOrg != ya) this.yd = 0.0F;
		if (zaOrg != za) this.zd = 0.0F;

		x = ((aabb.x0 + aabb.x1) / 2.0F);
		y = (aabb.y0 + heightOffset);
		z = ((aabb.z0 + aabb.z1) / 2.0F);
	}

	protected void moveWithWall(float xa, float ya, float za) {
		float xaOrg = xa;
		float yaOrg = ya;
		float zaOrg = za;

		ArrayList<AABB> aABBs = level.getCubes(aabb.expand(xa, ya, za));
		aABBs.addAll(level.getWallCubes());

		for (int i = 0; i < aABBs.size(); i++)
			ya = aABBs.get(i).clipYCollide(aabb, ya);
		aabb.move(0.0F, ya, 0.0F);

		for (int i = 0; i < aABBs.size(); i++)
			xa = aABBs.get(i).clipXCollide(aabb, xa);
		aabb.move(xa, 0.0F, 0.0F);

		for (int i = 0; i < aABBs.size(); i++)
			za = aABBs.get(i).clipZCollide(aabb, za);
		aabb.move(0.0F, 0.0F, za);

		horizontalCollision = ((xaOrg != xa) || (zaOrg != za));
		onGround = ((yaOrg != ya) && (yaOrg < 0.0F));

		if (xaOrg != xa) this.xd = 0.0F;
		if (yaOrg != ya) this.yd = 0.0F;
		if (zaOrg != za) this.zd = 0.0F;

		x = ((aabb.x0 + aabb.x1) / 2.0F);
		y = (aabb.y0 + heightOffset);
		z = ((aabb.z0 + aabb.z1) / 2.0F);
	}

	protected void moveRelative(float xa, float za, float speed) {
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

	public boolean isLit() {
		int xTile = (int)Math.floor(this.x);
		int yTile = (int)Math.floor(this.y);
		int zTile = (int)Math.floor(this.z);
		return level.isLit(xTile, yTile, zTile);
	}

	public boolean isInWater() {
		return level.containsLiquid(aabb.grow(0.0F, -0.4F, 0.0F), LiquidType.Water);
	}

	public boolean isInLava() {
		return level.containsLiquid(aabb.grow(0.0F, -0.1F, 0.0F), LiquidType.Lava);
	}

	public boolean isFree(float xa, float ya, float za) {
		AABB box = aabb.cloneMove(xa, ya, za);
		ArrayList<AABB> aABBs = this.level.getCubes(box);
		if (aABBs.size() > 0) return false;
		if (this.level.containsAnyLiquid(box)) return false;
		return true;
	}
	
	public float distanceToSqr(Player player) {
		float xd = player.x - this.x;
		float yd = player.y - this.y;
		float zd = player.z - this.z;
		return xd * xd + yd * yd + zd * zd;
	}
	
	public void render(Tesselator t, float a) {
	}
	
	public byte getBrightness() {
		return level.getBrightness((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
	}

}
