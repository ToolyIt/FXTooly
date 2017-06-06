package nl.fxtooly.documentum;

import java.io.ByteArrayInputStream;

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
	public static <T> T getObject(IDfSysObject object, Class<T> responseType) {
		ByteArrayInputStream content = null;
		try {
			content = object.getContent();
			ObjectMapper om = new ObjectMapper();
			return om.readValue(IOUtils.toByteArray(content), responseType);
		} catch (Exception e) {
			ToolyExceptionHandler.handle(e);
			return null;
		} finally {
			IOUtils.closeQuietly(content);
		}
	}
}
