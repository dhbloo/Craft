package craft.world;

import java.util.Comparator;

import craft.Player;

public class DirtyChunkSorter implements Comparator<Chunk> {
	private Player player;

	public DirtyChunkSorter(Player player) {
		this.player = player;
	}

	public int compare(Chunk c0, Chunk c1) {
		float d0 = c0.distanceToSqr(player);
		float d1 = c1.distanceToSqr(player);
		return d0 < d1 ? -1 : d0 > d1 ? 1 : 0;
	}
}
