package com.library.controller;

import com.library.Util.DBConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ManageMembersController {

    @FXML private TextField searchField;
    @FXML private TableView<Member> memberTable;
    @FXML private TableColumn<Member, String> memberIdColumn;
    @FXML private TableColumn<Member, String> nameColumn;
    @FXML private TableColumn<Member, String> emailColumn;
    @FXML private TableColumn<Member, String> phoneColumn;
    @FXML private TableColumn<Member, String> addressColumn;
    @FXML private Label memberCountLabel;

    private final ObservableList<Member> memberList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        memberIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMemberId()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullName()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        phoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        addressColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        loadAllMembersFromDB();
    }

    private void loadAllMembersFromDB() {
        memberList.clear();
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM member ORDER BY created_at DESC");
            while (rs.next()) {
                memberList.add(new Member(
                        rs.getString("member_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                ));
            }
            memberTable.setItems(memberList);
            updateMemberCount();
        } catch (Exception e) {
            e.printStackTrace();
            showWarning("DB Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            loadAllMembersFromDB();
            return;
        }
        ObservableList<Member> searchResults = FXCollections.observableArrayList();
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT * FROM member WHERE member_id LIKE ? OR full_name LIKE ? OR email LIKE ? OR phone LIKE ? OR address LIKE ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            String like = "%" + searchText + "%";
            for (int i = 1; i <= 5; i++) pst.setString(i, like);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                searchResults.add(new Member(rs.getString("member_id"), rs.getString("full_name"), rs.getString("email"), rs.getString("phone"), rs.getString("address")));
            }
            memberTable.setItems(searchResults);
            memberCountLabel.setText("Members: " + searchResults.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        loadAllMembersFromDB();
    }

    @FXML
    private void handleEdit() {
        Member selectedMember = memberTable.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a member from the table.").showAndWait();
            return;
        }
        TextField memberIdField = new TextField(selectedMember.getMemberId());
        TextField nameField = new TextField(selectedMember.getFullName());
        TextField emailField = new TextField(selectedMember.getEmail());
        TextField phoneField = new TextField(selectedMember.getPhone());
        TextField addressField = new TextField(selectedMember.getAddress());
        memberIdField.setDisable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(10));
        grid.add(new Label("Member ID:"), 0, 0); grid.add(memberIdField, 1, 0);
        grid.add(new Label("Full Name:"), 0, 1); grid.add(nameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2); grid.add(emailField, 1, 2);
        grid.add(new Label("Phone Number:"), 0, 3); grid.add(phoneField, 1, 3);
        grid.add(new Label("Address:"), 0, 4); grid.add(addressField, 1, 4);
        nameField.setPrefWidth(300); emailField.setPrefWidth(300); phoneField.setPrefWidth(300); addressField.setPrefWidth(300);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Member");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(grid);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (nameField.getText().trim().isEmpty()) { showWarning("Please enter the member name."); event.consume(); return; }
            if (!emailField.getText().contains("@") || !emailField.getText().contains(".")) { showWarning("Please enter a valid email address."); event.consume(); return; }
            if (!phoneField.getText().trim().matches("\\d{10}")) { showWarning("Phone number must contain 10 digits."); event.consume(); return; }
            if (addressField.getText().trim().isEmpty()) { showWarning("Please enter the address."); event.consume(); }
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    Connection conn = DBConnection.getInstance().getConnection();
                    PreparedStatement pst = conn.prepareStatement("UPDATE member SET full_name = ?, email = ?, phone = ?, address = ?, updated_at = NOW() WHERE member_id = ?");
                    pst.setString(1, nameField.getText().trim());
                    pst.setString(2, emailField.getText().trim());
                    pst.setString(3, phoneField.getText().trim());
                    pst.setString(4, addressField.getText().trim());
                    pst.setString(5, selectedMember.getMemberId());
                    pst.executeUpdate();
                    selectedMember.setFullName(nameField.getText().trim());
                    selectedMember.setEmail(emailField.getText().trim());
                    selectedMember.setPhone(phoneField.getText().trim());
                    selectedMember.setAddress(addressField.getText().trim());
                    memberTable.refresh();
                    new Alert(Alert.AlertType.INFORMATION, "The member details have been updated in DB.").showAndWait();
                } catch (Exception e) {
                    showWarning("DB Update Failed: " + e.getMessage());
                }
            }
        });
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Invalid Information");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleDelete() {
        Member selectedMember = memberTable.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a member from the table.").showAndWait();
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + selectedMember.getFullName() + " (" + selectedMember.getMemberId() + ")?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Connection conn = DBConnection.getInstance().getConnection();
                    PreparedStatement pst = conn.prepareStatement("DELETE FROM member WHERE member_id = ?");
                    pst.setString(1, selectedMember.getMemberId());
                    pst.executeUpdate();
                    memberList.remove(selectedMember);
                    updateMemberCount();
                    new Alert(Alert.AlertType.INFORMATION, "The member has been deleted from DB.").showAndWait();
                } catch (Exception e) {
                    showWarning("Delete Failed: " + e.getMessage());
                }
            }
        });
    }

    private void updateMemberCount() {
        memberCountLabel.setText("Members: " + memberList.size());
    }

    public static class Member {
        private final String memberId;
        private String fullName;
        private String email;
        private String phone;
        private String address;

        public Member(String memberId, String fullName, String email, String phone, String address) {
            this.memberId = memberId; this.fullName = fullName; this.email = email; this.phone = phone; this.address = address;
        }
        public String getMemberId() { return memberId; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getAddress() { return address; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public void setEmail(String email) { this.email = email; }
        public void setPhone(String phone) { this.phone = phone; }
        public void setAddress(String address) { this.address = address; }
    }
}