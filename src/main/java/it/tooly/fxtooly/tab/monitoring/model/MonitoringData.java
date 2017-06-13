package it.tooly.fxtooly.tab.monitoring.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class MonitoringData {
	private Map<String, Integer> data = new LinkedHashMap<>();

	public Map<String, Integer> getData() {
		return data;
	}

	public void setData(Map<String, Integer> data) {
		this.data = data;
	}
}
