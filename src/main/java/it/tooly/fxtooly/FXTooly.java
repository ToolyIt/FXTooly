package it.tooly.fxtooly;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

import it.tooly.fxtooly.model.Repository;
import it.tooly.fxtooly.model.ToolySettings;
import it.tooly.fxtooly.tab.connector.ConnectorManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXTooly extends Application {
	private static String userId = UUID.randomUUID().toString();
	private static TextField status = null;
	private static Parent root = null;
	private static List<ToolyTab> tabs = null;

	public static void setTabs(List<ToolyTab> tabs){
		FXTooly.tabs = tabs;
	}
	public static List<ToolyTab> getTabs(){
		return FXTooly.tabs;
	}
	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		  @Override
		    public void run() {
			  ConnectorManager.get().disconnect();
		    }
		});
		try {
			InetAddress addr = InetAddress.getLocalHost();
			userId = addr.getHostName();
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
			userId = UUID.randomUUID().toString();
		}

		FXTooly.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("FXTooly");
		root = FXMLLoader.load(getClass().getResource("Main.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setMaximized((Boolean) ToolySettings.getLocalSetting(ToolySettings.S_FULLSCREEN).getValue());
		Parameters parameters = getParameters();
		List<String> raw = parameters.getRaw();
		if (raw.size() == 3) {
			Platform.runLater(() ->{
				Repository repository = new Repository(raw.get(0));
				repository.setUsername(raw.get(1));
				repository.setPassword(raw.get(2));
				ConnectorManager.get().connect(repository);
				FXTooly.setStatus("Connected to " + repository.getName() + " as " + repository.getUsername());
				FXTooly.reInit();
			});
		}
		primaryStage.show();
	}
	public static void setStatusField(TextField status){
		FXTooly.status = status;
	}
	public static void setStatus(String text){
		if (FXTooly.status != null) {
			Platform.runLater(() -> {
				FXTooly.status.setText(text);
			});
		}
	}
	public static void reInit(){
		for (ToolyTab tab: FXTooly.tabs) {
			if (tab.getController() != null) {
				tab.getController().initialize();
			}
		}
	}
	public static String getUserId(){
		return userId;
	}
}
