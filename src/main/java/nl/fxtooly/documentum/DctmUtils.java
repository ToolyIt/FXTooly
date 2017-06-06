package nl.fxtooly.documentum;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.commons.io.IOUtils;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.fxtooly.ToolyExceptionHandler;

public class DctmUtils {
	private DctmUtils(){}

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
	public static IDfSysObject saveObject(IDfSession session, String type, Object content) {
		ObjectMapper om = new ObjectMapper();

		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			IOUtils.write(om.writeValueAsBytes(content), bos);

			IDfSysObject object = (IDfSysObject)session.getObjectByQualification(
					"dm_sysobject where folder('"+session.getUser(null).getDefaultFolder()+"') and object_name='" + type + "'");

			if (object == null) {
				object = (IDfSysObject) session.newObject("dm_sysobject");
				object.link(session.getUser(null).getDefaultFolder());
				object.setObjectName(type);
				object.save();
			}

			object.setContentType("unknown");
			object.setContent(bos);

			object.save();
			return object;
		} catch (Exception e) {
			ToolyExceptionHandler.handle(e);
			return null;
		}
	}
	public static <T> T getObject(IDfSession session, String type, Class<T> responseType) {
		ByteArrayInputStream content = null;
		try {
			IDfSysObject object = (IDfSysObject)session.getObjectByQualification(
					"dm_sysobject where folder('"+session.getUser(null).getDefaultFolder()+"') and object_name='" + type + "'");
			if (object == null) {
				object = saveObject(session, type, responseType.newInstance());
			}
			if (object != null) {
				content = object.getContent();
				ObjectMapper om = new ObjectMapper();
				return om.readValue(IOUtils.toByteArray(content), responseType);
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
}
