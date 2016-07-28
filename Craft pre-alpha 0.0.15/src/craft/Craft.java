package craft;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import craft.Player.PlayerMode;
import craft.environment.Clouds;
import craft.environment.Sky;
import craft.gui.DebugInfo;
import craft.gui.DisplayInfo;
import craft.gui.GuiRenderer;
import craft.item.PickupEngine;
import craft.phys.AABB;
import craft.renderer.Frustum;
import craft.renderer.Texture;
import craft.setting.DisplayOption;
import craft.setting.GameSetting;
import craft.world.RenderChunk;
import craft.world.World;
import craft.world.WorldIO;
import craft.world.WorldRenderer;
import craft.world.tile.CalmLiquidTile;
import craft.world.tile.Face;
import craft.world.tile.LiquidTile;
import craft.world.tile.Tile;

public class Craft implements Runnable {
	public static final String VERSION = "0.0.15";
	public static final String FILENAME = "save\\world.dat";
	private Timer timer = new Timer(30);
	private GameSetting gameSetting = GameSetting.instance;
	//private Tick tick = new Tick(this, 30);
	public World world = new World();
	private WorldIO worldIO = new WorldIO();
	private WorldRenderer worldRenderer;
	private Camera camera;
	public Player player;
	private PickupEngine pickupEngine;
	private GuiRenderer guiRenderer;
	private Selector selector;
	private Clouds clouds;
	private Sky sky;
	private DebugInfo debugInfo;
	private int tickCount = 0;
	public int lastFps = 0;
	public int lastTickCount = 0;
	private long lastClickTime = 0L;

	private FloatBuffer fogColor0 = BufferUtils.createFloatBuffer(4);
	private FloatBuffer fogColor1 = BufferUtils.createFloatBuffer(4);

	private IntBuffer selectBuffer = BufferUtils.createIntBuffer(2000);
	public HitResult hitResult = null;
	
	public static Craft Instance;

	private void init() throws LWJGLException {
		fogColor0.put(new float[] { 0.76F, 0.84F, 0.89F, 1.0F });
		fogColor0.flip();
		fogColor1.put(new float[] { 0.9215F, 0.9215F, 0.9215F, 1.0F });
		fogColor1.flip();

		Display.setTitle("Craft " + VERSION);
		setDisplayMode(gameSetting.getBoolean(DisplayOption.FullScreen));

		Display.create();
		Keyboard.create();
		Mouse.create();

		GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glClearDepth(1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.01F);
		GL11.glCullFace(GL11.GL_BACK);
		Texture.loadTexture();
		Texture.loadGuiTexture();
		DisplayInfo.reSizeGLScene();

		checkGlError("Startup");

		selector = new Selector();
		guiRenderer = new GuiRenderer(selector);
		debugInfo = new DebugInfo(this, guiRenderer);

		if (!worldIO.load(FILENAME, world, selector)) {
			world.createNewMap(1024, 1024, 128);
			selector.addTile(Tile.sand, 10);
			selector.addTile(Tile.water);
			selector.addTile(Tile.lava);
			selector.addTile(Tile.goldOre);
		}
		worldRenderer = new WorldRenderer(world);
		pickupEngine = new PickupEngine(world);
		player = new Player(world);
		clouds = new Clouds(world.length, world.width, 20, 0.3F, 2, 0.5F);
		sky = new Sky((world.length + world.width + world.height) * 2);
		
		camera = new Camera(player);
		
		/*for (int i = 0; i < 10; i++) {
			Human human = new Human(world, 0.0F, 0.0F, 0.0F);
			human.resetPos();
			world.entityList.add(human);
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
			DisplayInfo.reSizeGLScene();
	}

	public void run() {
		System.out.println("Craft " + VERSION + " start running...");
		Instance = this;
		try {
			init();
		} catch (Exception e) {
			System.out.println("Craft " + VERSION + "启动错误：" + e.toString());
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Craft启动错误：" + e.toString(), "Failed to start Craft", JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
		long lastTime = System.currentTimeMillis();
		int fps = 0;
		try {
			while (!Display.isCloseRequested()) {
				if (Display.wasResized())
					DisplayInfo.reSizeGLScene();
				
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
				if (gameSetting.getBoolean(DisplayOption.limitFrames))
					Display.sync(60);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Craft运行错误：" + e.toString(), "Runtime Error!", JOptionPane.WARNING_MESSAGE);
		} finally {
			destory();
		}
	}

	private void destory() {
		Display.destroy();
		Keyboard.destroy();
		Mouse.destroy();
	}

	protected void tick() {
		inputTick(true);
		player.tick();
		world.updateChunkState(player);
		world.tick(player);
		world.particleEngine.tick();
		world.entityList.tickAll();
		world.tileEntityList.updateAll();
		pickupEngine.tick(player, selector);
		clouds.tick();
		//world.update(player);
	}

	private void inputTick(boolean inTick) {
		if (inTick) {
			if ((Mouse.isButtonDown(0)) && hitResult != null && (player.mode == PlayerMode.Normal || player.mode == PlayerMode.Flying && System.currentTimeMillis() - lastClickTime > 300)) {
				lastClickTime = System.currentTimeMillis();
				Tile oldTile = Tile.tiles[(byte) world.getBlock(hitResult.x, hitResult.y, hitResult.z)];
				if (oldTile != Tile.air) {
					/**将静态液体方块变为普通液体方块*/
					if (oldTile instanceof CalmLiquidTile)
						oldTile = Tile.tiles[oldTile.id - 1];
					player.destroyTile(oldTile, hitResult.x, hitResult.y, hitResult.z, pickupEngine);
					/**获取方块的拾取方块*/
				}
			}
			while (Mouse.next() && this.hitResult != null) {
				if ((player.mode == PlayerMode.Flying) && (Mouse.getEventButton() == 0) && (Mouse.getEventButtonState())) {
					Tile oldTile = Tile.tiles[(byte) world.getBlock(hitResult.x, hitResult.y, hitResult.z)];
					if (oldTile != Tile.air) {
						if (oldTile instanceof CalmLiquidTile)
							oldTile = Tile.tiles[oldTile.id - 1];
						player.destroyTile(oldTile, hitResult.x, hitResult.y, hitResult.z, pickupEngine);
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
								world.setBlock(x, y, z, selectedTile.id);
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

			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && gameSetting.getBoolean(DisplayOption.FullScreen))
				System.exit(0);
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) || !Display.isActive())
				Mouse.setGrabbed(false);
			if (!Mouse.isGrabbed() && (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) && Mouse.isInsideWindow())
				Mouse.setGrabbed(true);

			while (Keyboard.next()) {
				if (Keyboard.getEventKey() == Keyboard.KEY_F && Keyboard.getEventKeyState())
					player.toggleMode();
				if (Keyboard.getEventKey() == Keyboard.KEY_F2 && Keyboard.getEventKeyState()) {
					gameSetting.toggleBoolean(DisplayOption.FullScreen);;
					try {
						setDisplayMode(gameSetting.getBoolean(DisplayOption.FullScreen));
					} catch (LWJGLException e) {
						JOptionPane.showMessageDialog(null, "Craft错误：屏幕切换失败！" + e.toString(), "Error!", JOptionPane.WARNING_MESSAGE);
						e.printStackTrace();
					}
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_R && Keyboard.getEventKeyState())
					player.resetPos();
				if (Keyboard.getEventKey() == Keyboard.KEY_B && Keyboard.getEventKeyState())
					gameSetting.toggleBoolean(DisplayOption.ShowBob);
				if (Keyboard.getEventKey() == Keyboard.KEY_F3 && Keyboard.getEventKeyState())
					gameSetting.toggleBoolean(DisplayOption.ShowDebugInfo);
				if (Keyboard.getEventKey() == Keyboard.KEY_L && Keyboard.getEventKeyState())
					gameSetting.toggleBoolean(DisplayOption.limitFrames);
				if (Keyboard.getEventKey() == Keyboard.KEY_F1 && Keyboard.getEventKeyState())
					worldIO.save(FILENAME, world, selector);
				if (Keyboard.getEventKey() == Keyboard.KEY_F10 && Keyboard.getEventKeyState())
					try {
						takeScreenShots("ScreenShots\\" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".png");
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, "保存屏幕截图失败！" + e.toString(), "Error!", JOptionPane.WARNING_MESSAGE);
						e.printStackTrace();
					}
			}
		}
	}

	private void render(float a) {
		// GL更新
		pick(a);
		checkGlError("Picked");
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		camera.setupCamera(a);
		checkGlError("Set up camera");

		Texture.bind(1);
		Frustum frustum = Frustum.getFrustum();
		worldRenderer.cull(player, frustum);
		
		worldRenderer.updateDirtyChunks(player);
		checkGlError("Update chunks");
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_FOG);
		setupFog(0);
		worldRenderer.render(player, 0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		worldRenderer.render(player, 1);
		GL11.glDisable(GL11.GL_BLEND);
		checkGlError("Rendered world");

		world.particleEngine.render(player, a);
		checkGlError("Rendered particles");
		
		pickupEngine.render(player, a);
		checkGlError("Rendered droppedItems");

		world.entityList.render(player, frustum, a);
		checkGlError("Rendered entities");
		
		setupFog(1);
		worldRenderer.renderSurroundingGround();
		checkGlError("Rendered Ground");
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_FOG);
		
		sky.render();
		checkGlError("Rendered sky");
		
		GL11.glDisable(GL11.GL_LIGHTING);
		
		clouds.render(a);
		checkGlError("Rendered clouds");

		if (hitResult != null && !(Tile.tiles[world.getBlock(hitResult.x, hitResult.y, hitResult.z)] instanceof LiquidTile)) {
			worldRenderer.renderHit(hitResult);
			if (Mouse.isButtonDown(0))
				worldRenderer.renderCrack(world, player);
		}

		camera.setupOrthoCamera();
		guiRenderer.renderGui(a);
		if (gameSetting.getBoolean(DisplayOption.ShowDebugInfo))
			debugInfo.show();
		checkGlError("Rendered Gui");
		Display.update();
	}

	private void pick(float a) {
		this.selectBuffer.clear();
		GL11.glSelectBuffer(this.selectBuffer);
		GL11.glRenderMode(GL11.GL_SELECT);
		camera.setupPickCamera(a, DisplayInfo.getWidth() / 2, DisplayInfo.getHeight() / 2);
		this.worldRenderer.pick(this.player);
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
		Tile tile = Tile.tiles[world.getBlock((int) Math.floor(player.x), (int) Math.floor(player.y), (int) Math.floor(player.z))];
		float b = world.globalBrightness;
		GL11.glClearColor(0.5F * b, 0.8F * b, 1.0F * b, 1.0F);
		if (tile instanceof LiquidTile) {
			if (((LiquidTile) tile).getFog()) {
				GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_VIEWPORT_BIT);
				GL11.glFogf(GL11.GL_FOG_DENSITY, 0.1F);
				GL11.glFog(GL11.GL_FOG_COLOR, getBuffer(0.02F, 0.02F, 0.2F, 1.0F));
				GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, getBuffer(0.3F, 0.3F, 0.7F, 1.0F));
			} else {
				GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_VIEWPORT_BIT);
				GL11.glFogf(GL11.GL_FOG_DENSITY, 2.0F);
				GL11.glFog(GL11.GL_FOG_COLOR, getBuffer(0.6F, 0.1F, 0.0F, 1.0F));
				GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, getBuffer(0.4F, 0.3F, 0.3F, 1.0F));
			}
		} else if (i == 0) {
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_VIEWPORT_BIT);
			GL11.glFogf(GL11.GL_FOG_DENSITY, 0.002F);
			GL11.glFog(GL11.GL_FOG_COLOR, fogColor0);
			GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, getBuffer(b, b, b, 1.0F));
		} else if (i == 1) {
			/*GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
			GL11.glFogf(GL11.GL_FOG_START, 50.0F);
			GL11.glFogf(GL11.GL_FOG_END, 500.0F);*/
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_VIEWPORT_BIT);
			GL11.glFogf(GL11.GL_FOG_DENSITY, 0.004F);
			GL11.glFog(GL11.GL_FOG_COLOR, fogColor1);
			GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, getBuffer(b, b, b, 1.0F));
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
		for (AABB b : world.entityList.getEntitiesAABB(aabb)) {
			if (b.intersects(aabb))
				return false;
		}
		return true;
	}

	public void takeScreenShots(String Filename) throws IOException {
		GL11.glReadBuffer(GL11.GL_FRONT);
		int bpp = 4;
		ByteBuffer buffer = BufferUtils.createByteBuffer(DisplayInfo.getWidth() * DisplayInfo.getHeight() * bpp);
		GL11.glReadPixels(0, 0, DisplayInfo.getWidth(), DisplayInfo.getHeight(), GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

		File file = new File(Filename); // The file to save to.
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		String format = "PNG"; // Example: "PNG" or "JPG"
		BufferedImage image = new BufferedImage(DisplayInfo.getWidth(), DisplayInfo.getHeight(), BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < DisplayInfo.getWidth(); x++)
			for (int y = 0; y < DisplayInfo.getHeight(); y++) {
				int i = (x + (DisplayInfo.getWidth() * y)) * bpp;
				int r = buffer.get(i) & 0xFF;
				int g = buffer.get(i + 1) & 0xFF;
				int b = buffer.get(i + 2) & 0xFF;
				image.setRGB(x, DisplayInfo.getHeight() - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
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
	
	/**获取当前系统时间*/
	public static long getTime() {
		return Sys.getTime() * 1000L / Sys.getTimerResolution();
	}
	
	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++)
			if (args[i].equalsIgnoreCase("-fullscreen"))
				GameSetting.instance.setValue(DisplayOption.FullScreen, true);
		
		Thread craft = new Thread(new Craft());
		craft.setName("Craft-Main");
		craft.start();
	}

}
