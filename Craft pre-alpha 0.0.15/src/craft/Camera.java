package craft;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import craft.gui.DisplayInfo;
import craft.setting.DisplayOption;
import craft.setting.GameSetting;

public class Camera {
	/**Camera类，用于3D摄像机位置变换*/
	private Player player;
	private IntBuffer viewportBuffer = BufferUtils.createIntBuffer(16);

	public Camera(Player player) {
		this.player = player;
	}
	
	public void moveCameraToPlayer(float a) {
		GL11.glTranslatef(0.0F, 0.0F, -0.15F);
		if (GameSetting.instance.getBoolean(DisplayOption.ShowBob) && player.shouldShowBob())
			shakeCamera(a);
		GL11.glRotatef(player.xRot, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(player.yRot, 0.0F, 1.0F, 0.0F);

		float x = player.xo + (player.x - player.xo) * a;
		float y = player.yo + (player.y - player.yo) * a;
		float z = player.zo + (player.z - player.zo) * a;
		GL11.glTranslatef(-x, -y, -z);
	}
	
	public void shakeCamera(float a) {
		float wd = player.walkDist + (player.walkDist - player.walkDistO) * a;
		float bob = player.bob + (player.bob - player.oBob) * a;
		float tilt = player.tilt + (player.tilt - player.oTilt) * a;
		GL11.glTranslatef((float) Math.sin(wd * Math.PI) * bob * 0.5F, (float) -Math.abs(Math.cos(wd * Math.PI) * bob), 0.0F);
		GL11.glRotatef((float) Math.sin(wd * Math.PI) * bob * 3.0F, 0.0F, 0.0F, 1.0F);
		GL11.glRotatef((float) Math.abs(Math.cos(wd * Math.PI + 0.2F) * bob) * 5.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(tilt, 1.0F, 0.0F, 0.0F);
	}
	
	public void setupCamera(float a) {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(GameSetting.instance.getFloat(DisplayOption.Fov) + player.getFovOffset(a), (float) DisplayInfo.getWidth() / DisplayInfo.getHeight(), 0.1F, 100000.0F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		moveCameraToPlayer(a);
	}
	
	public void setupOrthoCamera() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, DisplayInfo.getWidth(), DisplayInfo.getHeight(), 0, -1.0, 1.0);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
	}
	
	public void setupPickCamera(float a, int x, int y) {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		viewportBuffer.clear();
		GL11.glGetInteger(GL11.GL_VIEWPORT, viewportBuffer);
		viewportBuffer.flip();
		viewportBuffer.limit(16);
		GLU.gluPickMatrix(x, y, 5.0F, 5.0F, viewportBuffer);
		GLU.gluPerspective(GameSetting.instance.getFloat(DisplayOption.Fov) + player.getFovOffset(a), (float) DisplayInfo.getWidth() / DisplayInfo.getHeight(), 0.1F, 1000.0F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		moveCameraToPlayer(a);
	}

}
