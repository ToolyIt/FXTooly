package nl.fxtooly;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import nl.fxtooly.model.QueryResultRow;

public class ToolyContextMenu extends ContextMenu {
	public ToolyContextMenu(QueryResultRow row) {
		super();
		MenuItem mi = new MenuItem("To Clipboard");
		mi.setGraphic(ToolyUtils.getImage(ToolyUtils.IMAGE_CLIPBOARD));
		getItems().addAll(mi);
		mi.setOnAction(ev -> {
			final Clipboard clipboard = Clipboard.getSystemClipboard();
			final ClipboardContent content = new ClipboardContent();
			content.putString(row.getValues().get(0));
			clipboard.setContent(content);
		});
	}
}
