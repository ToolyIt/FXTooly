package nl.fxtooly.tab.queryexecutor;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import nl.fxtooly.FXTooly;
import nl.fxtooly.ToolyExceptionHandler;
import nl.fxtooly.ToolyTabController;
import nl.fxtooly.ToolyUtils;
import nl.fxtooly.documentum.DctmUtils;
import nl.fxtooly.model.Queries;
import nl.fxtooly.model.Query;
import nl.fxtooly.model.QueryResult;
import nl.fxtooly.model.QueryResultRow;
import nl.fxtooly.tab.connector.ConnectorManager;

public class QueryExecutorController implements ToolyTabController{
	public final static String LOCAL_QUERY_HISTORY = "local_queries";
	public final static String REMOTE_QUERY_HISTORY = "remote_queries";
	@FXML
	BorderPane content;
	@FXML
	TableView<QueryResultRow> results;
	@FXML
	Button execute;
	@FXML
	TextArea query;
	@FXML
	TreeView<Query> prevQueries;
	@FXML
	Button saveQuery;
	@FXML
	TextField queryName;

	ObservableList<Query> localQueries = FXCollections.observableArrayList();
	ObservableList<Query> remoteQueries = FXCollections.observableArrayList();

	@FXML
	public void initialize() {
		execute.setDisable(!ConnectorManager.get().isConnected());

		if (prevQueries.getRoot() == null) {
			TreeItem<Query> rootNode = new TreeItem<>(new Query("Queries", null));
			prevQueries.setRoot(rootNode);

			if (ConnectorManager.get().isConnected()) {
				initQueryHistory("Shared Queries", remoteQueries, DctmUtils.getObject(
						ConnectorManager.get().getConnectedRepository().getSession(),
						REMOTE_QUERY_HISTORY,
						Queries.class));
			}

			initQueryHistory("Local Queries", localQueries, ToolyUtils.getObject(LOCAL_QUERY_HISTORY, Queries.class));

			prevQueries.getSelectionModel().selectedItemProperty().addListener((o, item1, item2) ->{
				if (item2.getValue().getContent() != null) {
					query.setText(item2.getValue().getContent());
					queryName.setText(item2.getValue().getName());
				}
			});
			rootNode.setExpanded(true);
		}
	}
	public void initQueryHistory(String node, ObservableList<Query> queries, Queries qs) {
		TreeItem<Query> treeItem = new TreeItem<>(new Query(node, null));
		treeItem.setExpanded(true);

		queries.addListener((ListChangeListener.Change<? extends Query> q) -> {
			ObservableList<? extends Query> list = q.getList();
			treeItem.getChildren().clear();
			for (Query query: list) {
				TreeItem<Query> queryItem = new TreeItem<>(query);
				treeItem.getChildren().add(queryItem);
			}
		});

		prevQueries.getRoot().getChildren().add(treeItem);
		queries.addAll(qs.getList());
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
				Query query2 = new Query(null, query.getText());
				if (queryExecutorManager.addQuery(LOCAL_QUERY_HISTORY, query2)) {
					localQueries.add(query2);
				}
			} catch (DfException e) {
				FXTooly.setStatus(e.getMessage());
				ToolyExceptionHandler.handle(e);
			}
		});
	}
	public void saveQuery() {
		Queries object = DctmUtils.getObject(
				ConnectorManager.get().getConnectedRepository().getSession(),
				REMOTE_QUERY_HISTORY,
				Queries.class);
		boolean add = true;
		for (Query q: object.getList()) {
			if (q.getName().equals(queryName.getText())) {
				q.setContent(query.getText());
				add = false;
			}
			if (q.getContent().equals(query.getText())) {
				add = false;
			}
		}
		if (add) {
			object.getList().add(new Query(queryName.getText(), query.getText()));
		}
		IDfSysObject saveObject = DctmUtils.saveObject(ConnectorManager.get().getConnectedRepository().getSession(),
		REMOTE_QUERY_HISTORY, object);
		if (saveObject != null) {
			remoteQueries.clear();
			remoteQueries.addAll(object.getList());
		}
	}
}
