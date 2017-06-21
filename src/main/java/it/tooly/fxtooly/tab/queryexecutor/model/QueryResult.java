package it.tooly.fxtooly.tab.queryexecutor.model;

import java.util.LinkedList;
import java.util.List;

import it.tooly.shared.model.util.IModelList;
import it.tooly.shared.model.util.StrictModelList;

public class QueryResult extends StrictModelList<QueryResultRow> implements IModelList<QueryResultRow> {

	private static final long serialVersionUID = 1884478895050756482L;
	public final static String ATT_OBJECTID = "r_object_id";

	private List<String> columnNames = new LinkedList<>();
	// private List<QueryResultRow> rows = new LinkedList<>();

	public QueryResult(String... columnNames){
		for (String cn: columnNames){
			this.columnNames.add(cn);
		}
	}
	public QueryResult(){}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public boolean hasColumn(String columnName) {
		return this.columnNames.contains(columnName);
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}
}
