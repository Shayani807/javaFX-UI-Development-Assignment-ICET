package com.library.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class ReturnBookController {

    @FXML
    private ComboBox<String> borrowedBookComboBox;

    @FXML
    private TextField memberField;

    @FXML
    private TextField bookField;

    @FXML
    private DatePicker borrowedDatePicker;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private DatePicker returnDatePicker;

    @FXML
    private Label statusLabel;

    @FXML
    private Label messageLabel;


    @FXML
    public void initialize() {

        borrowedBookComboBox.getItems().addAll(
                "B001 - Java Programming",
                "B002 - Database Management",
                "B003 - Software Engineering"
        );

        returnDatePicker.setValue(
                LocalDate.now()
        );

        statusLabel.setText("Not Checked");


        borrowedBookComboBox.setOnAction(event -> {

            if (borrowedBookComboBox.getValue() != null) {

                String selected =
                        borrowedBookComboBox.getValue();

                bookField.setText(selected);

                memberField.setText(
                        "M001 - Kamal Perera"
                );

                borrowedDatePicker.setValue(
                        LocalDate.now().minusDays(10)
                );

                dueDatePicker.setValue(
                        LocalDate.now().plusDays(4)
                );

                checkOverdueStatus();
            }
        });
    }


    // ================= CHECK OVERDUE =================

    private void checkOverdueStatus() {

        LocalDate dueDate =
                dueDatePicker.getValue();

        LocalDate returnDate =
                returnDatePicker.getValue();


        if (dueDate == null || returnDate == null) {

            statusLabel.setText(
                    "Not Checked"
            );

            return;
        }


        if (returnDate.isAfter(dueDate)) {

            statusLabel.setText(
                    "OVERDUE"
            );

        } else {

            statusLabel.setText(
                    "ON TIME"
            );
        }
    }


    // ================= RETURN BOOK =================

    @FXML
    private void handleReturnBook() {

        if (borrowedBookComboBox.getValue() == null) {

            messageLabel.setText(
                    "Please select a borrowed book."
            );

            return;
        }


        if (borrowedDatePicker.getValue() == null) {

            messageLabel.setText(
                    "Please select the borrowed date."
            );

            return;
        }


        if (dueDatePicker.getValue() == null) {

            messageLabel.setText(
                    "Please select the due date."
            );

            return;
        }


        if (returnDatePicker.getValue() == null) {

            messageLabel.setText(
                    "Please select the return date."
            );

            return;
        }


        LocalDate dueDate =
                dueDatePicker.getValue();

        LocalDate returnDate =
                returnDatePicker.getValue();


        if (returnDate.isBefore(
                borrowedDatePicker.getValue())) {

            messageLabel.setText(
                    "Return date cannot be before borrowed date."
            );

            return;
        }


        checkOverdueStatus();


        String status;

        if (returnDate.isAfter(dueDate)) {

            status = "OVERDUE";

        } else {

            status = "RETURNED ON TIME";
        }


        Alert alert = new Alert(
                Alert.AlertType.INFORMATION
        );

        alert.setTitle("Book Returned");

        alert.setHeaderText(
                "Book Returned Successfully"
        );

        alert.setContentText(
                "Book: "
                        + bookField.getText()
                        + "\nMember: "
                        + memberField.getText()
                        + "\nReturn Date: "
                        + returnDate
                        + "\nStatus: "
                        + status
        );

        alert.showAndWait();


        messageLabel.setText(
                "Book returned successfully!"
        );
    }


    // ================= CLEAR =================

    @FXML
    private void handleClear() {

        borrowedBookComboBox.setValue(null);

        memberField.clear();

        bookField.clear();

        borrowedDatePicker.setValue(null);

        dueDatePicker.setValue(null);

        returnDatePicker.setValue(
                LocalDate.now()
        );

        statusLabel.setText(
                "Not Checked"
        );

        messageLabel.setText("");

        borrowedBookComboBox.requestFocus();
    }
}