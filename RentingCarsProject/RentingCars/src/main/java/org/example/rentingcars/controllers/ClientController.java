package org.example.rentingcars.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import org.example.rentingcars.*;

import java.util.Date;
import java.util.List;

public class ClientController {
    @FXML
    private ListView<String> availableCarsListView;
    private boolean isLoaded = false;

    @FXML
    private ListView<Cars> carsListView;

    UsersService usersService = new UsersService();

    @FXML
    private void loadAvailableCars(){
        CarsService carsService = new CarsService();
        List<Cars> availabeCars = carsService.getAvailableCars();
        if(isLoaded == false){
            for(Cars car : availabeCars){
                availableCarsListView.getItems().add(car.getId() + " " + car.getBrand() + " " + car.getModel() + " (" + car.getYear() + ") ");
            }
        }
        isLoaded = true;
    }

    @FXML
    public void submitRequest() {
        String clientName = usersService.getCurrentUserName(); // Получаване на текущото име на клиента
        Cars selectedCar = carsListView.getSelectionModel().getSelectedItem(); // Избраният автомобил

        if (selectedCar == null) {
            showAlert("Моля, изберете кола.");
            return;
        }

        RentalRequests request = new RentalRequests();
        request.setClientName(clientName);
        request.setCar(selectedCar);
        request.setCarBrand(selectedCar.getBrand());
        request.setRequestTime(new Date());
        request.setStatus("pending");

        // Записване на таблица за заявки в базата данни
        DatabaseManager.executeWithTransaction(session -> session.save(request));

        showAlert("Заявката е подадена успешно!");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
