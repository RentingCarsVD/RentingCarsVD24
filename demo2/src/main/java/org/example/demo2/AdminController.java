package org.example.demo2;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.example.demo2.model.Admin;

import java.io.IOException;

public class AdminController {

    @FXML
    private TextField companyName; // Име на компания
    @FXML
    private TextField operatorName; // Име на Оператор
    @FXML
    private TextField operatorPassword; // Парола на Оператор

    private Admin admin = new Admin("adminUser", "adminPass");

    @FXML
    protected void onCreateCompanyClick(){
        String company = companyName.getText();
        // метод на Admin класа
        admin.createCompany(company);
    }
    @FXML
    protected void onCreateOperatorClick(){
        String operator = operatorName.getText();
        String password = operatorPassword.getText();
        // метод на Admin класа
        admin.createOperator(operator, password);
    }
    @FXML
    public void initialize(){
        admin.displayRole();
    }
}
