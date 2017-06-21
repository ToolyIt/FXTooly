package it.tooly.fxtooly.documentum.fx;

import it.tooly.shared.model.IModelObject;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

public class ObjectTable extends TableView<IModelObject> {

	ObjectTable() {
		super();
		setRowFactory(tv -> {
			TableRow<IModelObject> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty()) {
					IModelObject rowObject = row.getItem();
					if (!rowObject.hasNullId()) {
						if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {

						}
					}
				}
			});
			return row;
		});
	}

	/*
	 * public void setQueryResult(QueryResult queryResult){
	 * getColumns().clear(); getItems().clear();
	 *
	 * for (int i = 0; i < queryResult.getColumnNames().size(); i++) { String cn
	 * = queryResult.getColumnNames().get(i); if ("format".equals(cn)) {
	 * TableColumn<QueryResultRow, ImageView> col = new TableColumn<>();
	 * col.setCellValueFactory(new PropertyValueFactory<>("format"));
	 * getColumns().add(col); } else if ("r_lock_owner".equals(cn)) {
	 * TableColumn<QueryResultRow, Label> col = new TableColumn<>();
	 * col.setCellValueFactory(new PropertyValueFactory<>("lockOwner"));
	 * getColumns().add(col); } else { TableColumn<QueryResultRow, String> col =
	 * new TableColumn<>(cn); col.setCellValueFactory(new
	 * PropertyValueFactory<>("nextValue")); getColumns().add(col); } }
	 * ObservableList<QueryResultRow> data =
	 * FXCollections.observableArrayList(queryResult.getRows()); setItems(data);
	 *
	 * setOnMousePressed(e -> { QueryResultRow selectedItem =
	 * getSelectionModel().getSelectedItem(); if (e.isSecondaryButtonDown()) {
	 * try { setContextMenu(new ObjectContextMenu(queryResult, selectedItem)); }
	 * catch (DfException ex) { ToolyExceptionHandler.handle(ex); } } if
	 * (e.isPrimaryButtonDown() && e.getClickCount() == 2) { try {
	 * IDfPersistentObject object = DctmUtilsFX.getObject(queryResult,
	 * selectedItem); if (object != null) { if (object instanceof IDfDocument) {
	 * Desktop.getDesktop().open(new File(((IDfDocument)object).getFile(null)));
	 * } else { DctmUtilsFX.showDump(object); } } } catch (DfException |
	 * IOException ex) { ToolyExceptionHandler.handle(ex); } }
	 *
	 * }); }
	 */
}
