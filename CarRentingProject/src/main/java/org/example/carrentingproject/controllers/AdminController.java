package org.example.carrentingproject.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.log4j.Logger;
import org.example.carrentingproject.CarStatus;
import org.example.carrentingproject.SceneLoader;
import org.example.carrentingproject.database.DatabaseManager;
import org.example.carrentingproject.models.*;
import org.example.carrentingproject.repositories.*;
import org.example.carrentingproject.repositories.CarRepository.Period;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminController implements RepositoryInjected {
    private static final Logger log = Logger.getLogger(AdminController.class);

    @FXML
    private AnchorPane registerOperatorPanel; // Регистрация на Оператор
    @FXML
    private AnchorPane carTableViewPanel; // Справка на коли за период
    @FXML
    private AnchorPane firmViewPanel; // Справка за фирми

    @FXML
    private DatePicker startDatePicker; // Начална дата
    @FXML
    private DatePicker endDatePicker; // Крайна дата

    @FXML
    private TableColumn<CarStatus, String> carNameColumn;

    @FXML
    private TableColumn<CarStatus, String> carStatusColumn;

    @FXML
    private TableView<CarStatus> availableCarsTable; // Таблица за коли

    @FXML
    private TextField firmNameField, firmAddressField; // Fields за фирмата
    @FXML
    private ListView listView; // General лист
    @FXML
    private ListView listFirmView;

    @FXML
    private ComboBox<Firm> firmComboBox; // Избор на фирма
    @FXML
    private ComboBox<Operator> operatorComboBox; // Избор на Оператор
    @FXML
    private TextField operatorName, operatorEmail; // Име, E-mail на Оператор
    @FXML
    private PasswordField operatorPassword; // Парола на Оператор
    @FXML
    private Label firmLabel;

    private Firm selectedFirm;

    private FirmRepository firmRepository;
    private UserRepository userRepository;
    private AdminRepository adminRepository;
    private CarRepository carRepository;
    private RequestRepository requestRepository;

    public AdminController() {}

    @FXML
    public void initialize()
    {
        if (listView != null) {
            listView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    Object selectedItem = listView.getSelectionModel().getSelectedItem();

                    if (selectedItem instanceof Firm) {
                        if (firmViewPanel.isVisible()){
                            selectedFirm = (Firm) selectedItem;
                            firmLabel.setText(selectedFirm.toString());
                        }
                    }
                }
            });
        }
        if (listFirmView != null) {
            listFirmView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    Object selectedItem = listFirmView.getSelectionModel().getSelectedItem();

                    if (selectedItem instanceof Car) {
                        Car selectedCar = (Car) selectedItem;
                        showCarHistory(selectedCar.getId());
                    } else if (selectedItem instanceof Client) {
                        Client selectedClient = (Client) selectedItem;
                        showClientsHistoryInFirm(selectedClient);
                    } else if (selectedItem instanceof Operator) {
                        Operator selectedOperator = (Operator) selectedItem;
                        showOperatorWork(selectedOperator);
                    }
                }
            });
        }
    }


    public String formatDate(LocalDate date) {
        if (date != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
            return date.format(formatter);
        }
        return "";
    }

    private void showClientsHistoryInFirm(Client client) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("История на заявките на клиента");

        TableView<RentalRequest> tableView = new TableView<>();

        TableColumn<RentalRequest, Long> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<RentalRequest, LocalDateTime> dateColumn = new TableColumn<>("Дата");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("rentalStartDate"));

        TableColumn<RentalRequest, String> carColumn = new TableColumn<>("Автомобил");
        carColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCar().getBrand()));

        TableColumn<RentalRequest, RentalRequestController.RentalStatus> statusColumn = new TableColumn<>("Статус");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<RentalRequest, String> conditionAtStartColumn = new TableColumn<>("Състояние при наемане");
        conditionAtStartColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getCarConditionBefore() != null
                                ? cellData.getValue().getCarConditionBefore()
                                : "Не е зададено"
                ));

        TableColumn<RentalRequest, String> conditionAtEndColumn = new TableColumn<>("Състояние при връщане");
        // Премахваме returnStatus и връщаме само ConditionAfter
        conditionAtEndColumn.setCellValueFactory(cellData -> {
            String conditionAfter = cellData.getValue().getCarConditionAfter();
            return new SimpleStringProperty(conditionAfter != null ? conditionAfter : "Не е зададено");
        });

        TableColumn<RentalRequest, String> amountColumn = new TableColumn<>("Дължима сума");
        amountColumn.setCellValueFactory(cellData -> {
            Double amount = cellData.getValue().getAmountDue();
            return new SimpleStringProperty(amount != null ? String.format("%.2f", amount) : "Няма стойност");
        });

        tableView.getColumns().addAll(
                idColumn, dateColumn, carColumn, statusColumn,
                conditionAtStartColumn, conditionAtEndColumn, amountColumn
        );

        Set<RentalRequest> rentalRequests = client.getRentalRequests();
        if (rentalRequests != null && !rentalRequests.isEmpty()) {
            tableView.setItems(FXCollections.observableArrayList(rentalRequests));
        }

        VBox vbox = new VBox();
        vbox.getChildren().addAll(new Label("Заявки на клиента: " + client.getClientName()), tableView);
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Scene scene = new Scene(vbox, 820, 310);
        scene.getStylesheets().add(getClass().getResource("/css/register.css").toExternalForm());

        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void showCarHistory(Long carId) {

        // Извличаме историята на заявките
        List<RentalRequest> rentalRequests = requestRepository.getRentalRequestsForCar(carId);
        String carName = rentalRequests.stream()
                .map(RentalRequest::getCarName) // RentalRequest трябва да има метод getCarName()
                .findFirst()
                .orElse("Неизвестен автомобил");

        if (rentalRequests == null || rentalRequests.isEmpty()) {
            showAlert("Информация", "Няма намерени заявки за избрания автомобил.", Alert.AlertType.INFORMATION);
            return;
        }

        // Форматираме информацията за показване
        StringBuilder history = new StringBuilder("История на aвтомобил: " + carName + ":\n\n");
        for (RentalRequest request : rentalRequests) {
            history.append("Заявка ID: ").append(request.getId())
                    .append("\nКлиент: ").append(request.getClientName())
                    .append("\nНачало: ").append(formatDate(request.getRentalStartDate()))
                    .append("\nКрай: ").append(formatDate(request.getRentalEndDate()))
                    .append("\nНаети дни: ").append(request.getInitialRentalDays())
                    .append("\nДължима сума: ").append(request.getAmountDue() != null ? request.getAmountDue() + " лв" : "Не е изчислено")
                    .append("\nСтатус: ").append(request.getStatus())
                    .append("\n\n");
        }

        // Показваме историята в диалогов прозорец
        showAlert("Aвтомобил: " + carName, history.toString(), Alert.AlertType.INFORMATION);
    }

    private void showOperatorWork(Operator operator) {
        // Извличаме заявките за оператора
        List<RentalRequest> rentalRequests = requestRepository.findRequestsByOperator(operator.getId());

        // Проверяваме дали има заявки
        if (rentalRequests == null || rentalRequests.isEmpty()) {
            showAlert("Информация", "Няма заявки за този оператор.", Alert.AlertType.INFORMATION);
            return;
        }

        // Създаваме нов прозорец за показване на информацията
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Работа на оператор: " + operator.getName());

        // Създаваме TableView за визуализиране на заявките
        TableView<RentalRequest> tableView = new TableView<>();

        // Колона за ID на заявката
        TableColumn<RentalRequest, Long> idColumn = new TableColumn<>("ID на заявката");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Колона за името на клиента
        TableColumn<RentalRequest, String> clientColumn = new TableColumn<>("Клиент");
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));

        // Колона за марката на колата
        TableColumn<RentalRequest, String> carBrandColumn = new TableColumn<>("Марка на колата");
        carBrandColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCar().getBrand()));

        // Колона за изминатите километри
        TableColumn<RentalRequest, String> kilometersColumn = new TableColumn<>("Изминати километри");
        kilometersColumn.setCellValueFactory(cellData -> {
            Car car = cellData.getValue().getCar();
            double initialKilometers = car.getKilometersAtStart(); // километрите при започване на наемането
            double finalKilometers = car.getKilometersDriven(); // километрите към края на наемането
            double kilometersDriven = finalKilometers - initialKilometers; // разликата за периода
            return new SimpleStringProperty(String.format("%.0f km", kilometersDriven)); // Форматираме резултата
        });

        // Колона за началната дата на наемането
        TableColumn<RentalRequest, String> startDateColumn = new TableColumn<>("Начална дата");
        startDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(formatDate(cellData.getValue().getRentalStartDate())));

        // Колона за наетите дни
        TableColumn<RentalRequest, Integer> rentalDaysColumn = new TableColumn<>("Наети дни");
        rentalDaysColumn.setCellValueFactory(new PropertyValueFactory<>("initialRentalDays"));

        // Колона за статуса на заявката
        TableColumn<RentalRequest, String> statusColumn = new TableColumn<>("Статус");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Добавяме колоните в TableView в зададения ред
        tableView.getColumns().addAll(
                idColumn,
                clientColumn,
                carBrandColumn,
                kilometersColumn,
                startDateColumn,
                rentalDaysColumn,
                statusColumn
        );

        // Добавяме заявките за оператора в TableView
        tableView.setItems(FXCollections.observableArrayList(rentalRequests));

        // Поставяме TableView в VBox контейнер за подредба
        VBox vbox = new VBox();
        vbox.getChildren().addAll(new Label("Заявки на оператор: " + operator.getName()), tableView);
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        // Създаваме сцена и я показваме
        Scene scene = new Scene(vbox, 820, 310);
        scene.getStylesheets().add(getClass().getResource("/css/register.css").toExternalForm());
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    // Помощен метод за форматиране на датата
    private String formatDate(LocalDateTime date) {
        if (date == null) {
            return "Не е зададена дата";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return date.format(formatter);
    }


    @FXML
    private void showOperatorsInFirm(){
        if (this.selectedFirm == null) {
            showAlert("Грешка!", "Няма избрана фирма от списъка", Alert.AlertType.WARNING);
            log.info("No firm selected!.");
            return;
        }
        List<Operator> operators = adminRepository.getOperatorsByFirm(selectedFirm.getId());

        if (operators == null || operators.isEmpty()) {
            showAlert("Грешка!", "Фирмата няма назначени служители.", Alert.AlertType.WARNING);
            log.info("The selected firm has no operators assigned.");
            listFirmView.getItems().clear();
            return;
        }
        listFirmView.getItems().setAll(operators);
    }

    @FXML
    private void showClientsInFirm()
    {
        if (this.selectedFirm == null) {
            showAlert("Грешка!", "Няма избрана фирма от списъка", Alert.AlertType.WARNING);
            log.info("No firm selected!.");
            return;
        }
        List<Client> clients = userRepository.getClientsByFirm( ((Firm) selectedFirm).getId() );

        if (clients == null || clients.isEmpty()) {
            showAlert("Грешка!", "Фирмата няма клиенти.", Alert.AlertType.WARNING);
            log.info("The selected firm has no clients.");
            listFirmView.getItems().clear();
            return;
        }
        listFirmView.getItems().setAll(clients);
    }


    @FXML
    private void showCarsInFirm()
    {
        if (this.selectedFirm == null) {
            showAlert("Грешка!", "Няма избрана фирма от списъка", Alert.AlertType.WARNING);
            log.info("No firm selected!.");
            return;
        }
        List<Car> cars = carRepository.getCarsByFirm( ((Firm) selectedFirm).getId() );

        if (cars == null || cars.isEmpty()) {
            showAlert("Грешка!", "Фирмата няма налични автомобили.", Alert.AlertType.WARNING);
            log.info("The selected firm has no available cars.");
            listFirmView.getItems().clear();
            return;
        }
        listFirmView.getItems().setAll(cars);
    }

    @FXML
    private void selectFirmAndViewHistory()
    {
        if(carTableViewPanel.isVisible() || registerOperatorPanel.isVisible()){
            carTableViewPanel.setVisible(false);
            registerOperatorPanel.setVisible(false);
        }
        boolean isVisible = firmViewPanel.isVisible();
        firmViewPanel.setVisible(!isVisible);

        List<Firm> firms = firmRepository.getAll();
        listView.getItems().setAll(firms);
    }

    @FXML
    private void showAllFirmsAndOperators(){
        List<Firm> firms = firmRepository.getAll();
        List<String> firmsWithOperators = new ArrayList<>();

        for (Firm firm : firms) {
            String operatorNames = firm.getOperatorNames();

            if (operatorNames != null && !operatorNames.isEmpty()) {
                firmsWithOperators.add("Фирма: " + firm.getName() + " Оператори: " + operatorNames);
            } else {
                firmsWithOperators.add("Фирма: " + firm.getName() + " Няма оператори");
            }
        }
        // Показваме фирмите в ListView
        listView.getItems().setAll(firmsWithOperators);
    }
    @FXML
    private void showAllOperators(){
        List<User> operators = adminRepository.getAllOperators();
        listView.getItems().setAll(operators); // Показваме операторите в ListView
    }
    @FXML
    private void showAllCars(){
        List<Car> cars = carRepository.getAll();
        listView.getItems().setAll(cars); // Показваме колите в ListView
    }

    @FXML
    private void removeSelectedOperator() {
        User selectedOperator = (User) listView.getSelectionModel().getSelectedItem();
        if (selectedOperator == null) {
            log.info("No operator selected.");
            return;
        }
        adminRepository.removeOperator(selectedOperator.getId());
        showAllOperators(); // Обновяваме ListView
    }

    @FXML
    private void carsInPeriodOfTime() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) {
            showAlert("Грешка", "Моля, изберете валиден диапазон от дати.", Alert.AlertType.ERROR);
            return;
        }

        if (startDate.isAfter(endDate)) {
            showAlert("Грешка", "Началната дата не може да бъде след крайната дата.", Alert.AlertType.ERROR);
            return;
        }

        List<Car> allCars = carRepository.getAll();
        ObservableList<CarStatus> carStatuses = FXCollections.observableArrayList();

        for (Car car : allCars) {
            // Извличаме всички заявки за този автомобил в избрания период
            List<RentalRequest> overlappingRequests = carRepository.findOverlappingRequests(car, startDate, endDate);

            if (overlappingRequests.isEmpty()) {
                carStatuses.add(new CarStatus(car.getBrand(), "Свободна за целия период"));
            } else {
                // Сортиране на заявките по начална дата
                overlappingRequests.sort(Comparator.comparing(RentalRequest::getRentalStartDate));

                // Обединяване на заетите периоди
                List<LocalDate[]> mergedPeriods = new ArrayList<>();
                LocalDate[] currentPeriod = new LocalDate[]{
                        overlappingRequests.get(0).getRentalStartDate(),
                        overlappingRequests.get(0).getRentalEndDate()
                };

                for (RentalRequest request : overlappingRequests) {
                    LocalDate requestStart = request.getRentalStartDate();
                    LocalDate requestEnd = request.getRentalEndDate();

                    if (!requestStart.isAfter(currentPeriod[1].plusDays(1))) {
                        // Припокриване или последователност – обединяваме
                        currentPeriod[1] = requestEnd.isAfter(currentPeriod[1]) ? requestEnd : currentPeriod[1];
                    } else {
                        // Добавяме текущия период и започваме нов
                        mergedPeriods.add(currentPeriod);
                        currentPeriod = new LocalDate[]{requestStart, requestEnd};
                    }
                }
                // Добавяме последния период
                mergedPeriods.add(currentPeriod);

                // Изчисляване на свободните периоди
                StringBuilder status = new StringBuilder();
                LocalDate freeStart = startDate;

                for (LocalDate[] period : mergedPeriods) {
                    LocalDate busyStart = period[0];
                    LocalDate busyEnd = period[1];

                    if (freeStart.isBefore(busyStart)) {
                        status.append("Свободна от ")
                                .append(formatDate(freeStart))
                                .append(" до ")
                                .append(formatDate(busyStart.minusDays(1)))
                                .append("\n");
                    }

                    status.append("Заета от ")
                            .append(formatDate(busyStart))
                            .append(" до ")
                            .append(formatDate(busyEnd))
                            .append("\n");

                    freeStart = busyEnd.plusDays(1);
                }

                if (freeStart.isBefore(endDate) || freeStart.equals(endDate)) {
                    status.append("Свободна от ")
                            .append(formatDate(freeStart))
                            .append(" до ")
                            .append(formatDate(endDate))
                            .append("\n");
                }

                carStatuses.add(new CarStatus(car.getBrand(), status.toString()));
            }
        }

        // Добавяне на данните в таблицата
        availableCarsTable.setItems(carStatuses);
    }


    @FXML
    private void manageOperatorsButton()
    {
        if(carTableViewPanel.isVisible() || firmViewPanel.isVisible()){
            carTableViewPanel.setVisible(false);
            firmViewPanel.setVisible(false);
        }
        boolean isVisible = registerOperatorPanel.isVisible();
        registerOperatorPanel.setVisible(!isVisible);

        if(!isVisible){
            firmComboBox.getItems().clear();
            firmComboBox.getItems().addAll(firmRepository.getAll());

            operatorComboBox.getItems().clear();
            List<Operator> allOperators = userRepository.getAllOperators();
            List<Operator> freeOperators = new ArrayList<>();

            List<Firm> allFirms = firmRepository.getAll();
            for (Operator operator : allOperators) {
                boolean isAssigned = false;
                for (Firm firm : allFirms) {
                    // Проверяваме дали операторът е в списъка на операторите за всяка фирма
                    if (firm.getOperatorNames() != null && firm.getOperatorNames().contains(operator.getName())) {
                        isAssigned = true;
                        break;  // Операторът вече е назначен към някоя фирма, излизаме от цикъла
                    }
                }
                // Ако операторът не е назначен към фирма, го добавяме в списъка
                if (!isAssigned) {
                    freeOperators.add(operator);
                }
            }
            // Добавяме само свободните оператори в ComboBox
            operatorComboBox.getItems().addAll(freeOperators);
        }
    }


    // Назначаване на оператор към избрана фирма
    @FXML
    private void addOperatorToFirm() {
        Firm selectedFirm = firmComboBox.getValue();
        Operator selectedOperator = operatorComboBox.getValue();

        if (selectedFirm != null && selectedOperator != null) {
            Long firmId = selectedFirm.getId(); // ID на фирмата
            Long operatorId = selectedOperator.getId(); // ID на оператора

            adminRepository.assignOperatorToFirm(firmId, operatorId);
            showAlert("Успех!", "Операторът беше назначен на фирма!", Alert.AlertType.INFORMATION);
            firmComboBox.getItems().clear();
            operatorComboBox.getItems().clear();
        } else {
            registerOperator();
        }
    }

    private void registerOperator(){
        String name = operatorName.getText().trim();
        String email = operatorEmail.getText().trim();
        String password = operatorPassword.getText().trim();

        // Проверка за празни полета
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Грешка!", "Всички полета са задължителни!", Alert.AlertType.ERROR);
            log.error("Missing operator details.");
            return;
        }

        Firm selectedFirm = firmComboBox.getSelectionModel().getSelectedItem();
        Operator operatorToAdd = new Operator(name, email, password, "OPERATOR");
        userRepository.save(operatorToAdd);

        if (selectedFirm != null) {
            adminRepository.assignOperatorToFirm(selectedFirm.getId(), operatorToAdd.getId());
            showAlert("Успех!", "Операторът беше регистриран и назначен на фирма!", Alert.AlertType.INFORMATION);
            log.info("Operator registered and assigned to firm successfully.");
        } else {
            showAlert("Успех!", "Операторът беше регистриран!", Alert.AlertType.INFORMATION);
            log.info("Operator registered.");
        }

        firmComboBox.getItems().clear();
        operatorName.clear();
        operatorEmail.clear();
        operatorPassword.clear();
    }

    // Регистриране на фирма
    @FXML
    private void firmRegistration(){
        String firmName = firmNameField.getText().trim();
        String firmAddress = firmAddressField.getText().trim();

        if (firmName.isEmpty() || firmAddress.isEmpty()) {
            showAlert("Грешка", "Моля, попълнете всички полета.", Alert.AlertType.ERROR);
            return;
        }

        Firm firm = new Firm(firmName, firmAddress, null);
        firmRepository.save(firm);
        showAlert("Успех", "Фирмата беше създадена успешно!", Alert.AlertType.INFORMATION);
        log.info("Фирмата беше създадена успешно: " + firm.getName());

        Stage loginStage = (Stage) firmNameField.getScene().getWindow();
        loginStage.close();
        firmNameField.clear();
        firmAddressField.clear();
    }

    // Прозорец за регистриране на фирма
    @FXML
    private void openCreateFirmWindow() throws IOException {
        List<Object> firmRepositories = new ArrayList<>();
        firmRepositories.add(firmRepository);

        Scene loginScene = SceneLoader.loadScene("firmView.fxml", 380, 270, firmRepositories);

        Stage createFirmStage = new Stage();
        createFirmStage.setTitle("Нова Фирма");
        createFirmStage.setScene(loginScene);
        createFirmStage.initModality(Modality.APPLICATION_MODAL);
        createFirmStage.showAndWait();
    }

    @FXML
    private void manageCarTableView()
    {
        if(registerOperatorPanel.isVisible() || firmViewPanel.isVisible()){
            registerOperatorPanel.setVisible(false);
            firmViewPanel.setVisible(false);
        }
        carNameColumn.setCellValueFactory(new PropertyValueFactory<>("carName"));
        carStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        boolean isVisible = carTableViewPanel.isVisible();
        carTableViewPanel.setVisible(!isVisible);
    }


    @Override
    public void setRepositories(List<Object> repositories) {
        for(Object o : repositories){
            if(o instanceof FirmRepository){
                this.firmRepository = (FirmRepository) o;
            } else if (o instanceof AdminRepository) {
                this.adminRepository = (AdminRepository) o;
            } else if (o instanceof UserRepository) {
                this.userRepository = (UserRepository) o;
            } else if (o instanceof CarRepository) {
                this.carRepository = (CarRepository) o;
            } else if (o instanceof RequestRepository) {
                this.requestRepository = (RequestRepository) o;
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

    @FXML
    private void logout() {
        Stage stage = (Stage) listView.getScene().getWindow();
        stage.close();
    }
}