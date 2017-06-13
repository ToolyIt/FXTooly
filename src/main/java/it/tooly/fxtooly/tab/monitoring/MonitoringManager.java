package it.tooly.fxtooly.tab.monitoring;

import java.util.LinkedHashMap;
import java.util.Map;

import it.tooly.fxtooly.documentum.DctmUtilsFX;
import it.tooly.fxtooly.tab.connector.ConnectorManager;
import it.tooly.fxtooly.tab.monitoring.model.MonitoringConfig;
import it.tooly.fxtooly.tab.monitoring.model.MonitoringConfigs;
import it.tooly.fxtooly.tab.monitoring.model.MonitoringData;

public class MonitoringManager {
	public final static String REMOTE_MONITORING_CONFIG = "remote_monitoring";
	public final static String REMOTE_MONITORING_DATA = "remote_monitoring_data";
	private Map<MonitoringConfig, Thread> monitoringThreads = new LinkedHashMap<>();
	private static MonitoringManager mm = null;

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
	public void startMonitoring(MonitoringConfig config){
		if (monitoringThreads.containsKey(config)) {
			return;
		}
		MonitoringData monitoringData = getMonitoringData(config);
		Runnable monitoringTask = () -> {
			while (true) {
				try {
					Thread.sleep(1000);
					System.out.println(Thread.currentThread().getName() + " is running");
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
        };

        Thread monitoringThread = new Thread(monitoringTask);
        monitoringThread.start();
        monitoringThreads.put(config, monitoringThread);
	}
	public void stopMonitoring(MonitoringConfig config){
		if (monitoringThreads.containsKey(config)) {
			Thread thread = monitoringThreads.get(config);
			thread.interrupt();
		}
	}
}
