package com.techelevator.camprgorund.model.jdbc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.camprgound.model.Reservation;
import com.techelevator.camprgound.model.ReservationDAO;

public class JDBCReservationDAO implements ReservationDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCReservationDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	private Reservation mapRowToReservation(SqlRowSet results) {
		Reservation res = new Reservation();
		res.setReservationId(results.getInt("reservation_id"));
		res.setSiteId(results.getInt("site_id"));
		res.setName(results.getString("name"));
		return res;
	}
	
	@Override
	public void createReservation(Reservation reservation) {
		String sqlCreateReservation = "INSERT INTO reservation (site_id, name, from_date, to_date, create_date) "
				+ "VALUES(?,?,?,?,?)";
		LocalDate date = LocalDate.now();
		jdbcTemplate.update(sqlCreateReservation, reservation.getSiteId(), reservation.getName(),
				reservation.getFromDate(), reservation.getToDate(), date);
	}
	
	@Override
	public List<Reservation> getAllReservations() {
		String sqlGetAllReservations = "SELECT * FROM reservation";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetAllReservations);
		List <Reservation> resList = new ArrayList<>();
		while (results.next()) {
			Reservation res = mapRowToReservation(results);
			resList.add(res);
		}
		return resList;
	}

	@Override
	public int getLatestReservationId() {
		String sqlGetLatestReservationId = "SELECT MAX (reservation_id) FROM reservation";
		SqlRowSet res = jdbcTemplate.queryForRowSet(sqlGetLatestReservationId);
		if (res.next()) {
			return res.getInt(1);
		}
		return res.getInt(1);
	}
}
