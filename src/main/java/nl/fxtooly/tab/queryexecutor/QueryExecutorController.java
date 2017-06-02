package nl.fxtooly.tab.queryexecutor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import nl.fxtooly.ToolyTabController;
import nl.fxtooly.ToolyUtils;
import nl.fxtooly.model.QueryResult;
import nl.fxtooly.model.QueryResultRow;
import nl.fxtooly.tab.connector.ConnectorManager;

public class QueryExecutorController implements ToolyTabController{
	@FXML
	BorderPane content;
	@FXML
	TableView<QueryResultRow> results;
	@FXML
	Button execute;
	@FXML
	TextArea query;
	@FXML
	TextField status;

	@FXML
	public void initialize() {
		execute.setDisable(!ConnectorManager.get().isConnected());
	}

	public void execute() {
		results.getItems().clear();
		results.getColumns().clear();
		results.setPlaceholder(new Label("Loading..."));
		status.setText("");

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				QueryExecutorManager queryExecutorManager = new QueryExecutorManager();
				QueryResult queryResult = queryExecutorManager.getQueryResult(query.getText());
				status.setText(queryResult.getRows().size() + " results");
				ToolyUtils.buildTable(results, queryResult);
			}
		});
	}
}
