package org.example.carrentingproject;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.example.carrentingproject.repositories.GenericRepository;
import org.example.carrentingproject.repositories.RepositoryInjected;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class SceneLoader {

    // Параметри : fxml/path, width, height, List<Object> repstrs
    public static Scene loadScene(String fxmlPath, int width, int height, List<Object> repositories) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlPath));
        Scene scene = new Scene(fxmlLoader.load(), width, height);

        Object controllerClass = fxmlLoader.getController();

        // Ако контролерът имплементира RepositoryInjected
        if (controllerClass instanceof RepositoryInjected)
        {
            ((RepositoryInjected) controllerClass).setRepositories(repositories);
        }
        return scene;
    }

}