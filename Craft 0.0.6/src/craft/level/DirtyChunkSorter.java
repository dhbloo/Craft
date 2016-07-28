package craft.level;

import java.util.Comparator;

import craft.Player;

public class DirtyChunkSorter implements Comparator<Chunk> {
	private Player player;

	public DirtyChunkSorter(Player player) {
		this.player = player;
	}

	public int compare(Chunk c0, Chunk c1)
	{
		boolean i0 = c0.visible;
		boolean i1 = c1.visible;
		if ((i0) && (!i1)) return -1;
		if ((i1) && (!i0)) return 1;
		return c0.distanceToSqr(player) < c1.distanceToSqr(player) ? -1 : c0.distanceToSqr(player) > c1.distanceToSqr(player) ? 1 : 0;
	}
}
