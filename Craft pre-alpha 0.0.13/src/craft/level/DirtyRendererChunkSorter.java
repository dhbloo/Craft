package craft.level;

import java.util.Comparator;

import craft.Player;

public class DirtyRendererChunkSorter implements Comparator<RenderChunk> {
	private Player player;

	public DirtyRendererChunkSorter(Player player) {
		this.player = player;
	}

	public int compare(RenderChunk c0, RenderChunk c1)
	{
		boolean i0 = c0.visible;
		boolean i1 = c1.visible;
		if ((i0) && (!i1)) return -1;
		if ((i1) && (!i0)) return 1;
		float d0 = c0.distanceToSqr(player);
		float d1 = c1.distanceToSqr(player);
		return d0 < d1 ? -1 : d0 > d1 ? 1 : 0;
	}
}
