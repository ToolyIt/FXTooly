package nl.fxtooly.tab.queryexecutor;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.common.DfException;

import nl.fxtooly.ToolyExceptionHandler;
import nl.fxtooly.ToolyUtils;
import nl.fxtooly.model.QueryResult;
import nl.fxtooly.model.QueryResultRow;
import nl.fxtooly.tab.connector.ConnectorManager;

public class QueryExecutorManager {

	public QueryResult getQueryResult(String query) {
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
		} catch (DfException e) {
			ToolyExceptionHandler.handle(e);
		} finally {
			ToolyUtils.closeCollection(col);
		}
		return qr;
	}
}
