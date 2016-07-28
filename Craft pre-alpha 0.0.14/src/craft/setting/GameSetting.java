package craft.setting;

import java.util.HashMap;

public class GameSetting {
	public static final GameSetting instance = new GameSetting();
	private HashMap<String, Object> setting;

	public GameSetting() {
		setting = new HashMap<String, Object>();
	}
	
	/**设置选项*/
	public void setValue(OptionInfo option, Object value) {
		set(option.getOptionName(), value);
	}
	
	/**获取选项*/
	public Object getValue(OptionInfo option) {
		Object object = get(option.getOptionName());
		return object == null ? option.getDefaultValue() : object;
	}
	
	private void set(String name, Object value) {
		setting.put(name, value);
	}
	
	private Object get(String name) {
		return setting.get(name);
	}

	public float getFloat(OptionInfo option) {
		return (float) getValue(option);
	}
	
	public int getInt(OptionInfo option) {
		return (int) getValue(option);
	}
	
	public boolean getBoolean(OptionInfo option) {
		return (boolean) getValue(option);
	}
	
	public void toggleBoolean(OptionInfo option) {
		setValue(option, !getBoolean(option));
	}
	
}
