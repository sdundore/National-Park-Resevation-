package com.techelevator.projects.model.jdbc;

import static org.junit.Assert.*;

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
import com.techelevator.projects.model.Project;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JDBCProjectDAOTest {

	JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
	private static SingleConnectionDataSource dataSource;
	private JDBCProjectDAO dao;
	private JDBCEmployeeDAO daoEmp;

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

		String sqlCreateDepartment = "INSERT INTO department (department_id, name)" + "VALUES(1000, 'Tasting')";
		String sqlCreateDepartmentTwo = "INSERT INTO department (department_id, name)" + "VALUES(2000, 'Baking')";
		String sqlCreateProject = "INSERT INTO project (project_id, name)" + "VALUES(1000, 'Test Project')";
		jdbcTemplate.update(sqlCreateDepartment);
		jdbcTemplate.update(sqlCreateDepartmentTwo);
		jdbcTemplate.update(sqlCreateProject);
		dao = new JDBCProjectDAO(dataSource);
		daoEmp = new JDBCEmployeeDAO(dataSource);
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
		String sqlCreateProjectEmployee = "INSERT INTO project_employee (project_id, employee_id)"
				+ "VALUES(1000, 2000)";
		jdbcTemplate.update(sqlCreateProjectEmployee);
	}

	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}

	@Test
	public void test_getAllActiveProjects_gets_all_projects() {
		List<Project> results = dao.getAllActiveProjects();
		assertNotNull(results);
		assertEquals(3, results.size());
	}

	@Test
	public void test_removeEmployeeFromProject_removes_employee_from_project() {
		createEmployeeWithProject();
		List<Employee> results1 = daoEmp.getEmployeesByProjectId((long) 1000);
		assertNotNull(results1);
		dao.removeEmployeeFromProject((long) 1000, (long) 2000);
		List<Employee> results2 = daoEmp.getEmployeesByProjectId((long) 1000);
		assertNotNull(results2);
		assertEquals(results2.size(), results1.size() - 1);
	}

	@Test
	public void test_addEmployeeToProject_adds_employee_emplpoyee_project() {
		createEmployee();
		List<Employee> results1 = daoEmp.getEmployeesByProjectId((long) 1000);
		assertNotNull(results1);
		dao.addEmployeeToProject((long) 1000, (long) 1000);
		List<Employee> results2 = daoEmp.getEmployeesByProjectId((long) 1000);
		assertNotNull(results2);
		assertEquals(results2.size(), results1.size() + 1);
	}
}
