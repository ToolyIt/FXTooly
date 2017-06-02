package nl.fxtooly;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;

public class MainController {
	@FXML TabPane tabs;
	@FXML Menu tabMenu;
	@FXML
	public void initialize(){
		try {
			Enumeration<URL> resources = this.getClass().getClassLoader().getResources("");
			List<ToolyTab> tabs = new LinkedList<>();
			while (resources.hasMoreElements()) {
				URL el = resources.nextElement();
				File root = new File(el.getPath());
				searchTabs(root, root, tabs);
			}
			this.tabs.getTabs().addAll(tabs);
			this.tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
			FXTooly.setTabs(tabs);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	public List<ToolyTab> searchTabs(File root, File folder, List<ToolyTab> tabs) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				searchTabs(root, file, tabs);
			} else {
			    String path = file.getAbsolutePath();
			    if (path.endsWith("fxml")) {
			    	File tabClass = new File(path.substring(0, path.indexOf("fxml") -1) + ".class");
			    	if (tabClass.exists()) {
			    		try (URLClassLoader cl = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()})){
			    			String clazz = tabClass.getAbsolutePath().substring(root.getAbsolutePath().length() + 1, tabClass.getAbsolutePath().length() - 6);
				    		Class<?> cls = cl.loadClass(clazz.replace("\\", "."));
				    		ToolyTab toolyTab = (ToolyTab) cls.newInstance();
				    		toolyTab.setText(toolyTab.getToolyTabName());
				    		toolyTab.setClosable(true);
				    		tabs.add(toolyTab);
				    		MenuItem menuItem = new MenuItem("Add " + toolyTab.getToolyTabName() + " tab");
				    		menuItem.setOnAction(e -> {
								try {
									ToolyTab newToolyTab = (ToolyTab) cls.newInstance();
									newToolyTab.setText(toolyTab.getToolyTabName());
					    			FXTooly.getTabs().add(newToolyTab);
					    			this.tabs.getTabs().add(newToolyTab);
								} catch (InstantiationException | IllegalAccessException e1) {
									ToolyExceptionHandler.handle(e1);
								}
				    		});
				    		tabMenu.getItems().add(menuItem);
			    		}
			    	}
			    }
			}
		}
		return tabs;
	}
}
