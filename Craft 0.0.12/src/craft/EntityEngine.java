package craft;

import java.util.ArrayList;

import craft.level.Level;
import craft.level.tile.EntityTile;
import craft.renderer.Frustum;
import craft.renderer.Tesselator;

public class EntityEngine {
	public static final int ENTITY_RENDER_DISTANCE = 32 * 32 * 16;
	protected Level level;
	private static ArrayList<Entity> entities = new ArrayList<Entity>();

	public EntityEngine(Level level) {
		this.level = level;
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
	
	public void tick() {
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).tick();
			if (entities.get(i).removed)
				entities.remove(i--);
		}
	}
	
	public void render(Player player, Frustum frustum, float a) {
		EntityTile.renderEntityTiles(Tesselator.instance, level, frustum, a);
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			if (frustum.isVisible(entity.aabb) && entity.distanceToSqr(player) < ENTITY_RENDER_DISTANCE)
				entity.render(a);
		}
	}

}
