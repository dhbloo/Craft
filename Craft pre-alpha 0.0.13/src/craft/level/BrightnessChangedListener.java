package craft.level;

public class BrightnessChangedListener {
	/**亮度更新范围*/
	private int bx0, bx1, by0, by1, bz0, bz1;
	private Level level;

	public BrightnessChangedListener(Level level) {
		this.level = level;
		bx0 = level.x1;
		by0 = level.y1;
		bz0 = level.z1;
		bx1 = level.x0;
		by1 = level.y0;
		bz1 = level.z0;
	}
	
	/**通知亮度改变
	 * @param x0 范围x0
	 * @param y0 范围y0
	 * @param z0 范围z0
	 * @param x1 长度范围
	 * @param y1 高度范围
	 * @param z1 宽度范围*/
	public void brightnessChanged(int x0, int y0, int z0, int xa, int ya, int za) {
		int x1 = x0 + xa;
		int y1 = y0 + ya;
		int z1 = z0 + za;
		if (x0 < bx0) bx0 = x0;
		if (x1 > bx1) bx1 = x1;
		if (y0 < by0) by0 = y0;
		if (y1 > by1) by1 = y1;
		if (z0 < bz0) bz0 = z0;
		if (z1 > bz1) bz1 = z1;
	}
	
	/**更新亮度改变*/
	public void update() {
		level.lightColumnChanged(bx0 - 1, by0 - 1, bz0 - 1, bx1 + 1, by1 + 1, bz1 + 1);
		bx0 = level.x1;
		by0 = level.y1;
		bz0 = level.z1;
		bx1 = level.x0;
		by1 = level.y0;
		bz1 = level.z0;
	}

}
