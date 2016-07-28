package craft.level;

public class Brightness extends Coord{
	/**亮度分级*/
	public static final byte BRIGHTNESS_GRADE = 24;
	/**最大亮度*/
	public static final byte MAX = BRIGHTNESS_GRADE - 1;
	/**亮度*/
	public byte b;
	/**创建空亮度信息区块*/
	public Brightness(int x, int y, int z, byte brightness) {
		super(x, y, z);
		this.b = brightness;
	}
	
	public Brightness() {
		super();
	}
	
	
	
}
