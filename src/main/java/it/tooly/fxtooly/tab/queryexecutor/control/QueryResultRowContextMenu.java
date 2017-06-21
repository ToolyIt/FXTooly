package it.tooly.fxtooly.tab.queryexecutor.control;

import com.documentum.fc.common.DfException;

import it.tooly.fxtooly.control.ObjectContextMenu;
import it.tooly.fxtooly.tab.queryexecutor.model.QueryResult;
import it.tooly.fxtooly.tab.queryexecutor.model.QueryResultRow;

public class QueryResultRowContextMenu extends ObjectContextMenu {
	public QueryResultRowContextMenu(QueryResult result, QueryResultRow row) throws DfException {
		super(row);
		if (row != null) {
			addDumpItem(row);
			addDestroyItem(row);
		}
	}
}
