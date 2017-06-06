package nl.fxtooly.tab.queryexecutor;

import java.util.List;

import com.documentum.fc.common.DfException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import nl.fxtooly.FXTooly;
import nl.fxtooly.ToolyExceptionHandler;
import nl.fxtooly.ToolyTabController;
import nl.fxtooly.ToolyUtils;
import nl.fxtooly.model.Queries;
import nl.fxtooly.model.Query;
import nl.fxtooly.model.QueryResult;
import nl.fxtooly.model.QueryResultRow;
import nl.fxtooly.tab.connector.ConnectorManager;

public class QueryExecutorController implements ToolyTabController{
	public final static String LOCAL_QUERY_HISTORY = "local_queries";
	@FXML
	BorderPane content;
	@FXML
	TableView<QueryResultRow> results;
	@FXML
	Button execute;
	@FXML
	TextArea query;
	@FXML
	TreeView<String> prevQueries;
	TreeItem<String> lastQueries;
	Queries localQueries = null;

	@FXML
	public void initialize() {
		execute.setDisable(!ConnectorManager.get().isConnected());
		initQueryHistory();
	}
	public void initQueryHistory() {
		TreeItem<String> value = new TreeItem<>("Queries");
		value.setExpanded(true);
		value.getChildren().add(new TreeItem<String>("Saved queries"));

		if (lastQueries == null) {
			lastQueries = new TreeItem<>("Last queries");
			lastQueries.setExpanded(true);
		}

		ObservableList<TreeItem<String>> children = FXCollections.observableArrayList();

		if (localQueries == null) {
			localQueries = ToolyUtils.getObject(LOCAL_QUERY_HISTORY, Queries.class);
		}

		for (Query q: localQueries.getList()) {
			TreeItem<String> treeItem = new TreeItem<>(q.getContent());
			children.add(treeItem);
		}

		lastQueries.getChildren().clear();
		lastQueries.getChildren().addAll(children);
		value.getChildren().add(lastQueries);

		prevQueries.setRoot(value);
	}
	public void execute() {
		results.getItems().clear();
		results.getColumns().clear();
		results.setPlaceholder(new Label("Loading..."));
		FXTooly.setStatus("");

		Platform.runLater(() -> {
			QueryExecutorManager queryExecutorManager = new QueryExecutorManager();
			QueryResult queryResult;
			try {
				queryResult = queryExecutorManager.getQueryResult(query.getText());
				FXTooly.setStatus(queryResult.getRows().size() + " results");
				ToolyUtils.buildTable(results, queryResult);
				Query query2 = new Query(query.getText());
				if (queryExecutorManager.addQuery(LOCAL_QUERY_HISTORY, query2)) {
					List<Query> list = localQueries.getList();
					list.add(query2);
					localQueries.setList(list);
				}
				initQueryHistory();
			} catch (DfException e) {
				FXTooly.setStatus(e.getMessage());
				ToolyExceptionHandler.handle(e);
			}
		});
	}
}
