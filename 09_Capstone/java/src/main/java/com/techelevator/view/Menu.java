package com.techelevator.view;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class Menu {

	private PrintWriter out;
	private Scanner in;

	public Menu(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output);
		this.in = new Scanner(input);
	}

	public Object getChoiceFromOptions(Object[] options) {
		Object choice = null;
		while (choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		return choice;
	}

	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null;
		String userInput = in.nextLine();
		try {
			int selectedOption = Integer.valueOf(userInput);
			if (selectedOption <= options.length) {
				choice = options[selectedOption - 1];
			}
		} catch (NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will
			// be null
		}
		if (choice == null) {
			out.println("\n*** " + userInput + " is not a valid option ***\n");
		}
		return choice;
	}

	private void displayMenuOptions(Object[] options) {
		out.println();
		for (int i = 0; i < options.length; i++) {
			int optionNum = i + 1;
			out.println(optionNum + ") " + options[i]);

		}
		out.print("\nPlease choose an option >>> ");
		out.flush();
	}

	public Object getChoiceFromCampgroundOptions(Object[] options) {
		Object choice = null;
		while (choice == null) {
			displayCampgroundMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		return choice;
	}

	private void displayCampgroundMenuOptions(Object[] options) {
		out.println();
		out.printf("%10s %25s %11s %14s\n", "Name", "Open", "Close", "Daily Fee");
		for (int i = 0; i < options.length; i++) {
			int optionNum = i + 1;
			out.println("#" + optionNum + "    " + options[i]);
		}
		out.print("\nWhich campground would you like to select? ");
		out.flush();
	}

	public Object getChoiceFromSiteOptions(Object[] options) {
		Object choice = null;
		while (choice == null) {
			displaySiteMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		return choice;
	}

	private void displaySiteMenuOptions(Object[] options) {
		out.println();
		for (int i = 0; i < options.length; i++) {
			int optionNum = i + 1;
			out.print(optionNum + ") " + options[i]);
		}
		out.print("\n\nWhich site should be reserved? ");
		out.flush();
	}
}
