package com.techelevator.camprgound.model;

public class Site {

	private int siteId;
	private int campgroundId;
	private int siteNumber;
	private int maxOccupancy;
	private boolean isAccessible;
	private int rvLength;
	private boolean utilities;

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public int getCampgroundId() {
		return campgroundId;
	}

	public void setCampgroundId(int campgroundId) {
		this.campgroundId = campgroundId;
	}

	public int getSiteNumber() {
		return siteNumber;
	}

	public void setSiteNumber(int siteNumber) {
		this.siteNumber = siteNumber;
	}

	public int getMaxOccupancy() {
		return maxOccupancy;
	}

	public void setMaxOccupancy(int maxOccupancy) {
		this.maxOccupancy = maxOccupancy;
	}

	public boolean isAccessible() {
		return isAccessible;
	}

	public void setAccessible(boolean isAccessible) {
		this.isAccessible = isAccessible;
	}

	public int getRvLength() {
		return rvLength;
	}

	public void setRvLength(int rvLength) {
		this.rvLength = rvLength;
	}

	public boolean isUtilities() {
		return utilities;
	}

	public void setUtilities(boolean utilities) {
		this.utilities = utilities;
	}
	
	private String convertBool(boolean bool) {
		if (bool) {
			return "Yes";
		} 
		return "No";
	}

	public String makeString(Site site) {
		String siteInfo = String.format("%2s %10s %12s %10s %16s\t", site.getSiteNumber(), site.getMaxOccupancy(),
				convertBool(site.isAccessible), site.getRvLength(), convertBool(site.isUtilities()));
		return siteInfo;
	}
}
