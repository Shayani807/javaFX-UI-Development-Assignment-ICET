package com.library.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class BorrowingHistoryController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<BorrowingRecord> historyTable;

    @FXML
    private TableColumn<BorrowingRecord, String> memberIdColumn;

    @FXML
    private TableColumn<BorrowingRecord, String> bookTitleColumn;

    @FXML
    private TableColumn<BorrowingRecord, LocalDate> issueDateColumn;

    @FXML
    private TableColumn<BorrowingRecord, LocalDate> dueDateColumn;

    @FXML
    private TableColumn<BorrowingRecord, LocalDate> returnDateColumn;

    @FXML
    private TableColumn<BorrowingRecord, String> statusColumn;

    @FXML
    private Label historyCountLabel;


    private ObservableList<BorrowingRecord> historyList =
            FXCollections.observableArrayList();


    @FXML
    public void initialize() {

        // Table columns
        memberIdColumn.setCellValueFactory(
                new PropertyValueFactory<>("memberId")
        );

        bookTitleColumn.setCellValueFactory(
                new PropertyValueFactory<>("bookTitle")
        );

        issueDateColumn.setCellValueFactory(
                new PropertyValueFactory<>("issueDate")
        );

        dueDateColumn.setCellValueFactory(
                new PropertyValueFactory<>("dueDate")
        );

        returnDateColumn.setCellValueFactory(
                new PropertyValueFactory<>("returnDate")
        );

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );


        // Sample borrowing records
        historyList.addAll(

                new BorrowingRecord(
                        "M001",
                        "Java Programming",
                        LocalDate.of(2026, 9, 10),
                        LocalDate.of(2026, 9, 24),
                        null,
                        "Borrowed"
                ),

                new BorrowingRecord(
                        "M002",
                        "Database Management",
                        LocalDate.of(2026, 9, 5),
                        LocalDate.of(2026, 9, 19),
                        LocalDate.of(2026, 9, 18),
                        "Returned"
                ),

                new BorrowingRecord(
                        "M003",
                        "Software Engineering",
                        LocalDate.of(2026, 9, 1),
                        LocalDate.of(2026, 9, 15),
                        null,
                        "Overdue"
                ),

                new BorrowingRecord(
                        "M001",
                        "Computer Networks",
                        LocalDate.of(2026, 9, 20),
                        LocalDate.of(2026, 10, 4),
                        null,
                        "Borrowed"
                )
        );


        historyTable.setItems(historyList);

        updateHistoryCount();
    }


    @FXML
    private void handleSearch() {

        String searchText =
                searchField.getText()
                        .trim()
                        .toLowerCase();


        if (searchText.isEmpty()) {

            historyTable.setItems(historyList);

            updateHistoryCount();

            return;
        }


        ObservableList<BorrowingRecord> searchResults =
                FXCollections.observableArrayList();


        for (BorrowingRecord record : historyList) {

            if (record.getMemberId()
                    .toLowerCase()
                    .contains(searchText)

                    || record.getBookTitle()
                    .toLowerCase()
                    .contains(searchText)

                    || record.getStatus()
                    .toLowerCase()
                    .contains(searchText)) {

                searchResults.add(record);
            }
        }


        historyTable.setItems(searchResults);


        historyCountLabel.setText(
                "Records: " + searchResults.size()
        );
    }


    @FXML
    private void handleClearSearch() {

        searchField.clear();

        historyTable.setItems(historyList);

        updateHistoryCount();
    }


    private void updateHistoryCount() {

        historyCountLabel.setText(
                "Records: " + historyList.size()
        );
    }


    // Borrowing Record Model
    public static class BorrowingRecord {

        private final String memberId;

        private final String bookTitle;

        private final LocalDate issueDate;

        private final LocalDate dueDate;

        private final LocalDate returnDate;

        private final String status;


        public BorrowingRecord(
                String memberId,
                String bookTitle,
                LocalDate issueDate,
                LocalDate dueDate,
                LocalDate returnDate,
                String status
        ) {

            this.memberId = memberId;

            this.bookTitle = bookTitle;

            this.issueDate = issueDate;

            this.dueDate = dueDate;

            this.returnDate = returnDate;

            this.status = status;
        }


        public String getMemberId() {

            return memberId;
        }


        public String getBookTitle() {

            return bookTitle;
        }


        public LocalDate getIssueDate() {

            return issueDate;
        }


        public LocalDate getDueDate() {

            return dueDate;
        }


        public LocalDate getReturnDate() {

            return returnDate;
        }


        public String getStatus() {

            return status;
        }
    }
}