package it.tooly.fxtooly.tab.monitoring.model;

import javafx.beans.Observable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Callback;

public class MonitoringConfig {
	private SimpleStringProperty name = new SimpleStringProperty();
	private SimpleStringProperty query = new SimpleStringProperty();
	private SimpleIntegerProperty interval = new SimpleIntegerProperty();

	public String getName() {
		return name.get();
	}
	public void setName(String name) {
		this.name.set(name);
	}
	public String getQuery() {
		return query.get();
	}
	public void setQuery(String query) {
		this.query.set(query);
	}
	public int getInterval() {
		return interval.get();
	}
	public void setInterval(int interval) {
		this.interval.set(interval);
	}
	public static Callback<MonitoringConfig, Observable[]> extractor() {
        return new Callback<MonitoringConfig, Observable[]>() {
            @Override
            public Observable[] call(MonitoringConfig param) {
                return new Observable[]{param.name, param.query, param.interval};
            }
        };
    }
	@Override
	public String toString() {
		return "name: " + name + " and interval: " + getInterval() + "s";
	}
}