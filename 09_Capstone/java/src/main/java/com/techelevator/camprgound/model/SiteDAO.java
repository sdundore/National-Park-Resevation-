package com.techelevator.camprgound.model;

import java.time.LocalDate;
import java.util.List;

public interface SiteDAO {

	public List<Site> getSiteByAvailability(LocalDate localDate, LocalDate localDate2, int campgroundChoiceId);

	public void printSiteInformation();

	public String getCost(LocalDate fromDate, LocalDate toDate, Campground campground);

	Site createASite(Site newSite);

}
