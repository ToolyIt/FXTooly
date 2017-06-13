package it.tooly.fxtooly;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

public class ToolyPane extends BorderPane {
	private ToolyPaneController controller = null;

	public ToolyPane(){
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
		fxmlLoader.setClassLoader(getClass().getClassLoader());
		fxmlLoader.setRoot(this);
		try {
			fxmlLoader.load();
			this.controller = fxmlLoader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public ToolyPaneController getController(){
		return controller;
	}
	public String getName() {
		return this.getClass().getSimpleName().replaceAll("([A-Z])", " $1");
	}
}
