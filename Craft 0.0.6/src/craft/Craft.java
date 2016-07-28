package craft;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.swing.JOptionPane;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import craft.gui.GuiRenderer;
import craft.level.Chunk;
import craft.level.Level;
import craft.level.LevelRenderer;
import craft.level.tile.EntityTile;
import craft.level.tile.Face;
import craft.level.tile.LiquidTile;
import craft.level.tile.Tile;
import craft.particle.ParticleEngine;
import craft.phys.AABB;
import craft.renderer.Frustum;
import craft.renderer.Texture;

public class Craft implements Runnable{
	private static boolean FullScreen = false;
	private static final String VERSION = "0.0.6";
	private static final String FILENAME = "save\\level.dat";
	private static final float CROSSHAIR_LENGTH = 10.0F;
	private int width,height;
	private Timer timer = new Timer(60.0F);
	private Level level;
	private LevelRenderer levelRenderer;
	private Player player;
	private ParticleEngine particleEngine;
	private GuiRenderer guiRenderer;
	private Selector selector;
	private int crosshairList = 0;
	private long lastClickTime = 0L;

	private FloatBuffer fogColor0 = BufferUtils.createFloatBuffer(4);
	private FloatBuffer fogColor1 = BufferUtils.createFloatBuffer(4);

	private IntBuffer viewportBuffer = BufferUtils.createIntBuffer(16);
	private IntBuffer selectBuffer = BufferUtils.createIntBuffer(2000);
	private HitResult hitResult = null;

	public void init() throws LWJGLException {
		int col = 920330;
		this.fogColor0.put(new float[] { 0.5F, 0.8F, 1.0F, 1.0F });
		this.fogColor0.flip();
		this.fogColor1.put(new float[] { (col >> 16 & 0xFF) / 255.0F, (col >> 8 & 0xFF) / 255.0F, (col & 0xFF) / 255.0F, 1.0F });
		this.fogColor1.flip();

		Display.setTitle("Craft " + VERSION);
		setDisplayMode(FullScreen);

		Display.create();
		Keyboard.create();
		Mouse.create();

		width = Display.getWidth();
		height = Display.getHeight();

		GL11.glClearColor(0.5F, 0.8F, 1.0F, 1.0F);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glClearDepth(1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_FRONT);
		GL11.glFrontFace(GL11.GL_CCW);
		Texture.loadTexture();
		Texture.loadGuiTexture();
		reSizeGLScene();

		crosshairList = drawCrosshair();

		level = new Level(256, 256, 256, FILENAME);
		levelRenderer = new LevelRenderer(level);
		particleEngine = new ParticleEngine(level);
		selector = new Selector();
		guiRenderer = new GuiRenderer(width, height, selector);
		player = new Player(level);
		
		selector.addTile(Tile.sand);
		selector.addTile(Tile.water);
		
		Mouse.setGrabbed(true);
	}

	public void setDisplayMode(boolean FullScreen) throws LWJGLException {
		if(FullScreen) {
			/*DisplayMode targetDisplayMode = null;
			DisplayMode[] modes = Display.getAvailableDisplayModes();
			for (int i=0;i<modes.length;i++) {
				DisplayMode current = modes[i];
				if ((current.getWidth() == Display.getDesktopDisplayMode().getWidth()) && (current.getHeight() == Display.getDesktopDisplayMode().getHeight())) {
					if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) && (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
						targetDisplayMode = current;
						break;
					}
				}
			}
			if(targetDisplayMode == null) {
				throw new LWJGLException("Failed to find value mode: "+width+"x"+height+" fs="+FullScreen);
			}*/
			Display.setDisplayMode(Display.getDesktopDisplayMode());
			Display.setFullscreen(true);
		} else {
			Display.setFullscreen(false);
			Display.setDisplayMode(new DisplayMode(1000,600));
			Display.setLocation(100, 100);
			Display.setResizable(true);
		}
		if(Display.isCreated())
			reSizeGLScene();
	}

	public void run() {
		System.out.println("Æô¶¯¡£¡£¡£");
		try {
			init();
		} catch (Exception e) {
			System.out.println("Craft " + VERSION + "Æô¶¯´íÎó£º" + e.toString());
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "CraftÆô¶¯´íÎó£º" + e.toString(), "Failed to start Craft", JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
		long lastTime = System.currentTimeMillis();
		int fps=0;
		try {
			while (!Display.isCloseRequested()) {
				if(Display.wasResized()) reSizeGLScene();
				timer.advanceTime();
				for(int i = 0; i < timer.ticks; i++) {
					tick();
				}
				Display.setTitle("Craft " + VERSION + "  Player:X=" + Math.floor(player.x) + "  Y=" + Math.floor(player.y) + "  Z=" + Math.floor(player.z));
				render(timer.a);
				fps++;
				while (System.currentTimeMillis() >= lastTime + 1000L)
				{
					System.out.println(fps + " fps, " + Chunk.updates + " Chunks updated, totalUpdates:" + Chunk.totalUpdates);
					lastTime+=1000L;
					fps=0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "CraftÔËÐÐ´íÎó£º" + e.toString(), "Runtime Error!", JOptionPane.WARNING_MESSAGE);
		} finally {
			destory();
		}
	}

	private void destory() {
		Display.destroy();
		Keyboard.destroy();
		Mouse.destroy();
	}

	public void reSizeGLScene() {
		width=Display.getWidth();
		height=Display.getHeight();
		GL11.glViewport(0, 0, width, height);
	}

	public void tick() {
		inputTick();
		player.tick();
		level.tick();
		particleEngine.tick();
		EntityTile.ticks(level, player);
	}

	public void inputTick() {
		if(Mouse.isGrabbed()) {
			float xo = Mouse.getDX();
			float yo = Mouse.getDY();
			player.turn(xo, yo);
		}
		if (player.mode == PlayerMode.Normal) {
			if ((Mouse.isButtonDown(0)) && hitResult != null && (System.currentTimeMillis() - lastClickTime > 25)) {
				lastClickTime = System.currentTimeMillis();
				Tile oldTile = Tile.tiles[(byte) level.getBlock(hitResult.x, hitResult.y, hitResult.z)];
				if (oldTile != Tile.air) {
					if (oldTile.destroy(level, hitResult.x, hitResult.y, hitResult.z, particleEngine, true))
						selector.addTile(oldTile);
				}
			}
		} else {
			if ((Mouse.isButtonDown(0)) && hitResult != null && (System.currentTimeMillis() - lastClickTime > 300)) {
				lastClickTime = System.currentTimeMillis();
				Tile oldTile = Tile.tiles[(byte) level.getBlock(hitResult.x, hitResult.y, hitResult.z)];
				if (oldTile != Tile.air) {
					if (oldTile.destroy(level, hitResult.x, hitResult.y, hitResult.z, particleEngine, false))
						selector.addTile(oldTile);
				}
			}
		}
		while (Mouse.next() && this.hitResult != null) {
			if ((player.mode == PlayerMode.Flying) && (Mouse.getEventButton() == 0) && (Mouse.getEventButtonState())) {
				Tile oldTile = Tile.tiles[(byte) level.getBlock(hitResult.x, hitResult.y, hitResult.z)];
				if (oldTile != Tile.air) {
					oldTile.destroy(level, hitResult.x, hitResult.y, hitResult.z, particleEngine, false);
					selector.addTile(oldTile);
				}
				lastClickTime = System.currentTimeMillis();
			}
			if ((Mouse.getEventButton() == 0) && (!Mouse.getEventButtonState()))
				Tile.cancelDestroy();
			if ((Mouse.getEventButton() == 1) && (Mouse.getEventButtonState())) {
				int x = hitResult.x;
				int y = hitResult.y;
				int z = hitResult.z;

				if (hitResult.face == Face.Bottom) 	y--;
				if (hitResult.face == Face.Top) 			y++;
				if (hitResult.face == Face.Back) 		z--;
				if (hitResult.face == Face.Front) 		z++;
				if (hitResult.face == Face.Left) 			x--;
				if (hitResult.face == Face.Right) 		x++;
				
				if(isFree(Tile.getAABB(x, y, z)) && selector.getTileCount(selector.getPoint()) > 0) {
					level.setBlock(x, y, z, selector.getSelectTile().id);
					if(selector.getSelectTile() instanceof EntityTile)
						Tile.sand.add(x, y, z);
					selector.removeOneTile();
					//level.setBlock(x, y, z, Tile.water.id);
				}
			}
		}
		int d = Mouse.getDWheel();
		if(d < 0)
			selector.forward();
		if(d > 0)
			selector.backward();
		
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && FullScreen)
			System.exit(0);
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) || !Display.isActive())
			Mouse.setGrabbed(false);
		if(!Mouse.isGrabbed() && (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) && Mouse.isInsideWindow())
			Mouse.setGrabbed(true);

		while (Keyboard.next()) {
			if(Keyboard.getEventKey() == Keyboard.KEY_F  && Keyboard.getEventKeyState())
				if(player.mode == PlayerMode.Normal)
					player.mode = PlayerMode.Flying;
				else
					player.mode = PlayerMode.Normal;
			if(Keyboard.getEventKey() == Keyboard.KEY_F2  && Keyboard.getEventKeyState()) {
				FullScreen = !FullScreen;
				try {
					setDisplayMode(FullScreen);
				} catch (LWJGLException e) {
					JOptionPane.showMessageDialog(null, "Craft´íÎó£ºÆÁÄ»ÇÐ»»Ê§°Ü£¡" + e.toString(), "Error!", JOptionPane.WARNING_MESSAGE);
					e.printStackTrace();
				}
			}
			if(Keyboard.getEventKey() == Keyboard.KEY_R  && Keyboard.getEventKeyState())
				player.resetPos();
			if(Keyboard.getEventKey() == Keyboard.KEY_F1  && Keyboard.getEventKeyState())
				level.save(FILENAME);
		}
	}

	public void render(float a) {
		//GL¸üÐÂ
		pick(a);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);	

		setupCamera(a);
		levelRenderer.cull(Frustum.getFrustum());
		levelRenderer.updateDirtyChunks(player);
		setupFog(0);
		GL11.glEnable(GL11.GL_FOG);
		levelRenderer.render(player, 0);
		levelRenderer.render(player, 1);
		particleEngine.render(player, a);
		EntityTile.renderEntityTiles(level, Frustum.getFrustum());
		GL11.glDisable(GL11.GL_FOG);
		GL11.glDisable(GL11.GL_LIGHTING);

		if (hitResult != null && !(Tile.tiles[level.getBlock(hitResult.x, hitResult.y, hitResult.z)] instanceof LiquidTile)) {
			levelRenderer.renderHit(hitResult);
			if(Mouse.isButtonDown(0))
				Tile.tiles[level.getBlock(hitResult.x, hitResult.y, hitResult.z)].renderCrack(level);
		}
		
		setupOrthoCamera();
		GL11.glTranslatef(width / 2, height / 2, -1.0F);
		GL11.glCallList(crosshairList);
		guiRenderer.drawGui(a);
		//guiRenderer.font.print("ABCDefghijklmnOPQrstUvwxYZ", 50, 20);
		Display.update();
	}

	private void moveCameraToPlayer(float a) {
		GL11.glTranslatef(0.0F, 0.0F, -0.15F);
		GL11.glRotatef(this.player.xRot, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(this.player.yRot, 0.0F, 1.0F, 0.0F);

		float x = player.xo + (player.x - player.xo) * a;
		float y = player.yo + (player.y - player.yo) * a;
		float z = player.zo + (player.z - player.zo) * a;
		GL11.glTranslatef(-x, -y, -z);
	}

	private void setupCamera(float a) {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(70.0F, (float)width/height, 0.1F, 1000.0F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		moveCameraToPlayer(a);
	}

	private void setupOrthoCamera() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, height, 0, 1.0, 100.0);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
	}

	private void setupPickCamera(float a, int x, int y) {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		this.viewportBuffer.clear();
		GL11.glGetInteger(GL11.GL_VIEWPORT, this.viewportBuffer);
		this.viewportBuffer.flip();
		this.viewportBuffer.limit(16);
		GLU.gluPickMatrix(x, y, 5.0F, 5.0F, this.viewportBuffer);
		GLU.gluPerspective(70.0F, (float) width / height, 0.1F, 1000.0F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		moveCameraToPlayer(a);
	}

	private void pick(float a) {
		this.selectBuffer.clear();
		GL11.glSelectBuffer(this.selectBuffer);
		GL11.glRenderMode(GL11.GL_SELECT);
		setupPickCamera(a, this.width / 2, this.height / 2);
		this.levelRenderer.pick(this.player);
		int hits = GL11.glRenderMode(GL11.GL_RENDER);
		this.selectBuffer.flip();
		this.selectBuffer.limit(this.selectBuffer.capacity());

		long closest = 0L;
		int[] names = new int[10];
		int hitNameCount = 0;
		for (int i = 0; i < hits; i++) {
			int nameCount = this.selectBuffer.get();
			long minZ = this.selectBuffer.get();
			this.selectBuffer.get();

			long dist = minZ;

			if ((dist < closest) || (i == 0)) {
				closest = dist;
				hitNameCount = nameCount;
				for (int j = 0; j < nameCount; j++)
					names[j] = this.selectBuffer.get();
			} else {
				for (int j = 0; j < nameCount; j++) {
					this.selectBuffer.get();
				}
			}
		}
		if (hitNameCount > 0) {
			this.hitResult = new HitResult(names[0], names[1], names[2], names[3], Face.getFace(names[4]));
		} else {
			this.hitResult = null;
		}
	}

	private int drawCrosshair() {
		int list = GL11.glGenLists(1);
		GL11.glNewList(list, GL11.GL_COMPILE);
		GL11.glLineWidth(1.125F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);		
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3f(CROSSHAIR_LENGTH, 0.0F, 0.0F);
		GL11.glVertex3f(-CROSSHAIR_LENGTH, 0.0F, 0.0F);
		GL11.glVertex3f(0.0F, CROSSHAIR_LENGTH, 0.0F);
		GL11.glVertex3f(0.0F, -CROSSHAIR_LENGTH, 0.0F);
		GL11.glEnd();
		GL11.glEndList();
		return list;
	}

	private void setupFog(int i) {
		if (i == 0) {
			GL11.glFogi(GL11.GL_FOG_INDEX, 0);
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_VIEWPORT_BIT);
			GL11.glFogf(GL11.GL_FOG_DENSITY, 0.001F);
			GL11.glFog(GL11.GL_FOG_COLOR, fogColor0);
			GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, getBuffer(1.0F, 1.0F, 1.0F, 1.0F));
		}
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
	
	private FloatBuffer getBuffer(float a, float b, float c, float d) {
		FloatBuffer lb = BufferUtils.createFloatBuffer(16);
	    lb.clear();
	    lb.put(a).put(b).put(c).put(d);
	    lb.flip();
	    return lb;
	  }
	
	private boolean isFree(AABB aabb) {
		if(player.aabb.intersects(aabb))
			return false;
		for(AABB b:EntityTile.getTilesAABB()) {
			if(b.intersects(aabb))
				return false;
		}
		return true;
	}

	public static void main(String[] args) {
		new Thread(new Craft()).start();
	}

}
