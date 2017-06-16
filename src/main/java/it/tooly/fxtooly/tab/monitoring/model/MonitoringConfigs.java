package it.tooly.fxtooly.tab.monitoring.model;

import java.util.LinkedList;
import java.util.List;

public class MonitoringConfigs {
	private List<MonitoringConfig> list = new LinkedList<MonitoringConfig>();

	public List<MonitoringConfig> getList() {
		return list;
	}
	public void setList(List<MonitoringConfig> list) {
		this.list = list;
	}
}
