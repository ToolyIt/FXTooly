package it.tooly.fxtooly.documentum;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.documentum.fc.client.DfObjectNotFoundException;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.tooly.dctmclient.model.IRepository;
import it.tooly.fxtooly.ToolyExceptionHandler;
import it.tooly.fxtooly.tab.connector.ConnectorManager;
import it.tooly.fxtooly.tab.queryexecutor.model.QueryResult;
import it.tooly.fxtooly.tab.queryexecutor.model.QueryResultRow;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;


public class DctmUtilsFX {
	private DctmUtilsFX() {
	}

	public static void closeCollection(IDfCollection col) {
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

	public static QueryResult executeQuery(final IDfSession session, final String queryString) {
		IDfCollection col = null;
		QueryResult qr = new QueryResult();
		try {
			col = executeQuery(session, queryString, DfQuery.EXECREAD_QUERY);
			while (col.next()) {
				QueryResultRow qrr = new QueryResultRow();
				List<String> values = new LinkedList<>();
				for (int i = 0; i < col.getAttrCount(); i++) {
					if (qr.getColumnNames().isEmpty()) {
						qr.getColumnNames().add(col.getAttr(i).getName());
					}
					values.add(col.getString(col.getAttr(i).getName()));
				}
				qrr.setValues(values);
				qr.getRows().add(qrr);
			}
		} catch (DfException e){
			ToolyExceptionHandler.handle(e);
			return qr;
		} finally {
			closeCollection(col);
		}
		return qr;
	}

	public static IDfSysObject saveObject(IDfSession session, String type, Object content) {
		ObjectMapper om = new ObjectMapper();

		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			byte[] writeValueAsBytes = om.writeValueAsBytes(content);
			IOUtils.write(writeValueAsBytes, bos);

			IDfSysObject object = (IDfSysObject) session.getObjectByQualification("dm_sysobject where folder('"
					+ session.getUser(null).getDefaultFolder() + "') and object_name='" + type + "'");

			if (object == null) {
				object = (IDfSysObject) session.newObject("dm_sysobject");
				object.link(session.getUser(null).getDefaultFolder());
				object.setObjectName(type);
				object.save();
			}

			object.setContentType("crtext");
			object.setContent(bos);

			object.save();

			Method[] methods = content.getClass().getMethods();
			for (Method m: methods) {
				if (m.getName().equals("setVstamp")) {
					m.invoke(content, object.getVStamp());
				}
			}

			return object;
		} catch (Exception e) {
			ToolyExceptionHandler.handle(e);
			return null;
		}
	}

	public static <T> T getObject(IDfSession session, String type, Class<T> responseType) {
		return getObject(session, type, responseType, -99);
	}
	public static <T> T getObject(IDfSession session, String type, Class<T> responseType, int prevVstamp) {
		ByteArrayInputStream content = null;
		try {
			IDfSysObject object = (IDfSysObject) session.getObjectByQualification("dm_sysobject where folder('"
					+ session.getUser(null).getDefaultFolder() + "') and object_name='" + type + "'" + (prevVstamp != -99 ? " and i_vstamp != "+prevVstamp+"" : ""));
			if (object == null && prevVstamp == -99) {
				object = saveObject(session, type, responseType.newInstance());
			}
			if (object == null && prevVstamp != -99) {
				return null;
			}
			if (object != null) {
				content = object.getContent();
				ObjectMapper om = new ObjectMapper();
				T rObject = om.readValue(IOUtils.toByteArray(content), responseType);
				if (prevVstamp != -1) {
					Method[] methods = responseType.getMethods();
					for (Method m: methods) {
						if (m.getName().equals("setVstamp")) {
							m.invoke(rObject, object.getVStamp());
						}
					}
				}
				return rObject;
			} else {
				return responseType.newInstance();
			}
		} catch (Exception e) {
			ToolyExceptionHandler.handle(e);
			return null;
		} finally {
			IOUtils.closeQuietly(content);
		}
	}

	public static IDfSysObject getObject(String objectId) throws Exception {
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
		return doc;
	}

	public static void showDump(IDfPersistentObject object){
		try {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alert.setTitle("Dump of object " + object.getObjectId().getId());
			alert.setResizable(true);

			Label label = new Label("Object dump:");

			TextArea textArea = new TextArea(object.dump());
			textArea.setEditable(false);
			textArea.setWrapText(true);

			textArea.setMaxWidth(Double.MAX_VALUE);
			textArea.setMaxHeight(Double.MAX_VALUE);
			GridPane.setVgrow(textArea, Priority.ALWAYS);
			GridPane.setHgrow(textArea, Priority.ALWAYS);

			GridPane expContent = new GridPane();
			expContent.setMaxWidth(Double.MAX_VALUE);
			expContent.add(label, 0, 0);
			expContent.add(textArea, 0, 1);

			alert.getDialogPane().setContent(expContent);
			alert.show();
		} catch (Exception e) {
			ToolyExceptionHandler.handle(e);
		}
	}

	public static IDfPersistentObject getObject(QueryResult result, QueryResultRow row) throws DfException{
		int ci = result.getColumnNames().indexOf(QueryResult.ATT_OBJECTID);
		if (ci > -1) {
			return ConnectorManager.getSession().getObject(new DfId(row.getValues().get(ci)));
		} else return null;
	}
}
