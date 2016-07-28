package craft.level;

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

public class LevelIO {
	public static final String FILE_FLAG = "Craft Save Date";
	public static final String FILE_VER_STR = "Ver.";
	public static final int FILE_CURRENT_VER = 0001;

	public boolean load(String fileName, Level level, Selector selector) {		
		try {
			File file = new File(fileName);
			if (!file.exists())
				return false;
			DataInputStream in = new DataInputStream(new GZIPInputStream(new FileInputStream(file)));
			if (!(in.readUTF().equals(FILE_FLAG))) {
				in.close();
				return false;
				//throw new IOException("不是Craft存档文件");
			}
			if (!(in.readUTF().equals(FILE_VER_STR)) || in.readInt() != FILE_CURRENT_VER) {
				in.close();
				throw new IOException("Craft存档文件版本不符合");
			}
			in.readUTF();
			selector.readDate(in);
			in.readUTF();
			level.length = in.readInt();
			level.height = in.readInt();
			level.width = in.readInt();
			byte[][][] blocks = new byte [level.length][level.height][level.width];
			for(int x = 0; x < level.length; x++)
				for(int y = 0; y < level.height; y++)
					in.readFully(blocks[x][y]);
			in.close();
			level.setDate(level.length, level.width, level.height, blocks);
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Craft载入存档错误：" + e.toString(), "Failed to load date", JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
			return false;
		}
	}

	public boolean save(String fileName, Level level, Selector selector) {
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			DataOutputStream out = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
			out.writeUTF(FILE_FLAG);
			out.writeUTF(FILE_VER_STR);
			out.writeInt(0001);
			out.writeUTF("Selector Date");
			selector.saveDate(out);
			out.writeUTF("Level Date");
			out.writeInt(level.length);
			out.writeInt(level.height);
			out.writeInt(level.width);
			byte[][][] blocks = level.getDate();
			for(int x = 0; x < level.length; x++)
				for(int y = 0; y < level.height; y++)
					out.write(blocks[x][y]);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
