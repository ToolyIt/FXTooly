package it.tooly.fxtooly.model;

import java.util.LinkedList;
import java.util.List;

import it.tooly.fxtooly.ToolyUtils;
import it.tooly.fxtooly.tab.settings.SettingsController;

public class ToolySettings {
	public final static String S_FULLSCREEN = "fullscreen";
	public final static String S_USERNAME = "username";
	private List<ToolySetting> list = new LinkedList<>();

	public List<ToolySetting> getList() {
		return list;
	}

	public void setList(List<ToolySetting> list) {
		this.list = list;
	}
	public static ToolySettings getDefaultSettings(){
		ToolySettings def = new ToolySettings();
		def.getList().add(new ToolySetting(S_FULLSCREEN, "Open fullscreen:", true));
		def.getList().add(new ToolySetting(S_USERNAME, "Your displayname:", System.getProperty("user.name")));
		return def;
	}
	public static ToolySetting getLocalSetting(String name){
		ToolySettings object = ToolyUtils.getObject(SettingsController.LOCAL_SETTINGS, ToolySettings.class);
		for (ToolySetting ts: object.getList()) {
			if (ts.getName().equals(name)){
				return ts;
			}
		}
		return null;
	}
}
