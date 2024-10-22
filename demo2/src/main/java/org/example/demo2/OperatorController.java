package org.example.demo2;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.example.demo2.model.Operator;

public class OperatorController {

    private Operator operator = new Operator("operator1", "operatorPass");

    @FXML
    private TextField clientName; //// Име на Клиент

    @FXML
    protected void onRegisterClientClick(){
        String client = clientName.getText();
        // метод на Operator класа
        operator.registerClient(client);
    }
    @FXML
    public void initialize(){
        operator.displayRole();
    }
    public void onRegisterCar(){
        return;
    }
}
