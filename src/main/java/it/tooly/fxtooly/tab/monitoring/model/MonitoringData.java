package it.tooly.fxtooly.tab.monitoring.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class MonitoringData {
	private Map<Long, Long> data = new LinkedHashMap<>();

	public Map<Long, Long> getData() {
		return data;
	}

	public void setData(Map<Long, Long> data) {
		this.data = data;
	}
}
