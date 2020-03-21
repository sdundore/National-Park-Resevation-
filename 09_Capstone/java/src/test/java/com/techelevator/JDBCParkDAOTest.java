package com.techelevator;

import static org.junit.Assert.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import com.techelevator.camprgorund.model.jdbc.JDBCParkDAO;
import com.techelevator.camprgound.model.Park;


public class JDBCParkDAOTest {
	
	JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
	private static SingleConnectionDataSource dataSource;
	private JDBCParkDAO dao;
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
		dao = new JDBCParkDAO(dataSource);

	}

	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}
	
	private Park getPark(int parkId, String name, String location, Date estDate, int area, 
			int visitors, String description) {
		Park thePark = new Park();
		thePark.setParkId(parkId);
		thePark.setName(name);
		thePark.setLocation(location);
		thePark.setEstDate(estDate);
		thePark.setArea(area);
		thePark.setVisitors(visitors);
		thePark.setDescription(description);
		
		return thePark;
	}

	@Test
	public void test_getAllParks_gets_all_parks() {
		List<Park> results = dao.getAllParks();
		int startNum = results.size();
		Park thePark = getPark(1000, "Test Park", "Test Location", sqlDate, 
				100000, 100000, "Test Description");
		dao.createAPark(thePark);
		List<Park> results2 = dao.getAllParks();
		int endNum = results2.size();
		assertNotNull(results);
		assertEquals(startNum + 1, endNum);
	}
	
	@Test
	public void test_getParkInformation_gets_park_information() {
		Park thePark = getPark(1000, "Test Park", "Test Location", sqlDate, 
				100000, 100000, "Test Description");
		dao.createAPark(thePark);
		List<Park> results = dao.getParkInformation("Test Park");
		assertNotNull(results);
		assertEquals(thePark.getParkId(),results.get(0).getParkId());
		assertEquals(thePark.getName(),results.get(0).getName());
		assertEquals(thePark.getLocation(),results.get(0).getLocation());
		assertEquals(thePark.getArea(),results.get(0).getArea());
		assertEquals(thePark.getVisitors(),results.get(0).getVisitors());
		assertEquals(thePark.getDescription(),results.get(0).getDescription());
	}

}	
