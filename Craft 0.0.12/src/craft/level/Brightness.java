package craft.level;

public class Brightness extends Coord{
	/**���ȷּ�*/
	public static final byte BRIGHTNESS_GRADE = 24;
	/**�������*/
	public static final byte MAX = BRIGHTNESS_GRADE - 1;
	/**����*/
	public byte b;
	/**������������Ϣ����*/
	public Brightness(int x, int y, int z, byte brightness) {
		super(x, y, z);
		this.b = brightness;
	}
	
	public Brightness() {
		super();
	}
	
	
	
}
