package org.example.carrentingproject.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.example.carrentingproject.GlobalData;
import org.example.carrentingproject.models.Car;
import org.example.carrentingproject.models.RentalRequest;
import org.example.carrentingproject.repositories.CarRepository;
import org.example.carrentingproject.repositories.RepositoryInjected;
import org.example.carrentingproject.repositories.RequestRepository;
import org.example.carrentingproject.repositories.UserRepository;

import java.text.DecimalFormat;
import java.util.List;

public class ConditionAfterController implements RepositoryInjected {
    private static final Logger log = Logger.getLogger(ConditionAfterController.class);

    @FXML
    private Label carLabel;

    @FXML
    private Label totalAmountLabel;

    @FXML
    private TextField kilometersField, conditionField;

    @FXML
    private ComboBox<PricePerKilometerAndDay> pricePerKilometerComboBox;
    @FXML
    private ComboBox<RentalCondition> rentalConditionComboBox;

    RentalRequest rentalRequest = GlobalData.getRentalRequest();

    private RequestRepository requestRepository;
    private CarRepository carRepository;


    public enum RentalCondition {
        WITHOUT_ISSUES("Безпроблемно"),
        WITH_ISSUES("С възникнали проблеми");

        private final String description;

        RentalCondition(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    public enum PricePerKilometerAndDay {
        LUXURY("Луксозна", 1.5, 50.0),
        FAMILY("Семейна", 1.0, 30.0),
        CITY("Градска", 0.5, 20.0);

        private final String description;
        private final double pricePerKm;
        private final double pricePerDay;

        PricePerKilometerAndDay(String description, double pricePerKm, double pricePerDay) {
            this.description = description;
            this.pricePerKm = pricePerKm;
            this.pricePerDay = pricePerDay;
        }

        public String getDescription() {
            return description;
        }

        public double getPricePerKm() {
            return pricePerKm;
        }

        public double getPricePerDay() {
            return pricePerDay;
        }

        @Override
        public String toString() {
            return description + " - " + pricePerKm + " лв./км, " + pricePerDay + " лв./ден";
        }
    }


    @FXML
    private void initialize()
    {
        Car carInfo = rentalRequest.getCar();

        carLabel.setText(carInfo.getBrand() + " " + carInfo.getModel() + " (" + carInfo.getType() + ")\n"
                + "наети дни: " + rentalRequest.getInitialRentalDays() + "\n"
                + String.format("%,.0f", rentalRequest.getCar().getKilometersDriven()) + "km");

        pricePerKilometerComboBox.getItems().addAll(PricePerKilometerAndDay.values());
        pricePerKilometerComboBox.setValue(PricePerKilometerAndDay.CITY);

        rentalConditionComboBox.getItems().addAll(RentalCondition.values());
        rentalConditionComboBox.setValue(RentalCondition.WITHOUT_ISSUES);

        rentalConditionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Ако е избрано "С възникнали проблеми", правим TextField видим
            if (newValue == RentalCondition.WITH_ISSUES) {
                conditionField.setVisible(true);
            } else {
                conditionField.setVisible(false);
            }
        });
        conditionField.setVisible(false);
    }


    @FXML
    private void calculateAmount()
    {
        PricePerKilometerAndDay selectedPrice = pricePerKilometerComboBox.getValue();
        double pricePerKm = selectedPrice.getPricePerKm();
        double pricePerDay = selectedPrice.getPricePerDay();
        int rentalDays = rentalRequest.getInitialRentalDays();

        if (kilometersField.getText().isEmpty()) {
            showAlert("Грешка", "Моля, въведете изминати километри!", Alert.AlertType.INFORMATION);
            return; // Прекратяваме изпълнението, ако е празно
        }

        double kilometersDriven = Double.parseDouble(kilometersField.getText());

        double totalAmount = (pricePerDay * rentalDays) + (pricePerKm * kilometersDriven);

        rentalRequest.setAmountDue(totalAmount);

        totalAmountLabel.setText(String.format("Обща сума: %.2f лв.", totalAmount));
    }

    @FXML
    private void updateCarRequest()
    {
        if (conditionField.getText().isEmpty()) {
            rentalRequest.setCarConditionAfter(rentalConditionComboBox.getValue().toString());
        } else {
            rentalRequest.setCarConditionAfter(conditionField.getText());
        }
        rentalRequest.getCar().setKilometersDriven(rentalRequest.getCar().getKilometersDriven()+Double.parseDouble(kilometersField.getText()));

        requestRepository.update(rentalRequest);
        carRepository.update(rentalRequest.getCar());
        showAlert("Успех", "Сумата е изчислена успешно!", Alert.AlertType.INFORMATION);
        Stage loginStage = (Stage) carLabel.getScene().getWindow();
        loginStage.close();
    }

    @Override
    public void setRepositories(List<Object> repositories) {
        for (Object o : repositories) {
            if (o instanceof RequestRepository) {
                this.requestRepository = (RequestRepository) o;
            } else if (o instanceof CarRepository) {
                this.carRepository = (CarRepository) o;
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
