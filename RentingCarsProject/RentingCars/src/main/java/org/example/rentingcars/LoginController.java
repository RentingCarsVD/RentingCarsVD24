package org.example.rentingcars;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.io.IOException;
import java.util.List;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private UsersService usersService = new UsersService();

    public void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        String accountType = usersService.getUserAccountType(username, password);

        if (accountType != null) {
            // Запазване на текущото име на логнатия потребител
            usersService.setCurrentUserName(username);
            showAlert("Успешен вход!", Alert.AlertType.INFORMATION);
            navigateToScene(accountType); // Пренасочване на потребителя
        } else {
            showAlert("Грешно име или парола!", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void navigateToScene(String accountType) {
        try {
            String fxmlFile = "";

            switch (accountType) {
                case "Admin":
                    fxmlFile = "adminScene.fxml"; // Името на FXML файла за администратора
                    break;
                case "Client":
                    fxmlFile = "clientScene.fxml"; // Името на FXML файла за клиента
                    break;
                case "Operator":
                    fxmlFile = "operatorScene.fxml"; // Името на FXML файла за оператора
                    break;
                default:
                    showAlert("Невалиден тип акаунт!", Alert.AlertType.ERROR);
                    return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Грешка при зареждане на сцената: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}