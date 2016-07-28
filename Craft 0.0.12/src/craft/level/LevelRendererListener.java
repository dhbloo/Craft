package craft.level;

public abstract interface LevelRendererListener {
	public abstract void blockChanged(int x, int y, int z);

	public abstract void lightColumnChanged(int x0, int y0, int z0, int x1, int y1, int z1);

	public abstract void allChanged();

}
