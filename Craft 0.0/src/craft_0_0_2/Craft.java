package craft_0_0_2;

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

import craft_0_0_2.level.Chunk;
import craft_0_0_2.level.Level;
import craft_0_0_2.level.LevelRenderer;
import craft_0_0_2.level.tile.Face;
import craft_0_0_2.level.tile.Tile;
import craft_0_0_2.level.tile.TileConst;
import craft_0_0_2.renderer.Texture;

public class Craft implements Runnable{
	@SuppressWarnings("unused")
	private static final boolean FullScreen = false;
	private static final String VERSION = "0.0.2";
	private static final float CROSSHAIR_LENGTH = 10.0F;
	private int width,height;
	private Timer timer = new Timer(60.0F);
	private Level level;
	private LevelRenderer levelRenderer;
	private Player player;
	private int crosshairList = 0;
	
	private IntBuffer viewportBuffer = BufferUtils.createIntBuffer(16);
	private IntBuffer selectBuffer = BufferUtils.createIntBuffer(2000);
	private HitResult hitResult = null;
	
	public void init() throws LWJGLException {
		Display.setDisplayMode(new DisplayMode(1000,600));
		Display.setTitle("Craft " + VERSION);
		Display.setLocation(100, 100);
		Display.setResizable(false);
		
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
		reSizeGLScene();
		
		crosshairList = drawCrosshair();
		
		level = new Level(64, 64, 256);
		levelRenderer = new LevelRenderer(level);
		level.calcLightDepths(level.x0, level.z0, level.length, level.width);
		
		player = new Player(level);

		Mouse.setGrabbed(true);
	}
	
	public void run() {
		System.out.println("启动。。。");
		try {
			init();
		} catch (Exception e){
			System.out.println("Craft " + VERSION + "启动错误：" + e.toString());
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Craft启动错误：" + e.toString(), "Failed to start Craft", JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
		long lastTime = System.currentTimeMillis();
		int fps=0;
		while (!Display.isCloseRequested()) {
			if(Display.wasResized()) reSizeGLScene();
			if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) || !Display.isActive())
				Mouse.setGrabbed(false);
			if(!Mouse.isGrabbed() && (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) && Mouse.isInsideWindow())
				Mouse.setGrabbed(true);
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
		Display.destroy();
	}
	
	public void reSizeGLScene() {
		width=Display.getWidth();
		height=Display.getHeight();
		/*GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(70, (float)width/height, 0.1F, 1000.0F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();*/
	}
	
	public void tick() {
		mouseTick();
		player.tick();
	}
	
	public void mouseTick() {
		if(Mouse.isGrabbed()) {
			float xo = Mouse.getDX();
		    float yo = Mouse.getDY();
		    player.turn(xo, yo);
		}
		while (Mouse.next() && this.hitResult != null) {
			if ((Mouse.getEventButton() == 0) && (Mouse.getEventButtonState())) {
				Tile oldTile = Tile.tiles[(byte) level.getBlock(hitResult.x, hitResult.y, hitResult.z)];
				boolean changed = level.setBlock(hitResult.x, hitResult.y, hitResult.z, TileConst.AIR.value);
				if ((oldTile != null) && (changed)) {
					//System.out.printf("%d, %d, %d", hitResult.x, hitResult.y, hitResult.z);
					//oldTile.destroy(this.level, this.hitResult.x, this.hitResult.y, this.hitResult.z, this.particleEngine);
				}
			}
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
				
				if(!player.aabb.intersects(Tile.getAABB(x, y, z)))
					this.level.setBlock(x, y, z, TileConst.GRASS.value);
			}
		}
	}
	
	public void render(float a) {
		//GL更新
	    pick(a);
	    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);	
	    
	    setupCamera(a);
	    levelRenderer.updateDirtyChunks(player);
		levelRenderer.render(player);

		if (hitResult != null)
			levelRenderer.renderHit(hitResult);
		
		setupOrthoCamera();
	    GL11.glCallList(crosshairList);
	    
		Display.update();
	}
	
	private void moveCameraToPlayer(float a) {
	    GL11.glTranslatef(0.0F, 0.0F, -0.2F);
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
	    GL11.glOrtho(-width / 2, width / 2, -height / 2, height / 2, 1.0, 100.0);
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
		GL11.glLoadIdentity();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3f(CROSSHAIR_LENGTH, 0.0F, -1.0F);
		GL11.glVertex3f(-CROSSHAIR_LENGTH, 0.0F, -1.0F);
		GL11.glVertex3f(0.0F, CROSSHAIR_LENGTH, -1.0F);
		GL11.glVertex3f(0.0F, -CROSSHAIR_LENGTH, -1.0F);
		GL11.glEnd();
		GL11.glEndList();
		return list;
	}
	
	public static void main(String[] args) {
		new Thread(new Craft()).start();
	}

}
