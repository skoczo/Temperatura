package com.skoczo.view;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.context.RequestContext;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import com.skoczo.utils.TempUtil;

@ManagedBean
@SessionScoped
public class Wykres {
	@Inject
	private TempUtil tempUtil;

	private Date start;
	private Date stop;
	private SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private LineChartModel chart;

	/**
	 * @return the dateModel
	 */
	public LineChartModel getChart() {
		if (chart == null) {
			init();
		}
		
		return chart;
	}

	
	public void init() {
		chart = new LineChartModel();

		chart.setLegendPosition("e");

		if (stop != null && start != null && stop.before(start)) {
			stop = null;
			start = null;
		}

		if (start == null || stop == null) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			start = cal.getTime();

			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 0);

			stop = cal.getTime();
		}

		LineChartSeries dwor = new LineChartSeries();
		dwor.setLabel("Dwór");
		ResultSet res = tempUtil.getOutTemp(start, stop);
		setDataInChart(dwor, res);
		if (dwor.getData().size() > 0)
			chart.addSeries(dwor);

		LineChartSeries sh1 = new LineChartSeries();
		sh1.setLabel("Wedzarnia 1");
		res = tempUtil.getSh1Temp(start, stop);
		setDataInChart(sh1, res);
		if (sh1.getData().size() > 0)
			chart.addSeries(sh1);

		LineChartSeries sh2 = new LineChartSeries();
		sh2.setLabel("Wedzarnia 2");
		res = tempUtil.getSh2Temp(start, stop);
		setDataInChart(sh2, res);
		if (sh2.getData().size() > 0)
			chart.addSeries(sh2);

		if (dwor.getData().size() == 0 || sh1.getData().size() == 0
				|| sh2.getData().size() == 0) {
			chart = new LineChartModel();
			showInfo("Brak danych w tym zakresie");
			return;
		}

		RequestContext.getCurrentInstance().update("chart");
//		FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("chart");
		
		chart.setTitle("Wykres temperatur");
		chart.setZoom(true);
		chart.getAxis(AxisType.Y).setLabel("Temperatura");
		DateAxis axis = new DateAxis("Daty");
		axis.setTickAngle(50);
		axis.setTickCount(5);
		axis.setMin(dateFormatter.format(start));
		axis.setMax(dateFormatter.format(stop));
		axis.setTickFormat("%b %#d, %y %H:%#M:%S");

		chart.getAxes().put(AxisType.X, axis);
	}

	private void showInfo(String string) {
		FacesContext context = FacesContext.getCurrentInstance();
		// context.getExternalContext().getFlash().setKeepMessages(true);
		context.addMessage(null, new FacesMessage("Error", string));
	}

	private void setDataInChart(LineChartSeries dwor, ResultSet res) {
		try {
			while (res.next()) {
				long date = res.getLong(1);
				int temp = res.getInt(2);

				dwor.set(dateFormatter.format((new Date(date))), temp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (res != null)
				try {
					res.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * @param dateModel
	 *            the dateModel to set
	 */
	public void setChart(LineChartModel dateModel) {
		this.chart = dateModel;
	}

	/**
	 * @return the start
	 */
	public Date getStart() {
		return start;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(Date start) {
		this.start = start;
	}

	/**
	 * @return the stop
	 */
	public Date getStop() {
		return stop;
	}

	/**
	 * @param stop
	 *            the stop to set
	 */
	public void setStop(Date stop) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(stop);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 0);

		this.stop = cal.getTime();
	}

}
