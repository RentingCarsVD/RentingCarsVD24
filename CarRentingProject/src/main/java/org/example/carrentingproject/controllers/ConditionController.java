package org.example.carrentingproject.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.example.carrentingproject.GlobalData;
import org.example.carrentingproject.models.Operator;
import org.example.carrentingproject.models.RentalRequest;
import org.example.carrentingproject.repositories.CarRepository;
import org.example.carrentingproject.repositories.RepositoryInjected;
import org.example.carrentingproject.repositories.RequestRepository;
import org.example.carrentingproject.repositories.UserRepository;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConditionController implements RepositoryInjected {
    private static final Logger log = Logger.getLogger(ConditionController.class);

    @FXML
    private TextField conditionField;
    @FXML
    private Label requestFromClient;

    private RequestRepository requestRepository;
    private CarRepository carRepository;

    private ScheduledExecutorService scheduler;
    //public RentalRequest rentalRequest; // Request Обект

    Operator currentOperator = GlobalData.getCurrentOperator();
    RentalRequest rentalRequest = GlobalData.getRentalRequest();

    @FXML
    private void initialize()
    {
        requestFromClient.setOnMouseEntered(event -> {
            requestDetails();
        });
        requestFromClient.setOnMouseExited(event -> {
            resetLabelText(); // Връща текста към оригиналния
        });
    }

    // Детайли на заявката
    public void requestDetails()
    {
        if (rentalRequest == null) { // Създаване на обекта, ако липсва
            Operator currentOperator = GlobalData.getCurrentOperator();
            rentalRequest = requestRepository.findRequestByOperator(currentOperator.getId());
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy, HH:mm");

        requestFromClient.setText("ID: " + rentalRequest.getId() + " Клиент: " + rentalRequest.getClientName() + "\n"
                + "дата: " + rentalRequest.getRequestTime().format(formatter) + ", Status: " + rentalRequest.getStatus() + "\n"
                + "кола: " + rentalRequest.getCar().getBrand() + " изминати километри: " + String.format("%,.0f", rentalRequest.getCar().getKilometersDriven()) +"km" + "\n"
                + "дни за наемане: " + rentalRequest.getRentalDays());
    }
    private void resetLabelText() {
        requestFromClient.setText("Преместете мишката за детайли."); // Оригинален текст
    }

    // Стартира отброяването на дните
    private void startScheduler()
    {
            log.info("Scheduler е стартиран.");
            scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    int daysLeft = rentalRequest.getRentalDays();

                    if (daysLeft > 0) {
                        rentalRequest.setRentalDays(daysLeft - 1);
                        log.info("Остават: " + rentalRequest.getRentalDays() + " дни за заявка с ID " + rentalRequest.getId());

                        if (rentalRequest.getRentalDays() == 0) {
                            completeRequest(rentalRequest);
                        }
                        // Обновяване на базата
                        requestRepository.update(rentalRequest);
                        Platform.runLater(() -> requestDetails());
                    }
                } catch (Exception e) {
                    log.error("Грешка при обновяване на заявки: ", e);
                }
            }, 0, 30, TimeUnit.SECONDS);
    }
    private void completeRequest(RentalRequest request)
    {
        request.setStatus(RentalRequestController.RentalStatus.COMPLETED);
        request.getCar().setAvailable(true);
        carRepository.update(request.getCar());
    }

    @FXML
    private void handleButtonClick(ActionEvent event){
        Button clickedButton = (Button) event.getSource();
        String condition = conditionField.getText();

        if (condition == null || condition.trim().isEmpty()) {
            showAlert("Грешка", "Моля, въведете състоянието на колата!", Alert.AlertType.WARNING);
            return;
        }
        if (rentalRequest == null) { // Създаване на обекта, ако липсва
            Operator currentOperator = GlobalData.getCurrentOperator();
            rentalRequest = requestRepository.findRequestByOperator(currentOperator.getId());
        }

        if (clickedButton.getId().equals("acceptButton")) {
            acceptRequest(condition);
            startScheduler();
        } else if (clickedButton.getId().equals("declineButton")) {
            // Логика за отхвърляне на заявката
            declineRequest();
        }
    }

    private void acceptRequest(String condition)
    {
        if (rentalRequest != null)
        {
            rentalRequest.setCarConditionBefore(condition);
            rentalRequest.setStatus(RentalRequestController.RentalStatus.APPROVED);
            rentalRequest.getCar().setAvailable(false);

            requestRepository.update(rentalRequest);
            carRepository.update(rentalRequest.getCar());
            showAlert("Успех", "Заявката е приета успешно!", Alert.AlertType.INFORMATION);
            Stage loginStage = (Stage) requestFromClient.getScene().getWindow();
            loginStage.close();
        }
    }

    private void declineRequest()
    {
        if (rentalRequest != null)
        {
            rentalRequest.setStatus(RentalRequestController.RentalStatus.REJECTED);
            rentalRequest.getCar().setAvailable(true);

            requestRepository.update(rentalRequest);
            carRepository.update(rentalRequest.getCar());
            showAlert("Успех", "Заявката е отказана успешно!", Alert.AlertType.INFORMATION);
            Stage loginStage = (Stage) requestFromClient.getScene().getWindow();
            loginStage.close();
        }
    }

    public void stopScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
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