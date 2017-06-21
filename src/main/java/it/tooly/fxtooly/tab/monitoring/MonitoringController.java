package it.tooly.fxtooly.tab.monitoring;

import java.util.Date;

import it.tooly.fxtooly.FXTooly;
import it.tooly.fxtooly.ToolyPaneController;
import it.tooly.fxtooly.documentum.DctmUtilsFX;
import it.tooly.fxtooly.tab.connector.ConnectorManager;
import it.tooly.fxtooly.tab.monitoring.model.MonitoringConfig;
import it.tooly.fxtooly.tab.monitoring.model.MonitoringConfigs;
import it.tooly.fxtooly.tab.queryexecutor.model.QueryResult;
import it.tooly.fxtooly.tab.queryexecutor.model.QueryResultRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class MonitoringController implements ToolyPaneController{
	@FXML
	Label intervalLbl;
	@FXML
	NumberAxis timeAxis;
	@FXML
    TextField name;
    @FXML
    TextArea query;
    @FXML
    Slider interval;
    @FXML
    Button save;
    @FXML
    Button stop;
    @FXML
    Button start;
	@FXML
	TableView<MonitoringConfig> monitorConfigs;
	@FXML
	LineChart<Long, Long> chart;
	private ObservableList<MonitoringConfig> observableArrayList = null;
	@FXML
    public void initialize() {
		interval.valueProperty().addListener((ov, oldV, newV) -> {
			intervalLbl.setText("Interval: " + newV.intValue() + "s");
		});
		if (ConnectorManager.isConnected()) {
			observableArrayList = FXCollections.observableArrayList(MonitoringConfig.extractor());
			observableArrayList.addAll(MonitoringManager.get().getMonitoringConfigs().getList());

			TableColumn<MonitoringConfig, String> col = new TableColumn<>();
			col.setText("Monitor name");
			col.setCellValueFactory(new PropertyValueFactory<>("name"));
			monitorConfigs.getColumns().add(col);
			TableColumn<MonitoringConfig, Integer> col2 = new TableColumn<>();
			col2.setText("Interval in sec.");
			col2.setCellValueFactory(new PropertyValueFactory<>("interval"));
			monitorConfigs.getColumns().add(col2);
			monitorConfigs.setItems(observableArrayList);
			intervalLbl.setText("Interval: 5s");
			monitorConfigs.getSelectionModel().selectedItemProperty().addListener((obs, os, ns) -> {
				if (ns != null) {
					start.setDisable(false);
					name.setText(ns.getName());
					query.setText(ns.getQuery());
					interval.setValue(ns.getInterval());
					intervalLbl.setText("Interval: " + ns.getInterval() + "s");
				}
			});
		}
    }
	public void startMonitoring(){
		MonitoringConfig selectedItem = monitorConfigs.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			MonitoringManager.get().stopMonitoring();
			chart.setData(MonitoringManager.get().startMonitoring(selectedItem));
			stop.setDisable(false);
			start.setDisable(true);
			timeAxis.setLabel("Seconds since " + new Date(MonitoringManager.get().getStartTime()));
		}
	}
	public void stopMonitoring(){
		MonitoringManager.get().stopMonitoring();
		chart.getData().clear();
		stop.setDisable(true);
		monitorConfigs.getSelectionModel().clearSelection();
	}
	public void save(){
		if (testQuery(query.getText())) {
			MonitoringConfigs monitoringConfigs = MonitoringManager.get().getMonitoringConfigs();
			MonitoringConfig mc = null;
			for (MonitoringConfig mcs: monitoringConfigs.getList()) {
				if (mcs.getName().equals(name.getText())) {
					mc = mcs;
				}
			}
			if (mc == null) {
				mc = new MonitoringConfig();
				monitoringConfigs.getList().add(mc);
			}
			mc.setName(name.getText());
			mc.setQuery(query.getText());
			mc.setInterval(new Double(interval.getValue()).intValue());
			observableArrayList.clear();
			observableArrayList.addAll(monitoringConfigs.getList());
			DctmUtilsFX.saveObject(ConnectorManager.getSession(), MonitoringManager.REMOTE_MONITORING_CONFIG, monitoringConfigs);
			FXTooly.setStatus("Monitoring config with " + mc + " saved.");
		}
	}
	private boolean testQuery(String query){
		QueryResult executeQuery = DctmUtilsFX.executeQuery(ConnectorManager.getSession(), query);
		if (executeQuery.isEmpty()) {
			FXTooly.setStatus("Query does not have any results.");
			return false;
		}
		QueryResultRow queryResultRow = executeQuery.get(0);
		if (queryResultRow.getValues().size() != 2) {
			FXTooly.setStatus("Query result must have only 2 columns (series name and count).");
			return false;
		}
		return true;
	}
}