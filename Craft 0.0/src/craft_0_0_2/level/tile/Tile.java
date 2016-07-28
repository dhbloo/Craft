package craft_0_0_2.level.tile;

import org.lwjgl.opengl.GL11;
import craft_0_0_2.level.Level;
import craft_0_0_2.phys.AABB;
import craft_0_0_2.renderer.Texture;

public class Tile {
	public static final Tile[] tiles = new Tile[256];
	public static final Tile air=null;
	public static final Tile stone=new Tile(TileConst.STONE);
	public static final Tile grass=new GrassTile(TileConst.GRASS);
	public static final Tile soil=new SoilTile(TileConst.SOIL);
	public static final Tile cobblestone=new Tile(TileConst.COBBLESTONE);
	public static final Tile bedrock=new Tile(TileConst.BEDROCK);
	public static final Tile oak_planks=new Tile(TileConst.OAK_PLANKS);
	public int tex;
	public final int id;
	
	protected Tile(int id) {
		tiles[id] = this;
		this.id=id;
	}
	
	protected Tile(int id, int tex) {
		this(id);
		this.tex=tex;
	}
	
	protected Tile(TileConst tile) {
		this.id = tile.value;
		tiles[id] = this;
		this.tex = tile.getTex();
	}
	
	protected int getTexture(Face face) {
	    return this.tex;
	}
	
	public void render(Level level,int x,int y,int z) {
		float c1 = 1.0F;
	    float c2 = 0.8F;
	    float c3 = 0.6F;
	    float b = 0.0F;
	    if(shouldRenderFace(level, x - 1, y, z)) {
	    	b =  level.getBrightness(x - 1, y, z);
	    	GL11.glColor3f(c2 * b, c2 * b, c2 * b);
	    	renderFace(x, y, z, Face.Left);
	    }
	    if(shouldRenderFace(level, x + 1, y, z)) {
	    	b =  level.getBrightness(x + 1, y, z);
	    	GL11.glColor3f(c2 * b, c2 * b, c2 * b);
	    	renderFace(x, y, z, Face.Right);
	    }
	    if(shouldRenderFace(level, x, y - 1, z)) {
	    	b =  level.getBrightness(x, y - 1, z);
	    	GL11.glColor3f(c3 * b, c3 * b, c3 * b);
	    	renderFace(x, y, z, Face.Bottom);
	    }
	    if(shouldRenderFace(level, x, y + 1, z)) {
	    	b =  level.getBrightness(x, y + 1, z);
	    	GL11.glColor3f(c1 * b, c1 * b, c1 * b);
	    	renderFace(x, y, z, Face.Top);
	    }
	    if(shouldRenderFace(level, x, y, z - 1)) {
	    	b =  level.getBrightness(x, y, z - 1);
	    	GL11.glColor3f(c2 * b, c2 * b, c2 * b);
	    	renderFace(x, y, z, Face.Back);
	    }
	    if(shouldRenderFace(level, x, y, z + 1)) {
	    	b =  level.getBrightness(x, y, z + 1);
	    	GL11.glColor3f(c2 * b, c2 * b, c2 * b);
	    	renderFace(x, y, z, Face.Front);
	    }
	}
	
	private boolean shouldRenderFace(Level level, int x, int y, int z) {
	    if (level.isLightBlock(x, y, z)) 
	    	return true; 
	    return false;
	  }
	
	public void renderFace(int x, int y, int z, Face face) {
		int tex = getTexture(face);
		float x0 = x + 0.0F;
	    float x1 = x + 1.0F;
	    float y0 = y + 0.0F;
	    float y1 = y + 1.0F;
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
	    	GL11.glTexCoord2f(1, 1);
	    	GL11.glVertex3f(x1, y0, z1);
	    	GL11.glTexCoord2f(0, 1);
	    	GL11.glVertex3f(x0, y0, z1);
	    	break;
	    case Back:
	    	GL11.glTexCoord2f(1, 0);
	    	GL11.glVertex3f(x0, y1, z0);
	    	GL11.glTexCoord2f(1, 1);
	    	GL11.glVertex3f(x0, y0, z0);
	    	GL11.glTexCoord2f(0, 1);
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
	    	GL11.glTexCoord2f(1, 1);
	    	GL11.glVertex3f(x0, y0, z1);
	    	GL11.glTexCoord2f(0, 1);
	    	GL11.glVertex3f(x0, y0, z0);
	    	break;
	    case Right:
	    	GL11.glTexCoord2f(0, 0);
	    	GL11.glVertex3f(x1, y1, z1);
	    	GL11.glTexCoord2f(1, 0);
	    	GL11.glVertex3f(x1, y1, z0);
	    	GL11.glTexCoord2f(1, 1);
	    	GL11.glVertex3f(x1, y0, z0);
	    	GL11.glTexCoord2f(0, 1);
	    	GL11.glVertex3f(x1, y0, z1);
	    	break;
	    }
	    GL11.glEnd();
	}
	
	public static void renderFaceNoTexture(int x, int y, int z, Face face) {
		float x0 = x + 0.0F;
	    float x1 = x + 1.0F;
	    float y0 = y + 0.0F;
	    float y1 = y + 1.0F;
	    float z0 = z + 0.0F;
	    float z1 = z + 1.0F;
	    GL11.glBegin(GL11.GL_QUADS);
	    switch(face) {
	    case Front:
	    	GL11.glTexCoord2f(0, 0);
	    	GL11.glVertex3f(x0, y1, z1);
	    	GL11.glTexCoord2f(1, 0);
	    	GL11.glVertex3f(x1, y1, z1);
	    	GL11.glTexCoord2f(1, 1);
	    	GL11.glVertex3f(x1, y0, z1);
	    	GL11.glTexCoord2f(0, 1);
	    	GL11.glVertex3f(x0, y0, z1);
	    	break;
	    case Back:
	    	GL11.glTexCoord2f(1, 0);
	    	GL11.glVertex3f(x0, y1, z0);
	    	GL11.glTexCoord2f(1, 1);
	    	GL11.glVertex3f(x0, y0, z0);
	    	GL11.glTexCoord2f(0, 1);
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
	    	GL11.glTexCoord2f(1, 1);
	    	GL11.glVertex3f(x0, y0, z1);
	    	GL11.glTexCoord2f(0, 1);
	    	GL11.glVertex3f(x0, y0, z0);
	    	break;
	    case Right:
	    	GL11.glTexCoord2f(0, 0);
	    	GL11.glVertex3f(x1, y1, z1);
	    	GL11.glTexCoord2f(1, 0);
	    	GL11.glVertex3f(x1, y1, z0);
	    	GL11.glTexCoord2f(1, 1);
	    	GL11.glVertex3f(x1, y0, z0);
	    	GL11.glTexCoord2f(0, 1);
	    	GL11.glVertex3f(x1, y0, z1);
	    	break;
	    }
	    GL11.glEnd();
	}
	
	public static void renderLine(int x, int y, int z) {
		float x0 = x + 0.0F;
		float x1 = x + 1.0F;
		float y0 = y + 0.0F;
		float y1 = y + 1.0F;
		float z0 = z + 0.0F;
		float z1 = z + 1.0F;
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(x0, y1, z1);
		GL11.glVertex3f(x1, y1, z1);
		GL11.glVertex3f(x1, y0, z1);
		GL11.glVertex3f(x0, y0, z1);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(x0, y1, z0);
		GL11.glVertex3f(x0, y0, z0);
		GL11.glVertex3f(x1, y0, z0);
		GL11.glVertex3f(x1, y1, z0);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(x1, y1, z0);
		GL11.glVertex3f(x1, y1, z1);
		GL11.glVertex3f(x0, y1, z1);
		GL11.glVertex3f(x0, y1, z0);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(x0, y0, z0);
		GL11.glVertex3f(x0, y0, z1);
		GL11.glVertex3f(x1, y0, z1);
		GL11.glVertex3f(x1, y0, z0);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(x0, y1, z0);
		GL11.glVertex3f(x0, y1, z1);
		GL11.glVertex3f(x0, y0, z1);
		GL11.glVertex3f(x0, y0, z0);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(x1, y1, z1);
		GL11.glVertex3f(x1, y1, z0);
		GL11.glVertex3f(x1, y0, z0);
		GL11.glVertex3f(x1, y0, z1);
		GL11.glEnd();
	}
	
	public static AABB getAABB(int x, int y, int z) {
		return new AABB(x, y, z, x + 1, y + 1, z + 1);
	}
	
	public void tick(Level level, int x, int y, int z) {
		
	}
}
