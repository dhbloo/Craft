package craft.setting;

public enum DisplayOption implements OptionInfo {
	/**��Ұ��С*/
	Fov(70.0F),
	/**�Ƿ����ӽ�ҡ��*/
	ShowBob(false),
	/**������ʾ����*/
	RenderDistance(64.0F * 64.0F * 64.0F),
	/**ȫ��*/
	FullScreen(false),
	/**�Ƿ���ʾ������Ϣ*/
	ShowDebugInfo(true),
	/**����֡��*/
	limitFrames(false);
	
	/**Ĭ��ֵ*/
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
