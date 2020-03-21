package com.techelevator.projects.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.DepartmentDAO;

public class JDBCDepartmentDAO implements DepartmentDAO {
	
	private JdbcTemplate jdbcTemplate;

	public JDBCDepartmentDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Department> getAllDepartments() {
		List <Department> departments = new ArrayList<>();
		String sqlGetAllDepartments = "SELECT department_id, name FROM department";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetAllDepartments);
		while (results.next()) {
			Department department = mapRowToDepartment(results);
			departments.add(department);
		}
	
		return departments;
	}
	
	private Department mapRowToDepartment(SqlRowSet results) {
		Department department = new Department();
	//	department.setId(results.getLong("department_id"));
		department.setName(results.getString("name"));
		return department;
	}

	@Override
	public List<Department> searchDepartmentsByName(String nameSearch) {
		List <Department> departments = new ArrayList<>();
		String sqlSearchDepartmentsByName = "SELECT department_id, name FROM department " +
				"WHERE name = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSearchDepartmentsByName, nameSearch);
		while (results.next()) {
			Department department = mapRowToDepartment(results);
			departments.add(department);
		}
	
		return departments;
	}


	@Override
	public void saveDepartment(Department updatedDepartment) {
		String sqlSaveDepartment = "UPDATE department SET name = ? WHERE department_id = ?";  
		jdbcTemplate.update(sqlSaveDepartment, updatedDepartment.getName(), updatedDepartment.getId());
	}

	@Override
	public Department createDepartment(Department newDepartment) {
		String sqlCreateDepartment = "INSERT INTO department (name) VALUES(?)";  				
		jdbcTemplate.update(sqlCreateDepartment, newDepartment.getName());
		return newDepartment;
	}

	@Override
	public Department getDepartmentById(Long id) {
		Department departmentId = new Department();
		String sqlSearchDepartmentsByName = "SELECT department_id, name FROM department " +
				"WHERE department_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSearchDepartmentsByName, id);
		if (results.next()) {
			departmentId = mapRowToDepartment(results);
		}
			return departmentId;
	}

}
