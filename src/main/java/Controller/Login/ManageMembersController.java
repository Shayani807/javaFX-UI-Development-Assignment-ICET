package com.library.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class ManageMembersController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Member> memberTable;

    @FXML
    private TableColumn<Member, String> memberIdColumn;

    @FXML
    private TableColumn<Member, String> nameColumn;

    @FXML
    private TableColumn<Member, String> emailColumn;

    @FXML
    private TableColumn<Member, String> phoneColumn;

    @FXML
    private TableColumn<Member, String> addressColumn;

    @FXML
    private Label memberCountLabel;


    private final ObservableList<Member> memberList =
            FXCollections.observableArrayList();


    // ==========================================
    // INITIALIZE
    // ==========================================

    @FXML
    public void initialize() {

        memberIdColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().getMemberId()
                )
        );

        nameColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().getFullName()
                )
        );

        emailColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().getEmail()
                )
        );

        phoneColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().getPhone()
                )
        );

        addressColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().getAddress()
                )
        );


        // Sample Members
        memberList.addAll(

                new Member(
                        "M001",
                        "Kamal Perera",
                        "kamal@gmail.com",
                        "0712345678",
                        "Colombo"
                ),

                new Member(
                        "M002",
                        "Nimali Silva",
                        "nimali@gmail.com",
                        "0771234567",
                        "Negombo"
                ),

                new Member(
                        "M003",
                        "Saman Fernando",
                        "saman@gmail.com",
                        "0759876543",
                        "Gampaha"
                ),

                new Member(
                        "M004",
                        "Amaya Perera",
                        "amaya@gmail.com",
                        "0764567890",
                        "Kandy"
                )
        );


        memberTable.setItems(memberList);

        updateMemberCount();
    }


    // ==========================================
    // SEARCH
    // ==========================================

    @FXML
    private void handleSearch() {

        String searchText =
                searchField.getText()
                        .trim()
                        .toLowerCase();

        if (searchText.isEmpty()) {

            memberTable.setItems(memberList);

            updateMemberCount();

            return;
        }


        ObservableList<Member> searchResults =
                FXCollections.observableArrayList();


        for (Member member : memberList) {

            if (
                    member.getMemberId()
                            .toLowerCase()
                            .contains(searchText)

                            || member.getFullName()
                            .toLowerCase()
                            .contains(searchText)

                            || member.getEmail()
                            .toLowerCase()
                            .contains(searchText)

                            || member.getPhone()
                            .contains(searchText)

                            || member.getAddress()
                            .toLowerCase()
                            .contains(searchText)
            ) {

                searchResults.add(member);
            }
        }


        memberTable.setItems(searchResults);

        memberCountLabel.setText(
                "Members: " + searchResults.size()
        );
    }


    // ==========================================
    // CLEAR SEARCH
    // ==========================================

    @FXML
    private void handleClearSearch() {

        searchField.clear();

        memberTable.setItems(memberList);

        updateMemberCount();
    }


    // ==========================================
    // EDIT MEMBER
    // ==========================================

    @FXML
    private void handleEdit() {

        Member selectedMember =
                memberTable.getSelectionModel()
                        .getSelectedItem();


        // Check selection
        if (selectedMember == null) {

            Alert alert =
                    new Alert(Alert.AlertType.WARNING);

            alert.setTitle("Edit Member");

            alert.setHeaderText(
                    "No Member Selected"
            );

            alert.setContentText(
                    "Please select a member from the table."
            );

            alert.showAndWait();

            return;
        }


        // ======================================
        // CREATE EDIT FIELDS
        // ======================================

        TextField memberIdField =
                new TextField(
                        selectedMember.getMemberId()
                );

        TextField nameField =
                new TextField(
                        selectedMember.getFullName()
                );

        TextField emailField =
                new TextField(
                        selectedMember.getEmail()
                );

        TextField phoneField =
                new TextField(
                        selectedMember.getPhone()
                );

        TextField addressField =
                new TextField(
                        selectedMember.getAddress()
                );


        // Member ID cannot be changed
        memberIdField.setDisable(true);


        // ======================================
        // GRID
        // ======================================

        GridPane grid =
                new GridPane();

        grid.setHgap(10);

        grid.setVgap(12);

        grid.setPadding(
                new Insets(10, 10, 10, 10)
        );


        grid.add(
                new Label("Member ID:"),
                0,
                0
        );

        grid.add(
                memberIdField,
                1,
                0
        );


        grid.add(
                new Label("Full Name:"),
                0,
                1
        );

        grid.add(
                nameField,
                1,
                1
        );


        grid.add(
                new Label("Email:"),
                0,
                2
        );

        grid.add(
                emailField,
                1,
                2
        );


        grid.add(
                new Label("Phone Number:"),
                0,
                3
        );

        grid.add(
                phoneField,
                1,
                3
        );


        grid.add(
                new Label("Address:"),
                0,
                4
        );

        grid.add(
                addressField,
                1,
                4
        );


        // Set field width
        nameField.setPrefWidth(300);
        emailField.setPrefWidth(300);
        phoneField.setPrefWidth(300);
        addressField.setPrefWidth(300);


        // ======================================
        // DIALOG
        // ======================================

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle("Edit Member");

        dialog.setHeaderText(
                "Edit Member Details"
        );


        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        ButtonType.OK,
                        ButtonType.CANCEL
                );


        dialog.getDialogPane()
                .setContent(grid);


        // ======================================
        // SAVE BUTTON VALIDATION
        // ======================================

        Button saveButton =
                (Button) dialog.getDialogPane()
                        .lookupButton(ButtonType.OK);


        saveButton.addEventFilter(
                javafx.event.ActionEvent.ACTION,
                event -> {

                    String name =
                            nameField.getText().trim();

                    String email =
                            emailField.getText().trim();

                    String phone =
                            phoneField.getText().trim();

                    String address =
                            addressField.getText().trim();


                    // Name validation
                    if (name.isEmpty()) {

                        showWarning(
                                "Please enter the member name."
                        );

                        event.consume();

                        return;
                    }


                    // Email validation
                    if (!email.contains("@")
                            || !email.contains(".")) {

                        showWarning(
                                "Please enter a valid email address."
                        );

                        event.consume();

                        return;
                    }


                    // Phone validation
                    if (!phone.matches("\\d{10}")) {

                        showWarning(
                                "Phone number must contain 10 digits."
                        );

                        event.consume();

                        return;
                    }


                    // Address validation
                    if (address.isEmpty()) {

                        showWarning(
                                "Please enter the address."
                        );

                        event.consume();
                    }
                }
        );


        // ======================================
        // SHOW DIALOG
        // ======================================

        dialog.showAndWait()
                .ifPresent(result -> {

                    if (result == ButtonType.OK) {

                        // Update Member
                        selectedMember.setFullName(
                                nameField.getText().trim()
                        );

                        selectedMember.setEmail(
                                emailField.getText().trim()
                        );

                        selectedMember.setPhone(
                                phoneField.getText().trim()
                        );

                        selectedMember.setAddress(
                                addressField.getText().trim()
                        );


                        // Refresh Table
                        memberTable.refresh();


                        // Success message
                        Alert success =
                                new Alert(
                                        Alert.AlertType.INFORMATION
                                );

                        success.setTitle(
                                "Member Updated"
                        );

                        success.setHeaderText(
                                "Member Updated Successfully"
                        );

                        success.setContentText(
                                "The member details have been updated."
                        );

                        success.showAndWait();
                    }
                });
    }


    // ==========================================
    // WARNING MESSAGE
    // ==========================================

    private void showWarning(String message) {

        Alert alert =
                new Alert(Alert.AlertType.WARNING);

        alert.setTitle("Validation Error");

        alert.setHeaderText(
                "Invalid Information"
        );

        alert.setContentText(message);

        alert.showAndWait();
    }


    // ==========================================
    // DELETE MEMBER
    // ==========================================

    @FXML
    private void handleDelete() {

        Member selectedMember =
                memberTable.getSelectionModel()
                        .getSelectedItem();


        if (selectedMember == null) {

            Alert alert =
                    new Alert(Alert.AlertType.WARNING);

            alert.setTitle("Delete Member");

            alert.setHeaderText(
                    "No Member Selected"
            );

            alert.setContentText(
                    "Please select a member from the table."
            );

            alert.showAndWait();

            return;
        }


        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Delete Member"
        );

        confirmation.setHeaderText(
                "Delete Member"
        );

        confirmation.setContentText(

                "Are you sure you want to delete\n\n"
                        + selectedMember.getFullName()
                        + " ("
                        + selectedMember.getMemberId()
                        + ")?"
        );


        confirmation.showAndWait()
                .ifPresent(response -> {

                    if (response == ButtonType.OK) {

                        memberList.remove(
                                selectedMember
                        );

                        memberTable.setItems(
                                memberList
                        );

                        updateMemberCount();


                        Alert success =
                                new Alert(
                                        Alert.AlertType.INFORMATION
                                );

                        success.setTitle(
                                "Member Deleted"
                        );

                        success.setHeaderText(
                                "Member Deleted Successfully"
                        );

                        success.setContentText(
                                "The member has been deleted."
                        );

                        success.showAndWait();
                    }
                });
    }


    // ==========================================
    // UPDATE COUNT
    // ==========================================

    private void updateMemberCount() {

        memberCountLabel.setText(
                "Members: "
                        + memberList.size()
        );
    }


    // ==========================================
    // MEMBER MODEL
    // ==========================================

    public static class Member {

        private final String memberId;

        private String fullName;

        private String email;

        private String phone;

        private String address;


        public Member(
                String memberId,
                String fullName,
                String email,
                String phone,
                String address
        ) {

            this.memberId = memberId;

            this.fullName = fullName;

            this.email = email;

            this.phone = phone;

            this.address = address;
        }


        public String getMemberId() {
            return memberId;
        }


        public String getFullName() {
            return fullName;
        }


        public String getEmail() {
            return email;
        }


        public String getPhone() {
            return phone;
        }


        public String getAddress() {
            return address;
        }


        // ======================================
        // SETTERS
        // ======================================

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }


        public void setEmail(String email) {
            this.email = email;
        }


        public void setPhone(String phone) {
            this.phone = phone;
        }


        public void setAddress(String address) {
            this.address = address;
        }
    }
}