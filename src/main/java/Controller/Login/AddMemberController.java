package Controller.Login;

import dto.MemberDTO;
import model.MemberModel;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class AddMemberController {

    @FXML private TextField memberIdField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;

    private final MemberModel memberModel = new MemberModel();

    @FXML
    private void handleAddMember() {
        try {
            String id = memberIdField.getText().trim();
            String name = nameField.getText().trim();

            if (name.isEmpty() || id.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "ID and Name required!").show();
                return;
            }

            MemberDTO dto = new MemberDTO(
                    id,
                    name,
                    emailField.getText().trim(),
                    phoneField.getText().trim(),
                    addressField.getText().trim()
            );

            if (memberModel.saveMember(dto)) {
                new Alert(Alert.AlertType.INFORMATION, "Member Added Successfully!").show();
                memberIdField.clear();
                nameField.clear();
                emailField.clear();
                phoneField.clear();
                addressField.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }
}