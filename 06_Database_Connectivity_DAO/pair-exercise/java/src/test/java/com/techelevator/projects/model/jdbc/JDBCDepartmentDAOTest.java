package com.techelevator.projects.model.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import com.techelevator.projects.model.Department;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class JDBCDepartmentDAOTest {

	JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
	private static SingleConnectionDataSource dataSource;
	private JDBCDepartmentDAO dao;

	@BeforeClass
	public static void setupDatasource() {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/department_projects");
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
		dao = new JDBCDepartmentDAO(dataSource);

	}

	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}

	private void assertDepartmentsAreEquals(Department theDept, Department savedDept) {

		assertEquals(theDept.getId(), savedDept.getId());
		assertEquals(theDept.getName(), savedDept.getName());
	}

	@Test
	public void test_save_department_updates_department() {
		Department theDept = new Department();
		theDept.setName("Tasting");
		dao.createDepartment(theDept);
		List<Department> results = dao.searchDepartmentsByName("Tasting");
		Department newName = results.get(0);
		newName.setName("Searching");
		dao.saveDepartment(newName);
		assertEquals(newName.getId(), theDept.getId());
		assertEquals("Searching", newName.getName());
	}

	@Test
	public void test_create_department_creates_department() {
		Department theDept = new Department();
		theDept.setName("Tasting");
		dao.createDepartment(theDept);
		List<Department> results = dao.searchDepartmentsByName(theDept.getName());
		assertNotNull(results);
		assertEquals(1, results.size());
		Department savedDept = results.get(0);
		assertDepartmentsAreEquals(theDept, savedDept);
	}

	@Test
	public void test_getAllDepartments_gets_all_departments() {
		List<Department> results = dao.getAllDepartments();
		assertNotNull(results);
		assertEquals(4, results.size());
	}

	private void createDepartment() {
		String sqlCreateDepartment = "INSERT INTO department (department_id, name)" + "VALUES(1000, 'Tasting')";
		jdbcTemplate.update(sqlCreateDepartment);

	}

	@Test
	public void test_get_department_by_id_gets_department() {
		createDepartment();
		Department results = dao.getDepartmentById((long) 1000);
		assertNotNull(results);
		assertEquals("Tasting", results.getName());
	}

}