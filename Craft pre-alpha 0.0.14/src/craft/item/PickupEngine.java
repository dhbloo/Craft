package craft.item;

import java.util.ArrayList;

import craft.Player;
import craft.Selector;
import craft.level.Level;
import craft.renderer.Tesselator;

public class PickupEngine {
	public static final float PICKUP_DISTANCE = 4.0F;
	protected Level level;
	private ArrayList<DroppedItem> droppedItems = new ArrayList<DroppedItem>();

	public PickupEngine(Level level) {
		this.level = level;
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

	    Tesselator t = Tesselator.instance;
	    t.begin();
		for (int i = 0; i < this.droppedItems.size(); i++) {
			float b = 0.9F;
			t.color(b, b, b);
			droppedItems.get(i).render(t, a);
		}
		t.end();
	}
	
}
