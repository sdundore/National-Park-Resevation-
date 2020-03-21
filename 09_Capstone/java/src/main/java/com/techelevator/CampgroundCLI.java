package com.techelevator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import com.techelevator.camprgorund.model.jdbc.JDBCCampgroundDAO;
import com.techelevator.camprgorund.model.jdbc.JDBCParkDAO;
import com.techelevator.camprgorund.model.jdbc.JDBCReservationDAO;
import com.techelevator.camprgorund.model.jdbc.JDBCSiteDAO;
import com.techelevator.camprgound.model.Campground;
import com.techelevator.camprgound.model.CampgroundDAO;
import com.techelevator.camprgound.model.Park;
import com.techelevator.camprgound.model.ParkDAO;
import com.techelevator.camprgound.model.Reservation;
import com.techelevator.camprgound.model.ReservationDAO;
import com.techelevator.camprgound.model.Site;
import com.techelevator.camprgound.model.SiteDAO;
import com.techelevator.view.Menu;

public class CampgroundCLI {

	private Menu menu;
	private static ParkDAO parkDAO;
	private static CampgroundDAO campgroundDAO;
	private static SiteDAO siteDAO;
	private static ReservationDAO reservationDAO;
	private static CampgroundCLI application;
	private String parkChoice = "";
	private int campgroundChoiceId = 0;
	private int siteChoiceId = 0;
	private List<Site> siteList = new ArrayList<>();
	private String arrival = "";
	private String departure = "";

	private static final String CAMPGROUND_MENU_VIEW_CAMPGROUNDS = "View Campgrounds";
	private static final String CAMPGROUND_MENU_SEARCH_RESERVATION = "Search For Reservation";
	private static final String CAMPGROUND_MENU_RETURN = "Return To Previous Screen";
	private static final String[] CAMPGROUND_MENU_OPTIONS = new String[] { CAMPGROUND_MENU_VIEW_CAMPGROUNDS,
			CAMPGROUND_MENU_SEARCH_RESERVATION, CAMPGROUND_MENU_RETURN };

	private static final String RESERVATION_MENU_SEARCH_RESERVATION = "Search For Available Reservation";
	private static final String RESERVATION_MENU_RETURN = "Return To Previous Screen";
	private static final String[] RESERVATION_MENU_OPTIONS = new String[] { RESERVATION_MENU_SEARCH_RESERVATION,
			RESERVATION_MENU_RETURN };

	public static void main(String[] args) {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/campground");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		application = new CampgroundCLI(dataSource);
		application.run();
	}

	public CampgroundCLI(DataSource datasource) {
		parkDAO = new JDBCParkDAO(datasource);
		campgroundDAO = new JDBCCampgroundDAO(datasource);
		siteDAO = new JDBCSiteDAO(datasource);
		reservationDAO = new JDBCReservationDAO(datasource);
		this.menu = new Menu(System.in, System.out);
	}

	private void run() {
		handleParks();
	}

	private void handleParks() {
		boolean parkMenu = true;
		System.out.println("Select a park for further information.");
		while (parkMenu) {
			List<Park> parkList = parkDAO.getAllParks();
			String[] nameList = new String[parkList.size() + 1];
			for (int i = 0; i < parkList.size(); i++) {
				nameList[i] = parkList.get(i).getName();
			}
			nameList[nameList.length - 1] = "Quit";
			String choice = (String) menu.getChoiceFromOptions(nameList);
			if (choice.equals(nameList[nameList.length - 1])) {
				System.out.println("Goodbye!");
				System.exit(0);
			} else {
				parkChoice = choice;
				parkDAO.getParkInformation(parkChoice);
				parkMenu = false;
			}
		}
		handleCampground();
	}

	private void handleCampground() {
		boolean campgroundMenu = true;
		while (campgroundMenu) {
			String choice = (String) menu.getChoiceFromOptions(CAMPGROUND_MENU_OPTIONS);
			if (choice.equals(CAMPGROUND_MENU_VIEW_CAMPGROUNDS)) {
				handleSite();
				campgroundMenu = false;
			} else if (choice.equals(CAMPGROUND_MENU_SEARCH_RESERVATION)) {
				handleReservation();
				campgroundMenu = false;
			} else if (choice.equals(CAMPGROUND_MENU_RETURN)) {
				handleParks();
				campgroundMenu = false;
			}
		}
	}

	private void handleSite() {
		boolean reservationMenu = true;
		campgroundDAO.printCampgroundInformation(campgroundDAO.getCampgroundByParkName(parkChoice));
		while (reservationMenu) {
			String choice = (String) menu.getChoiceFromOptions(RESERVATION_MENU_OPTIONS);
			if (choice.equals(RESERVATION_MENU_SEARCH_RESERVATION)) {
				handleReservation();
			} else if (choice.equals(RESERVATION_MENU_RETURN)) {
				handleCampground();
				reservationMenu = false;
			}
		}
	}

	private void handleReservation() {
		List<Campground> campgroundList = campgroundDAO.getCampgroundByParkName(parkChoice);
		String[] nameList = new String[campgroundList.size() + 1];
		for (int i = 0; i < campgroundList.size(); i++) {
			nameList[i] = campgroundList.get(i).makeString(campgroundList.get(i));
		}
		nameList[nameList.length - 1] = "Cancel";
		String campgroundChoice = (String) menu.getChoiceFromCampgroundOptions(nameList);
		for (int x = 0; x < campgroundList.size(); x++) {
			if (campgroundChoice.equals(nameList[nameList.length - 1])) {
				handleSite();
			} else if (campgroundChoice.contains(campgroundList.get(x).getName())) {
				campgroundChoiceId = campgroundList.get(x).getCampgroundId();
			}
		}
		getReservationDates();
	}

	private void getReservationDates() {
		Scanner input = new Scanner(System.in);
		boolean format = false;
		while (!format) {
			System.out.println("\nWhat is the arrival date (YYYY-MM-DD) ? ");
			arrival = input.nextLine();
			if (arrival.matches("\\d{4}-\\d{2}-\\d{2}")) {
				format = true;
			} else {
				System.out.println("\nPlease enter a valid date");
			}
		}
		format = false;
		while (!format) {
			System.out.println("\nWhat is the departure date (YYYY-MM-DD) ? ");
			departure = input.nextLine();
			if (arrival.matches("\\d{4}-\\d{2}-\\d{2}")) {
				format = true;
			} else {
				System.out.println("\nPlease enter a valid date");
			}
		}
		siteList = siteDAO.getSiteByAvailability(convertFromDate(arrival), convertToDate(departure),
				campgroundChoiceId);
		siteDAO.printSiteInformation();
		displayReservationOptions();
		insertReservation();
		input.close();
	}

	private void displayReservationOptions() {
		String[] siteArray = new String[siteList.size()];
		for (int i = 0; i < siteArray.length; i++) {
			siteArray[i] = siteList.get(i).makeString(siteList.get(i)) + siteDAO.getCost(convertFromDate(arrival),
					convertToDate(departure), campgroundDAO.getCampgroundDailyFeeBySiteId(siteList.get(i).getSiteId()));
		}
		if (siteArray.length == 0) {
			System.out.println("\nThere are no available sites for those dates.");
			handleReservation();
		}
		siteArray[siteArray.length - 1] = "Cancel";
		String siteChoice = (String) menu.getChoiceFromSiteOptions(siteArray);
		for (int x = 0; x < siteList.size(); x++) {
			if (siteChoice.equals(siteArray[siteArray.length - 1])) {
				handleReservation();
			} else if (siteChoice.contains(siteList.get(x).makeString(siteList.get(x)))) {
				siteChoiceId = siteList.get(x).getSiteId();
			}
		}
	}

	private void insertReservation() {
		Scanner input = new Scanner(System.in);
		System.out.println("\nWhat name should the reservation be under? ");
		String custName = input.nextLine();
		Reservation newRes = new Reservation(campgroundChoiceId, custName, convertFromDate(arrival),
				convertToDate(departure));
		reservationDAO.createReservation(newRes);
		System.out.println("The reservation has been made. Your confirmation ID is "
				+ reservationDAO.getLatestReservationId() + "\n");
		handleParks();
		input.close();
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
