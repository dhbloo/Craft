package craft.level.Brightness;


public class Brightness {
	/**�������*/
	public static final byte MAX = 24;
	
	/**����*/
	public byte[][][] b;
	/**����ķ���*/
	@SuppressWarnings("unused")
	private byte[][][] direction;
	
	/**������������Ϣ����*/
	public Brightness(int length, int height, int width) {
		this.b = new byte[length][height][width];
		this.direction = new byte[length][height][width];
	}
	
	
	
}
