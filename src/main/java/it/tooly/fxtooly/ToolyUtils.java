package it.tooly.fxtooly;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.tooly.fxtooly.documentum.ObjectContextMenu;
import it.tooly.fxtooly.model.QueryResult;
import it.tooly.fxtooly.model.QueryResultRow;
import it.tooly.fxtooly.tab.connector.ConnectorManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ToolyUtils {
	public static final String IMAGE_FOLDER = "folder";
	public static final String IMAGE_FOLDER_OPENED = "opened_folder";
	public static final String IMAGE_HOME = "home";
	public static final String IMAGE_TRASH = "empty_trash";
	public static final String IMAGE_CLIPBOARD = "clipboard";
	public static final String IMAGE_LOCK = "lock";

	private ToolyUtils(){}
	private static Map<String, Image> formats = new LinkedHashMap<>();

	public static void buildTable(TableView<QueryResultRow> table, QueryResult queryResult){
		table.getColumns().clear();
		table.getItems().clear();
		for (int i = 0; i < queryResult.getColumnNames().size(); i++) {
			String cn = queryResult.getColumnNames().get(i);
			if ("format".equals(cn)) {
				TableColumn<QueryResultRow, ImageView> col = new TableColumn<>();
				col.setCellValueFactory(new PropertyValueFactory<>("format"));
				table.getColumns().add(col);
			} else if ("r_lock_owner".equals(cn)) {
				TableColumn<QueryResultRow, Label> col = new TableColumn<>();
				col.setCellValueFactory(new PropertyValueFactory<>("lockOwner"));
				table.getColumns().add(col);
			} else {
				TableColumn<QueryResultRow, String> col = new TableColumn<>(cn);
				col.setCellValueFactory(new PropertyValueFactory<>("nextValue"));
				table.getColumns().add(col);
			}
		}
		ObservableList<QueryResultRow> data = FXCollections.observableArrayList(queryResult.getRows());
		table.setItems(data);
		table.setOnMousePressed(e -> {
			if (e.isSecondaryButtonDown()) {
				try {
					QueryResultRow selectedItem = table.getSelectionModel().getSelectedItem();
					table.setContextMenu(new ObjectContextMenu(queryResult, selectedItem));
				} catch (DfException ex) {
					ToolyExceptionHandler.handle(ex);
				}
			}
		});
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
	public static ImageView getImage(String image){
		return getImage(image, 16.0, 16.0);
	}
	public static ImageView getImage(String imageType, double fitWidth, double fitHeight){
		Image image = new Image(ToolyUtils.class.getResourceAsStream("/" + imageType + ".png"));
		ImageView imageView = new ImageView(image);
		imageView.setFitHeight(fitHeight);
		imageView.setFitWidth(fitWidth);
		return imageView;
	}
	public static File getSaveLocation(){
		String userHome = System.getProperty("user.home");
		if (userHome != null) {
			File f = new File(userHome + "/.fxtooly");
			if (!f.exists()) {
				f.mkdirs();
			}
			return f;
		}
		return null;
	}
	public static void saveObject(String fileName, Object object) {
		ObjectMapper om = new ObjectMapper();
		try (FileOutputStream fos = new FileOutputStream(new File(getSaveLocation(), fileName))){
			String content = om.writeValueAsString(object);
			IOUtils.write(content, fos);
		} catch (IOException e) {
			ToolyExceptionHandler.handle(e);
		}
	}
	public static <T> T getObject(String fileName, Class<T> responseType) {
		File file = new File(getSaveLocation(), fileName);
		if (!file.exists())
			try {
				return responseType.newInstance();
			} catch (InstantiationException | IllegalAccessException e1) {
				ToolyExceptionHandler.handle(e1);
			}
		try (FileInputStream fos = new FileInputStream(file)){
			byte[] byteArray = IOUtils.toByteArray(fos);
			ObjectMapper om = new ObjectMapper();
			return om.readValue(byteArray, responseType);
		} catch (Exception e) {
			ToolyExceptionHandler.handle(e);
			return null;
		}
	}
}
