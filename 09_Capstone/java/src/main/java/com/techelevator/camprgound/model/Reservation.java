package com.techelevator.camprgound.model;

import java.sql.Date;
import java.time.LocalDate;

public class Reservation {

	private int reservationId;
	private int siteId;
	private String name;
	private LocalDate fromDate;
	private LocalDate toDate;
	private Date createDate;

	public Reservation() {

	}

	public Reservation(int siteId, String name, LocalDate arrival, LocalDate departure) {
		this.siteId = siteId;
		this.name = name;
		this.fromDate = arrival;
		this.toDate = departure;
	}

	public int getReservationId() {
		return reservationId;
	}

	public void setReservationId(int reservationId) {
		this.reservationId = reservationId;
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getFromDate() {
		return fromDate;
	}

	public void setFromDate(LocalDate fromDate) {
		this.fromDate = fromDate;
	}

	public LocalDate getToDate() {
		return toDate;
	}

	public void setToDate(LocalDate toDate) {
		this.toDate = toDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
