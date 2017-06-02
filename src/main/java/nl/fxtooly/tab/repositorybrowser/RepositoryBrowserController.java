package nl.fxtooly.tab.repositorybrowser;

import com.documentum.fc.common.DfId;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import nl.fxtooly.ToolyTabController;
import nl.fxtooly.ToolyUtils;
import nl.fxtooly.model.QueryResult;
import nl.fxtooly.model.QueryResultRow;
import nl.fxtooly.tab.connector.ConnectorManager;

public class RepositoryBrowserController implements ToolyTabController{
	private Image image = new Image(getClass().getResourceAsStream("/Folder-icon.png"));
	private Image imageSubfolders = new Image(getClass().getResourceAsStream("/Folder-Desktop-icon.png"));
	private RepositoryBrowserManager rbm = new RepositoryBrowserManager();
	@FXML
	private TreeView<QueryResultRow> folders;

	@FXML
	private TableView<QueryResultRow> documents;

	@FXML
	private TableView<QueryResultRow> documentinfo;

	@FXML
	public void initialize() {
		if (ConnectorManager.get().getConnectedRepository() != null) {
			String name = ConnectorManager.get().getConnectedRepository().getName();
			QueryResultRow qr = new QueryResultRow();
			qr.getValues().add(name);
			TreeItem<QueryResultRow> rootItem = new TreeItem<>(qr, new ImageView(image));
			rootItem.setExpanded(true);
			addSubFolders(rootItem, null);
			folders.setRoot(rootItem);
		}
		folders.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<QueryResultRow>>() {
			@Override
			public void changed(ObservableValue<? extends TreeItem<QueryResultRow>> observable,
					TreeItem<QueryResultRow> oldValue, TreeItem<QueryResultRow> newValue) {
				addSubFolders(newValue, newValue.getValue().getValues().get(1));
				newValue.setExpanded(true);
				QueryResult documentsQueryResult = rbm.getItems(newValue.getValue().getValues().get(1), false);
				documents.getItems().clear();
				documents.getColumns().clear();
				ToolyUtils.buildTable(documents, documentsQueryResult);
			}
		});
		documents.setOnMousePressed(event -> {
			QueryResultRow selectedItem = documents.getSelectionModel().getSelectedItem();
			for (String v: selectedItem.getValues()) {
				if (DfId.isObjectId(v)){
					ToolyUtils.buildTable(documentinfo, rbm.getObjectInfo(v));
				}
			}
			if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
	        	for (String v: selectedItem.getValues()) {
	        		if (DfId.isObjectId(v)){
	        			ToolyUtils.openFile(v);
	        		}
	        	}
	        }
		});
	}
	private void addSubFolders(TreeItem<QueryResultRow> rootItem, String folderId){
		QueryResult items = rbm.getItems(folderId, true);
		for (QueryResultRow r : items.getRows()) {
			TreeItem<QueryResultRow> item = null;
			if (!"0".equals(r.getValues().get(2))) {
				item = new TreeItem<>(r, new ImageView(imageSubfolders));
			} else {
				item = new TreeItem<>(r, new ImageView(image));
			}
			rootItem.getChildren().add(item);
		}
	}
}
