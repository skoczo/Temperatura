package com.skoczo.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.inject.Named;

import com.skoczo.utils.TempUtil;

@Singleton
@Named
public class TemperatureManager {
	@Inject
	private TempUtil temperatureUtils;

	private String DWOR = "28-000002729dd6";
	private String WEDZARNIA1 = "28-0000027233fc";
	private String WEDZARNIA2 = "28-0000027233fd";

	private Float lastDwor;
	private Float lastSh1;
	private Float lastSh2;

	// private static String dir = "/sys/devices/w1_bus_master1";
	private static String dir = "/home/skoczo/w1";
	private Map<String, Float> temperatury = new HashMap<String, Float>();

	public Float getDwor() {
		return temperatury.get(DWOR);
	}

	public Float getWedzarnia1() {
		return temperatury.get(WEDZARNIA1);
	}

	public Float getWedzarnia2() {
		return temperatury.get(WEDZARNIA2);
	}

	@Resource
	private TimerService timerService;

	@Schedule(hour = "*", minute = "*/5", persistent = false)
	public void updateDatabase() {
		try {
			if (temperatury.get(DWOR) != null) {
				if (lastDwor == null) {
					lastDwor = temperatury.get(DWOR);
					temperatureUtils.addOutTemp(Calendar.getInstance()
							.getTime(), Math.round(temperatury.get(DWOR)));
				}

				// if (!lastDwor.equals(temperatury.get(DWOR))) {
				temperatureUtils.addOutTemp(Calendar.getInstance().getTime(),
						Math.round(temperatury.get(DWOR)));
				// }
			}

			if (temperatury.get(WEDZARNIA1) != null) {
				if (lastSh1 == null) {
					lastSh1 = temperatury.get(WEDZARNIA1);
					temperatureUtils.addSH1(Calendar.getInstance().getTime(),
							Math.round(temperatury.get(WEDZARNIA1)));
				}

				temperatureUtils.addSH1(Calendar.getInstance().getTime(),
						Math.round(temperatury.get(WEDZARNIA1)));
			}

			if (temperatury.get(WEDZARNIA2) != null) {
				if (lastSh2 == null) {
					lastSh2 = temperatury.get(WEDZARNIA2);
					temperatureUtils.addSH2(Calendar.getInstance().getTime(),
							Math.round(temperatury.get(WEDZARNIA2)));
				}

				temperatureUtils.addSH2(Calendar.getInstance().getTime(),
						Math.round(temperatury.get(WEDZARNIA2)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Schedule(hour = "*", minute = "*", second = "*/60", persistent = false)
	public void readTemperature() throws IOException {
		for (File sensor : (new File(dir)).listFiles()) {
			if (sensor.isDirectory()) {
				if (sensor.getName().equals(DWOR)
						|| sensor.getName().equals(WEDZARNIA1)
						|| sensor.getName().equals(WEDZARNIA2)) {
					BufferedReader reader = new BufferedReader(new FileReader(
							new File(sensor, "w1_slave")));
					String crc = reader.readLine();
					String temperatura = reader.readLine();
					int index = temperatura.indexOf("t=");

					float temp = Float.parseFloat(temperatura
							.substring(index + 2)) / 1000;

					temperatury.put(sensor.getName(), temp);
					reader.close();
				}
			}
		}
	}
}
