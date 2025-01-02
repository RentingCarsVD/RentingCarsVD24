package org.example.carrentingproject.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.example.carrentingproject.GlobalData;
import org.example.carrentingproject.SceneLoader;
import org.example.carrentingproject.models.*;
import org.example.carrentingproject.repositories.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientController implements RepositoryInjected {
    private static final Logger log = Logger.getLogger(ClientController.class);

    @FXML
    private Label firmNameLabel;
    @FXML
    private ListView<Car> carlistView;
    @FXML
    private ListView<String> generalListView;

    private FirmRepository firmRepository;
    private CarRepository carRepository;
    private UserRepository userRepository;
    private RequestRepository requestRepository;

    Client currentClient = GlobalData.getCurrentClient();
    Car currentCar = GlobalData.getCurrentCar();


    @FXML // Label за фирма
    private void initialize()
    {
        if (currentClient != null && currentClient.getSelectedFirm() != null)
        {
            firmNameLabel.setText("Фирма: " + currentClient.getSelectedFirm().getName());
        } else {
            firmNameLabel.setText("Изберете фирма");
        }

        generalListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Check for double-click
                updateClientWithFirm(); // Call the method
            }
        });
    }

    @FXML // Списък с фирми
    private void showFirmsList() {
        List<Firm> firms = firmRepository.getAll();
        if (firms == null || firms.isEmpty()) {
            showAlert("Няма налични фирми.");
            log.info("No firms found in the database.");
            generalListView.getItems().clear();
        } else {
            generalListView.getItems().setAll(firms.stream()
                    .map(Firm::getName)
                    .toList());
        }

    }

    @FXML // Списък с колите на фирмата
    private void showFirmsCarsList()
    {
        Firm clientSelectedFirm = currentClient.getSelectedFirm();
        if (clientSelectedFirm == null) {
            showAlert("Моля, изберете фирма преди да продължите!");
            log.error("No firm selected by the client.");
            return;
        }

        List<Car> cars = carRepository.getCarsByFirm(clientSelectedFirm.getId());
        if (cars == null || cars.isEmpty()) {
            showAlert("Фирмата няма налични автомобили.");
            log.info("The selected firm has no available cars.");
            carlistView.getItems().clear(); // Изчистваме списъка с автомобили, ако няма такива
            return;
        }
        carlistView.getItems().setAll(cars);
    }

    @FXML // Прозорец за заявки + избор на кола
    private void rentalWindowButton() throws IOException
    {
        Car selectedCar = carlistView.getSelectionModel().getSelectedItem();
        GlobalData.setCurrentCar(selectedCar);
        if (selectedCar == null) {
            showAlert("Моля, изберете кола от списъка!");
            return;
            }

        List<Object> repositories = new ArrayList<>();
        repositories.add(requestRepository);
        repositories.add(userRepository);

        Scene rentalScene = SceneLoader.loadScene("rentalRequestView.fxml", 490, 280, repositories);

        Stage createFirmStage = new Stage();
        createFirmStage.setTitle("Попълване на заявка");
        createFirmStage.setScene(rentalScene);
        createFirmStage.initModality(Modality.APPLICATION_MODAL);
        createFirmStage.showAndWait();
    }

    @FXML // Списък с наети коли
    private void showOwnedCars()
    {
        List<RentalRequest> requests = requestRepository.findRequestsByClient(currentClient.getId());

        if (requests != null && !requests.isEmpty()) {
            ObservableList<String> rentedCars = FXCollections.observableArrayList();
            for (RentalRequest request : requests) {
                String carInfo = "Кола: " + request.getCar().getBrand() +
                        ", Модел: " + request.getCar().getModel() +
                        ", Дни на наем: " + request.getRentalDays();
                rentedCars.add(carInfo);
            }
            generalListView.setItems(rentedCars);
        } else {
            generalListView.setItems(FXCollections.observableArrayList("Нямате наети коли."));
        }
    }

    // Aктуализираме firm_id & selected_firm_id за избрана фирма
    @FXML
    private void updateClientWithFirm() {
        // Избор на фирма от ListView
        String selectedFirmName = generalListView.getSelectionModel().getSelectedItem();
        if (selectedFirmName == null) {
            showAlert("Моля, изберете фирма!");
            return;
        }
        Firm selectedFirm = firmRepository.findByName(selectedFirmName)
                .orElseThrow(() -> new IllegalArgumentException("Фирмата не е намерена."));

        // Извличаме текущия клиент от GlobalData
        //Client currentClient = GlobalData.getCurrentClient();
        if (currentClient == null) {
            showAlert("Няма избран клиент!");
            return;
        }

        currentClient.setSelectedFirm(selectedFirm);
        currentClient.setFirm(selectedFirm);

        try {
            userRepository.update(currentClient);
            GlobalData.setCurrentClient(currentClient);
            showAlert("Успешно избрахте фирма за наемане на коли!");
            updateFirmNameLabel(selectedFirm);
        } catch (Exception e) {
            log.error("Failed to update client with firm.", e);
            showAlert("Възникна грешка при актуализацията на клиента: " + e.getMessage());
        }
    }

    private void updateFirmNameLabel(Firm selectedFirm) {
        firmNameLabel.setText("Фирма: " + selectedFirm.getName());
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm()); // Път до CSS файла
        dialogPane.getStyleClass().add("dialog-pane");

        alert.showAndWait();
    }

    @Override
    public void setRepositories(List<Object> repositories) {
        for (Object o : repositories) {
            if (o instanceof FirmRepository) {
                this.firmRepository = (FirmRepository) o;
            } else if (o instanceof CarRepository) {
                this.carRepository = (CarRepository) o;
            } else if (o instanceof UserRepository) {
                this.userRepository = (UserRepository) o;
            } else if (o instanceof RequestRepository) {
                this.requestRepository = (RequestRepository) o;
            }
        }
    }

    @FXML
    private void logout() {
        Stage stage = (Stage) carlistView.getScene().getWindow();
        stage.close();
    }
}
