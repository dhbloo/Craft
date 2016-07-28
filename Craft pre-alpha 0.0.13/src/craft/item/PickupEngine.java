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
		int time = (int) (System.nanoTime() * 0.0000001D);

	    float xa = -(float)Math.cos(time % 360 * Math.PI / 180.0D);
	    float za = -(float)Math.sin(time % 360 * Math.PI / 180.0D);
	    float ya = 1.0F;
	    float yd = (float)(Math.sin(time * 0.06F) + 1) * 0.1F;

	    Tesselator t = Tesselator.instance;
	    t.begin();
		for (int i = 0; i < this.droppedItems.size(); i++) {
			float b = 0.9F; //(droppedItems.get(i).getBrightness() / Brightness.MAX) * 0.9F + 0.1F;
			t.color(b, b, b);
			droppedItems.get(i).render(t, a, xa, ya, za, yd);
		}
		t.end();
	}
	
}
