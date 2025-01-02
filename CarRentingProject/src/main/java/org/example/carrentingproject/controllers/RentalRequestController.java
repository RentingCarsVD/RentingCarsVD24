package org.example.carrentingproject.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.log4j.Logger;
import org.example.carrentingproject.GlobalData;
import org.example.carrentingproject.models.*;
import javafx.scene.control.*;
import org.example.carrentingproject.repositories.*;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class RentalRequestController implements RepositoryInjected {
    private static final Logger log = Logger.getLogger(RentalRequestController.class);

    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<String> operatorComboBox;
    @FXML
    private Label carLabel;
    @FXML
    private Label rentalDaysLabel;

    private LocalDate startDate;
    private LocalDate endDate;

    private RequestRepository requestRepository;
    private UserRepository userRepository;

    Car selectedCar = GlobalData.getCurrentCar();
    Client currentClient = GlobalData.getCurrentClient();

    private long rentalDays;

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
                + "изминати км: " + String.format("%,.0f", selectedCar.getKilometersDriven())  + "km");

        startDatePicker.setOnAction(event -> handleStartDateSelection());
        endDatePicker.setOnAction(event -> handleEndDateSelection());

        endDatePicker.setDayCellFactory(createEndDateCellFactory());
    }

    private void handleStartDateSelection()
    {
        startDate = startDatePicker.getValue();
        if (startDate != null) {
            endDatePicker.setDisable(false);
        }
    }
    private void handleEndDateSelection()
    {
        endDate = endDatePicker.getValue();
        if (startDate != null && endDate != null) {
            updateRentalDaysLabel();
        }
    }
    private void updateRentalDaysLabel()
    {
        if (startDate != null && endDate != null)
        {
            rentalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
            rentalDaysLabel.setText("Дни за наем: " + rentalDays);
        }
    }


    private Callback<DatePicker, DateCell> createEndDateCellFactory() {
        return datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                // Блокираме всички дати, които са преди началната дата
                if (startDate != null && date.isBefore(startDate)) {
                    setStyle("-fx-background-color: #d3d3d3;"); // Сив цвят за невалидни дати
                    setDisable(true); // Дата става неактивна
                }
            }
        };
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
            if (startDate == null || endDate == null) {
                showAlert("Грешка", "Моля, изберете дати!", Alert.AlertType.ERROR);
                return;
            }

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
                    LocalDateTime.now(),
                    (int) rentalDays,
                    RentalStatus.PENDING,
                    chosenOperator,
                    currentClient.getSelectedFirm(),
                    null,
                    null,
                    null

            );
            rentalRequest.setInitialRentalDays(rentalRequest.getRentalDays());
            rentalRequest.setRentalStartDate(startDate);
            rentalRequest.setRentalEndDate(endDate);

            currentClient.addRentalRequest(rentalRequest);

            requestRepository.save(rentalRequest);
            showAlert("Успех!", "Заявката беше изпратена успешно!", Alert.AlertType.INFORMATION);
            log.info("Successfully submitted rental request.");

            Stage loginStage = (Stage) carLabel.getScene().getWindow();
            loginStage.close();
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
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm()); // Път до CSS файла
        dialogPane.getStyleClass().add("dialog-pane");

        alert.showAndWait();
    }
}
