package org.example.demo2;

import javafx.fxml.FXML;
import java.io.IOException;

public class HelloController {

    @FXML // Метод за отваряне на Админ прозорец
    private void onAdminLogin() throws IOException {
        HelloApplication app = (HelloApplication) HelloApplication.getInstance();
        app.showAdminScene(); // Показва admin сцената
    }

    @FXML // Метод за отваряне на Оператор прозорец
    private void onOperatorLogin() throws IOException {
        HelloApplication app = (HelloApplication) HelloApplication.getInstance();
        app.showOperatorScene(); // Показва operator сцената
    }

    @FXML // Метод за отваряне на Клиент прозорец
    private void onClientLogin() throws IOException {
        HelloApplication app = (HelloApplication) HelloApplication.getInstance();
        app.showClientScene(); // Показва client сцената
    }
}