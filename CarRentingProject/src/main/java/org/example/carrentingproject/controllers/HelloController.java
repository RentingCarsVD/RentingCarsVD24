package org.example.carrentingproject.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.example.carrentingproject.HelloApplication;
import org.example.carrentingproject.SceneLoader;
import org.example.carrentingproject.GlobalData;
import org.example.carrentingproject.database.DatabaseManager;
import org.example.carrentingproject.models.*;
import org.example.carrentingproject.repositories.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HelloController implements RepositoryInjected {
    private static final Logger log = Logger.getLogger(HelloController.class);

    @FXML
    private TextField registerName, registerEmail, loginName;
    @FXML
    private PasswordField registerPass, loginPass;
    @FXML
    private ComboBox<String> accountTypeComboBox;

    private UserRepository userRepository;

    // Конструктор без параметри за FXML
    public HelloController() {}

    // Създаване на конкретен потребител
    private User createUserByAccountType(String name, String email, String password, String accountType) {
        switch (accountType) {
            case "CLIENT":
                return new Client(name, email, password, accountType);
            case "OPERATOR":
                return new Operator(name, email, password, accountType);
            case "ADMIN":
                return new Admin(name, email, password, accountType);
            default:
                return new User(name, email, password, accountType);
        }
    }

    // Метод за регистрация от hello-view.fxml
    @FXML
    protected void registerUser()
    {
        String name = registerName.getText();
        String email = registerEmail.getText();
        String password = registerPass.getText();
        String accountType = accountTypeComboBox.getValue();

        if(name.isEmpty() || email.isEmpty() || password.isEmpty() || accountType == null){
            showAlert("Всички полета както и изборът на акаунт са задължителни!");
            return;
        }

        if(userRepository.isUsernameExists(name)){
            showAlert("Потребителското име е заето!");
            return;
        }

        User newUser = createUserByAccountType(name, email, password, accountType);
        userRepository.save(newUser);
            showAlert("Успешна регистрация на потребител: " + newUser.getName());

        registerName.clear();
        registerEmail.clear();
        registerPass.clear();
    }

    //Метод за вход от login-view.fxml
    @FXML
    protected void loginUser() throws IOException {
        GlobalData.setCurrentOperator(null);
        GlobalData.setCurrentClient(null);
        String username = loginName.getText();
        String password = loginPass.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Моля, въведете потребителско име и парола!");
            return;
        }

        Optional<User> userInDb = userRepository.findByUsername(username);

        if (userInDb.isEmpty()) {
            showAlert("Потребител с това потребителско име не съществува.");
            return;
        }

        // Обект User
        User user = userInDb.get();

        // Проверка за сходни пароли
        if (!user.getPassword().equals(password)) {
            showAlert("Грешна парола. Моля, опитайте отново.");
            return;
        }

        if (user instanceof Client) {
            // Записваме клиента в GlobalData
            GlobalData.setCurrentClient((Client) user);
        }
        else if (user instanceof Operator) {
            // Записваме оператора в GlobalData
            GlobalData.setCurrentOperator((Operator) user);
        }


        //Взимаме типа на на акаунта
        String accountType = user.getAccountType();
        log.info("Успешен вход: " + user.getName() + " с тип акаунт: " + accountType);
            showAlert("Добре дошли, " + user.getName() + "!");
        navigateToScene(accountType);
    }

    // Отваряне на Login прозорче oт hello-view.fxml
    @FXML
    protected void openLoginWindow() throws IOException {
        List<Object> listOfRepostrs = new ArrayList<>();
        listOfRepostrs.add(userRepository);

        Scene loginScene = SceneLoader.loadScene("login-view.fxml", 380, 270, listOfRepostrs);

        Stage loginStage = new Stage();
        loginStage.setTitle("Вход");
        loginStage.setScene(loginScene);
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.showAndWait();
    }

    @FXML
    protected void navigateToScene(String accountType) throws IOException {
        FirmRepository firmRepository = new FirmRepository(Firm.class);
        UserRepository userRepository = new UserRepository(User.class, DatabaseManager.getInstance());
        AdminRepository adminRepository = new AdminRepository(Firm.class, firmRepository, userRepository);
        CarRepository carRepository = new CarRepository(Car.class, DatabaseManager.getInstance());
        RequestRepository requestRepository = new RequestRepository(RentalRequest.class, DatabaseManager.getInstance());

        List<Object> repositories = new ArrayList<>();
        repositories.add(userRepository);
        repositories.add(firmRepository);
        repositories.add(adminRepository);
        repositories.add(carRepository);
        repositories.add(requestRepository);

        Scene nextScene;

        if ("ADMIN".equals(accountType)) {
            nextScene = SceneLoader.loadScene("/org/example/carrentingproject/adminView.fxml",
                    740, 540, repositories);
        } else if ("OPERATOR".equals(accountType)) {
            nextScene = SceneLoader.loadScene("/org/example/carrentingproject/operatorView.fxml",
                    680, 480, repositories);
        } else {
            nextScene = SceneLoader.loadScene("/org/example/carrentingproject/clientView.fxml",
                    600, 440, repositories);
        }
        // Създаване на нов Stage
        Stage stage = new Stage();
        stage.setTitle("Сцена за " + accountType);
        stage.setScene(nextScene);
        stage.show();
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
        for(Object o : repositories){
            if(o instanceof UserRepository){
                this.userRepository = (UserRepository) o;
            }
        }
    }
}