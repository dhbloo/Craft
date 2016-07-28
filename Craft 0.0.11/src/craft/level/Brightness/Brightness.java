package craft.level.Brightness;


public class Brightness {
	/**最大亮度*/
	public static final byte MAX = 24;
	
	/**亮度*/
	public byte[][][] b;
	/**照向的方向*/
	@SuppressWarnings("unused")
	private byte[][][] direction;
	
	/**创建空亮度信息区块*/
	public Brightness(int length, int height, int width) {
		this.b = new byte[length][height][width];
		this.direction = new byte[length][height][width];
	}
	
	
	
}
