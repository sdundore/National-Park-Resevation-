package com.techelevator.projects.model.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

import com.techelevator.projects.model.Employee;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JDBCEmployeeDAOTest {

	JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
	private static SingleConnectionDataSource dataSource;
	private JDBCEmployeeDAO dao;

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

		String sqlCreateDepartment = "INSERT INTO department (department_id, name)" + 
				"VALUES(1000, 'Tasting')";
		String sqlCreateDepartmentTwo = "INSERT INTO department (department_id, name)" + 
				"VALUES(2000, 'Baking')";
		String sqlCreateProject = "INSERT INTO project (project_id, name)" + 
				"VALUES(1000, 'Test Project')";
		jdbcTemplate.update(sqlCreateDepartment);
		jdbcTemplate.update(sqlCreateDepartmentTwo);
		jdbcTemplate.update(sqlCreateProject);
		dao = new JDBCEmployeeDAO(dataSource);

	}

	public void createEmployee() {
		String sqlCreateEmployee = "INSERT INTO employee (employee_id, department_id, first_name, last_name, birth_date, gender, hire_date)"
				+ "VALUES(1000, 1000, 'Random', 'Guy', '2001-09-12', 'M', '2018-01-25')";
		jdbcTemplate.update(sqlCreateEmployee);
	}
	
	public void createEmployeeWithProject() {
		String sqlCreateEmployee = "INSERT INTO employee (employee_id, department_id, first_name, last_name, birth_date, gender, hire_date)"
				+ "VALUES(2000, 1000, 'Scott', 'Kirschner', '2001-09-12', 'M', '2018-01-25')";
		jdbcTemplate.update(sqlCreateEmployee);
		String sqlCreateProjectEmployee = "INSERT INTO project_employee (project_id, employee_id)" + 
				"VALUES(1000, 2000)";
		jdbcTemplate.update(sqlCreateProjectEmployee);
	}

	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}

	@Test
	public void test_getAllEmployees_gets_all_employees() {
		List<Employee> results = dao.getAllEmployees();
		assertNotNull(results);
		assertEquals(12, results.size());
	}

	@Test
	public void test_searchEmployeesByName_finds_employee() {
		createEmployee();
		List<Employee> results = dao.searchEmployeesByName("Random", "Guy");
		assertNotNull(results);
		assertEquals("Random", results.get(0).getFirstName());
		assertEquals("Guy", results.get(0).getLastName());
	}

	@Test
	public void test_getEmployeesByDepartment_finds_employee() {
		createEmployee();
		List<Employee> results = dao.getEmployeesByDepartmentId(1000);
		assertNotNull(results);
		assertEquals("Random", results.get(0).getFirstName());
		assertEquals("Guy", results.get(0).getLastName());
	}
	
	@Test
	public void test_getEmployeesWithoutProjects_finds_employee() {
		createEmployee();
		List<Employee> results = dao.getEmployeesWithoutProjects();
		assertNotNull(results);
		boolean hasNoProject = false;
		for (Employee guy: results) {
			if (guy.getId() == 1000) {
				hasNoProject = true;
			}
		}
		assertTrue(hasNoProject);
	}
	
	@Test
	public void test_getEmployeesByProjectId_finds_employee() {
		createEmployeeWithProject();
		List<Employee> results = dao.getEmployeesByProjectId((long) 1000);
		assertNotNull(results);
		boolean hasProject = false;
		for (Employee scott: results) {
			if (scott.getId() == 2000) {
				hasProject = true;
			}
		}
		assertTrue(hasProject);
	}
	
	@Test
	public void test_changeEmployeeDepartment_changes_department() {
		createEmployee();
		dao.changeEmployeeDepartment((long) 1000,(long) 2000);
		List<Employee> results = dao.getEmployeesByDepartmentId(2000);
		assertNotNull(results);
		assertEquals("Random", results.get(0).getFirstName());
		assertEquals("Guy", results.get(0).getLastName());
	}

}
