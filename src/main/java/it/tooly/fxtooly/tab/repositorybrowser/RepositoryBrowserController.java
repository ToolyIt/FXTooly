package it.tooly.fxtooly.tab.repositorybrowser;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;

import it.tooly.fxtooly.ToolyExceptionHandler;
import it.tooly.fxtooly.ToolyTabController;
import it.tooly.fxtooly.ToolyUtils;
import it.tooly.fxtooly.documentum.fx.ObjectContextMenu;
import it.tooly.fxtooly.documentum.fx.ObjectTable;
import it.tooly.fxtooly.model.QueryResult;
import it.tooly.fxtooly.model.QueryResultRow;
import it.tooly.fxtooly.tab.connector.ConnectorManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.shape.SVGPath;

public class RepositoryBrowserController implements ToolyTabController{
	private RepositoryBrowserManager rbm = new RepositoryBrowserManager();
	@FXML
	private TreeView<QueryResultRow> folders;

	@FXML
	private ObjectTable documents;

	@FXML
	private ObjectTable documentinfo;

	@FXML
	public void initialize() {
		SVGPath svg = new SVGPath();
		svg.setContent("");
		if (ConnectorManager.getSelectedRepository() != null) {
			String name = ConnectorManager.getSelectedRepository().getName();
			QueryResultRow qr = new QueryResultRow();
			qr.getValues().add(name);
			TreeItem<QueryResultRow> rootItem = new TreeItem<>(qr, ToolyUtils.getImage(ToolyUtils.IMAGE_HOME));
			rootItem.setExpanded(true);
			addSubFolders(rootItem, null);
			folders.setRoot(rootItem);
		} else {

		}
		folders.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<QueryResultRow>>() {
			@Override
			public void changed(ObservableValue<? extends TreeItem<QueryResultRow>> observable,
					TreeItem<QueryResultRow> oldValue, TreeItem<QueryResultRow> newValue) {
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
			}
		});
		documents.setOnMousePressed(e -> {
			QueryResultRow selectedItem = documents.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				for (String v: selectedItem.getValues()) {
					if (DfId.isObjectId(v)){
						documentinfo.setQueryResult(rbm.getObjectInfo(v));
					}
				}
				if (e.isPrimaryButtonDown() && e.getClickCount() == 2) {
		        	for (String v: selectedItem.getValues()) {
		        		if (DfId.isObjectId(v)){
		        			ToolyUtils.openFile(v);
		        		}
		        	}
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
