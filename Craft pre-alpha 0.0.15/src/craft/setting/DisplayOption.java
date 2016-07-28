package craft.setting;

public enum DisplayOption implements OptionInfo {
	/**视野大小*/
	Fov(70.0F),
	/**是否有视角摇晃*/
	ShowBob(false),
	/**区块显示距离*/
	RenderDistance(64.0F * 64.0F * 64.0F),
	/**全屏*/
	FullScreen(false),
	/**是否显示调试信息*/
	ShowDebugInfo(true),
	/**限制帧数*/
	limitFrames(false);
	
	/**默认值*/
	private final Object defaultValue;
	
	private DisplayOption(Object defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	@Override
	public String getOptionName() {
		return this.name();
	}

	@Override
	public Object getDefaultValue() {
		return this.defaultValue;
	}

}
