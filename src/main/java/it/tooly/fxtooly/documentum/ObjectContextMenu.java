package it.tooly.fxtooly.documentum;

import java.util.Optional;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;

import it.tooly.fxtooly.ToolyContextMenu;
import it.tooly.fxtooly.ToolyExceptionHandler;
import it.tooly.fxtooly.ToolyUtils;
import it.tooly.fxtooly.model.QueryResultRow;
import it.tooly.fxtooly.tab.connector.ConnectorManager;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;

public class ObjectContextMenu extends ToolyContextMenu {
	public ObjectContextMenu(QueryResultRow row) throws DfException{
		super(row);
		for (String v: row.getValues()) {
			if (DfId.isObjectId(v)) {
				IDfSysObject object =
						(IDfSysObject) ConnectorManager.get().getConnectedRepository().getSession().getObject(new DfId(v));
				addDestroyItem(object);
			}
		}
	}
	public void addDestroyItem(IDfSysObject object){
		MenuItem cut = new MenuItem("Destroy ..");
		cut.setGraphic(ToolyUtils.getImage(ToolyUtils.IMAGE_TRASH));
		getItems().addAll(cut);
		cut.setOnAction(ev ->{
			try {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Remove object " + object.getObjectName());
				String id = object.getObjectId().getId();
				alert.setHeaderText(id.startsWith("0b") || id.startsWith("0c")?
						"Folder "+object.getObjectName()+" and all descendents will be removed!" :
							"Document "+object.getObjectName()+" and all versions will be destroyed!");
				alert.setContentText("Continue?");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
					ObjectDestroyer objectDestroyer = new ObjectDestroyer(
							ConnectorManager.get().getConnectedRepository().getBackgroundSession(),
							new String[]{object.getObjectId().getId()});
					Thread thread = new Thread(objectDestroyer);
					thread.start();
				}
			} catch (DfException e) {
				ToolyExceptionHandler.handle(e);
			}
		});
	}
}
