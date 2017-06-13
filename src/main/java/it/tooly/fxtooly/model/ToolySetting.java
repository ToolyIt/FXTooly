package it.tooly.fxtooly.model;

import java.util.List;

public class ToolySetting {
	private String name;
	private String display;
	private Object value;
	private List<Object> selection;

	public ToolySetting(){}
	public ToolySetting(String name, String display, Object value, List<Object> selection){
		this.value = value;
		this.name = name;
		this.display = display;
		this.selection = selection;
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
	public List<Object> getSelection() {
		return selection;
	}
	public void setSelection(List<Object> selection) {
		this.selection = selection;
	}
}
