package org.example.demo2;

import javafx.fxml.FXML;
import org.example.demo2.model.Client;

public class ClientController {

    private Client client =  new Client("client1", "clientPass");

    @FXML
    public void initialize(){
        client.displayRole();
    }
}
