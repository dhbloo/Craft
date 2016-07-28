package craft.gui;

import java.text.DecimalFormat;

import craft.Craft;
import craft.setting.DisplayOption;
import craft.setting.GameSetting;
import craft.world.Chunk;
import craft.world.RenderChunk;

public class DebugInfo {
	private Craft craft;
	private GuiRenderer guiRenderer;
	private GameSetting gameSetting;

	public DebugInfo(Craft craft, GuiRenderer guiRenderer) {
		this.craft = craft;
		this.guiRenderer = guiRenderer;
		this.gameSetting = GameSetting.instance;
	}
	
	public void show() {
		guiRenderer.font.clean();
		guiRenderer.font.printIn("Craft " + Craft.VERSION, RGBAColor.white);
		guiRenderer.font.printIn("FullScreen:" + gameSetting.getBoolean(DisplayOption.FullScreen), RGBAColor.white);
		guiRenderer.font.printIn(RenderChunk.updates + " Chunks updated, totalUpdates:" + RenderChunk.totalUpdates + ", tickCount:" + craft.lastTickCount, RGBAColor.white);
		int x = (int) Math.floor(craft.player.x), y = (int) Math.floor(craft.player.y), z = (int) Math.floor(craft.player.z);
		int chunkx = (x + craft.world.length >> 1) >> Chunk.LENGTH_SHIFT_COUNT;
		int chunkz = (z + craft.world.width >> 1) >> Chunk.LENGTH_SHIFT_COUNT;
		int xl = x & Chunk.LENGTH_AND_COUNT;
		int zl = z & Chunk.LENGTH_AND_COUNT;
		guiRenderer.font.printIn("Player:X=" + x + "  Y=" + y + "  Z=" + z, RGBAColor.white);
		guiRenderer.font.printIn("PlayerPos: " + xl + " " + y + " " + zl + " in Chunk " + chunkx + " " + chunkz, RGBAColor.white);
		guiRenderer.font.printIn("Player:RotX=" + Math.floor(craft.player.xRot) + "  RotY=" + Math.floor(craft.player.yRot) + "  Face:" + craft.player.getYRotFace(), RGBAColor.white);
		guiRenderer.font.printIn("Mode:" + craft.player.mode.name() + " Bob:" + gameSetting.getBoolean(DisplayOption.ShowBob), RGBAColor.white);
		guiRenderer.font.printIn("Entities Count:" + craft.world.entityList.getEntitiesCount(), RGBAColor.white);
		guiRenderer.font.printIn("Tile Entities Count:" + craft.world.tileEntityList.getTileEntitiesCount(), RGBAColor.white);
		if (craft.hitResult != null)
			guiRenderer.font.printIn("Pick:X=" + craft.hitResult.x + "  Y=" + craft.hitResult.y + "  Z=" + craft.hitResult.z + "  Face=" + craft.hitResult.face.name(), RGBAColor.white);
		guiRenderer.font.printIn("Memory Used:" + new DecimalFormat("####.00").format((Runtime.getRuntime().totalMemory()) / 1000000.0) + "MB", RGBAColor.white);
		guiRenderer.font.printIn("Fps:" + craft.lastFps, RGBAColor.white);
		guiRenderer.font.printIn("BR:" + craft.world.getBrightness((int)Math.floor(craft.player.x), (int)Math.floor(craft.player.y), (int)Math.floor(craft.player.z)), new RGBAColor(1.0F, 0, 0, 1.0F));
	}

}
