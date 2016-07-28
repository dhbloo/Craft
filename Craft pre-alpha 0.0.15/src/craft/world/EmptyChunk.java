package craft.world;

public class EmptyChunk extends Chunk {

	public EmptyChunk(World world, int x0, int y0, int z0) {
		super(world, x0, y0, z0);
		int count = world.height >> BlockStorage.STORAGE_SHIFT_COUNT;
		BlockStorage[] blockStorages = new BlockStorage[count];
		for (int i = 0; i < count; i++)
			blockStorages[i] = new EmptyBlockStorage();
		setDate(blockStorages);
	}

}
