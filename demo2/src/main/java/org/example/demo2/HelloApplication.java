package org.example.demo2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


public class HelloApplication extends Application {
    // Статична променлива за съхранение на екземпляра
    private static HelloApplication instance;

    // За да имаме достъп до сцената в целия клас
    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        // Запазване на инстанцията
        instance = this;
        this.primaryStage = stage;
        showLoginScene();  // ще започнем с екрана за логин
    }

    // Статичен метод за връщане на инстанцията
    public static HelloApplication getInstance() {
        return instance;
    }

    // Показване на login екран
    private void showLoginScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Показване на admin екран
    public void showAdminScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("admin-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        primaryStage.setScene(scene);
    }

    // Показване на operator екран
    public void showOperatorScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("operator-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        primaryStage.setScene(scene);
    }

    // Показване на client екран
    public void showClientScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("client-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}