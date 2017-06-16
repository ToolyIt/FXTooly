package it.tooly.fxtooly.tab.monitoring;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.tooly.fxtooly.FXTooly;
import it.tooly.fxtooly.documentum.DctmUtilsFX;
import it.tooly.fxtooly.tab.connector.ConnectorManager;
import it.tooly.fxtooly.tab.monitoring.model.MonitoringConfig;
import it.tooly.fxtooly.tab.monitoring.model.MonitoringConfigs;
import it.tooly.fxtooly.tab.monitoring.model.MonitoringData;
import it.tooly.fxtooly.tab.queryexecutor.model.QueryResult;
import it.tooly.fxtooly.tab.queryexecutor.model.QueryResultRow;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

public class MonitoringManager {
	public final static String REMOTE_MONITORING_CONFIG = "remote_monitoring";
	public final static String REMOTE_MONITORING_DATA = "remote_monitoring_data";
	private Map<MonitoringConfig, Thread> monitoringThreads = new LinkedHashMap<>();
	private static MonitoringManager mm = null;
	private MonitoringData monitoringData = null;
	private ObservableList<XYChart.Series<Long, Long>> data = null;
	private long startTime = 0l;

	private MonitoringManager(){}
	public static MonitoringManager get(){
		if (mm == null) {
			mm = new MonitoringManager();
		}
		return mm;
	}
	public MonitoringConfigs getMonitoringConfigs(){
		return DctmUtilsFX.getObject(ConnectorManager.getSession(), REMOTE_MONITORING_CONFIG, MonitoringConfigs.class);
	}
	private MonitoringData getMonitoringData(MonitoringConfig config){
		return DctmUtilsFX.getObject(ConnectorManager.getSession(), REMOTE_MONITORING_DATA + config.getName(), MonitoringData.class);
	}
	public ObservableList<XYChart.Series<Long, Long>> startMonitoring(MonitoringConfig config){
		data = FXCollections.observableArrayList();
		if (monitoringThreads.containsKey(config)) {
			return data;
		}
		monitoringData = getMonitoringData(config);
		if (monitoringData.getData().isEmpty()) {
			startTime = System.currentTimeMillis();
		} else {
			startTime = monitoringData.getData().entrySet().iterator().next().getKey();
		}
		Runnable monitoringTask = () -> {
			while (true) {
				try {
					QueryResult qr = DctmUtilsFX.executeQuery(ConnectorManager.getSession(), config.getQuery());

					for (QueryResultRow qrr : qr) {
						List<Object> values = qrr.getValues();
						addData(values.get(0).toString(), System.currentTimeMillis(),
								Long.parseLong(values.get(1).toString()));
					}
					Thread.sleep(config.getInterval() * 1000);
				} catch (InterruptedException e) {
					break;
				}
			}
        };

        Thread monitoringThread = new Thread(monitoringTask);
        monitoringThread.setDaemon(true);
        monitoringThread.start();
        monitoringThreads.put(config, monitoringThread);
        FXTooly.setStatus("Started monitoring config " + config.getName());
        return data;
	}
	public boolean isMonitoring(){
		return !monitoringThreads.isEmpty();
	}
	public void stopMonitoring() {
		for (Entry<MonitoringConfig, Thread> t: monitoringThreads.entrySet()) {
			t.getValue().interrupt();
		}
		monitoringThreads.clear();
	}
	public void stopMonitoring(MonitoringConfig config) {
		if (config != null && monitoringThreads.containsKey(config)) {
			Thread thread = monitoringThreads.remove(config);
			thread.interrupt();
		}
	}
	private void addData(String seriesName, long time, long yData){
		Platform.runLater(() -> {
			Series<Long, Long> series = null;
			for (XYChart.Series<Long, Long> sers: this.data) {
				if (sers.getName().equals(seriesName)) {
					series = sers;
				}
			}
			if (series == null) {
				series = new Series<Long, Long>();
				series.setName(seriesName);
				this.data.add(series);
			}
			series.getData().add(new Data<Long, Long>((time - getStartTime()) / 1000l, yData));
		});
	}
	public long getStartTime() {
		return startTime;
	}
}
