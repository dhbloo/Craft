package craft.level.Brightness;

import craft.level.tile.Face;

public class Direction {
	@SuppressWarnings("unused")
	private static final byte FRONT = Face.Front.getByte();
	@SuppressWarnings("unused")
	private static final byte BACK = Face.Back.getByte();
	@SuppressWarnings("unused")
	private static final byte TOP = Face.Top.getByte();
	@SuppressWarnings("unused")
	private static final byte BOTTOM = Face.Bottom.getByte();
	@SuppressWarnings("unused")
	private static final byte LEFT = Face.Left.getByte();
	@SuppressWarnings("unused")
	private static final byte RIGHT = Face.Right.getByte();
	
	private static final byte FRONTBIT = 2;
	private static final byte BACKBIT = 4;
	private static final byte TOPBIT = 8;
	private static final byte BOTTOMBIT = 16;
	private static final byte LEFTBIT = 32;
	private static final byte RIGHTBIT = 64;
	
	@SuppressWarnings("unused")
	private byte d;
	
	public Direction(Face direction) {
		if (direction == null)
			d = 6;
		else
			d = direction.getByte();
	}
	
	@SuppressWarnings("unused")
	private byte getBit(Face direction) {
		switch (direction) {
		case Front:return FRONTBIT; 
		case Back:return BACKBIT; 
		case Top:return TOPBIT; 
		case Bottom:return BOTTOMBIT; 
		case Left:return LEFTBIT; 
		case Right:return RIGHTBIT;
		}
		return 0;
	}
	

}
