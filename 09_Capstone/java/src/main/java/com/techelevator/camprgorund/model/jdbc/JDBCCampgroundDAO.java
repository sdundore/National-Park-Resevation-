package com.techelevator.camprgorund.model.jdbc;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.camprgound.model.Campground;
import com.techelevator.camprgound.model.CampgroundDAO;

public class JDBCCampgroundDAO implements CampgroundDAO {

	private JdbcTemplate jdbcTemplate;
	private String pattern = "MM/dd/yyyy";
	private List<Campground> campgroundInfo;
	NumberFormat commaFormat = NumberFormat.getInstance();
	SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
	NumberFormat numberFormat = new DecimalFormat("##.00");

	public JDBCCampgroundDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private String getMonth(int month) {
		return new DateFormatSymbols().getMonths()[month - 1];
	}

	private Campground mapRowToCampground(SqlRowSet results) {
		Campground theCampground = new Campground();
		theCampground.setCampgroundId(results.getInt("campground_id"));
		theCampground.setParkId(results.getInt("park_id"));
		theCampground.setName(results.getString("name"));
		theCampground.setOpenFrom(results.getInt("open_from_mm"));
		theCampground.setOpenTo(results.getInt("open_to_mm"));
		theCampground.setDailyFee(results.getBigDecimal("daily_fee"));
		return theCampground;
	}
	
	@Override
	public Campground createACampground (Campground campground) {
		String sqlCreateACampground = "INSERT INTO campground (campground_id, park_id, name, open_from_mm, open_to_mm, daily_fee) " + 
				"VALUES(?,?,?,?,?,?)";
		jdbcTemplate.update(sqlCreateACampground, campground.getCampgroundId(), campground.getParkId(), campground.getName(), campground.getOpenFrom(),
				campground.getOpenTo(), campground.getDailyFee());
		return campground;
	}

	@Override
	public List<Campground> getCampgroundByParkId(int parkId) {
		List<Campground> campgroundId = new ArrayList<>();
		String sqlSearchCampgroundById = "SELECT * FROM campground " + "WHERE park_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSearchCampgroundById, parkId);
		while (results.next()) {
			Campground camp = mapRowToCampground(results);
			campgroundId.add(camp);
		}
		return campgroundId;
	}

	@Override
	public Campground getCampgroundDailyFeeBySiteId(int siteId) {
		Campground campgroundFee = new Campground();
		String sqlSearchCampgroundById = "SELECT * FROM campground "
				+ "JOIN site ON campground.campground_id = site.campground_id " + "WHERE site_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSearchCampgroundById, siteId);
		while (results.next()) {
			campgroundFee = mapRowToCampground(results);
		}
		return campgroundFee;
	}

	@Override
	public List<Campground> getCampgroundByParkName(String parkName) {
		campgroundInfo = new ArrayList<>();
		String sqlSearchCampgroundById = "SELECT * FROM campground " + "JOIN park ON campground.park_id = park.park_id "
				+ "WHERE park.name = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSearchCampgroundById, parkName);
		while (results.next()) {
			Campground camp = mapRowToCampground(results);
			campgroundInfo.add(camp);
		}
		return campgroundInfo;
	}

	@Override
	public void printCampgroundInformation(List<Campground> campground) {
		System.out.println();
		System.out.printf("%10s %25s %11s %14s\n", "Name", "Open", "Close", "Daily Fee");
		for (int i = 0; i < campground.size(); i++) {
			System.out.printf("%-5s %-25s %-10s %-10s %-15s\n", "#" + (i + 1), campground.get(i).getName(),
					getMonth(campground.get(i).getOpenFrom()), getMonth(campground.get(i).getOpenTo()),
					"$" + (numberFormat.format(campground.get(i).getDailyFee())));
		}
	}
}
