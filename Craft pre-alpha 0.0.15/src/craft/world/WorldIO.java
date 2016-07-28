package craft.world;

import javax.swing.JOptionPane;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import craft.Selector;

public class WorldIO {
	public static final String FILE_FLAG = "Craft Save Date";
	public static final String FILE_VER_STR = "Ver.";
	public static final int FILE_CURRENT_VER = 0002;

	public boolean load(String fileName, World world, Selector selector) {		
		try {
			File file = new File(fileName);
			if (!file.exists())
				return false;
			DataInputStream in = new DataInputStream(new GZIPInputStream(new FileInputStream(file)));
			if (!(in.readUTF().equals(FILE_FLAG))) {
				in.close();
				return false;
				//throw new IOException("����Craft�浵�ļ�");
			}
			if (!(in.readUTF().equals(FILE_VER_STR)) || in.readInt() != FILE_CURRENT_VER) {
				in.close();
				throw new IOException("Craft�浵�ļ��汾������");
			}
			in.readUTF();
			selector.readDate(in);
			in.readUTF();
			world.length = in.readInt();
			world.height = in.readInt();
			world.width = in.readInt();
			world.name = in.readUTF();
			world.createTime = in.readUTF();
			world.createEmptyMap(world.length, world.width, world.height);
			/*for(int x = 0; x < world.xChunks; x++)
				for(int z = 0; z < world.zChunks; z++)
					world.chunks[x][z].read(in);*/
			in.close();
			world.calcHeights(world.x0, world.z0, world.length, world.height);
			world.setBrightnessAllUpdate();
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Craft����浵����" + e.toString(), "Failed to load date", JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
			return false;
		}
	}

	public boolean save(String fileName, World world, Selector selector) {
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			DataOutputStream out = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
			out.writeUTF(FILE_FLAG);
			out.writeUTF(FILE_VER_STR);
			out.writeInt(FILE_CURRENT_VER);
			out.writeUTF("Selector Date");
			selector.saveDate(out);
			out.writeUTF("World Date");
			out.writeInt(world.length);
			out.writeInt(world.height);
			out.writeInt(world.width);
			out.writeUTF(world.name);
			out.writeUTF(world.createTime);
			/*for(int x = 0; x < world.xChunks; x++)
				for(int z = 0; z < world.zChunks; z++)
					world.chunks[x][z].save(out);*/
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}