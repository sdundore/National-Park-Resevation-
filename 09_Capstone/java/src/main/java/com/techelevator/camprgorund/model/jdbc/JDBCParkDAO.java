package com.techelevator.camprgorund.model.jdbc;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.camprgound.model.Park;
import com.techelevator.camprgound.model.ParkDAO;

public class JDBCParkDAO implements ParkDAO {

	private JdbcTemplate jdbcTemplate;
	private String pattern = "MM/dd/yyyy";
	private List<Park> theParkInfo;
	NumberFormat commaFormat = NumberFormat.getInstance();
	SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

	public JDBCParkDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private Park mapRowToPark(SqlRowSet results) {
		Park thePark = new Park();
		thePark.setParkId(results.getInt("park_id"));
		thePark.setName(results.getString("name"));
		thePark.setLocation(results.getString("location"));
		thePark.setEstDate(results.getDate("establish_date"));
		thePark.setArea(results.getInt("area"));
		thePark.setVisitors(results.getInt("visitors"));
		thePark.setDescription(results.getString("description"));
		return thePark;
	}

	@Override
	public List<Park> getAllParks() {
		List<Park> parks = new ArrayList<>();
		String sqlGetAllParks = "SELECT * FROM park";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetAllParks);
		while (results.next()) {
			Park thePark = mapRowToPark(results);
			parks.add(thePark);
		}
		return parks;
	}

	@Override
	public Park createAPark(Park newPark) {
		String sqlCreateAPark = "INSERT INTO park (park_id, name, location, "
				+ "establish_date, area, visitors, description) " + "VALUES(?,?,?,?,?,?,?)";
		jdbcTemplate.update(sqlCreateAPark, newPark.getParkId(), newPark.getName(), newPark.getLocation(),
				newPark.getEstDate(), newPark.getArea(), newPark.getVisitors(), newPark.getDescription());
		return newPark;
	}

	@Override
	public List<Park> getParkInformation(String name) {
		theParkInfo = new ArrayList<>();
		String sqlGetParkInformation = "Select * FROM park WHERE name = ?";
		SqlRowSet result = jdbcTemplate.queryForRowSet(sqlGetParkInformation, name);
		while (result.next()) {
			Park thePark = mapRowToPark(result);
			theParkInfo.add(thePark);
		}
		printParkInformation();
		return theParkInfo;
	}

	private String printParkInformation() {
		String descriptionFormat = "";
		String parkInfo = "";
		for (Park description : theParkInfo) {
			String[] descriptionSplit = description.getDescription().split(" ");
			for (int i = 0; i < descriptionSplit.length; i++) {
				if (i % 10 == 0) {
					descriptionFormat += "\n" + descriptionSplit[i] + " ";
				} else {
					descriptionFormat += descriptionSplit[i] + " ";
				}
			}
		}
		for (Park info : theParkInfo) {
			parkInfo = String.format("\n%15s\n %-15s\n %-15s\n %-15s\n %-15s\n %-15s\n", info.getName() + " National Park",
					"Location: " + info.getLocation(), "Established: " + dateFormat.format(info.getEstDate()),
					"Area: " + commaFormat.format(info.getArea()) + "sq km",
					"Annual Visitors: " + commaFormat.format(info.getVisitors()), descriptionFormat);
		}
		return parkInfo;
	}

}
