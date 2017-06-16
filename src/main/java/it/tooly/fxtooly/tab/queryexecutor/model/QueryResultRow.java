package it.tooly.fxtooly.tab.queryexecutor.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;

import it.tooly.dctmclient.model.DctmObject;
import it.tooly.fxtooly.ToolyUtils;
import it.tooly.shared.model.IModelObject;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class QueryResultRow extends DctmObject implements IModelObject {
	private int counter = 0;
	private SimpleBooleanProperty dirty = new SimpleBooleanProperty(false);

	public QueryResultRow(String id) {
		super(id);
		addStringAttribute("column_1", id);
	}

	public QueryResultRow(String colName, String colValue) {
		super(colValue);
		addStringAttribute(colName, colValue);
	}

	public QueryResultRow(List<String> values) {
		super(values.get(0));
		for (int x = 1; x < values.size(); x++) {
			addStringAttribute("column_" + (x + 1), values.get(x));
		}
	}

	public QueryResultRow(IDfTypedObject object) throws DfException {
		super(object);
	}

	public List<Object> getValues() {
		return new ArrayList<>(this.attributeValues.values());
	}

	public void setValues(List<String> values) {
		this.attributes.clear();
		this.attributeValues.clear();
		for (int x = 0; x < values.size(); x++) {
			addStringAttribute("column_" + (x + 1), values.get(x));
		}
	}

	public String getNextValue() {
		String[] attrNames = (String[]) attributeValues.keySet().toArray();
		String v = attributeValues.get(attrNames[counter]).toString();
		counter++;
		if (counter == attributeValues.size()) {
			counter = 0;
		}
		return v;
	}

	public ImageView getFormat(){
		ImageView typeIcon = ToolyUtils.getTypeIcon(getAttrValueAt(counter).toString());
		counter++;
		if (counter == attributeValues.size()) {
			counter = 0;
		}
		return typeIcon;
	}

	public Label getLockOwner() {
		Label lbl = new Label();
		String string = getAttrValueAt(counter).toString();
		counter++;
		if (counter == attributeValues.size()) {
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

	public static Callback<QueryResultRow, Observable[]> extractor() {
        return new Callback<QueryResultRow, Observable[]>() {
            @Override
            public Observable[] call(QueryResultRow param) {
                return new Observable[]{param.dirty};
            }
        };
    }

	public void forceRefresh() {
		dirty.set(true);
	}
}
