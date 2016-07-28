package craft.entity;

import java.util.ArrayList;

import craft.Player;
import craft.phys.AABB;
import craft.renderer.Frustum;
import craft.renderer.Tesselator;
import craft.world.World;

public class EntityList {
	public static final int ENTITY_RENDER_DISTANCE = 32 * 32 * 32;
	@SuppressWarnings("unused")
	private World world;
	private ArrayList<Entity> entities;

	public EntityList(World world) {
		this.world = world;
		this.entities = new ArrayList<Entity>();
	}
	
	public void add(Entity entity) {
		entities.add(entity);
	}
	
	public void remove(Entity entity) {
		entities.remove(entity);
	}
	
	public void remove(int index) {
		entities.remove(index);
	}
	
	/**����ȫ��ʵ��*/
	public void tickAll() {
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).tick();
			if (entities.get(i).removed)
				entities.remove(i--);
		}
	}
	
	/**��Ⱦʹ�õ�ʵ��*/
	public void render(Player player, Frustum frustum, float a) {
		Tesselator tesselator = Tesselator.instance;
		tesselator.begin();
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			if (frustum.isVisible(entity.aabb) && entity.distanceToSqr(player) < ENTITY_RENDER_DISTANCE)
				entity.render(tesselator, a);
		}
		tesselator.end();
	}
	
	/**��ȡʵ�������*/
	public int getEntitiesCount() {
		return entities.size();
	}

	/**����ʹ�õ�ʵ����ײAABB*/
	public ArrayList<AABB> getAllEntitiesAABB() {
		ArrayList<AABB> aabbs = new ArrayList<AABB>();
		for (int i = 0; i < entities.size(); i++) {
			aabbs.add(entities.get(i).aabb);
		}
		return aabbs;
	}
	
	/**���ָ����Χʵ��AABB*/
	public ArrayList<AABB> getEntitiesAABB(AABB aabb) {
		ArrayList<AABB> aabbs = new ArrayList<AABB>();
		for (int i = 0; i < entities.size(); i++) {
			AABB entityAABB = entities.get(i).aabb;
			if (aabb.intersects(entityAABB))
				aabbs.add(entityAABB);
		}
		return aabbs;
	}
	
}
