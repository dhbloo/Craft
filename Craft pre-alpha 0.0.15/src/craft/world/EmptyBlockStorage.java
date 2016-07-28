package craft.world;

public class EmptyBlockStorage extends BlockStorage {

	public EmptyBlockStorage() {
		super(new byte[SIZE * SIZE * SIZE]);
	}

}
