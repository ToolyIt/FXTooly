package nl.fxtooly.model;

import java.util.LinkedList;
import java.util.List;

import javafx.scene.image.ImageView;
import nl.fxtooly.ToolyUtils;

public class QueryResultRow {

	private List<String> values = new LinkedList<>();
	private int counter = 0;

	public QueryResultRow(){

	}
	public QueryResultRow(String... values){
		for (String v: values) {
			this.values.add(v);
		}
	}
	public List<String> getValues() {
		return values;
	}
	public void setValues(List<String> values) {
		this.values = values;
	}
	@Override
	public String toString() {
		if (values.isEmpty()) {
			return "";
		} else {
			return values.get(0);
		}
	}
	public String getNextValue() {
		String v = values.get(counter);
		counter++;
		if (counter == values.size()) {
			counter = 0;
		}
		return v;
	}
	public ImageView getFormat(){
		ImageView typeIcon = ToolyUtils.getTypeIcon(values.get(counter));
		counter++;
		if (counter == values.size()) {
			counter = 0;
		}
		return typeIcon;
	}
}
