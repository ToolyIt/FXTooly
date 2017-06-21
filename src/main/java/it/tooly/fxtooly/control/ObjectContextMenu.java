package it.tooly.fxtooly.control;

import java.util.Optional;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;

import it.tooly.dctmclient.model.DctmObject;
import it.tooly.fxtooly.ToolyExceptionHandler;
import it.tooly.fxtooly.ToolyUtils;
import it.tooly.fxtooly.documentum.DctmUtilsFX;
import it.tooly.fxtooly.documentum.ObjectDestroyer;
import it.tooly.shared.model.IModelObject;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class ObjectContextMenu extends ContextMenu {
	public ObjectContextMenu(IModelObject object) {
		super();
		if (object == null || object.hasNullId())
			return;
		MenuItem mi = new MenuItem("Id to Clipboard");
		mi.setGraphic(ToolyUtils.getImage(ToolyUtils.IMAGE_CLIPBOARD));
		getItems().addAll(mi);
		mi.setOnAction(ev -> {
			final Clipboard clipboard = Clipboard.getSystemClipboard();
			final ClipboardContent content = new ClipboardContent();
			content.putString(object.getId());
			clipboard.setContent(content);
		});
	}

	public void addDumpItem(IModelObject object) {
		MenuItem cut = new MenuItem("Dump ..");
		cut.setGraphic(ToolyUtils.getImage(ToolyUtils.IMAGE_DETAILS));
		getItems().addAll(cut);
		cut.setOnAction(ev -> {
			if (object instanceof DctmObject) {
				IDfSysObject object2Dump = null;
				try {
					object2Dump = DctmUtilsFX.getObject(object.getId());
				} catch (Exception e) {
					ToolyExceptionHandler.handle(e);
				}
				if (object2Dump != null)
					DctmUtilsFX.showDump(object2Dump);
			} else {
				// TODO Default dump for any model object
			}
		});
	}

	public void addDestroyItem(IModelObject object) {
		MenuItem cut = new MenuItem("Destroy ..");
		cut.setGraphic(ToolyUtils.getImage(ToolyUtils.IMAGE_TRASH));

		getItems().addAll(cut);
		cut.setOnAction(ev -> {
			try {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				String name = object.getId();
				if (object instanceof DctmObject) {
					IDfTypedObject typedObj = ((DctmObject) object).getTypedObject();
					if (typedObj != null && object instanceof IDfSysObject) {

						name = ((IDfSysObject) object).getObjectName();
						alert.setTitle("Remove object " + name);
						String id = ((IDfSysObject) typedObj).getObjectId().getId();
						alert.setHeaderText(id.startsWith("0b") || id.startsWith("0c")
								? "Folder " + name + " and all descendents will be removed!"
								: "Document " + name + " and all versions will be destroyed!");
						alert.setContentText("Continue?");

						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == ButtonType.OK) {
							// ObjectDestroyer objectDestroyer = new
							// ObjectDestroyer(
							// ConnectorManager.get().getConnectedRepository().getBackgroundSession(),
							// new String[]{object.getObjectId().getId()});
							ObjectDestroyer objectDestroyer = new ObjectDestroyer((IDfSysObject) typedObj);
							Thread thread = new Thread(objectDestroyer);
							thread.start();
						}
					}
				}

			} catch (DfException e) {
				ToolyExceptionHandler.handle(e);
			}
		});
	}
}
