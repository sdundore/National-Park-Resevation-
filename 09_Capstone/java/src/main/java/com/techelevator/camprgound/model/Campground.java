package com.techelevator.camprgound.model;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public class Campground {

	private int campgroundId;
	private int parkId;
	private String name;
	private int openFrom;
	private int openTo;
	private BigDecimal dailyFee;
	private String pattern = "MM/dd/yyyy";
	SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
	NumberFormat numberFormat = new DecimalFormat("##.00");

	private String getMonth(int month) {
		return new DateFormatSymbols().getMonths()[month - 1];
	}

	public int getCampgroundId() {
		return campgroundId;
	}

	public void setCampgroundId(int campgroundId) {
		this.campgroundId = campgroundId;
	}

	public int getParkId() {
		return parkId;
	}

	public void setParkId(int parkId) {
		this.parkId = parkId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOpenFrom() {
		return openFrom;
	}

	public void setOpenFrom(int openFrom) {
		this.openFrom = openFrom;
	}

	public int getOpenTo() {
		return openTo;
	}

	public void setOpenTo(int openTo) {
		this.openTo = openTo;
	}

	public BigDecimal getDailyFee() {
		return dailyFee;
	}

	public void setDailyFee(BigDecimal d) {
		this.dailyFee = d;
	}

	public String makeString(Campground campground) {
		String campgroundInfo = String.format("%-25s %-10s %-10s %-15s", campground.getName(),
				getMonth(campground.getOpenFrom()), getMonth(campground.getOpenTo()),
				"$" + (numberFormat.format(campground.getDailyFee())));
		return campgroundInfo;
	}

}
