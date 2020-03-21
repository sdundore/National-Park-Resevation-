package com.techelevator.camprgound.model;

import java.util.List;

public interface ParkDAO {

	public List<Park> getAllParks();

	public List<Park> getParkInformation(String name);

	public Park createAPark(Park newPark);

}
