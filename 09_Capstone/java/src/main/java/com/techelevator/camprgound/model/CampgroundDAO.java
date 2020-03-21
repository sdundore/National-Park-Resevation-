package com.techelevator.camprgound.model;

import java.util.List;

public interface CampgroundDAO {

	public List<Campground> getCampgroundByParkId(int parkId);

	public List<Campground> getCampgroundByParkName(String parkName);

	public Campground getCampgroundDailyFeeBySiteId(int siteid);

	public void printCampgroundInformation(List<Campground> campground);

	Campground createACampground(Campground campground);

}
