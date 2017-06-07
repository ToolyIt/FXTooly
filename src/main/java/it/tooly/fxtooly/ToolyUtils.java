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

import com.documentum.fc.client.DfObjectNotFoundException;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfId;
import com.fasterxml.jackson.databind.ObjectMapper;


import it.tooly.dctmclient.model.IRepository;
import it.tooly.fxtooly.tab.connector.ConnectorManager;
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
			IDfSysObject doc = null;
			for (IRepository repo : ConnectorManager.getConnectedRepositories()) {
				try {
					doc = (IDfSysObject) ConnectorManager.getSession(repo).getObject(new DfId(objectId));
				} catch (DfObjectNotFoundException nfe) {
					// Ignore
				}
			}
			if (doc == null)
				throw new Exception("Object with id " + objectId + " not found in repository.");
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
