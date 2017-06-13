package it.tooly.fxtooly.tab.queryexecutor;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;

import it.tooly.fxtooly.FXTooly;
import it.tooly.fxtooly.ToolyExceptionHandler;
import it.tooly.fxtooly.ToolyTabController;
import it.tooly.fxtooly.ToolyUtils;
import it.tooly.fxtooly.documentum.DctmUtilsFX;
import it.tooly.fxtooly.documentum.fx.ObjectTable;
import it.tooly.fxtooly.model.Queries;
import it.tooly.fxtooly.model.Query;
import it.tooly.fxtooly.model.QueryResult;
import it.tooly.fxtooly.model.QueryResultRow;
import it.tooly.fxtooly.tab.connector.ConnectorManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

public class QueryExecutorController implements ToolyTabController{
	public final static String LOCAL_QUERY_HISTORY = "local_queries";
	public final static String REMOTE_QUERY_HISTORY = "remote_queries";
	@FXML
	BorderPane content;
	@FXML
	ObjectTable results;
	@FXML
	Button execute;
	@FXML
	TextArea query;
	@FXML
	TabPane queryCache;
	@FXML
	Button saveQuery;
	@FXML
	TextField queryName;
	@FXML
	ComboBox<QueryResultRow> types;

	ObservableList<Query> localQueries = FXCollections.observableArrayList();
	ObservableList<Query> remoteQueries = FXCollections.observableArrayList();

	public void selectType(){
		query.setText("select * from " + types.getSelectionModel().getSelectedItem().getValues().get(0));
	}
	public void switchUpdateSelect(){
		if (query.getText().contains("select")) {
			if (query.getText().contains("where")) {
				query.setText(query.getText().replaceAll("select(.*)from(.*)where", "update $2 objects where"));
			} else {
				query.setText(query.getText().replaceAll("select(.*)from(.*)", "update $2 objects"));
			}
		} else {
			if (query.getText().contains("where")) {
				query.setText(query.getText().replaceAll("update(.*)objects(.*)where", "select * from $1 where"));
			} else {
				query.setText(query.getText().replaceAll("update(.*)objects", "select * from $1"));
			}
		}
		query.setText(query.getText().replaceAll("\\s+", " "));
	}
	public void addQueryCacheTab(String name, Queries queries, ObservableList<Query> obsQueries){
		TableView<Query> queriesView = new TableView<>();
		Tab tab = new Tab(name, queriesView);

		TableColumn<Query, String> col = new TableColumn<>("Name");
		col.setCellValueFactory(new PropertyValueFactory<>("name"));
		queriesView.getColumns().add(col);
		TableColumn<Query, String> col2 = new TableColumn<>("Execute count");
		col2.setCellValueFactory(new PropertyValueFactory<>("useCount"));
		queriesView.getColumns().add(col2);
		TableColumn<Query, String> col3 = new TableColumn<>("Query");
		col3.setCellValueFactory(new PropertyValueFactory<>("content"));
		queriesView.getColumns().add(col3);

		obsQueries.addAll(queries.getList());
		queriesView.setItems(obsQueries);
		queriesView.getSelectionModel().selectedItemProperty().addListener((obs, os, ns) -> {
		    if (ns != null) {
		    	query.setText(ns.getContent());
		    	queryName.setText(ns.getName());
		    }
		});
		queryCache.getTabs().add(tab);
	}
	@FXML
	public void initialize() {
		execute.setDisable(!ConnectorManager.isConnected());
		queryCache.getTabs().clear();
		addQueryCacheTab("Local cached queries", ToolyUtils.getObject(LOCAL_QUERY_HISTORY, Queries.class), this.localQueries);
		if (ConnectorManager.isConnected()) {
			Queries remoteQueries = DctmUtilsFX.getObject(
					ConnectorManager.getSession(),
					REMOTE_QUERY_HISTORY,
					Queries.class);
			addQueryCacheTab("Remote cached queries", remoteQueries, this.remoteQueries);
			QueryResult qr = DctmUtilsFX.executeQuery(ConnectorManager.getSession(),
					"select name from dm_type order by name");
			ObservableList<QueryResultRow> items = FXCollections.observableArrayList();
			types.setItems(items);
			items.addAll(qr.getRows());
		}
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
				results.setQueryResult(queryResult);
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
		Queries object = DctmUtilsFX.getObject(
				ConnectorManager.getSession(),
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
		IDfSysObject saveObject = DctmUtilsFX.saveObject(ConnectorManager.getSession(),
		REMOTE_QUERY_HISTORY, object);
		if (saveObject != null) {
			remoteQueries.clear();
			remoteQueries.addAll(object.getList());
		}
	}
}
