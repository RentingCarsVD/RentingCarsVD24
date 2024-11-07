package org.example.rentingcars.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.example.rentingcars.Cars;
import org.example.rentingcars.CarsService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperatorController {

    @FXML
    private ComboBox<String> typeComboBox; // За избора на тип: луксозна, семейна, градска
    @FXML
    private ComboBox<String> brandComboBox; // За избора на марка
    @FXML
    private ComboBox<String> modelComboBox; // За избора на модел
    @FXML
    private ComboBox<String> categoryComboBox; // За избора на категория
    @FXML
    private ComboBox<String> yearComboBox; // За избора на година
    @FXML
    private TextField featuresField; // За характеристиките
    @FXML
    private ListView<String> availableCarsListView;
    private boolean isLoaded = false;

    // Карта за съхранение на марки и модели, свързани с различните типове
    private Map<String, List<String>> typeToBrands;
    private Map<String, List<String>> brandToModels;

    @FXML
    public void initialize() {
        setupComboBoxes();
    }

    private void setupComboBoxes() {
        // Запълване на карта с марки за всеки тип
        typeToBrands = new HashMap<>();
        typeToBrands.put("луксозна", List.of("Mercedes", "BMW", "Audi"));
        typeToBrands.put("семейна", List.of("Toyota", "Volkswagen", "Ford"));
        typeToBrands.put("градска", List.of("Honda", "Fiat", "Hyundai"));

        // Запълване на карта с модели за всяка марка
        brandToModels = new HashMap<>();
        brandToModels.put("Mercedes", List.of("S-Class", "E-Class"));
        brandToModels.put("BMW", List.of("5 Series", "3 Series"));
        brandToModels.put("Audi", List.of("A6", "A4"));
        brandToModels.put("Toyota", List.of("Highlander", "Camry"));
        brandToModels.put("Volkswagen", List.of("Passat", "Tiguan"));
        brandToModels.put("Ford", List.of("Explorer", "Focus"));
        brandToModels.put("Honda", List.of("Civic", "Fit"));
        brandToModels.put("Fiat", List.of("500", "Panda"));
        brandToModels.put("Hyundai", List.of("i10", "i20"));

        // Запълване на падащото меню за класът на колата (луксозна, семейна, градска)
        typeComboBox.setItems(FXCollections.observableArrayList("луксозна", "семейна", "градска"));

        // Добавяне на слушател за модела
        typeComboBox.setOnAction(event -> updateBrandComboBox());

        // Добавяне на слушател за марката
        brandComboBox.setOnAction(event -> updateModelComboBox());

        // Запълване на година и категория (те остават фиксирани)
        yearComboBox.setItems(FXCollections.observableArrayList("2020", "2021", "2022", "2023"));
        categoryComboBox.setItems(FXCollections.observableArrayList("седан", "сув", "комби"));
    }

    // Метод за актуализация на ComboBox-а за марките на базата на избрания клас
    private void updateBrandComboBox() {
        String selectedType = typeComboBox.getValue();//Променлива за типа на колата
        if (selectedType != null) {
            List<String> brands = typeToBrands.get(selectedType);//Лист за марките
            if (brands != null) {
                brandComboBox.setItems(FXCollections.observableArrayList(brands));
                brandComboBox.setDisable(false); // Активираме полето за избор на марка
            }
        }
    }

    // Метод за актуализация на ComboBox-а за моделите на базата на избраната марка
    private void updateModelComboBox() {
        String selectedBrand = brandComboBox.getValue();//Променлива за марката
        if (selectedBrand != null) {
            List<String> models = brandToModels.get(selectedBrand);//Лист за модела
            if (models != null) {
                modelComboBox.setItems(FXCollections.observableArrayList(models));
                modelComboBox.setDisable(false); // Активираме полето за избор на модел
            }
        }
    }

    @FXML
    public void onRegisterCar() {
        // Проверка дали всички полета са попълнени
        if (typeComboBox.getValue() == null || brandComboBox.getValue() == null ||
                modelComboBox.getValue() == null || categoryComboBox.getValue() == null ||
                yearComboBox.getValue() == null || featuresField.getText().isEmpty()) {

            System.out.println("Моля, попълнете всички полета!");
            return; // Излизаме от метода, ако има непопълнени полета
        }
        // Вземане на избраните стойности от падащите списъци
        String type = typeComboBox.getValue();
        String brand = brandComboBox.getValue();
        String model = modelComboBox.getValue();
        String category = categoryComboBox.getValue();
        int year = Integer.parseInt(yearComboBox.getValue());
        String features = featuresField.getText();

        // Създаване на обект Cars
        Cars car = new Cars(brand, model, category, type, year, features, true);

        // Създаване на инстанция на CarsService и записване в базата
        CarsService carsService = new CarsService();
        carsService.addCar(car);

        // Потвърждение
        System.out.println("Колата е успешно регистрирана!");
    }

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