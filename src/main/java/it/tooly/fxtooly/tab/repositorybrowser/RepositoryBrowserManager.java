package it.tooly.fxtooly.tab.repositorybrowser;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;

import it.tooly.fxtooly.ToolyExceptionHandler;
import it.tooly.fxtooly.tab.connector.ConnectorManager;
import it.tooly.fxtooly.tab.queryexecutor.QueryExecutorManager;
import it.tooly.fxtooly.tab.queryexecutor.model.QueryResult;
import it.tooly.fxtooly.tab.queryexecutor.model.QueryResultRow;


public class RepositoryBrowserManager {

	public QueryResult getItems(String folderId, boolean folders) throws DfException{
		QueryExecutorManager qm = new QueryExecutorManager();
		if (folders) {
			String query = "select object_name, r_object_id, r_link_cnt from %s where %s order by object_name asc enable(row_based)";
			if (folderId == null) {
				return qm.getQueryResult(String.format(query, "dm_cabinet", "is_private = 0"));
			} else {
				return qm.getQueryResult(String.format(query, "dm_folder", "folder(id('" + folderId + "'))"));
			}
		} else {
			return qm.getQueryResult("select f.dos_extension as format, d.r_lock_owner, d.object_name, d.r_creation_date, d.r_modify_date, d.a_status, d.r_object_id from dm_document d, dm_format f where d.a_content_type = f.name and folder(id('" + folderId + "')) order by d.object_name asc");
		}
	}
	public QueryResult getObjectInfo(String objectId){
		QueryResult qr = new QueryResult("Property", "Value");
		try {
			IDfSysObject object = (IDfSysObject) ConnectorManager.getSession().getObject(new DfId(objectId));
			for (int i = 0; i< object.getAttrCount(); i++){
				String name = object.getAttr(i).getName();
				qr.add(new QueryResultRow(name, object.getString(name)));
			}
		} catch (DfException e) {
			ToolyExceptionHandler.handle(e);
		}
		return qr;
	}
}
