package it.tooly.fxtooly;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.documentum.fc.common.DfException;

import it.tooly.dctmclient.model.IRepository;
import it.tooly.dctmclient.model.IUserAccount;
import it.tooly.dctmclient.model.Repository;
import it.tooly.dctmclient.model.UserAccount;
import it.tooly.fxtooly.model.ToolySettings;
//github.com/ToolyIt/FXTooly
import it.tooly.fxtooly.tab.connector.ConnectorManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class FXTooly extends Application {
	private static String userId = UUID.randomUUID().toString();
	private static TextField status = null;
	private static Stage primaryStage = null;
	private static Parent root = null;
	private static List<ToolyPane> toolyPanes = new LinkedList<ToolyPane>();
	private static boolean hasFocus = true;

	public static void main(String[] args) {
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

		FXTooly.launch(args);
	}
	public static void setTitle(String title){
		primaryStage.setTitle("FXTooly" + (title != null ? " - " + title : ""));
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXTooly.primaryStage = primaryStage;
		setTitle(null);
		root = FXMLLoader.load(getClass().getResource("Main.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setMaximized((Boolean) ToolySettings.getLocalSetting(ToolySettings.S_FULLSCREEN).getValue());
		Parameters parameters = getParameters();
		List<String> raw = parameters.getRaw();
		if (raw.size() == 3) {
			Platform.runLater(() ->{
				try {
					IRepository repository = new Repository("unknown id", raw.get(0), null);
					IUserAccount userAccount = new UserAccount(raw.get(1), raw.get(2));
					ConnectorManager.connect(repository, userAccount);
					FXTooly.setStatus("Connected to " + repository.getName() + " as " + userAccount.getLoginName());
					setTitle("Connected to " + repository.getName() + " as " + userAccount.getLoginName());
					FXTooly.reInit();
				} catch (DfException e) {
					ToolyExceptionHandler.handle(e);
				}
			});
		}
		primaryStage.focusedProperty().addListener((ov, ob, nb) -> {
			hasFocus = nb;
		});
		primaryStage.getIcons().add(new Image(FXTooly.class.getResourceAsStream("/fxtooly.png")));
		primaryStage.show();
	}
	public static void requestFocus(String message){
		if (!hasFocus) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Alert information");
			alert.setHeaderText("New message arrvied!");
			alert.setContentText(message);

			alert.showAndWait();
		}
		setStatus("A new message arrived: " + (message.length() > 100 ? message.substring(100) : message));
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
		for (ToolyPane tPane: FXTooly.toolyPanes) {
			if (tPane.getController() != null) {
				tPane.getController().initialize();
			}
		}
	}
	public static String getUserId(){
		return userId;
	}

	public static List<ToolyPane> getToolyPanes() {
		return toolyPanes;
	}

	public static void setToolyPanes(List<ToolyPane> toolyPanes) {
		FXTooly.toolyPanes = toolyPanes;
	}
}
