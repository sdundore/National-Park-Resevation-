package com.techelevator.camprgorund.model.jdbc;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.camprgound.model.Campground;
import com.techelevator.camprgound.model.Site;
import com.techelevator.camprgound.model.SiteDAO;

public class JDBCSiteDAO implements SiteDAO {

	private JdbcTemplate jdbcTemplate;
	private List<Site> availableSites;
	DecimalFormat df = new DecimalFormat("#.00");

	
	public JDBCSiteDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private Site mapRowToSite(SqlRowSet results) {
		Site theSite = new Site();
		theSite.setSiteId(results.getInt("site_id"));
		theSite.setCampgroundId(results.getInt("campground_id"));
		theSite.setSiteNumber(results.getInt("site_number"));
		theSite.setMaxOccupancy(results.getInt("max_occupancy"));
		theSite.setAccessible(results.getBoolean("accessible"));
		theSite.setRvLength(results.getInt("max_rv_length"));
		theSite.setUtilities(results.getBoolean("utilities"));
		return theSite;
	}

	@Override
	public Site createASite(Site newSite) {
		String sqlCreateASite = "INSERT INTO site (site_id, campground_id, site_number, max_occupancy, " + 
				"accessible, max_rv_length, utilities) " + 
				"VALUES(?,?,?,?,?,?,?)";
		jdbcTemplate.update(sqlCreateASite, newSite.getSiteId(), newSite.getCampgroundId(),
				newSite.getSiteNumber(), newSite.getMaxOccupancy(), newSite.isAccessible(),
				newSite.getRvLength(), newSite.isUtilities());
		return newSite;
	}
	
	@Override
	public List<Site> getSiteByAvailability(LocalDate fromDate, LocalDate toDate, int campgroundId) {
		availableSites = new ArrayList<>();
		String sqlGetSiteByAvailability = "SELECT * FROM site WHERE campground_id = ? "
				+ "AND site_id NOT IN ( SELECT site_id FROM reservation "
				+ "WHERE (? BETWEEN to_date AND from_date) OR (? BETWEEN to_date AND from_date) "
				+ "OR (? < to_date AND ? > from_date)) " + "LIMIT 6";
		SqlRowSet result = jdbcTemplate.queryForRowSet(sqlGetSiteByAvailability, campgroundId, fromDate, toDate,
				fromDate, toDate);
		while (result.next()) {
			Site theSite = mapRowToSite(result);
			availableSites.add(theSite);
		}
		return availableSites;
	}

	@Override
	public void printSiteInformation() {
		System.out.printf("\n%10s %10s %10s %10s %10s %9s", "Site No.", "Max Occup.", "Accessible?", "Max RV Length",
				"Utility", "Cost");
	}

	@Override
	public String getCost(LocalDate fromDate, LocalDate toDate, Campground campground) {
		LocalDate date1 = fromDate;
		LocalDate date2 = toDate;
		long daysBetween = ChronoUnit.DAYS.between(date1, date2);
		BigDecimal cost = campground.getDailyFee().multiply(new BigDecimal(daysBetween));
		return "$" + df.format(cost) + "\n";
	}
}
