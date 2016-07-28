package craft.world;

public class BlockStorage {
	/**方块储存单位长度*/
	public static final int SIZE = 16;
	/**方块储存右移位*/
	public static final int STORAGE_SHIFT_COUNT = SIZE >> 2;
	
	/**方块ID数组
	 * 大小为16*16*16
	 * */
	private byte[] blockArray;
	/**亮度信息三维数组*/
	private byte[] brightnessArray;

	public BlockStorage(byte[] blockArray) {
		this.blockArray = blockArray;
		this.brightnessArray = new byte[blockArray.length];
	}
	
	/**获取方块ID*/
	public byte getBlockID(int x, int y, int z) {
		return blockArray[y << 8 | x << 4 | z];
	}
	
	/**设置方块ID*/
	public void setBlockID(int x, int y, int z, byte blockID) {
		blockArray[y << 8 | x << 4 | z] = blockID;
	}
	
	/**获取方块亮度*/
	public byte getBlockBrightness(int x, int y, int z) {
		return brightnessArray[y << 8 | x << 4 | z];
	}
	
	/**设置方块亮度*/
	public void setBlockBrightness(int x, int y, int z, byte brightness) {
		this.brightnessArray[y << 8 | x << 4 | z] = brightness;
	}

}
