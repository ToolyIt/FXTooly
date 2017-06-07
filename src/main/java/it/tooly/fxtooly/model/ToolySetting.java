package it.tooly.fxtooly.model;

public class ToolySetting {
	private String name;
	private String display;
	private Object value;

	public ToolySetting(){}
	public ToolySetting(String name, String display, Object value){
		this.value = value;
		this.name = name;
		this.display = display;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
}
