package nl.fxtooly;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;

public abstract class ToolyTab extends Tab{
	private ToolyTabController controller = null;

	public ToolyTab(){
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
		fxmlLoader.setRoot(this);
		try {
			fxmlLoader.load();
			this.controller = fxmlLoader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String getToolyTabName(){
		return this.getClass().getSimpleName().replaceAll("([A-Z])", " $1");
	}
	public ToolyTabController getController(){
		return controller;
	}
}
