package it.tooly.fxtooly.model;

import java.util.LinkedList;
import java.util.List;

import it.tooly.fxtooly.FXTooly;
import it.tooly.fxtooly.ToolyPane;
import it.tooly.fxtooly.ToolyUtils;
import it.tooly.fxtooly.tab.queryexecutor.QueryExecutor;
import it.tooly.fxtooly.tab.settings.SettingsController;

public class ToolySettings {
	public final static String S_FULLSCREEN = "fullscreen";
	public final static String S_USERNAME = "username";
	public final static String S_TAB = "tab";
	private List<ToolySetting> list = new LinkedList<>();

	public List<ToolySetting> getList() {
		return list;
	}

	public void setList(List<ToolySetting> list) {
		this.list = list;
	}
	public static ToolySettings getDefaultSettings(){
		List<Object> boolSel = new LinkedList<>();
		boolSel.add(true);
		boolSel.add(false);

		ToolySettings def = new ToolySettings();
		def.getList().add(new ToolySetting(S_FULLSCREEN, "Open fullscreen:", true, boolSel));
		def.getList().add(new ToolySetting(S_USERNAME, "Your displayname:", System.getProperty("user.name"), null));
		List<ToolyPane> toolyPanes = FXTooly.getToolyPanes();
		List<Object> paneNames = new LinkedList<>();
		for (ToolyPane tp: toolyPanes) {
			paneNames.add(tp.getName());
		}
		def.getList().add(new ToolySetting(S_TAB, "Default tab:", QueryExecutor.class.getSimpleName(), paneNames));
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
