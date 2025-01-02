package org.example.carrentingproject.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.example.carrentingproject.ClientData;
import org.example.carrentingproject.GlobalData;
import org.example.carrentingproject.SceneLoader;
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
    private TableView<ClientData> clientsTable; // Tаблица за задължения
    @FXML
    private TableColumn<ClientData, Integer> requestIdColumn;
    @FXML
    private TableColumn<ClientData, String> clientNameColumn;
    @FXML
    private TableColumn<ClientData, Double> amountDueColumn;

    @FXML
    private ListView<Object> listView;
    @FXML
    private Label firmNameLabel; // Лабел за името на фирмата
    @FXML
    private ComboBox<String> typeComboBox, brandComboBox, modelComboBox,
            categoryComboBox, yearComboBox;
    @FXML
    private TextField featuresField; // Характеристики на колата

    private CarRepository carRepository;
    private RequestRepository requestRepository;
    private UserRepository userRepository;

    Operator currentOperator = GlobalData.getCurrentOperator(); // Вземаме текущия оператор

    public RentalRequest rentalRequest = new RentalRequest(); // Request Обект

    List<RentalRequest> requestsList = new ArrayList<>();


    @FXML // Label за фирмата към която е назначен оператора
    private void initialize()
    {
        if (currentOperator != null && currentOperator.getFirm() != null) {
            firmNameLabel.setText("Фирма: " + currentOperator.getFirm().getName());
        } else {
            firmNameLabel.setText("Изберете фирма");
        }
        setupComboboxes();

        requestIdColumn.setCellValueFactory(data -> data.getValue().requestIdProperty().asObject());
        clientNameColumn.setCellValueFactory(data -> data.getValue().clientNameProperty());
        amountDueColumn.setCellValueFactory(data -> data.getValue().amountDueProperty().asObject());

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) // Проверка за двойно щракване
            {
                Object selectedItem = listView.getSelectionModel().getSelectedItem();

                if (selectedItem instanceof RentalRequest)
                {
                    // Casting на обекта в RentalRequest
                    RentalRequest selectedRequest = (RentalRequest) selectedItem;
                    GlobalData.setRentalRequest(selectedRequest);
                    try {
                        if (selectedRequest.getStatus() == RentalRequestController.RentalStatus.COMPLETED) {
                            showConditionAfterWindow(selectedRequest);
                        } else {
                            showConditionWindow(selectedRequest);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    setDisable(false); // Уверяваме се, че празните редове са активни
                } else {
                    setText(item.toString());

                    if (item instanceof RentalRequest) {
                        RentalRequest request = (RentalRequest) item;

                        // Проверка: Ако amountDue не е null, правим реда сив и неактивен
                        if (request.getAmountDue() != null) {
                            setStyle("-fx-background-color: lightgray;");
                            setDisable(true); // Правим реда неактивен
                        } else {
                            setStyle(""); // Нормален стил за останалите случаи
                            setDisable(false); // Редът остава активен
                        }
                    } else {
                        setStyle(""); // Нормален стил за други типове обекти
                        setDisable(false); // Редът остава активен
                    }
                }
            }
        });

    }

    @FXML
    private void showClientRegistrationForm() {
        Stage registrationStage = new Stage();
        registrationStage.setTitle("Регистрация на нов клиент");

        // Полета за въвеждане
        Label nameLabel = new Label("Име на клиента:");
        TextField nameField = new TextField();

        Label emailLabel = new Label("Имейл:");
        TextField emailField = new TextField();

        Label passwordLabel = new Label("Парола:");
        PasswordField passwordField = new PasswordField();

        // Бутон за потвърждение
        Button registerButton = new Button("Регистрирай");
        registerButton.setOnAction(event -> {
            if (nameField.getText().isEmpty() || emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                showAlert("Грешка", "Всички полета са задължителни!", Alert.AlertType.ERROR);
                return;
            }

            if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                showAlert("Грешка", "Моля, въведете валиден имейл адрес.", Alert.AlertType.ERROR);
                return;
            }

            if (passwordField.getText().length() < 6) {
                showAlert("Грешка", "Паролата трябва да съдържа поне 6 символа.", Alert.AlertType.ERROR);
                return;
            }

            User newUser = new User();
            newUser.setName(nameField.getText());
            newUser.setEmail(emailField.getText());
            newUser.setPassword(passwordField.getText());
            newUser.setAccountType("CLIENT");

            try {
                userRepository.save(newUser);
                showAlert("Успех", "Клиентът е регистриран успешно!", Alert.AlertType.INFORMATION);
                registrationStage.close();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Грешка", "Неуспешно записване на клиента: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });

        // Бутон за отмяна
        Button cancelButton = new Button("Отказ");
        cancelButton.setOnAction(event -> registrationStage.close());

        // Подреждане
        HBox buttonBox = new HBox(20, registerButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox formLayout = new VBox(10);
        formLayout.setPadding(new Insets(15));
        formLayout.getChildren().addAll(
                nameLabel, nameField,
                emailLabel, emailField,
                passwordLabel, passwordField,
                buttonBox
        );

        Scene scene = new Scene(formLayout, 320, 250);
        scene.getStylesheets().add(getClass().getResource("/css/register.css").toExternalForm());

        registrationStage.setScene(scene);
        registrationStage.showAndWait();
    }



    @FXML
    private void clientsTable()
    {
        ObservableList<ClientData> clients = FXCollections.observableArrayList();

        // Данни от repository
        List<RentalRequest> completedRequests = requestRepository.findRequestsByOperator(currentOperator.getId());

        for (RentalRequest request : completedRequests) {
            if (request.getStatus() == RentalRequestController.RentalStatus.COMPLETED && request.getAmountDue() != null) {
                long requestId = request.getId(); // ID на заявката
                String clientName = String.valueOf(request.getClientName()); // Име на клиента
                double amountDue = request.getAmountDue(); // Дължима сума
                clients.add(new ClientData((int) requestId, clientName, amountDue));
            }
        }

        // Задаване на данните към таблицата
        clientsTable.setItems(clients);
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
    private void loadCarsInFirm() {
        try {
            if (currentOperator == null || currentOperator.getFirm() == null) {
                showAlert("Грешка", "Не сте свързани с фирма. Моля, свържете се с фирма и опитайте отново.", Alert.AlertType.ERROR);
                log.error("Операторът няма асоциирана фирма.");
                return;
            }

            List<Car> cars = carRepository.getCarsByFirm(currentOperator.getFirm().getId());

            if (cars == null || cars.isEmpty()) {
                showAlert("Информация", "Няма налични коли за тази фирма.", Alert.AlertType.INFORMATION);
                log.info("Няма налични коли за фирмата: " + currentOperator.getFirm().getName());
                listView.getItems().clear(); // Изчистваме списъка
            } else {
                listView.getItems().setAll(cars);
                log.info("Коли за фирмата " + currentOperator.getFirm().getName() + " успешно заредени.");
            }
        } catch (Exception e) {
            showAlert("Грешка", "Възникна проблем при зареждане на колите.", Alert.AlertType.ERROR);
            log.error("Грешка при зареждане на колите: ", e);
        }
    }


    @FXML
    private void registerCar()
    {
        Firm operators_Firm = GlobalData.getCurrentOperator().getFirm();

        String type = typeComboBox.getValue();
        String brand = brandComboBox.getValue();
        String model = modelComboBox.getValue();
        String category = categoryComboBox.getValue();
        String yearValue = yearComboBox.getValue();
        String features = featuresField.getText();

        if (type == null || type.isEmpty() ||
                brand == null || brand.isEmpty() ||
                model == null || model.isEmpty() ||
                category == null || category.isEmpty() ||
                yearValue == null || yearValue.isEmpty() ||
                features == null || features.isEmpty()) {
            showAlert("Грешка!", "Попълнете всички данни за колата!", Alert.AlertType.WARNING);
            return;
        }
        int year = Integer.parseInt(yearValue);
        Car newCar = new Car(brand, model, category, type, year, features,
                true, operators_Firm, (double) randomKilometers());
        newCar.setKilometersAtStart(newCar.getKilometersDriven());
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
        requestsList = requestRepository.findRequestsByOperator(currentOperator.getId());

        if (requestsList == null || requestsList.isEmpty()) {
            showAlert("Информация", "Нямате назначени заявки.", Alert.AlertType.INFORMATION);
            log.info("No rental requests assigned to the current operator.");
        } else {
            // Филтрираме само тези заявки, които не са завършени (не са COMPLETED)
            List<RentalRequest> nonCompletedRequests = new ArrayList<>();
            for (RentalRequest request : requestsList) {
                if (request.getStatus() != RentalRequestController.RentalStatus.COMPLETED) {
                    nonCompletedRequests.add(request);
                }
            }
            // Ако има заявки, които не са COMPLETED, показваме ги
            if (!nonCompletedRequests.isEmpty()) {
                listView.getItems().setAll(nonCompletedRequests);
            } else {
                showAlert("Информация", "Няма подадени заявки.", Alert.AlertType.INFORMATION);
            }
        }
    }

    @FXML
    private void showCompletedRequests()
    {
        List<RentalRequest> requestsToOperator = requestRepository.findRequestsByOperator(currentOperator.getId());

        if (requestsToOperator == null || requestsToOperator.isEmpty())
        {
            showAlert("Информация", "Нямате завършени заявки.", Alert.AlertType.INFORMATION);
            log.info("No rental requests completed for the current operator.");
            listView.getItems().clear();
            return;
        } else {
            List<RentalRequest> completedRequests = new ArrayList<>();
            for (RentalRequest request : requestsToOperator) {
                if (request.getStatus() == RentalRequestController.RentalStatus.COMPLETED) {
                    completedRequests.add(request);
                }
            }
            if (!completedRequests.isEmpty()) {
                listView.getItems().setAll(completedRequests);
            }
        }
    }

    private void showConditionWindow(RentalRequest selectedRequest) throws IOException
    {
        List<Object> repositories = new ArrayList<>();
        repositories.add(requestRepository);
        repositories.add(carRepository);


        Scene conditionScene = SceneLoader.loadScene("conditionBefore.fxml", 380, 270, repositories);
        conditionScene.getStylesheets().add(getClass().getResource("/css/register.css").toExternalForm());

        Stage conditionStage = new Stage();
        conditionStage.setTitle("Състояние на колата при отдаване");
        conditionStage.setScene(conditionScene);
        conditionStage.initModality(Modality.APPLICATION_MODAL);
        conditionStage.showAndWait();
    }

    private void showConditionAfterWindow(RentalRequest selectedRequest) throws IOException
    {
        List<Object> repositories = new ArrayList<>();
        repositories.add(requestRepository);
        repositories.add(carRepository);

        Scene conditionScene = SceneLoader.loadScene("conditionAfter.fxml", 440, 350, repositories);
        conditionScene.getStylesheets().add(getClass().getResource("/css/register.css").toExternalForm());

        Stage conditionStage = new Stage();
        conditionStage.setTitle("Изчисляване на дължима сума");
        conditionStage.setScene(conditionScene);
        conditionStage.initModality(Modality.APPLICATION_MODAL);
        conditionStage.showAndWait();
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

    @Override
    public void setRepositories(List<Object> repositories) {
        for (Object o : repositories) {
            if (o instanceof CarRepository) {
                this.carRepository = (CarRepository) o;
            } else if (o instanceof RequestRepository) {
                this.requestRepository = (RequestRepository) o;
            } else if (o instanceof UserRepository) {
                this.userRepository = (UserRepository) o;
            }
        }
    }

    @FXML
    private void logout() {
        Stage stage = (Stage) listView.getScene().getWindow();
        stage.close();
    }
}