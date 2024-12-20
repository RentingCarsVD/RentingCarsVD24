package org.example.carrentingproject.controllers;

import javafx.collections.FXCollections;
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
    private ListView firmsListView;

    private FirmRepository firmRepository;
    private CarRepository carRepository;
    private UserRepository userRepository;
    private RequestRepository requestRepository;


    @FXML // Label за фирма
    private void initialize() {
        Client currentClient = GlobalData.getCurrentClient();
        if (currentClient != null && currentClient.getSelectedFirm() != null) {
            firmNameLabel.setText("Фирма: " + currentClient.getSelectedFirm().getName());
        } else {
            firmNameLabel.setText("Изберете фирма");
        }

    }

    @FXML // Списък с фирми
    private void showFirmsList() {
        List<Firm> firms = firmRepository.getAll();
        // Показваме фирмите в ListView
        firmsListView.getItems().setAll(firms);

    }

    @FXML // Списък с колите на фирмата
    private void showFirmsCarsList()
    {
        Client currentClient = GlobalData.getCurrentClient();

        Firm selectedFirm = currentClient.getSelectedFirm();
        if (selectedFirm == null) {
            showAlert("Моля, изберете фирма преди да продължите!");
            log.error("No firm selected by the client.");
            return;
        }

        List<Car> cars = carRepository.getCarsByFirm(selectedFirm.getId());
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

        Scene rentalScene = SceneLoader.loadScene("rentalRequestView.fxml", 380, 270, repositories);

        Stage createFirmStage = new Stage();
        createFirmStage.setTitle("Попълване на заявка");
        createFirmStage.setScene(rentalScene);
        createFirmStage.initModality(Modality.APPLICATION_MODAL);
        createFirmStage.showAndWait();
    }

    // Aктуализираме firm_id & selected_firm_id за избрана фирма
    @FXML
    private void updateClientWithFirm() {
        // Избор на фирма от ListView
        Firm selectedFirm = (Firm) firmsListView.getSelectionModel().getSelectedItem();
        if (selectedFirm == null) {
            showAlert("Моля, изберете фирма!");
            return;
        }
        // Извличаме текущия клиент от GlobalData
        Client currentClient = GlobalData.getCurrentClient();
        if (currentClient == null) {
            showAlert("Няма избран клиент!");
            return;
        }

        // Извличаме клиента от базата данни чрез неговото ID
        Optional<User> optionalUser = userRepository.getById(currentClient.getId());

        // Проверяваме дали това е обект от тип Client
        User user = optionalUser.get();

        if (user instanceof Client client)
        {
            client.setSelectedFirm(selectedFirm);  // Актуализираме `selectedFirm`
            client.setFirm(selectedFirm);  // Актуализираме също и `firm_id`

            try {
                userRepository.update(client);
                // Обновяваме клиента в GlobalData
                GlobalData.setCurrentClient(client);
                    showAlert("Успешно избрахте фирма за наемане на коли!");
                updateFirmNameLabel(selectedFirm);
            } catch (Exception e) {
                showAlert("Възникна грешка при актуализацията на клиента: " + e.getMessage());
            }
        } else {
            showAlert("Този потребител не е клиент.");
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
    // Затваряме текущата сцена
    @FXML
    private void logout() {
        Stage stage = (Stage) carlistView.getScene().getWindow();
        stage.close();
    }
}
