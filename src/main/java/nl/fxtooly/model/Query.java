package nl.fxtooly.model;

import java.util.Comparator;

public class Query {
	private String name;
	private String content;
	private int useCount = 1;

	public Query(){}
	public Query(String content){
		this.content = content;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getUseCount() {
		return useCount;
	}
	public void setUseCount(int useCount) {
		this.useCount = useCount;
	}
}
