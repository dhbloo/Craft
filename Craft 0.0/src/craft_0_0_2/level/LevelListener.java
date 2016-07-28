package craft_0_0_2.level;

public abstract interface LevelListener {
	public abstract void blockChanged(int x, int y, int z);

	public abstract void lightColumnChanged(int x, int z, int y0, int y1);

	public abstract void allChanged();
}
