package com.techelevator.view;


import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.services.TEnmoService;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleService {

	TEnmoService tenmoService;
	private PrintWriter out;
	private Scanner in;

	public ConsoleService(InputStream input, OutputStream output, TEnmoService tEnmoService) {
		this.out = new PrintWriter(output, true);
		this.in = new Scanner(input);
		this.tenmoService = tEnmoService;
	}

	public TEnmoService getTenmoService() { return tenmoService; }

	public Object getChoiceFromOptions(Object[] options) {
		Object choice = null;
		while (choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		out.println();
		return choice;
	}

	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null;
		String userInput = in.nextLine();
		try {
			int selectedOption = Integer.valueOf(userInput);
			if (selectedOption > 0 && selectedOption <= options.length) {
				choice = options[selectedOption - 1];
			}
		} catch (NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if (choice == null) {
			out.println(System.lineSeparator() + "*** " + userInput + " is not a valid option ***" + System.lineSeparator());
		}
		return choice;
	}

	private void displayMenuOptions(Object[] options) {
		out.println();
		for (int i = 0; i < options.length; i++) {
			int optionNum = i + 1;
			out.println(optionNum + ") " + options[i]);
		}
		out.print(System.lineSeparator() + "Please choose an option >>> ");
		out.flush();
	}

	public String getUserInput(String prompt) {
		out.print(prompt+": ");
		out.flush();
		return in.nextLine();
	}

	public Integer getUserInputInteger(String prompt) {
		Integer result = null;
		do {
			out.print(prompt+": ");
			out.flush();
			String userInput = in.nextLine();
			try {
				result = Integer.parseInt(userInput);
			} catch(NumberFormatException e) {
				out.println(System.lineSeparator() + "*** " + userInput + " is not valid ***" + System.lineSeparator());
			}
		} while(result == null);
		return result;
	}

	public void getBalance() {
		int userId = tenmoService.getCurrentUser().getUser().getId();
		BigDecimal accountBalance = tenmoService.getBalance(userId);
		System.out.println("Your current account balance is: $" + accountBalance);
	}

	public void transferMoney() {
//		int[] userArray = new int[userList.size()];
		List<User> userList = tenmoService.listOfUsers();
		Transfer transfer = new Transfer();
		System.out.println("-------------------------------------------");
		System.out.println("Users\nID				Name");
		System.out.println("-------------------------------------------");

		for(int i = 0; i < userList.size(); i++) {
			User user = userList.get(i);
			String userInfo = String.format("%d				%s", user.getId(), user.getUsername());
			System.out.println(userInfo);
		}

		System.out.println("Enter ID of user you are sending to (0 to cancel): ");
		String userInput = in.nextLine();
		if(!userInput.equals("0")) {
			int toAccount = Integer.parseInt(userInput);
			boolean isValid = false;
			for (User user : userList) {
				if (user.getId() == toAccount) {
					isValid = true;
				}
			}
			if (isValid) {
				transfer.setAccountTo(toAccount);
				transfer.setAccountFrom(tenmoService.getCurrentUser().getUser().getId());
				System.out.println("Enter Amount");
				String amountString = in.nextLine();
				int amount = Integer.parseInt(amountString);
				transfer.setAmount(BigDecimal.valueOf(amount));
				transfer.setTransferIdType(2);
				transfer.setTransferStatusId(2);
				tenmoService.sendTransfer(transfer);
			} else {
				transferMoney();
			}
		}
	}

	public void listOfTransfers() {
		System.out.println("-------------------------------------------\n" +
				"Transfers\n" +
				"ID From/To Amount\n" +
				"-------------------------------------------\n");
		User getUser = tenmoService.getCurrentUser().getUser();
		List<Transfer> transferList = tenmoService.listOfTransfers(getUser.getId());
		List<Transfer> usersTransferList = new ArrayList<>();

		for (Transfer transfer: transferList) {
			System.out.println(String.format("%d To: %s $ %s",transfer.getTransferId(), tenmoService.getUsername(transfer.getAccountTo()) , transfer.getAmount().toString()));
			usersTransferList.add(transfer);
		}

		System.out.println("---------\n" +
				"Please enter transfer ID to view details (0 to cancel): \"\n");

		String userInput = in.nextLine();

		if(!userInput.equals("0")) {
			int transferId = Integer.parseInt(userInput);
			boolean isContains = false;
			for (Transfer transfer: usersTransferList) {
				if (transfer.getTransferId() == transferId) {
					isContains = true;
				}
			}

			Transfer transfer = tenmoService.getTransferDetails(transferId);
			if (transfer != null && isContains) {
				System.out.println("--------------------------------------------\n" +
						"Transfer Details\n" +
						"--------------------------------------------\n");
				System.out.println(String.format(" Id: %d\n" +
						" From: %s\n" +
						" To: %s\n" +
						" Type: %s\n" +
						" Status: %s\n" +
						" Amount: $%s\n----------------------", transfer.getTransferId(), getUser.getUsername(), tenmoService.getUsername(transfer.getAccountTo()),
						tenmoService.getTransferTypeString(transfer.getTransferTypeId()), tenmoService.getTransferStatusString(transfer.getTransferStatusId()),
						transfer.getAmount().toString()));
			}
			else {
				System.out.println("You have entered an invalid transfer Id.");
				listOfTransfers();
			}
		}
	}


}
