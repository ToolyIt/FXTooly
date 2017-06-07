package it.tooly.fxtooly.tab.connector;

import it.tooly.dctmclient.model.IRepository;
import it.tooly.dctmclient.model.IUserAccount;
import it.tooly.dctmclient.model.UserAccount;
import it.tooly.fxtooly.FXTooly;
import it.tooly.fxtooly.ToolyTabController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ConnectorController implements ToolyTabController{
	@FXML
	private ComboBox<IRepository> repositories;
	@FXML
	private TextField username;
	@FXML
	private PasswordField password;
	@FXML
	private Button connect;
	@FXML
	private Button disconnect;

	@FXML
    public void initialize() {
		repositories.getItems().clear();
		repositories.getItems().addAll(ConnectorManager.getRepositories());
		username.setText("");
		password.setText("");
		connect.setDisable(true);
		disconnect.setDisable(true);
		repositories.valueProperty().addListener(new ChangeListener<IRepository>() {
			@Override
			public void changed(ObservableValue<? extends IRepository> observable, IRepository oldValue,
					IRepository newValue) {
				if (newValue != null) {
					connect.setDisable(ConnectorManager.isConnected(newValue));
					disconnect.setDisable(!ConnectorManager.isConnected(newValue));
				}
			}

		});
    }
	public void connect() {
		IRepository repository = repositories.getSelectionModel().getSelectedItem();
		IUserAccount userAccount = new UserAccount(username.getText(), password.getText());
		ConnectorManager.connect(repository, userAccount);
		FXTooly.reInit();
	}

	public void disconnect() {
		ConnectorManager.disconnect(repositories.getSelectionModel().getSelectedItem());
	}
}
