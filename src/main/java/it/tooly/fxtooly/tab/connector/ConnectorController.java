package it.tooly.fxtooly.tab.connector;

import it.tooly.fxtooly.FXTooly;
import it.tooly.fxtooly.ToolyTabController;
import it.tooly.fxtooly.model.Repository;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ConnectorController implements ToolyTabController{
	@FXML
	private ComboBox<Repository> repositories;
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
		repositories.getItems().addAll(ConnectorManager.get().getRepositories());
		username.setText("");
		password.setText("");
		connect.setDisable(true);
		disconnect.setDisable(true);
		repositories.valueProperty().addListener(new ChangeListener<Repository>() {
			@Override
			public void changed(ObservableValue<? extends Repository> observable, Repository oldValue,
					Repository newValue) {
				if (newValue != null) {
					connect.setDisable(newValue.getSession() != null);
					disconnect.setDisable(newValue.getSession() == null);
				}
			}

		});
    }
	public void connect() {
		Repository repository = repositories.getSelectionModel().getSelectedItem();
		repository.setUsername(username.getText());
		repository.setPassword(password.getText());
		ConnectorManager.get().connect(repository);
		FXTooly.reInit();
	}
	public void disconnect() {
		ConnectorManager.get().disconnect(repositories.getSelectionModel().getSelectedItem());
	}
}
