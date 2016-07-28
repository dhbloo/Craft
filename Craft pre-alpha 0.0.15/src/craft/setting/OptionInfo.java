package craft.setting;

public interface OptionInfo {
	
	/**获得设置项名称
	 * @return 设置项名称*/
	public String getOptionName();
	
	/**获得默认值*/
	public Object getDefaultValue();
	
}
