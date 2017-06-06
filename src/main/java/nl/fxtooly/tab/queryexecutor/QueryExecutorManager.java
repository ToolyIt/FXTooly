package nl.fxtooly.tab.queryexecutor;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.common.DfException;

import nl.fxtooly.ToolyUtils;
import nl.fxtooly.documentum.DctmUtils;
import nl.fxtooly.model.Queries;
import nl.fxtooly.model.Query;
import nl.fxtooly.model.QueryResult;
import nl.fxtooly.model.QueryResultRow;
import nl.fxtooly.tab.connector.ConnectorManager;

public class QueryExecutorManager {

	public QueryResult getQueryResult(String query) throws DfException {
		QueryResult qr = new QueryResult();
		IDfQuery q = new DfQuery();
		IDfCollection col = null;
		q.setDQL(query);
		try {
			col = q.execute(ConnectorManager.get().getConnectedRepository().getSession(), IDfQuery.DF_EXECREAD_QUERY);
			boolean fr = true;
			while (col.next()) {
				QueryResultRow row = new QueryResultRow();
				qr.getRows().add(row);
				for (int i = 0; i< col.getAttrCount(); i ++) {
					if (fr){
						qr.getColumnNames().add(col.getAttr(i).getName());
					}
					row.getValues().add((col.getString(col.getAttr(i).getName())));
				}
				fr = false;
			}
		} finally {
			DctmUtils.closeCollection(col);
		}
		return qr;
	}
	public Queries getQueries(String type){
		return ToolyUtils.getObject(type, Queries.class);
	}
	public boolean addQuery(String type, Query query){
		Queries object = ToolyUtils.getObject(type, Queries.class);
		boolean add = true;
		for (Query q: object.getList()) {
			if (q.getContent().equals(query.getContent())) {
				q.setUseCount(q.getUseCount() + 1);
				query.setUseCount(q.getUseCount() + 1);
				add = false;
			}
		}
		if (add) {
			object.getList().add(query);
		}
		ToolyUtils.saveObject(type, object);
		return add;
	}
}
