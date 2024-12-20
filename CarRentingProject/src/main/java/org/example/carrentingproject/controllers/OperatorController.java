package org.example.carrentingproject.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.example.carrentingproject.GlobalData;
import org.example.carrentingproject.SceneLoader;
import org.example.carrentingproject.database.DatabaseManager;
import org.example.carrentingproject.models.*;
import org.example.carrentingproject.repositories.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class OperatorController implements RepositoryInjected {
    private static final Logger log = Logger.getLogger(OperatorController.class);

    @FXML
    private ListView<Object> listView;
    @FXML
    private Label firmNameLabel;
    @FXML
    private ComboBox<String> typeComboBox, brandComboBox, modelComboBox,
            categoryComboBox, yearComboBox;
    @FXML
    private TextField featuresField; // Характеристики на колата

    private CarRepository carRepository;
    private RequestRepository requestRepository;


    @FXML // Label за фирмата към която е назначен оператора
    private void initialize() {
        Operator currentOperator = GlobalData.getCurrentOperator();
        if (currentOperator != null && currentOperator.getFirm() != null) {
            firmNameLabel.setText("Фирма: " + currentOperator.getFirm().getName());
        } else {
            firmNameLabel.setText("Изберете фирма");
        }
        setupComboboxes();

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) // Проверка за двойно щракване
            {
                RentalRequest selectedRequest = (RentalRequest) listView.getSelectionModel().getSelectedItem();
                if (selectedRequest != null) {
                    try {
                        showConditionWindow(selectedRequest);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    @FXML
    private void setupComboboxes(){
        Map<String, List<String>> typeToBrands = Map.of(
                "луксозна", List.of("Mercedes", "BMW", "Audi"),
                "семейна", List.of("Toyota", "Volkswagen", "Ford"),
                "градска", List.of("Honda", "Fiat", "Hyundai")
        );

        Map<String, List<String>> brandToModels = Map.of(
                "Mercedes", List.of("S-Class", "E-Class"),
                "BMW", List.of("5 Series", "3 Series"),
                "Audi", List.of("A6", "A4"),
                "Toyota", List.of("Highlander", "Camry"),
                "Volkswagen", List.of("Passat", "Tiguan"),
                "Ford", List.of("Explorer", "Focus"),
                "Honda", List.of("Civic", "Fit"),
                "Fiat", List.of("500", "Panda"),
                "Hyundai", List.of("i10", "i20")
        );
        // Задаваме опциите за типа на колата
        typeComboBox.setItems(FXCollections.observableArrayList(typeToBrands.keySet()));

        typeComboBox.setOnAction(event -> {
            String selectedType = typeComboBox.getValue();
            if (selectedType != null) {
                brandComboBox.setItems(FXCollections.observableArrayList(typeToBrands.getOrDefault(selectedType, List.of())));
                brandComboBox.setDisable(false);
                modelComboBox.getItems().clear();
                modelComboBox.setDisable(true);
            }
        });
        brandComboBox.setOnAction(event -> {
            String selectedBrand = brandComboBox.getValue();
            if (selectedBrand != null) {
                modelComboBox.setItems(FXCollections.observableArrayList(brandToModels.getOrDefault(selectedBrand, List.of())));
                modelComboBox.setDisable(false);
            }
        });

        // Задаваме фиксирани опции за година и категория
        yearComboBox.setItems(FXCollections.observableArrayList("2019","2020", "2021", "2022", "2023"));
        categoryComboBox.setItems(FXCollections.observableArrayList("седан", "SUV", "комби"));
    }

    @FXML
    private void loadCarsInFirm(){
        Operator currentOperator = GlobalData.getCurrentOperator();

        List<Car> cars = carRepository.
                getCarsByFirm(currentOperator.getFirm().getId());
        listView.getItems().setAll(cars);
    }

    @FXML
    private void registerCar()
    {
        Firm operators_Firm = GlobalData.getCurrentOperator().getFirm();

        String type = typeComboBox.getValue();
        String brand = brandComboBox.getValue();
        String model = modelComboBox.getValue();
        String category = categoryComboBox.getValue();
        int year = Integer.parseInt(yearComboBox.getValue());
        String features = featuresField.getText();

        if(type.isEmpty() || brand.isEmpty() || model.isEmpty() || category.isEmpty() || features.isEmpty()){
            showAlert("Грешка!", "Попълнете всички данни за колата!", Alert.AlertType.WARNING);
        }
        Car newCar = new Car(brand, model, category, type, year, features,
                true, operators_Firm, (double) randomKilometers());
        carRepository.save(newCar);
        loadCarsInFirm(); // Актуализиране на списъка
        showAlert("Успех!", "Успешно регистрирахте кола!", Alert.AlertType.CONFIRMATION);

    }

    public int randomKilometers() {
        Random random = new Random();

        // Задай диапазон от 0 до 500,000 километра
        int maxKilometers = 500000;
        int minKilometers = 0;

        // Връща случайна стойност в диапазона от 0 до 500,000 километра
        return random.nextInt((maxKilometers - minKilometers) + 1) + minKilometers;
    }

    @FXML
    private void showRequestsList()
    {
        Operator currentOperator = GlobalData.getCurrentOperator(); // Вземаме текущия оператор
        List<RentalRequest> requests = requestRepository.findRequestsByOperator(currentOperator.getId());
        if (requests == null || requests.isEmpty()) {
            showAlert("Информация", "Нямате назначени заявки.", Alert.AlertType.INFORMATION);
            log.info("No rental requests assigned to the current operator.");
        } else {
            listView.getItems().setAll(requests); // Зареждаме заявките в ListView
        }
    }

    private void showConditionWindow(RentalRequest selectedRequest) throws IOException
    {
        List<Object> repositories = new ArrayList<>();
        repositories.add(requestRepository);

        Scene conditionScene = SceneLoader.loadScene("conditionBefore.fxml", 380, 270, repositories);

        Stage conditionStage = new Stage();
        conditionStage.setTitle("Състояние на колата при отдаване");
        conditionStage.setScene(conditionScene);
        conditionStage.initModality(Modality.APPLICATION_MODAL);
        conditionStage.showAndWait();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void setRepositories(List<Object> repositories) {
        for (Object o : repositories) {
            if (o instanceof CarRepository) {
                this.carRepository = (CarRepository) o;
            } else if (o instanceof RequestRepository) {
                this.requestRepository = (RequestRepository) o;
            }
        }
    }
    // Бутон за изход
    @FXML
    private void logout() {
        Stage stage = (Stage) listView.getScene().getWindow();
        stage.close();
    }
}

