package it.tooly.fxtooly;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

import it.tooly.dctmclient.DctmClient;
import it.tooly.dctmclient.model.IRepository;
import it.tooly.dctmclient.model.IUserAccount;
import it.tooly.dctmclient.model.Repository;
import it.tooly.dctmclient.model.UserAccount;
import it.tooly.dctmclient.model.util.ModelMap;
import it.tooly.fxtooly.tab.connector.ConnectorManager;
import javafx.application.Application;
import javafx.application.Platform;
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
		if (args.length == 3) {
			IUserAccount userAccount = new UserAccount(args[1], args[2]);
			ModelMap<Repository> repoMap = DctmClient.getInstance().getRepositoryMap();
			IRepository repository = repoMap.get(args[0]);
			ConnectorManager.connect(repository, userAccount);
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
		  @Override
		    public void run() {
				ConnectorManager.disconnect();
		    }
		});
		try {
			InetAddress addr = InetAddress.getLocalHost();
			userId = addr.getHostName();
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
			userId = UUID.randomUUID().toString();
		}

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
