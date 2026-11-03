package Controller.Login;

import dto.MemberDTO;
import model.MemberModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class ManageMembersController {

    @FXML private TextField searchField;
    @FXML private TableView<MemberDTO> memberTable;
    @FXML private TableColumn<MemberDTO, String> memberIdColumn;
    @FXML private TableColumn<MemberDTO, String> nameColumn;
    @FXML private TableColumn<MemberDTO, String> emailColumn;
    @FXML private TableColumn<MemberDTO, String> phoneColumn;
    @FXML private TableColumn<MemberDTO, String> addressColumn;
    @FXML private Label memberCountLabel;

    private final ObservableList<MemberDTO> memberList = FXCollections.observableArrayList();
    private final MemberModel memberModel = new MemberModel();

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
            memberList.addAll(memberModel.getAllMembers());
            memberTable.setItems(memberList);
            updateMemberCount();
        } catch (Exception e) {
            e.printStackTrace();
            showWarning("DB Error: " + e.getMessage());
        }
    }

    @FXML private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) { loadAllMembersFromDB(); return; }
        try {
            ObservableList<MemberDTO> searchResults = FXCollections.observableArrayList();
            for (MemberDTO dto : memberModel.getAllMembers()) {
                if (dto.getMemberId().toLowerCase().contains(searchText) ||
                        dto.getFullName().toLowerCase().contains(searchText) ||
                        dto.getEmail().toLowerCase().contains(searchText)) {
                    searchResults.add(dto);
                }
            }
            memberTable.setItems(searchResults);
            memberCountLabel.setText("Members: " + searchResults.size());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void handleClearSearch() { searchField.clear(); loadAllMembersFromDB(); }

    @FXML private void handleEdit() {
        MemberDTO selectedMember = memberTable.getSelectionModel().getSelectedItem();
        if (selectedMember == null) { new Alert(Alert.AlertType.WARNING, "Please select a member").showAndWait(); return; }

        TextField memberIdField = new TextField(selectedMember.getMemberId()); memberIdField.setDisable(true);
        TextField nameField = new TextField(selectedMember.getFullName());
        TextField emailField = new TextField(selectedMember.getEmail());
        TextField phoneField = new TextField(selectedMember.getPhone());
        TextField addressField = new TextField(selectedMember.getAddress());

        GridPane grid = new GridPane(); grid.setHgap(10); grid.setVgap(12); grid.setPadding(new Insets(10));
        grid.add(new Label("Member ID:"), 0, 0); grid.add(memberIdField, 1, 0);
        grid.add(new Label("Full Name:"), 0, 1); grid.add(nameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2); grid.add(emailField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3); grid.add(phoneField, 1, 3);
        grid.add(new Label("Address:"), 0, 4); grid.add(addressField, 1, 4);

        Dialog<ButtonType> dialog = new Dialog<>(); dialog.setTitle("Edit Member"); dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL); dialog.getDialogPane().setContent(grid);
        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    MemberDTO dto = new MemberDTO(memberIdField.getText().trim(), nameField.getText().trim(), emailField.getText().trim(), phoneField.getText().trim(), addressField.getText().trim());
                    if (memberModel.updateMember(dto)) { loadAllMembersFromDB(); new Alert(Alert.AlertType.INFORMATION, "Updated!").showAndWait(); }
                } catch (Exception e) { showWarning(e.getMessage()); }
            }
        });
    }

    @FXML private void handleDelete() {
        MemberDTO selectedMember = memberTable.getSelectionModel().getSelectedItem();
        if (selectedMember == null) { new Alert(Alert.AlertType.WARNING, "Please select a member").showAndWait(); return; }
        Alert c = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selectedMember.getFullName() + "?");
        c.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try { if (memberModel.deleteMember(selectedMember.getMemberId())) { memberList.remove(selectedMember); updateMemberCount(); } } catch (Exception e) {}
            }
        });
    }

    private void updateMemberCount() { memberCountLabel.setText("Members: " + memberList.size()); }
    private void showWarning(String message) { new Alert(Alert.AlertType.WARNING, message).showAndWait(); }
}