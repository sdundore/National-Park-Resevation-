package com.techelevator.camprgound.model;

import java.util.List;

public interface ReservationDAO {

	public void createReservation(Reservation reservation);

	public int getLatestReservationId();

	List<Reservation> getAllReservations();

}
