package craft.level;

import java.util.Comparator;

import craft.Player;

public class DistanceChunkSorter implements Comparator<Chunk>{
	private Player player;

	public DistanceChunkSorter(Player player) {
		this.player = player;
	}

	public int compare(Chunk c0, Chunk c1) {
		return c0.distanceToSqr(player) < c1.distanceToSqr(player) ? -1 : c0.distanceToSqr(player) > c1.distanceToSqr(player) ? 1 : 0;
	}
}
