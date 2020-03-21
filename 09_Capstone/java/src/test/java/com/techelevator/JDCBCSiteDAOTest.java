package com.techelevator;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.camprgorund.model.jdbc.JDBCCampgroundDAO;
import com.techelevator.camprgorund.model.jdbc.JDBCSiteDAO;
import com.techelevator.camprgound.model.Campground;
import com.techelevator.camprgound.model.Site;

public class JDCBCSiteDAOTest {

	JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
	private static SingleConnectionDataSource dataSource;
	private JDBCSiteDAO dao;
	private JDBCCampgroundDAO campDao;
    java.util.Date utilDate = new java.util.Date();
    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
	DecimalFormat df = new DecimalFormat("#.00");
    
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
		dao = new JDBCSiteDAO(dataSource);
		campDao = new JDBCCampgroundDAO(dataSource);
		jdbcTemplate.update(sqlInsertPark);
		jdbcTemplate.update(sqlInsertCampground);


	}

	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}
	
	private Site getSite(int siteId, int campgroundId, int siteNumber, int maxOccupancy,
			boolean accessible, int RvLength, boolean utilities) {
		Site theSite = new Site();
		theSite.setSiteId(siteId);
		theSite.setCampgroundId(campgroundId);
		theSite.setSiteNumber(siteNumber);
		theSite.setMaxOccupancy(maxOccupancy);
		theSite.setAccessible(accessible);
		theSite.setRvLength(RvLength);
		theSite.setUtilities(utilities);
		return theSite;
	}
	
	private Campground getCampground(int campgroundId, int parkId, String name, int openFrom, int openTo, 
			BigDecimal bigDecimal) {
		Campground theCampground = new Campground();
		theCampground.setCampgroundId(campgroundId);
		theCampground.setParkId(parkId);
		theCampground.setName(name);
		theCampground.setOpenFrom(openFrom);
		theCampground.setOpenTo(openTo);
		theCampground.setDailyFee(bigDecimal);
		return theCampground;
	}

	
	@Test
	public void test_getSiteAvailability_returns_available_site() {
		Site theSite = getSite(1000, 1000, 1, 1, false, 0, false);
		dao.createASite(theSite);
		List<Site> results = dao.getSiteByAvailability(convertFromDate("2020-01-01"), 
				convertToDate("2020-01-02"), 1000);
		assertNotNull(results);
		assertEquals(1000, results.get(0).getSiteId());
	}
	
	@Test
	public void test_getSiteAvailability_returns_site_not_available() {
		Site theSiteNotAvailable = getSite(1000, 1000, 1, 1, false, 0, false);
		dao.createASite(theSiteNotAvailable);
		Site theSiteAvailable = getSite(2000, 1000, 1, 1, false, 0, false);
		dao.createASite(theSiteAvailable);
		String sqlInsertReservation = "INSERT INTO reservation (reservation_id, site_id,"
				+ " name, from_date, to_date, create_date) " +
				"VALUES (1000, 1000, 'Test Reservation', '2020-01-01', '2020-02-01', '2020-01-01')";
		jdbcTemplate.update(sqlInsertReservation);
		List<Site> results = dao.getSiteByAvailability(convertFromDate("2020-01-01"), 
				convertToDate("2020-01-02"), 1000);
		assertEquals(1, results.size());
		assertEquals(2000, results.get(0).getSiteId());
	}
	
	@Test
	public void test_getCost_returns_20_for_two_days() {
		Campground camp = getCampground(2000, 1000, "Test Campground", 01, 
				02, BigDecimal.valueOf(10));
		campDao.createACampground(camp);
		assertEquals("$" + df.format(BigDecimal.valueOf(20)) + "\n", dao.getCost(convertFromDate("2020-01-01"), 
				convertToDate("2020-01-03"), camp));
	}
	
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
