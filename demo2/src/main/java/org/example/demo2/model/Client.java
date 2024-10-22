package org.example.demo2.model;

public class Client extends User{

    public Client(String username, String password) {
        super(username, password);
    }

    @Override
    public void displayRole() {
        System.out.println("Роля: 'Клиент'");
    }
}
