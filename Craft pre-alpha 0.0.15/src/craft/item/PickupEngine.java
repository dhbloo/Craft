package craft.item;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import craft.Player;
import craft.Selector;
import craft.world.World;

public class PickupEngine {
	public static final float PICKUP_DISTANCE = 4.0F;
	protected World world;
	private ArrayList<DroppedItem> droppedItems = new ArrayList<DroppedItem>();

	public PickupEngine(World world) {
		this.world = world;
	}
	
	public void add(DroppedItem item) {
		droppedItems.add(item);
	}
	
	public void tick(Player player, Selector selector) {
		for (int i = 0; i < droppedItems.size(); i++) {
			DroppedItem item = droppedItems.get(i);
			item.tick();
			if (item.removed)
				droppedItems.remove(i--);
			else if (item.distanceToSqr(player) < PICKUP_DISTANCE) {
				selector.addTile(item.tile);
				droppedItems.remove(i--);
			}
		}
	}

	public void render(Player player, float a) {
		if (droppedItems.size() == 0) return;

		for (int i = 0; i < this.droppedItems.size(); i++) {
			DroppedItem droppedItem = droppedItems.get(i);
			GL11.glPushMatrix();
			droppedItem.render(a);
			GL11.glPopMatrix();
		}
	}
	
}
