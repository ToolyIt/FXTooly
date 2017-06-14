package it.tooly.fxtooly.tab.social;

import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import it.tooly.fxtooly.ToolyPaneController;
import it.tooly.fxtooly.documentum.DctmUtilsFX;
import it.tooly.fxtooly.model.ToolySettings;
import it.tooly.fxtooly.tab.connector.ConnectorManager;
import it.tooly.fxtooly.tab.social.model.Channel;
import it.tooly.fxtooly.tab.social.model.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

public class SocialController implements ToolyPaneController{
	public final static String CHANNEL_MESSAGES = "channel_messages";

	private ObservableList<Message> items = null;
	private Channel activeChannel = null;
	@FXML
	TableView<Message> messages;
	@FXML
	TextArea message;
	@FXML
	Button send;
	@FXML
    public void initialize() {
		if (activeChannel == null && ConnectorManager.isConnected()) {
			activeChannel = DctmUtilsFX.getObject(ConnectorManager.getSession(), CHANNEL_MESSAGES, Channel.class);
			display(activeChannel);
		}
		message.textProperty().addListener((obs, ov, nv) ->{
			send.setDisable(StringUtils.isEmpty(nv));
		});

		if (messages.getColumns().isEmpty()) {
			TableColumn<Message, String> fromCol = new TableColumn<>("From");
			fromCol.setCellValueFactory(new PropertyValueFactory<>("from"));
			fromCol.setMinWidth(120.0);
			messages.getColumns().add(fromCol);

			TableColumn<Message, String> dateCol = new TableColumn<>("Date sent");
			dateCol.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
			dateCol.setMinWidth(120.0);
			messages.getColumns().add(dateCol);

			TableColumn<Message, String> messageCol = new TableColumn<>("Message");
			messageCol.setCellValueFactory(new PropertyValueFactory<>("text"));
			messageCol.setMinWidth(480.0);
			messages.getColumns().add(messageCol);
		}

		Runnable task = () -> {
			while (true) {
				try {
					if (ConnectorManager.isConnected()) {
						Platform.runLater(() -> display(DctmUtilsFX.getObject(
								ConnectorManager.getSession(), CHANNEL_MESSAGES, Channel.class, activeChannel.getVstamp())));
						Thread.sleep(3000);
					}
				} catch (InterruptedException e) {
					break;
				}
			}
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
	public void send(){
		if (activeChannel != null) {
			send.setDisable(true);
			Message m = new Message();
			m.setFrom((String) ToolySettings.getLocalSetting(ToolySettings.S_USERNAME).getValue());
			m.setText(message.getText());
			m.setWhen(new Date());
			Channel channel = DctmUtilsFX.getObject(ConnectorManager.getSession(), CHANNEL_MESSAGES, Channel.class);
			channel.getMessages().add(m);
			if (DctmUtilsFX.saveObject(ConnectorManager.getSession(), CHANNEL_MESSAGES, activeChannel) != null) {
				display(channel);
				message.setText("");
			}
			message.requestFocus();
		}
	}
	public void display(Channel channel){
		if (channel != null) {
			activeChannel = channel;
			boolean refresh = false;
			if (items == null) {
				items = FXCollections.observableList(channel.getMessages());
				messages.setItems(items);
				refresh = true;
			} else {
				for (Message message: channel.getMessages()) {
					if (!items.contains(message)) {
						items.add(message);
						refresh = true;
					}
				}
			}
			if (refresh) {
				FXCollections.sort(items, (Comparator<Message>) (o1, o2) -> o1.getWhen().compareTo(o2.getWhen()));
				Platform.runLater(() -> messages.scrollTo(items.size()-1));
			}
		}
	}
}
