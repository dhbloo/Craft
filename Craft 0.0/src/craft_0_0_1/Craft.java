package craft_0_0_1;

import javax.swing.JOptionPane;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import craft_0_0_1.level.Level;
import craft_0_0_1.level.LevelRenderer;

public class Craft implements Runnable{
	@SuppressWarnings("unused")
	private static final boolean FullScreen = false;
	private static final float CROSSHAIR_LENGTH = 10.0F;
	private int width,height;
	private Timer timer = new Timer(60.0F);
	private Level level;
	private LevelRenderer levelRenderer;
	private Player player;
	private int crosshairList = 0;
	
	public void init() throws LWJGLException {
		Display.setDisplayMode(new DisplayMode(1000,600));
		Display.setTitle("Craft 0.0.1");
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
		GL11.glDepthFunc(GL11.GL_LESS);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		Texture.loadTexture();
		Texture.bind(1);
		reSizeGLScene();
		
		crosshairList = drawCrosshair();
		
		level = new Level(64, 64, 256);
		levelRenderer = new LevelRenderer(level);
		player = new Player(level);
		
		Mouse.setGrabbed(true);
	}
	
	public void run() {
		System.out.println("启动。。。");
		try {
			init();
		} catch (Exception e){
			System.out.println("Craft启动错误：" + e.toString());
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Craft启动错误：" + e.toString(), "Failed to start Craft", JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
		long lastTime = System.currentTimeMillis();
		int fps=0;
		while (!Display.isCloseRequested()) {
			if(Display.wasResized()) reSizeGLScene();
			if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
				Mouse.setGrabbed(false);
			if((Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) && Mouse.isInsideWindow())
				Mouse.setGrabbed(true);
			timer.advanceTime();
			for(int i = 0; i < timer.ticks; i++) {
				tick();
			}
			render();
			fps++;
			while (System.currentTimeMillis() >= lastTime + 1000L)
	        {
	          System.out.println(fps + " fps");
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
		if(Mouse.isGrabbed()) {
			float xo = Mouse.getDX();
		    float yo = Mouse.getDY();
		    player.turn(xo, yo);
		}
		player.tick();
	}
	
	public void render() {
		//GL更新
	    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);	
	    
	    setupCamera();
	    levelRenderer.updateDirtyChunks(player);
		levelRenderer.render(player);
		
		setupOrthoCamera();
	    GL11.glCallList(crosshairList);
	    
		Display.update();
	}
	
	private void moveCameraToPlayer() {
	    GL11.glTranslatef(0.0F, 0.0F, -0.2F);
	    GL11.glRotatef(this.player.xRot, 1.0F, 0.0F, 0.0F);
	    GL11.glRotatef(this.player.yRot, 0.0F, 1.0F, 0.0F);

	    float x = this.player.x;
	    float y = this.player.y;
	    float z = this.player.z;
	    GL11.glTranslatef(-x, -y, -z);
	  }
	
	private void setupCamera() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(70.0F, (float)width/height, 0.1F, 1000.0F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
	    moveCameraToPlayer();
	}

	private void setupOrthoCamera() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
	    GL11.glOrtho(-width / 2, width / 2, -height / 2, height / 2, 1.0, 100.0);
	    GL11.glMatrixMode(GL11.GL_MODELVIEW);
	    GL11.glLoadIdentity();
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
