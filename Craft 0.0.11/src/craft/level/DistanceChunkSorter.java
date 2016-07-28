package craft.level;

import java.util.Comparator;

import craft.Player;

public class DistanceChunkSorter implements Comparator<RenderChunk>{
	private Player player;

	public DistanceChunkSorter(Player player) {
		this.player = player;
	}

	public int compare(RenderChunk c0, RenderChunk c1) {
		float d0 = c0.distanceToSqr(player);
		float d1 = c1.distanceToSqr(player);
		return d0 < d1 ? -1 : d0 > d1 ? 1 : 0;
	}
}
