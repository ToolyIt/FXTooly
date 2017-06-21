package it.tooly.fxtooly;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import it.tooly.fxtooly.model.ToolySetting;
import it.tooly.fxtooly.model.ToolySettings;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

public class MainController {
	@FXML TextField status;
	@FXML TabPane tabs;
	@FXML Menu tabMenu;
	@FXML
	private ToolySetting selectedTabSetting = null;

	public void initialize(){
		try {
			selectedTabSetting = ToolySettings.getLocalSetting(ToolySettings.S_TAB);
			Enumeration<URL> resources = this.getClass().getClassLoader().getResources("");
			List<ToolyPane> toolyPanes = new LinkedList<>();
			while (resources.hasMoreElements()) {
				URL el = resources.nextElement();
				File root = new File(el.toURI());
				searchTabs(root, root, toolyPanes);
			}
			this.tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
			FXTooly.setStatusField(status);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	public List<ToolyPane> searchTabs(File root, File folder, List<ToolyPane> toolyPanes) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				searchTabs(root, file, toolyPanes);
			} else {
			    String path = file.getAbsolutePath();
			    if (path.endsWith("fxml")) {
			    	File tabClass = new File(path.substring(0, path.indexOf("fxml") -1) + ".class");
			    	if (tabClass.exists()) {
			    		try (URLClassLoader cl = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()})){
			    			String clazz = tabClass.getAbsolutePath().substring(root.getAbsolutePath().length() + 1, tabClass.getAbsolutePath().length() - 6);
				    		Class<?> cls = cl.loadClass(clazz.replace("\\", "."));
				    		ToolyPane toolyPane = addContent(cls);
				    		MenuItem menuItem = new MenuItem("Add " + toolyPane.getName() + " tab");
				    		menuItem.setOnAction(e -> {
				    			addContent(cls);
				    		});
				    		tabMenu.getItems().add(menuItem);
			    		}
			    	}
			    }
			}
		}
		return toolyPanes;
	}
	private ToolyPane addContent(Class<?> cls) {
		ToolyPane newPane = null;
		try {
			Tab tab = new Tab();
			newPane = (ToolyPane) cls.newInstance();
			tab.setText(newPane.getName());
			tab.setClosable(true);
			tab.setContent(newPane);
			if (selectedTabSetting != null && selectedTabSetting.getValue() != null
					&&
					((String) selectedTabSetting.getValue()).equals(newPane.getName())) {
				this.tabs.getSelectionModel().select(tab);
			}
			FXTooly.getToolyPanes().add(newPane);
			this.tabs.getTabs().add(tab);
		} catch (InstantiationException | IllegalAccessException e1) {
			ToolyExceptionHandler.handle(e1);
		}
		return newPane;
	}
}
