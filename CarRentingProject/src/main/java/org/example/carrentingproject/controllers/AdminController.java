package org.example.carrentingproject.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.example.carrentingproject.SceneLoader;
import org.example.carrentingproject.database.DatabaseManager;
import org.example.carrentingproject.models.Car;
import org.example.carrentingproject.models.Operator;
import org.example.carrentingproject.models.User;
import org.example.carrentingproject.models.Firm;
import org.example.carrentingproject.repositories.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminController implements RepositoryInjected {
    private static final Logger log = Logger.getLogger(AdminController.class);

    @FXML
    private AnchorPane registerOperatorPanel;
    @FXML
    private TextField firmNameField, firmAddressField;
    @FXML
    private ListView listView;
    @FXML
    private ComboBox<Firm> firmComboBox;
    @FXML
    private ComboBox<Operator> operatorComboBox;
    @FXML
    private TextField operatorName, operatorEmail; // Име, E-mail
    @FXML
    private PasswordField operatorPassword; // Парола

    private FirmRepository firmRepository;
    private UserRepository userRepository;
    private AdminRepository adminRepository;
    private CarRepository carRepository;

    public AdminController() {}

    @FXML
    private void showAllFirms(){
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
        // Показваме операторите в ListView
        listView.getItems().setAll(operators);
    }
    @FXML
    private void showAllCars(){
        List<Car> cars = carRepository.getAll();
        // Показваме операторите в ListView
        listView.getItems().setAll(cars);
    }

    @FXML
    private void removeSelectedOperator() {
        User selectedOperator = (User) listView.getSelectionModel().getSelectedItem();
        if (selectedOperator == null) {
            System.out.println("No operator selected.");
            return;
        }
        adminRepository.removeOperator(selectedOperator.getId());
        // Обновяваме ListView
        showAllOperators();
    }

    @FXML
    private void manageOperatorsButton() {
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
            }
        }
    }
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    // Затваряме текущата сцена
    @FXML
    private void logout() {
        Stage stage = (Stage) listView.getScene().getWindow();
        stage.close();
    }
}