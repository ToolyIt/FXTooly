package it.tooly.fxtooly.model;

import java.util.LinkedList;
import java.util.List;

public class QueryResult {
	public final static String ATT_OBJECTID = "r_object_id";

	private List<String> columnNames = new LinkedList<>();
	private List<QueryResultRow> rows = new LinkedList<>();

	public QueryResult(String... columnNames){
		for (String cn: columnNames){
			this.columnNames.add(cn);
		}
	}
	public QueryResult(){}
	public List<String> getColumnNames() {
		return columnNames;
	}
	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}
	public List<QueryResultRow> getRows() {
		return rows;
	}
	public void setRows(List<QueryResultRow> rows) {
		this.rows = rows;
	}
}
