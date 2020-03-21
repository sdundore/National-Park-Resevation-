package com.techelevator;

import static org.junit.Assert.assertEquals;


import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.camprgorund.model.jdbc.JDBCReservationDAO;
import com.techelevator.camprgound.model.Reservation;


public class JDBCReservationDaoTest {

	JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
	private static SingleConnectionDataSource dataSource;
	private JDBCReservationDAO dao;
    java.util.Date utilDate = new java.util.Date();
    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
    
	@BeforeClass
	public static void setupDatasource() {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/campground");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		dataSource.setAutoCommit(false);
	}

	@AfterClass
	public static void closeDataSource() {
		dataSource.destroy();
	}

	@Before
	public void setup() {
		String sqlInsertPark = "INSERT INTO park (park_id, name, location, establish_date,"
				+ " area, visitors, description) " +
				"VALUES (1000, 'Test Park', 'Test Location', '1900-01-01', 100000, 100000, 'Test Description')";
		String sqlInsertCampground = "INSERT INTO campground (campground_id, park_id, name, open_from_mm,"
				+ " open_to_mm, daily_fee) " +
				"VALUES (1000, 1000, 'Test Campground', 01, 02, 10.00)";
		String sqlInsertSite = "INSERT INTO site (site_id, campground_id, site_number, max_occupancy,"
				+ " accessible, max_rv_length, utilities) " +
				"VALUES (1000, 1000, 1000, 1, false, 1, false)";
		dao = new JDBCReservationDAO(dataSource);
		jdbcTemplate.update(sqlInsertPark);
		jdbcTemplate.update(sqlInsertCampground);
		jdbcTemplate.update(sqlInsertSite);
	}

	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}
	
	private Reservation getReservation(int reservationId, int siteId, String name,
			LocalDate fromDate, LocalDate toDate, Date createDate) {
		Reservation theReservation = new Reservation();
		theReservation.setReservationId(reservationId);
		theReservation.setSiteId(siteId);
		theReservation.setName(name);
		theReservation.setFromDate(fromDate);
		theReservation.setToDate(toDate);
		theReservation.setCreateDate(createDate);
		return theReservation;
	}
	
	@Test
	public void test_createReservation_creates_a_reservation() {
		List <Reservation> resultsBefore = dao.getAllReservations();
		Reservation theReservation = getReservation(1000, 1000, "Test Reservation", 
				convertFromDate("2021-03-01"), convertToDate("2021-03-02"), sqlDate);
		dao.createReservation(theReservation);
		List <Reservation> resultsAfter = dao.getAllReservations();
		assertEquals(resultsBefore.size() + 1, resultsAfter.size());
	}
	
	//Could not quite get this to work but wanted to leave it in
	
//	@Test
//	public void test_getLatestReservation_creates_reservation_and_returns_latest_id() {
//		Reservation theReservation = getReservation(1000, 1000, "Test Reservation", 
//				convertFromDate("2021-03-01"), convertToDate("2021-03-02"), sqlDate);
//		dao.createReservation(theReservation);
//		int results = dao.getLatestReservationId();
//		assertNotNull(results);
//		assertEquals(1, results);
//	}
	
//	private long getResId() {
//		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval(reservation_id)"
//				+ " FROM reservation");
//		if (nextIdResult.next()) {
//			return nextIdResult.getLong(1);
//		} else {
//			throw new RuntimeException("Something went wrong while getting an id for the new city");
//		}
//	}
	
	private LocalDate convertToDate(String toDate) {
		int toDtIntYear = Integer.parseInt(toDate.substring(0, 4));
		int toDtIntMonth = Integer.parseInt(toDate.substring(5, 7));
		int toDtIntDay = Integer.parseInt(toDate.substring(8, 10));
		LocalDate toDt = LocalDate.of(toDtIntYear, toDtIntMonth, toDtIntDay);
		return toDt;
	}

	private LocalDate convertFromDate(String fromDate) {
		int fromDtIntYear = Integer.parseInt(fromDate.substring(0, 4));
		int fromDtIntMonth = Integer.parseInt(fromDate.substring(5, 7));
		int fromDtIntDay = Integer.parseInt(fromDate.substring(8, 10));
		LocalDate fromDt = LocalDate.of(fromDtIntYear, fromDtIntMonth, fromDtIntDay);
		return fromDt;
	}
}
