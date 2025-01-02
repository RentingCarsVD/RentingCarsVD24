package org.example.carrentingproject;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ClientData {

    private final SimpleIntegerProperty requestId; // ID на заявката
    private final SimpleStringProperty clientName; // Име на клиента
    private final SimpleDoubleProperty amountDue;  // Дължима сума

    public ClientData(int requestId, String clientName, double amountDue)
    {
        this.requestId = new SimpleIntegerProperty(requestId);
        this.clientName = new SimpleStringProperty(clientName);
        this.amountDue = new SimpleDoubleProperty(amountDue);
    }

    public SimpleIntegerProperty requestIdProperty() {
        return requestId;
    }

    public SimpleStringProperty clientNameProperty() {
        return clientName;
    }

    public SimpleDoubleProperty amountDueProperty() {
        return amountDue;
    }

    public int getRequestId() {
        return requestId.get();
    }

    public String getClientName() {
        return clientName.get();
    }

    public double getAmountDue() {
        return amountDue.get();
    }
}

