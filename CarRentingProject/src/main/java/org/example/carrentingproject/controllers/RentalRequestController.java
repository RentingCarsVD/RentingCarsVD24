package org.example.carrentingproject.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.apache.log4j.Logger;
import org.example.carrentingproject.GlobalData;
import org.example.carrentingproject.models.*;
import javafx.scene.control.*;
import org.example.carrentingproject.repositories.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class RentalRequestController implements RepositoryInjected {
    private static final Logger log = Logger.getLogger(RentalRequestController.class);

    @FXML
    private TextField rentalDaysField;
    @FXML
    private ComboBox<String> operatorComboBox;
    @FXML
    private Label carLabel;

    private RequestRepository requestRepository;
    private UserRepository userRepository;

    Car selectedCar = GlobalData.getCurrentCar();
    Client currentClient = GlobalData.getCurrentClient();

    public enum RentalStatus {
        PENDING,   // Заявката е в изчакване
        APPROVED,  // Заявката е одобрена
        REJECTED,  // Заявката е отхвърлена
        COMPLETED  // Заявката е завършена
    }

    @FXML
    private void initialize() {
        operatorsInFirm();
        carLabel.setText("Заявка за наемане на кола: " + selectedCar.toString() + "\n"
                + "изминати км: " + selectedCar.getKilometersDriven() + "km");
    }

    // Показваме операторите работещи в съответната фирма
    @FXML
    private void operatorsInFirm() {
        // Запазваме избраната фирма от клиента
        Firm clients_selected_firm = GlobalData.getCurrentClient().getSelectedFirm();
        // Зареждаме операторите в comboboxa
        operatorComboBox.getItems().addAll(clients_selected_firm.getOperatorNames());
    }

    @FXML
    private void applyRentalRequest() {
        try {
            // Валидация на полето за дни
            if (rentalDaysField.getText().isEmpty()) {
                showAlert("Грешка!", "Моля, въведете брой дни за наемане!", Alert.AlertType.ERROR);
                return;
            }

            int rentalDays;
            try {
                rentalDays = Integer.parseInt(rentalDaysField.getText());
            } catch (NumberFormatException e) {
                showAlert("Грешка!", "Броят дни трябва да е валидно число!", Alert.AlertType.ERROR);
                return;
            }

            // Проверка дали е избран оператор
            String selected_operator = operatorComboBox.getValue();
            if (selected_operator == null || selected_operator.isEmpty()) {
                showAlert("Грешка!", "Моля, изберете оператор!", Alert.AlertType.ERROR);
                return;
            }

            Operator chosenOperator = userRepository.findByName(selected_operator)
                    .orElseThrow(() -> new IllegalArgumentException("Операторът не е намерен в базата данни."));

            // Създаване на заявка
            RentalRequest rentalRequest = new RentalRequest(
                    selectedCar,
                    currentClient,
                   //selectedCar.getBrand(),
                    LocalDateTime.now(),
                    rentalDays,
                    RentalStatus.PENDING, // Enum вместо стринг
                    chosenOperator,
                    currentClient.getSelectedFirm(), // Новото поле за фирма
                    null,
                    null,
                    null
            );

            // Запазване на заявката
            requestRepository.save(rentalRequest);
            showAlert("Успех!", "Заявката беше изпратена успешно!", Alert.AlertType.INFORMATION);
            log.info("Successfully submitted rental request.");
        } catch (Exception e) {
            log.error("An error occurred while applying rental request: " + e.getMessage(), e);
            showAlert("Грешка!", "Възникна проблем при изпращане на заявката.", Alert.AlertType.ERROR);
        }
    }

    @Override
    public void setRepositories(List<Object> repositories) {
        for (Object o : repositories) {
            if (o instanceof RequestRepository) {
                this.requestRepository = (RequestRepository) o;
            } else if (o instanceof UserRepository) {
                this.userRepository = (UserRepository) o;
            }
        }
    }
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
