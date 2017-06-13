package it.tooly.fxtooly.documentum.fx;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;

import it.tooly.fxtooly.ToolyExceptionHandler;
import it.tooly.fxtooly.documentum.DctmUtilsFX;
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
			QueryResultRow selectedItem = getSelectionModel().getSelectedItem();
			if (e.isSecondaryButtonDown()) {
				try {
					setContextMenu(new ObjectContextMenu(queryResult, selectedItem));
				} catch (DfException ex) {
					ToolyExceptionHandler.handle(ex);
				}
			}
			if (e.isPrimaryButtonDown() && e.getClickCount() == 2) {
				try {
					IDfPersistentObject object = DctmUtilsFX.getObject(queryResult, selectedItem);
					if (object != null) {
						if (object instanceof IDfDocument) {
							Desktop.getDesktop().open(new File(((IDfDocument)object).getFile(null)));
						} else {
							DctmUtilsFX.showDump(object);
						}
					}
				} catch (DfException | IOException ex) {
					ToolyExceptionHandler.handle(ex);
				}
			}

		});
	}
}
