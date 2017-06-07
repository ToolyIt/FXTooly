package it.tooly.fxtooly.documentum.fx;

import com.documentum.fc.common.DfException;

import it.tooly.fxtooly.ToolyExceptionHandler;
import it.tooly.fxtooly.documentum.ObjectContextMenu;
import it.tooly.fxtooly.model.QueryResult;
import it.tooly.fxtooly.model.QueryResultRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;

public class ObjectTable extends TableView<QueryResultRow>{
	public void setQueryResult(QueryResult queryResult){
		getColumns().clear();
		getItems().clear();
		for (int i = 0; i < queryResult.getColumnNames().size(); i++) {
			String cn = queryResult.getColumnNames().get(i);
			if ("format".equals(cn)) {
				TableColumn<QueryResultRow, ImageView> col = new TableColumn<>();
				col.setCellValueFactory(new PropertyValueFactory<>("format"));
				getColumns().add(col);
			} else if ("r_lock_owner".equals(cn)) {
				TableColumn<QueryResultRow, Label> col = new TableColumn<>();
				col.setCellValueFactory(new PropertyValueFactory<>("lockOwner"));
				getColumns().add(col);
			} else {
				TableColumn<QueryResultRow, String> col = new TableColumn<>(cn);
				col.setCellValueFactory(new PropertyValueFactory<>("nextValue"));
				getColumns().add(col);
			}
		}
		ObservableList<QueryResultRow> data = FXCollections.observableArrayList(queryResult.getRows());
		setItems(data);
		setOnMousePressed(e -> {
			if (e.isSecondaryButtonDown()) {
				try {
					QueryResultRow selectedItem = getSelectionModel().getSelectedItem();
					setContextMenu(new ObjectContextMenu(queryResult, selectedItem));
				} catch (DfException ex) {
					ToolyExceptionHandler.handle(ex);
				}
			}
		});
	}
}
