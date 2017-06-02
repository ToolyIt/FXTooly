package nl.fxtooly;

import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.fxtooly.model.Repository;
import nl.fxtooly.tab.connector.ConnectorManager;

public class FXTooly extends Application {
	private static Parent root = null;
	private static List<ToolyTab> tabs = null;

	public static void setTabs(List<ToolyTab> tabs){
		FXTooly.tabs = tabs;
	}
	public static List<ToolyTab> getTabs(){
		return FXTooly.tabs;
	}
	public static void main(String[] args) {
		if (args.length == 3) {
			Repository repository = new Repository(args[0]);
			repository.setUsername(args[1]);
			repository.setPassword(args[2]);
			ConnectorManager.get().connect(repository);
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
		  @Override
		    public void run() {
			  ConnectorManager.get().disconnect();
		    }
		});
		FXTooly.launch();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("FXTooly");
		root = FXMLLoader.load(getClass().getResource("Main.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	public static void reInit(){
		for (ToolyTab tab: FXTooly.tabs) {
			if (tab.getController() != null) {
				tab.getController().initialize();
			}
		}
	}
}
