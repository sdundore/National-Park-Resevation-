package com.techelevator;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.camprgorund.model.jdbc.JDBCCampgroundDAO;
import com.techelevator.camprgound.model.Campground;

public class JDBCCampgroundTest {

	JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
	private static SingleConnectionDataSource dataSource;
	private JDBCCampgroundDAO dao;
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
		dao = new JDBCCampgroundDAO(dataSource);
		jdbcTemplate.update(sqlInsertPark);
		}

	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
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
	public void test_getCampGroundByParkId_gets_park() {
		Campground theCampground = getCampground(1000, 1000, "Test Campground", 1, 2, BigDecimal.valueOf(35.00));
		dao.createACampground(theCampground);
		List<Campground> results = dao.getCampgroundByParkId(1000);
		assertNotNull(results);
		assertEquals(1000, results.get(0).getParkId());
		assertEquals("Test Campground", results.get(0).getName());
		assertEquals(1, results.get(0).getOpenFrom());
		assertEquals(2, results.get(0).getOpenTo());
		assertEquals(BigDecimal.valueOf(35.00), results.get(0).getDailyFee());
	}
	
	@Test
	public void test_getCampGroundFeeBySiteId_returns_35_for_Campground() {
		Campground theCampground = getCampground(1000, 1000, "Test Campground", 1, 2, BigDecimal.valueOf(35.00));
		dao.createACampground(theCampground);
		String sqlInsertSite = "INSERT INTO site (site_id, campground_id, site_number, max_occupancy,"
				+ " accessible, max_rv_length, utilities) " +
				"VALUES (1000, 1000, 1000, 1, false, 1, false)";
		jdbcTemplate.update(sqlInsertSite);
		Campground results = dao.getCampgroundDailyFeeBySiteId(1000);
		assertNotNull(results);
		assertEquals(BigDecimal.valueOf(35.00), results.getDailyFee());
	}
	
	@Test
	public void test_getCampgroundByParkName_returns_Test_Campground() {
		Campground theCampground = getCampground(1000, 1000, "Test Campground", 1, 2, BigDecimal.valueOf(35.00));
		dao.createACampground(theCampground);
		List <Campground> results = dao.getCampgroundByParkName("Test Park");
		assertNotNull(results);
		assertEquals("Test Campground", results.get(0).getName());
	}

}
