package it.tooly.fxtooly.control;

import it.tooly.fxtooly.ToolyUtils;
import it.tooly.shared.model.IModelObject;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class ToolyContextMenu extends ContextMenu {
	public ToolyContextMenu(IModelObject object) {
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
}
