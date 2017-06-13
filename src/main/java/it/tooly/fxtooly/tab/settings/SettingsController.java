package it.tooly.fxtooly.tab.settings;

import java.util.List;

import it.tooly.fxtooly.ToolyPaneController;
import it.tooly.fxtooly.ToolyUtils;
import it.tooly.fxtooly.model.ToolySetting;
import it.tooly.fxtooly.model.ToolySettings;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class SettingsController implements ToolyPaneController{
	public final static String LOCAL_SETTINGS = "local_settings";
	@FXML
	AnchorPane settingsPane;
	private int settingCounter = 0;
	private ToolySettings localSettings;
	@FXML
    public void initialize() {
		localSettings = ToolyUtils.getObject(LOCAL_SETTINGS, ToolySettings.class);
		ToolySettings defaultSettings = ToolySettings.getDefaultSettings();
		for (ToolySetting s: defaultSettings.getList()) {
			boolean add = true;
			for (ToolySetting localSetting: localSettings.getList()) {
				if (localSetting.getName().equals(s.getName())) {
					localSetting.setDisplay(s.getDisplay());
					localSetting.setSelection(s.getSelection());
					add = false;
				}
			}
			if (add) {
				localSettings.getList().add(s);
			}
		}
		ToolyUtils.saveObject(LOCAL_SETTINGS, localSettings);

		settingsPane.getChildren().clear();

		for (ToolySetting localSetting: localSettings.getList()) {
			addSetting(localSetting);
		}
    }
	private void addSetting(ToolySetting s){
		double space = 25.0;
		settingCounter++;
		Label l = new Label(s.getDisplay());
		l.setLayoutY(settingCounter * space + 3.0);
		if (s.getSelection() != null) {
			ComboBox<Object> cmb = new ComboBox<>();
			List<Object> selection = s.getSelection();
			for (Object sel: selection) {
				cmb.getItems().add(sel);
			}
			cmb.setLayoutX(120.0);
			cmb.setLayoutY(settingCounter * space);
			cmb.setValue(s.getValue());
			cmb.valueProperty().addListener((ov, oldValue, newValue) ->{
				s.setValue(newValue);
				ToolyUtils.saveObject(LOCAL_SETTINGS, localSettings);
			});
			settingsPane.getChildren().add(cmb);
		} else {
			TextField tf = new TextField();
			tf.setLayoutX(120.0);
			tf.setLayoutY(settingCounter * space);
			tf.setText((String) s.getValue());
			tf.textProperty().addListener((ov, oldValue, newValue) ->{
				s.setValue(newValue);
				ToolyUtils.saveObject(LOCAL_SETTINGS, localSettings);
			});
			settingsPane.getChildren().add(tf);
		}
		settingsPane.getChildren().add(l);
	}
}
