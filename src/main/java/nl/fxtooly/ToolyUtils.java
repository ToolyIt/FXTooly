package nl.fxtooly;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import nl.fxtooly.model.QueryResult;
import nl.fxtooly.model.QueryResultRow;
import nl.fxtooly.tab.connector.ConnectorManager;

public class ToolyUtils {
	private ToolyUtils(){}
	private static Map<String, Image> formats = new LinkedHashMap<>();
	public static void closeCollection(IDfCollection col){
		if (col != null)
			try {
				col.close();
			} catch (DfException e) {
				ToolyExceptionHandler.handle(e);
			}
	}
	public static IDfCollection executeQuery(final IDfSession session, final String queryString, final int type)
			throws DfException {
		IDfQuery query = new DfQuery();
		query.setDQL(queryString);
		return query.execute(session, type);
	}
	public static void buildTable(TableView<QueryResultRow> table, QueryResult queryResult){
		table.getColumns().clear();
		table.getItems().clear();
		for (int i = 0; i < queryResult.getColumnNames().size(); i++) {
			String cn = queryResult.getColumnNames().get(i);
			if ("format".equals(cn)) {
				TableColumn<QueryResultRow, ImageView> col = new TableColumn<>();
				col.setCellValueFactory(new PropertyValueFactory<>("format"));
				table.getColumns().add(col);
			} else {
				TableColumn<QueryResultRow, String> col = new TableColumn<>(cn);
				col.setCellValueFactory(new PropertyValueFactory<>("nextValue"));
				table.getColumns().add(col);
			}
		}
		ObservableList<QueryResultRow> data = FXCollections.observableArrayList(queryResult.getRows());
		table.setItems(data);
	}
	public static ImageView getTypeIcon(String format){
		String unknown = "_blank";
		if (formats.isEmpty()){
			formats.put(unknown, new Image(ToolyUtils.class.getResourceAsStream("/types/"+unknown+".png")));
		}
		if (formats.containsKey(format)) {
			return new ImageView(formats.get(format));
		} else {
			try (InputStream resourceAsStream = ToolyUtils.class.getResourceAsStream("/types/"+format+".png")){
				if (resourceAsStream != null) {
					Image image = new Image(resourceAsStream);
					formats.put(format, image);
					return new ImageView(image);
				} else {
					return new ImageView(formats.get(unknown));
				}
			} catch (IOException e) {
				ToolyExceptionHandler.handle(e);
				return new ImageView(formats.get(unknown));
			}
		}
	}
	public static void openFile(String objectId){
		try {
			IDfSysObject doc =
					(IDfSysObject) ConnectorManager.get().getConnectedRepository().getSession().getObject(new DfId(objectId));
			Desktop.getDesktop().open(new File(doc.getFile(null)));
		} catch (Exception e) {
			ToolyExceptionHandler.handle(e);
		}

	}
}
