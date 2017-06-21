package it.tooly.fxtooly.control;

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
							// TODO: Some generic way of handling a double click
						}
					}
				}
			});
			return row;
		});
	}


}
