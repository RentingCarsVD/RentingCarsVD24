package org.example.rentingcars.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.example.rentingcars.Cars;
import org.example.rentingcars.CarsService;

import java.util.List;

public class AdminController {
    @FXML
    private ListView<String> availableCarsListView;
    private boolean isLoaded = false;

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
}
