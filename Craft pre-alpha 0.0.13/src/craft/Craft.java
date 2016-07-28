package craft;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import craft.Player.PlayerMode;
import craft.environment.Clouds;
import craft.environment.Sky;
import craft.gui.GuiRenderer;
import craft.gui.RGBAColor;
import craft.item.PickupEngine;
import craft.level.Level;
import craft.level.LevelIO;
import craft.level.LevelRenderer;
import craft.level.RenderChunk;
import craft.level.tile.Face;
import craft.level.tile.Tile;
import craft.level.tile.tileEntity.CalmLiquidTile;
import craft.level.tile.tileEntity.LiquidTile;
import craft.particle.ParticleEngine;
import craft.phys.AABB;
import craft.renderer.Frustum;
import craft.renderer.Texture;

public class Craft implements Runnable {
	private static final String VERSION = "0.0.13";
	private static final String FILENAME = "save\\level.dat";
	private static final float FOV = 70.0F;
	private static boolean FullScreen = false;
	private int width, height;
	private Timer timer = new Timer(30);
	//private Tick tick = new Tick(this, 30);
	private Level level = new Level();
	private LevelIO levelIO = new LevelIO();
	private LevelRenderer levelRenderer;
	private Player player;
	private ParticleEngine particleEngine;
	private PickupEngine pickupEngine;
	private GuiRenderer guiRenderer;
	private Selector selector;
	private Clouds clouds;
	private Sky sky;
	private long lastClickTime = 0L;
	private int tickCount = 0;
	private int lastFps = 0;
	private int lastTickCount = 0;

	private FloatBuffer fogColor0 = BufferUtils.createFloatBuffer(4);

	private IntBuffer viewportBuffer = BufferUtils.createIntBuffer(16);
	private IntBuffer selectBuffer = BufferUtils.createIntBuffer(2000);
	private HitResult hitResult = null;

	private void init() throws LWJGLException {
		this.fogColor0.put(new float[] { 0.5F, 0.8F, 1.0F, 1.0F });
		this.fogColor0.flip();

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
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.01F);
		GL11.glCullFace(GL11.GL_BACK);
		Texture.loadTexture();
		Texture.loadGuiTexture();
		reSizeGLScene();

		checkGlError("Startup");

		selector = new Selector();
		guiRenderer = new GuiRenderer(width, height, selector);

		if (!levelIO.load(FILENAME, level, selector)) {
			level.createNewMap(512, 512, 128);
			selector.addTile(Tile.sand);
			selector.addTile(Tile.water);
			selector.addTile(Tile.lava);
		}
		levelRenderer = new LevelRenderer(level);
		particleEngine = new ParticleEngine(level);
		pickupEngine = new PickupEngine(level);
		player = new Player(level);
		clouds = new Clouds(level.length, level.width, 20, 0.3F, 2, 0.5F);
		sky = new Sky(level.length + level.width);
		
		/*for (int i = 0; i < 10; i++) {
			Human human = new Human(level, 0.0F, 0.0F, 0.0F);
			human.resetPos();
			entityEngine.add(human);
		}*/

		Mouse.setGrabbed(true);
		checkGlError("Post startup");
	}

	public void setDisplayMode(boolean FullScreen) throws LWJGLException {
		if (FullScreen) {
			Display.setDisplayMode(Display.getDesktopDisplayMode());
			Display.setFullscreen(true);
		} else {
			Display.setFullscreen(false);
			Display.setDisplayMode(new DisplayMode(1000, 600));
			Display.setLocation(100, 100);
			Display.setResizable(true);
		}
		if (Display.isCreated())
			reSizeGLScene();
	}

	public void run() {
		System.out.println("Craft " + VERSION + " start running...");
		try {
			init();
		} catch (Exception e) {
			System.out.println("Craft " + VERSION + "Æô¶¯´íÎó£º" + e.toString());
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "CraftÆô¶¯´íÎó£º" + e.toString(), "Failed to start Craft", JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
		long lastTime = System.currentTimeMillis();
		int fps = 0;
		try {
			while (!Display.isCloseRequested()) {
				if (Display.wasResized())
					reSizeGLScene();
				
				timer.advanceTime();
				for (int i = 0; i < timer.ticks; i++) {
					tick();
					tickCount++;
				}
				inputTick(false);
				checkGlError("Pre render");
		        render(timer.a);
		        checkGlError("Post render");
		        
				Display.setTitle("Craft " + VERSION + "  Player:X=" + Math.floor(player.x) + "  Y=" + Math.floor(player.y) + "  Z=" + Math.floor(player.z));
				fps++;
				while (System.currentTimeMillis() >= lastTime + 1000L) {
					System.out.println(fps + " fps, " + RenderChunk.updates + " Chunks updated, totalUpdates:" + RenderChunk.totalUpdates + ", tickCount:" + tickCount);
					lastTickCount = tickCount;
					tickCount = 0;
					lastTime += 1000L;
					lastFps = fps;
					fps = 0;
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

	private void reSizeGLScene() {
		width = Display.getWidth();
		height = Display.getHeight();
		GL11.glViewport(0, 0, width, height);
		if(guiRenderer != null)
			guiRenderer.reshape(width, height);
	}

	protected void tick() {
		inputTick(true);
		player.tick();
		level.updateChunkState(player);
		level.tick(player);
		particleEngine.tick();
		level.entityList.tickAll();
		level.tileEntityList.updateAll();
		pickupEngine.tick(player, selector);
		clouds.tick();
		//level.update(player);
	}

	private void inputTick(boolean inTick) {
		if (inTick) {
			int waitTime = (player.mode == PlayerMode.Normal) ? 25 : 300;
			if ((Mouse.isButtonDown(0)) && hitResult != null && (System.currentTimeMillis() - lastClickTime > waitTime)) {
				lastClickTime = System.currentTimeMillis();
				Tile oldTile = Tile.tiles[(byte) level.getBlock(hitResult.x, hitResult.y, hitResult.z)];
				if (oldTile != Tile.air) {
					/**½«¾²Ì¬ÒºÌå·½¿é±äÎªÆÕÍ¨ÒºÌå·½¿é*/
					if (oldTile instanceof CalmLiquidTile)
						oldTile = Tile.tiles[oldTile.id - 1];
					player.destroyTile(oldTile, hitResult.x, hitResult.y, hitResult.z, particleEngine, pickupEngine);
					/**»ñÈ¡·½¿éµÄÊ°È¡·½¿é*/
					//selector.addTile(oldTile.getDroppedTile());
				}
			}
			while (Mouse.next() && this.hitResult != null) {
				if ((player.mode == PlayerMode.Flying) && (Mouse.getEventButton() == 0) && (Mouse.getEventButtonState())) {
					Tile oldTile = Tile.tiles[(byte) level.getBlock(hitResult.x, hitResult.y, hitResult.z)];
					if (oldTile != Tile.air) {
						if (oldTile instanceof CalmLiquidTile)
							oldTile = Tile.tiles[oldTile.id - 1];
						player.destroyTile(oldTile, hitResult.x, hitResult.y, hitResult.z, particleEngine, pickupEngine);
					}
					lastClickTime = System.currentTimeMillis();
				}
				if ((Mouse.getEventButton() == 0) && (!Mouse.getEventButtonState()))
					player.cancelDestroy();
				if ((Mouse.getEventButton() == 1) && (Mouse.getEventButtonState())) {
					int x = hitResult.x;
					int y = hitResult.y;
					int z = hitResult.z;

					if (hitResult.face == Face.Bottom)
						y--;
					if (hitResult.face == Face.Top)
						y++;
					if (hitResult.face == Face.Back)
						z--;
					if (hitResult.face == Face.Front)
						z++;
					if (hitResult.face == Face.Left)
						x--;
					if (hitResult.face == Face.Right)
						x++;

					Tile selectedTile = selector.getSelectTile();
					if (selectedTile != null)
						if (isFree(selectedTile.getAABB(x, y, z)))
							if (!(selectedTile instanceof LiquidTile && y > 60)) {
								level.setBlock(x, y, z, selectedTile.id);
								selector.removeOneTile();
							}
				}
			}
			int d = Mouse.getDWheel();
			if (d < 0) selector.forward();
			if (d > 0) selector.backward();
		} else {
			if (Mouse.isGrabbed()) {
				float xo = Mouse.getDX();
				float yo = Mouse.getDY();
				player.turn(xo, yo);
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && FullScreen)
				System.exit(0);
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) || !Display.isActive())
				Mouse.setGrabbed(false);
			if (!Mouse.isGrabbed() && (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) && Mouse.isInsideWindow())
				Mouse.setGrabbed(true);

			while (Keyboard.next()) {
				if (Keyboard.getEventKey() == Keyboard.KEY_F && Keyboard.getEventKeyState())
					if (player.mode == PlayerMode.Normal)
						player.mode = PlayerMode.Flying;
					else
						player.mode = PlayerMode.Normal;
				if (Keyboard.getEventKey() == Keyboard.KEY_F2 && Keyboard.getEventKeyState()) {
					FullScreen = !FullScreen;
					try {
						setDisplayMode(FullScreen);
					} catch (LWJGLException e) {
						JOptionPane.showMessageDialog(null, "Craft´íÎó£ºÆÁÄ»ÇÐ»»Ê§°Ü£¡" + e.toString(), "Error!", JOptionPane.WARNING_MESSAGE);
						e.printStackTrace();
					}
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_R && Keyboard.getEventKeyState())
					player.resetPos();
				if (Keyboard.getEventKey() == Keyboard.KEY_F1 && Keyboard.getEventKeyState())
					levelIO.save(FILENAME, level, selector);
				if (Keyboard.getEventKey() == Keyboard.KEY_F10 && Keyboard.getEventKeyState())
					try {
						takeScreenShots("ScreenShots\\" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".png");
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, "±£´æÆÁÄ»½ØÍ¼Ê§°Ü£¡" + e.toString(), "Error!", JOptionPane.WARNING_MESSAGE);
						e.printStackTrace();
					}
			}
		}
	}

	private void render(float a) {
		// GL¸üÐÂ
		pick(a);
		checkGlError("Picked");
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		setupCamera(a);
		checkGlError("Set up camera");

		Texture.bind(1);
		Frustum frustum = Frustum.getFrustum();
		levelRenderer.cull(player, frustum);
		
		levelRenderer.updateDirtyChunks(player);
		checkGlError("Update chunks");
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		setupFog(0);
		GL11.glEnable(GL11.GL_FOG);
		levelRenderer.render(player, 0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		levelRenderer.render(player, 1);
		GL11.glDisable(GL11.GL_BLEND);
		checkGlError("Rendered level");

		particleEngine.render(player, a);
		checkGlError("Rendered particles");
		
		pickupEngine.render(player, a);
		checkGlError("Rendered droppedItems");

		level.entityList.render(player, frustum, a);
		checkGlError("Rendered entities");
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_FOG);
		
		sky.render();
		checkGlError("Rendered sky");
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		clouds.render();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
		checkGlError("Rendered clouds");
		
		GL11.glDisable(GL11.GL_LIGHTING);

		if (hitResult != null && !(Tile.tiles[level.getBlock(hitResult.x, hitResult.y, hitResult.z)] instanceof LiquidTile)) {
			levelRenderer.renderHit(hitResult);
			if (Mouse.isButtonDown(0))
				levelRenderer.renderCrack(level, player);
		}

		setupOrthoCamera();
		guiRenderer.drawGui(a);
		guiRenderer.font.clean();
		guiRenderer.font.printIn("Craft " + VERSION, RGBAColor.white, 0);
		guiRenderer.font.printIn("FullScreen:" + FullScreen, RGBAColor.white, 0);
		guiRenderer.font.printIn(RenderChunk.updates + " Chunks updated, totalUpdates:" + RenderChunk.totalUpdates + ", tickCount:" + lastTickCount, RGBAColor.white, 0);
		guiRenderer.font.printIn("Player:X=" + Math.floor(player.x) + "  Y=" + Math.floor(player.y) + "  Z=" + Math.floor(player.z), RGBAColor.white, 0);
		guiRenderer.font.printIn("Player:RotX=" + Math.floor(player.xRot) + "  RotY=" + Math.floor(player.yRot) + "  Face:" + player.getYRotFace(), RGBAColor.white, 0);
		guiRenderer.font.printIn("Mode:" + player.mode.name(), RGBAColor.white, 0);
		guiRenderer.font.printIn("Entities Count:" + level.entityList.getEntitiesCount(), RGBAColor.white, 0);
		guiRenderer.font.printIn("Tile Entities Count:" + level.tileEntityList.getTileEntitiesCount(), RGBAColor.white, 0);
		guiRenderer.font.printIn("Memory Used:" + new DecimalFormat("####.00").format((Runtime.getRuntime().totalMemory()) / 1000000.0) + "MB", RGBAColor.white, 0);
		guiRenderer.font.printIn("Fps:" + lastFps, RGBAColor.white, 0);
		guiRenderer.font.printIn("BR:" + level.getBrightness((int)Math.floor(player.x), (int)Math.floor(player.y), (int)Math.floor(player.z)), new RGBAColor(1.0F, 0, 0, 1.0F), 0);
		checkGlError("Rendered Gui");
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
		GLU.gluPerspective(FOV, (float) width / height, 0.1F, 2200.0F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		moveCameraToPlayer(a);
	}

	private void setupOrthoCamera() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, height, 0, -1.0, 1.0);
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
		GLU.gluPerspective(FOV, (float) width / height, 0.1F, 1000.0F);
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

	private void setupFog(int i) {
		Tile tile = Tile.tiles[level.getBlock((int) Math.floor(player.x), (int) Math.floor(player.y), (int) Math.floor(player.z))];
		float b = level.globalBrightness;
		GL11.glClearColor(0.5F * b, 0.8F * b, 1.0F * b, 1.0F);
		if (tile instanceof LiquidTile) {
			if (((LiquidTile) tile).getFog()) {
				GL11.glFogi(GL11.GL_FOG_INDEX, 0);
				GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_VIEWPORT_BIT);
				GL11.glFogf(GL11.GL_FOG_DENSITY, 0.1F);
				GL11.glFog(GL11.GL_FOG_COLOR, getBuffer(0.02F, 0.02F, 0.2F, 1.0F));
				GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, getBuffer(0.3F, 0.3F, 0.7F, 1.0F));
			} else {
				GL11.glFogi(GL11.GL_FOG_INDEX, 0);
				GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_VIEWPORT_BIT);
				GL11.glFogf(GL11.GL_FOG_DENSITY, 2.0F);
				GL11.glFog(GL11.GL_FOG_COLOR, getBuffer(0.6F, 0.1F, 0.0F, 1.0F));
				GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, getBuffer(0.4F, 0.3F, 0.3F, 1.0F));
			}
		} else if (i == 0) {
			GL11.glFogi(GL11.GL_FOG_INDEX, 0);
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_VIEWPORT_BIT);
			GL11.glFogf(GL11.GL_FOG_DENSITY, 0.001F);
			GL11.glFog(GL11.GL_FOG_COLOR, fogColor0);
			GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, getBuffer(b, b, b, 1.0F));
			/*GL11.glFogi(GL11.GL_FOG_INDEX, 1);
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
			GL11.glFogf(GL11.GL_FOG_START, 110.0F);
			GL11.glFogf(GL11.GL_FOG_END, 140.0F);
			GL11.glFog(GL11.GL_FOG_COLOR, fogColor0);*/
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
		if (aabb == null) return true;
		if (player.aabb.intersects(aabb))
			return false;
		for (AABB b : level.entityList.getEntitiesAABB(aabb)) {
			if (b.intersects(aabb))
				return false;
		}
		return true;
	}

	public void takeScreenShots(String Filename) throws IOException {
		GL11.glReadBuffer(GL11.GL_FRONT);
		int bpp = 4;
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

		File file = new File(Filename); // The file to save to.
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		String format = "PNG"; // Example: "PNG" or "JPG"
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				int i = (x + (width * y)) * bpp;
				int r = buffer.get(i) & 0xFF;
				int g = buffer.get(i + 1) & 0xFF;
				int b = buffer.get(i + 2) & 0xFF;
				image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
			}
		ImageIO.write(image, format, file);
	}

	private void checkGlError(String string) {
		int errorCode = GL11.glGetError();
		if (errorCode != 0) {
			String errorString = GLU.gluErrorString(errorCode);
			System.out.println("########## GL ERROR ##########");
			System.out.println("@ " + string);
			System.out.println(errorCode + ": " + errorString);
			System.exit(0);
		}
	}
	
	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++)
			if (args[i].equalsIgnoreCase("-fullscreen"))
				FullScreen = true;
		
		Thread craft = new Thread(new Craft());
		craft.setName("Craft-Main");
		craft.start();
	}

}
