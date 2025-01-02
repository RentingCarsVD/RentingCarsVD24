package org.example.carrentingproject;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.Logger;
import org.example.carrentingproject.database.DatabaseManager;
import org.example.carrentingproject.models.User;
import org.example.carrentingproject.repositories.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application {
    private static final Logger log = Logger.getLogger(HelloApplication.class);

    @Override
    public void init() throws Exception {
        try {
            log.info("Initializing database connection...");
            DatabaseManager.getInstance();
            log.info("Database connection initialized successfully.");
        } catch (Exception e) {
            log.error("Failed to initialize database connection.", e);
            throw new RuntimeException("Could not initialize database connection. Exiting application.");
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        UserRepository userRepository = new UserRepository(User.class, DatabaseManager.getInstance());
        List<Object> repositories = new ArrayList<>();
        repositories.add(userRepository);

        Scene scene = SceneLoader.loadScene("hello-view.fxml",440, 380, repositories);
        stage.setTitle("CarRentingApplication!");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        log.info("Application stopping...");
        DatabaseManager.getInstance().shutdown();
        log.info("Database connection closed.");
    }

    public static void main(String[] args) {
        launch();
    }
}