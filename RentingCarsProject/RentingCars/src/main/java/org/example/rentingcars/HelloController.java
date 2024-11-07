package org.example.rentingcars;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.rentingcars.Users;
import org.example.rentingcars.UsersService;

import java.io.IOException;

public class HelloController {

    @FXML
    private TextField nameField;          // Поле за името
    @FXML
    private TextField emailField;         // Поле за имейла
    @FXML
    private PasswordField passwordField;   // Поле за паролата
    @FXML
    private ComboBox<String> accountTypeComboBox; // Комбобокс за типа на акаунта
    @FXML
    private Button goToLoginButton;       // Бутон за навигация към логин страницата

    private UsersService usersService = new UsersService(); // Инстанция на UsersService

    @FXML
    protected void registerUser() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String accountType = accountTypeComboBox.getValue();

        // Проверка за валидност на полетата
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || accountType == null) {
            showAlert("Всички полета са задължителни!");
            return;
        }

        // Проверка дали потребителското име (или имейл) вече е заето
        if (usersService.isUsernameTaken(name)) {
            showAlert("Потребителското име вече е заето!");
            return;
        }

        // Създаване на нов потребител
        Users newUser = new Users(name, email, password, accountType);

        // Записване на потребителя в базата данни
        usersService.addUser(newUser);
        showAlert("Успешна регистрация на потребител: " + newUser.getName());
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void loginScene() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) goToLoginButton.getScene().getWindow(); // текущият прозорец
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Грешка при зареждане на сцената за вход: " + e.getMessage());
        }
    }
}
