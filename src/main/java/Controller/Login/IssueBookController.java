package com.library.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.time.LocalDate;

public class IssueBookController {

    @FXML
    private ComboBox<String> memberComboBox;

    @FXML
    private ComboBox<String> bookComboBox;

    @FXML
    private DatePicker issueDatePicker;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private Label messageLabel;


    @FXML
    public void initialize() {

        // Sample Members

        memberComboBox.getItems().addAll(
                "M001 - Kamal Perera",
                "M002 - Nimali Silva",
                "M003 - Saman Fernando"
        );


        // Sample Books

        bookComboBox.getItems().addAll(
                "B001 - Java Programming",
                "B002 - Database Management",
                "B003 - Software Engineering",
                "B004 - Computer Networks"
        );


        // Set today's date

        issueDatePicker.setValue(
                LocalDate.now()
        );


        // Set default due date - 14 days

        dueDatePicker.setValue(
                LocalDate.now().plusDays(14)
        );
    }


    // ================= ISSUE BOOK =================

    @FXML
    private void handleIssueBook() {

        String member =
                memberComboBox.getValue();

        String book =
                bookComboBox.getValue();

        LocalDate issueDate =
                issueDatePicker.getValue();

        LocalDate dueDate =
                dueDatePicker.getValue();


        // Check Member

        if (member == null) {

            messageLabel.setText(
                    "Please select a member."
            );

            return;
        }


        // Check Book

        if (book == null) {

            messageLabel.setText(
                    "Please select a book."
            );

            return;
        }


        // Check Issue Date

        if (issueDate == null) {

            messageLabel.setText(
                    "Please select the issue date."
            );

            return;
        }


        // Check Due Date

        if (dueDate == null) {

            messageLabel.setText(
                    "Please select the due date."
            );

            return;
        }


        // Check Date Order

        if (dueDate.isBefore(issueDate)) {

            messageLabel.setText(
                    "Due date must be after the issue date."
            );

            return;
        }


        // Success Alert

        Alert alert = new Alert(
                Alert.AlertType.INFORMATION
        );

        alert.setTitle("Book Issued");

        alert.setHeaderText(
                "Book Issued Successfully"
        );

        alert.setContentText(
                "Member: " + member
                        + "\nBook: " + book
                        + "\nIssue Date: " + issueDate
                        + "\nDue Date: " + dueDate
        );

        alert.showAndWait();


        messageLabel.setText(
                "Book issued successfully!"
        );


        handleClear();
    }


    // ================= CLEAR =================

    @FXML
    private void handleClear() {

        memberComboBox.setValue(null);

        bookComboBox.setValue(null);

        issueDatePicker.setValue(
                LocalDate.now()
        );

        dueDatePicker.setValue(
                LocalDate.now().plusDays(14)
        );

        messageLabel.setText("");

        memberComboBox.requestFocus();
    }
}