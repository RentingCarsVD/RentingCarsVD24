package org.example.carrentingproject;

import org.example.carrentingproject.models.Car;
import org.example.carrentingproject.models.Client;
import org.example.carrentingproject.models.Operator;
import org.example.carrentingproject.models.RentalRequest;

public class GlobalData {
    private static Client currentClient;
    private static Operator currenOperator;
    private static Car currentCar;
    private static RentalRequest currentRequest;

    public static Client getCurrentClient() {
        return currentClient;
    }

    public static Operator getCurrentOperator(){
        return currenOperator;
    }

    public static void setCurrentClient(Client client) {
        currentClient = client;
    }

    public static void setCurrentOperator(Operator operator) {
        currenOperator = operator;
    }

    public static Car getCurrentCar(){
        return currentCar;
    }

    public static void setCurrentCar(Car car) {
        currentCar = car;
    }

    public static RentalRequest getRentalRequest() {
        return currentRequest;
    }
    public static void setRentalRequest(RentalRequest rentalRequest) {
        currentRequest = rentalRequest;
    }


}