package nl.fxtooly.model;

public class Query {
	private String name;
	private String content;
	private int useCount = 1;

	public Query(){}
	public Query(String name, String content){
		this.name = name;
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
	@Override
	public String toString() {
		String nameLbl = name != null ? name.toUpperCase() : "";
		String useCountLbl = content != null ? " (" + useCount + ")" : "";
		String contentLbl = content != null ? " " + content : "";
		return nameLbl + useCountLbl + contentLbl;
	}
}
