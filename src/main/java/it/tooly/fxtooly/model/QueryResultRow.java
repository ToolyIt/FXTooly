package it.tooly.fxtooly.model;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import it.tooly.fxtooly.ToolyUtils;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

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
	public Label getLockOwner(){
		Label lbl = new Label();
		String string = values.get(counter);
		counter++;
		if (counter == values.size()) {
			counter = 0;
		}
		if (!StringUtils.isEmpty(string)) {
			ImageView typeIcon = ToolyUtils.getImage(ToolyUtils.IMAGE_LOCK);
			Tooltip tt = new Tooltip(string);
			lbl.setTooltip(tt);
			lbl.setGraphic(typeIcon);
			return lbl;
		} else return lbl;
	}
}
