package craft.level.tile;

import org.lwjgl.opengl.GL11;

import craft.level.Level;
import craft.particle.ParticleEngine;
import craft.renderer.Texture;

public class LiquidTile extends Tile{
	protected static final float H = 0.9F;
	protected final int cover;
	protected LiquidTile(int id, int tex, int cover, int destroyTime, boolean soildTile, boolean lightTile) {
		super(id, tex, destroyTime, soildTile, lightTile);
		this.cover = cover;
	}

	public void render(Level level, int x, int y, int z) {
		float c1 = 1.0F;
		float c2 = 0.8F;
		float c3 = 0.5F;
		float c4 = 0.6F;
		float b = 0.0F;
		GL11.glEnable(GL11.GL_BLEND);
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_SRC_COLOR);
		// float h = shouldRenderFace(level, x, y + 1, z) ? H : 1.0F;
		float h = (Tile.tiles[level.getBlock(x, y + 1, z)] instanceof LiquidTile) ? 1.0F :
			(Tile.tiles[level.getBlock(x - 1, y + 1, z)] instanceof LiquidTile)  ? 1.0F :
				(Tile.tiles[level.getBlock(x + 1, y + 1, z)] instanceof LiquidTile)  ? 1.0F :
					(Tile.tiles[level.getBlock(x, y + 1, z - 1)] instanceof LiquidTile)  ? 1.0F :
						(Tile.tiles[level.getBlock(x, y + 1, z + 1)] instanceof LiquidTile)  ? 1.0F : H;
		if(shouldRenderFace(level, x, y, z, h, Face.Left)) {
			b =  getBrightness(level, x - 1, y, z) * c4;
			GL11.glColor3f(b, b, b);
			renderFace(x, y, z, h, Face.Left);
		}
		if(shouldRenderFace(level, x, y, z, h, Face.Right)) {
			b =  getBrightness(level, x + 1, y, z) * c4;
			GL11.glColor3f(b, b, b);
			renderFace(x, y, z, h, Face.Right);
		}
		if(shouldRenderFace(level, x, y, z, h, Face.Bottom)) {
			b =  getBrightness(level, x, y - 1, z) * c3;
			GL11.glColor3f(b, b, b);
			renderFace(x, y, z, 1.0F, Face.Bottom);
		}
		if(shouldRenderFace(level, x, y, z, h, Face.Top)) {
			b =  getBrightness(level, x, y + 1, z) * c1;
			GL11.glColor3f(b, b, b);
			renderFace(x, y, z, h, Face.Top);
		}
		if(shouldRenderFace(level, x, y, z, h, Face.Back)) {
			b =  getBrightness(level, x, y, z - 1) * c2;
			GL11.glColor3f(b, b, b);
			renderFace(x, y, z, h, Face.Back);
		}
		if(shouldRenderFace(level, x, y, z, h, Face.Front)) {
			b =  getBrightness(level, x, y, z + 1) * c2;
			GL11.glColor3f(b, b, b);
			renderFace(x, y, z, h, Face.Front);
		}
		GL11.glDisable(GL11.GL_BLEND);
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
		if ((face == Face.Top && h < 1.0F) || !(Tile.tiles[level.getBlock(x, y, z)] instanceof LiquidTile))
			return true;
		return false;
	}

	protected void renderFace(int x, int y, int z, float h, Face face) {
		int tex = getTexture(face);
		float x0 = x + 0.0F;
		float x1 = x + 1.0F;
		float y0 = y + 0.0F;
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


}
