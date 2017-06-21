package it.tooly.fxtooly.tab.repositorybrowser;

import com.documentum.fc.common.DfException;

import it.tooly.fxtooly.ToolyExceptionHandler;
import it.tooly.fxtooly.ToolyPaneController;
import it.tooly.fxtooly.ToolyUtils;
import it.tooly.fxtooly.documentum.fx.ObjectContextMenu;
import it.tooly.fxtooly.documentum.fx.ObjectTable;
import it.tooly.fxtooly.tab.connector.ConnectorManager;
import it.tooly.fxtooly.tab.queryexecutor.model.QueryResult;
import it.tooly.fxtooly.tab.queryexecutor.model.QueryResultRow;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class RepositoryBrowserController implements ToolyPaneController{
	private RepositoryBrowserManager rbm = new RepositoryBrowserManager();
	@FXML
	private TreeView<QueryResultRow> folders;

	@FXML
	private ObjectTable documents;

	@FXML
	public void initialize() {
		if (ConnectorManager.isConnected()) {
			String name = ConnectorManager.getSelectedRepository().getName();
			QueryResultRow qr = new QueryResultRow();
			qr.getValues().add(name);
			TreeItem<QueryResultRow> rootItem = new TreeItem<>(qr, ToolyUtils.getImage(ToolyUtils.IMAGE_HOME));
			rootItem.setExpanded(true);
			addSubFolders(rootItem, null);
			folders.setRoot(rootItem);
		}
		folders.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
			if (newValue.getValue().getValues().size() > 1) {
				addSubFolders(newValue, newValue.getValue().getValues().get(1));
				newValue.setExpanded(true);
				QueryResult documentsQueryResult;
				try {
					documentsQueryResult = rbm.getItems(newValue.getValue().getValues().get(1), false);
					documents.getItems().clear();
					documents.getColumns().clear();
					documents.setQueryResult(documentsQueryResult);
				} catch (DfException e) {
					ToolyExceptionHandler.handle(e);
				}
			}
		});
	}
	private void addSubFolders(TreeItem<QueryResultRow> rootItem, String folderId){
		try {
			QueryResult items = rbm.getItems(folderId, true);
			rootItem.getChildren().clear();
			for (QueryResultRow r : items.getRows()) {
				TreeItem<QueryResultRow> item = null;
				if (!"0".equals(r.getValues().get(2))) {
					item = new TreeItem<>(r, ToolyUtils.getImage(ToolyUtils.IMAGE_FOLDER_OPENED));
				} else {
					item = new TreeItem<>(r, ToolyUtils.getImage(ToolyUtils.IMAGE_FOLDER));
				}
				rootItem.getChildren().add(item);
				folders.setOnMousePressed(e -> {
					if (e.isSecondaryButtonDown()) {
						try {
							folders.setContextMenu(new ObjectContextMenu(items, r));
						} catch (DfException ex) {
							ToolyExceptionHandler.handle(ex);
						}
					}
				});
			}
		} catch (DfException e) {
			ToolyExceptionHandler.handle(e);
		}
	}
}
