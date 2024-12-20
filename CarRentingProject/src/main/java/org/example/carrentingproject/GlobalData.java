package org.example.carrentingproject;

import org.example.carrentingproject.models.Car;
import org.example.carrentingproject.models.Client;
import org.example.carrentingproject.models.Operator;

public class GlobalData {
    private static Client currentClient;
    private static Operator currenOperator;
    private static Car currentCar;

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
}