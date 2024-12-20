package org.example.carrentingproject.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.carrentingproject.GlobalData;
import org.example.carrentingproject.models.Operator;
import org.example.carrentingproject.models.RentalRequest;
import org.example.carrentingproject.repositories.CarRepository;
import org.example.carrentingproject.repositories.RepositoryInjected;
import org.example.carrentingproject.repositories.RequestRepository;
import org.example.carrentingproject.repositories.UserRepository;

import java.util.List;

public class ConditionController implements RepositoryInjected {

    @FXML
    private TextField conditionField;
    @FXML
    private Label requestFromClient;

    //private RentalRequest rentalRequest;
    private UserRepository userRepository;
    private RequestRepository requestRepository;


    @FXML
    private void initialize()
    {
        Operator currentOperator = GlobalData.getCurrentOperator();
        List<RentalRequest> requests = requestRepository.findRequestsByOperator(currentOperator.getId());

        for (RentalRequest request : requests) {
            requestFromClient.setText("Request ID: " + request.getId() + "From: " + request.getClientName()
                    + "For: " + request.getRequestTime() + ", Status: " + request.getStatus());
        }
    }

    @Override
    public void setRepositories(List<Object> repositories) {
        for (Object o : repositories) {
            if (o instanceof RequestRepository) {
                this.requestRepository = (RequestRepository) o;
            } else if (o instanceof UserRepository) {
                this.userRepository = (UserRepository) o;
            }
        }
    }
}
