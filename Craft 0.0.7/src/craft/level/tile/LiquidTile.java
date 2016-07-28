package craft.level.tile;

import org.lwjgl.opengl.GL11;

import craft.Player;
import craft.level.Level;
import craft.particle.ParticleEngine;
import craft.renderer.Texture;

public class LiquidTile extends EntityTile implements Cloneable{
	public static final int WATER_UPDATE_TIME = 50;
	public static final int LAVA_UPDATE_TIME = 100;
	public static final int WATER_SPREAD_SPEED = 2;
	public static final int LAVA_SPREAD_SPEED = 0;
	protected static final float H = 0.9F;
	protected final int cover;
	protected LiquidType type;
	private int lastUpdate = 0;
	public enum LiquidType{
		Water,Lava;
		int getSpreadSpeed() {
			if(this == Water)
				return WATER_SPREAD_SPEED;
			else
				return LAVA_SPREAD_SPEED;
		}
		int getUpdateTime() {
			if(this == Water)
				return WATER_UPDATE_TIME;
			else
				return LAVA_UPDATE_TIME;
		}
	}
	//private static ArrayList<LiquidTile> liquidTiles = new ArrayList<LiquidTile>();

	protected LiquidTile(int id, int tex, int cover, int destroyTime, boolean soildTile, boolean lightTile, LiquidType type) {
		super(id, tex, destroyTime, soildTile, lightTile);
		this.cover = cover;
		this.type = type;
		this.updating = true;
	}

	public void render(Level level, int x, int y, int z) {
		float c1 = 1.0F;
		float c2 = 0.9F;
		float c3 = 0.6F;
		float c4 = 0.8F;
		float b = 0.0F;
		if (type == LiquidType.Water) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_SRC_COLOR);
		}
		float h = shouldRenderFace(level, x, y + 1, z) ? H : 1.0F;
		/*float h = (Tile.tiles[level.getBlock(x, y + 1, z)] instanceof LiquidTile) ? 1.0F :
			(Tile.tiles[level.getBlock(x - 1, y + 1, z)] instanceof LiquidTile)  ? 1.0F :
				(Tile.tiles[level.getBlock(x + 1, y + 1, z)] instanceof LiquidTile)  ? 1.0F :
					(Tile.tiles[level.getBlock(x, y + 1, z - 1)] instanceof LiquidTile)  ? 1.0F :
						(Tile.tiles[level.getBlock(x, y + 1, z + 1)] instanceof LiquidTile)  ? 1.0F : H;*/
		if(shouldRenderFace(level, x, y, z, h, Face.Left)) {
			b =  getBrightness(level, x - 1, y, z) * c4;
			GL11.glColor3f(b, b, b);
			renderFace(x, y, z, h, Face.Left);
			renderCover(x, y, z, h, Face.Left);
		}
		if(shouldRenderFace(level, x, y, z, h, Face.Right)) {
			b =  getBrightness(level, x + 1, y, z) * c4;
			GL11.glColor3f(b, b, b);
			renderFace(x, y, z, h, Face.Right);
			renderCover(x, y, z, h, Face.Right);
		}
		if(shouldRenderFace(level, x, y, z, h, Face.Bottom)) {
			b =  getBrightness(level, x, y - 1, z) * c3;
			GL11.glColor3f(b, b, b);
			renderFace(x, y, z, 1.0F, Face.Bottom);
			renderCover(x, y, z, h, Face.Bottom);
		}
		if(shouldRenderFace(level, x, y, z, h, Face.Top)) {
			b =  getBrightness(level, x, y + 1, z) * c1;
			GL11.glColor3f(b, b, b);
			renderFace(x, y, z, h, Face.Top);
			renderCover(x, y, z, h, Face.Top);
		}
		if(shouldRenderFace(level, x, y, z, h, Face.Back)) {
			b =  getBrightness(level, x, y, z - 1) * c2;
			GL11.glColor3f(b, b, b);
			renderFace(x, y, z, h, Face.Back);
			renderCover(x, y, z, h, Face.Back);
		}
		if(shouldRenderFace(level, x, y, z, h, Face.Front)) {
			b =  getBrightness(level, x, y, z + 1) * c2;
			GL11.glColor3f(b, b, b);
			renderFace(x, y, z, h, Face.Front);
			renderCover(x, y, z, h, Face.Front);
		}
		if (type == LiquidType.Water)
			GL11.glDisable(GL11.GL_BLEND);
	}

	protected void render(Level level) {
		//render(level, x, (int)y, z);
	}

	protected boolean shouldRenderFace(Level level, int x, int y, int z, float h, Face face) {
		switch(face) {
		case Front: z++; break;
		case Back:z--; break;
		case Top:y++; break;
		case Bottom:y--; break;
		case Left:x--; break;
		case Right:x++; break;
		}
		if (!level.isBlock(x, y, z))
			return false;
		//if ((face == Face.Top && h < 1.0F) || !(Tile.tiles[level.getBlock(x, y, z)] instanceof LiquidTile))
		if (level.isLightBlock(x, y, z) && !(Tile.tiles[level.getBlock(x, y, z)] instanceof LiquidTile))
			return true;
		return false;
	}

	protected void renderFace(int x, int y, int z, float h, Face face) {
		int tex = getTexture(face);
		float x0 = x + 0.0F;
		float x1 = x + 1.0F;
		float y0 = y + h - 1.0F;
		float y1 = y + h;
		float z0 = z + 0.0F;
		float z1 = z + 1.0F;
		Texture.bind(tex);
		GL11.glBegin(GL11.GL_QUADS);
		switch(face) {
		case Front:
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(x0, y1, z1);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(x1, y1, z1);
			GL11.glTexCoord2f(1, h);
			GL11.glVertex3f(x1, y0, z1);
			GL11.glTexCoord2f(0, h);
			GL11.glVertex3f(x0, y0, z1);
			break;
		case Back:
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(x0, y1, z0);
			GL11.glTexCoord2f(1, h);
			GL11.glVertex3f(x0, y0, z0);
			GL11.glTexCoord2f(0, h);
			GL11.glVertex3f(x1, y0, z0);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(x1, y1, z0);
			break;
		case Top:
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(x1, y1, z0);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(x1, y1, z1);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex3f(x0, y1, z1);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(x0, y1, z0);
			break;
		case Bottom:
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex3f(x0, y0, z0);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(x0, y0, z1);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(x1, y0, z1);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(x1, y0, z0);
			break;
		case Left:
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(x0, y1, z0);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(x0, y1, z1);
			GL11.glTexCoord2f(1, h);
			GL11.glVertex3f(x0, y0, z1);
			GL11.glTexCoord2f(0, h);
			GL11.glVertex3f(x0, y0, z0);
			break;
		case Right:
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(x1, y1, z1);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(x1, y1, z0);
			GL11.glTexCoord2f(1, h);
			GL11.glVertex3f(x1, y0, z0);
			GL11.glTexCoord2f(0, h);
			GL11.glVertex3f(x1, y0, z1);
			break;
		}
		GL11.glEnd();
	}

	protected void renderCover(int x, int y, int z, float h, Face face) {
		float x0 = x + 0.0F;
		float x1 = x + 1.0F;
		float y0 = y + h - 1.0F;
		float y1 = y + h;
		float z0 = z + 0.0F;
		float z1 = z + 1.0F;
		Texture.bind(this.cover);
		GL11.glBegin(GL11.GL_QUADS);
		switch(face) {
		case Front:
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(x1, y1, z1);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(x0, y1, z1);
			GL11.glTexCoord2f(0, h);
			GL11.glVertex3f(x0, y0, z1);
			GL11.glTexCoord2f(1, h);
			GL11.glVertex3f(x1, y0, z1);
			break;
		case Back:
			GL11.glTexCoord2f(1, h);
			GL11.glVertex3f(x0, y0, z0);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(x0, y1, z0);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(x1, y1, z0);
			GL11.glTexCoord2f(0, h);
			GL11.glVertex3f(x1, y0, z0);
			break;
		case Top:
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(x1, y1, z1);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(x1, y1, z0);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(x0, y1, z0);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex3f(x0, y1, z1);
			break;
		case Bottom:
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(x0, y0, z1);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex3f(x0, y0, z0);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(x1, y0, z0);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(x1, y0, z1);
			break;
		case Left:
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(x0, y1, z1);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(x0, y1, z0);
			GL11.glTexCoord2f(0, h);
			GL11.glVertex3f(x0, y0, z0);
			GL11.glTexCoord2f(1, h);
			GL11.glVertex3f(x0, y0, z1);
			break;
		case Right:
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(x1, y1, z0);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(x1, y1, z1);
			GL11.glTexCoord2f(0, h);
			GL11.glVertex3f(x1, y0, z1);
			GL11.glTexCoord2f(1, h);
			GL11.glVertex3f(x1, y0, z0);
			break;
		}
		GL11.glEnd();
	}

	public boolean destroy(Level level, int x, int y, int z, ParticleEngine particleEngine, boolean needTime) {
		time++;
		if(time < destroyTime * 0.3F && needTime)
			return false;
		level.setBlock(x, y, z, air.id);
		time = 0;
		return true;
	}

	protected void createParticles(Level level, int x, int y, int z, ParticleEngine particleEngine) {

	}

	protected boolean shouldRender() {
		return true;
	}

	protected void update(Level level, Player player) {
		if(lastUpdate++ >= type.getUpdateTime()) {
			updating = updateLiquid(level, x, (int)y, z, 0);
			lastUpdate = 0;
		}
	}

	private boolean updateLiquid(Level level, int x, int y, int z, int depth) {
		boolean hasChanged = false;
		while(level.getBlock(x, --y, z) == air.id) {
			boolean change = level.setBlock(x, y, z, id);
			if (change)
				hasChanged = true;
			//if ((!change) || (type == LiquidType.Lava)) 
			break;
		}
		y++;
		if((type == LiquidType.Water || (!hasChanged)) && level.isSolidBlock(x, y - 1, z)) {
			hasChanged |= checkLiquid(level, x - 1, y, z, depth);
			hasChanged |= checkLiquid(level, x + 1, y, z, depth);
			hasChanged |= checkLiquid(level, x, y, z - 1, depth);
			hasChanged |= checkLiquid(level, x, y, z + 1, depth);
		}
		if(!hasChanged) {
			//level.setBlockDirectly(x, y, z, calmWater.id);
			remove(this);
		}
		return hasChanged;
	}

	private boolean checkLiquid(Level level, int x, int y, int z, int depth) {
		boolean hasChanged = false;
		if (level.getBlock(x, y, z) == air.id) {
			boolean changed = level.setBlock(x, y, z, id);
			if ((changed) && (depth < type.getSpreadSpeed()))
				hasChanged |= updateLiquid(level, x, y, z, depth + 1);
		}
		return hasChanged;
	}

	@Override
	public void neighborChanged(Level level, int x, int y, int z, int block) {
		boolean hasAirNeighbor = false;
		if (level.getBlock(x - 1, y, z) == air.id) hasAirNeighbor = true;
		if (level.getBlock(x + 1, y, z) == air.id) hasAirNeighbor = true;
		if (level.getBlock(x, y - 1, z) == air.id) hasAirNeighbor = true;
		if (level.getBlock(x, y, z - 1) == air.id) hasAirNeighbor = true;
		if (level.getBlock(x, y, z + 1) == air.id) hasAirNeighbor = true;
		if (hasAirNeighbor)
			add(x, y, z);
		if (tiles[block] instanceof LiquidTile) {
			if (type == LiquidType.Water && ((LiquidTile)tiles[block]).getType() == LiquidType.Lava)
				level.setBlockDirectly(x, y, z, cobblestone.id);
			if (type == LiquidType.Lava && ((LiquidTile)tiles[block]).getType() == LiquidType.Water)
				level.setBlockDirectly(x, y, z, cobblestone.id);
		}

	}

	public LiquidType getType() {
		return this.type;
	}
	
	public boolean getFog() {
		return (type == LiquidType.Water);
	}
}
