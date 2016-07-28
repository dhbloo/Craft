package craft.world;

public class BlockStorage {
	/**���鴢�浥λ����*/
	public static final int SIZE = 16;
	/**���鴢������λ*/
	public static final int STORAGE_SHIFT_COUNT = SIZE >> 2;
	
	/**����ID����
	 * ��СΪ16*16*16
	 * */
	private byte[] blockArray;
	/**������Ϣ��ά����*/
	private byte[] brightnessArray;

	public BlockStorage(byte[] blockArray) {
		this.blockArray = blockArray;
		this.brightnessArray = new byte[blockArray.length];
	}
	
	/**��ȡ����ID*/
	public byte getBlockID(int x, int y, int z) {
		return blockArray[y << 8 | x << 4 | z];
	}
	
	/**���÷���ID*/
	public void setBlockID(int x, int y, int z, byte blockID) {
		blockArray[y << 8 | x << 4 | z] = blockID;
	}
	
	/**��ȡ��������*/
	public byte getBlockBrightness(int x, int y, int z) {
		return brightnessArray[y << 8 | x << 4 | z];
	}
	
	/**���÷�������*/
	public void setBlockBrightness(int x, int y, int z, byte brightness) {
		this.brightnessArray[y << 8 | x << 4 | z] = brightness;
	}

}
