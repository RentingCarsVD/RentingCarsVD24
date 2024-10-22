package org.example.demo2.model;

public class Operator extends User{

    public Operator(String username, String password) {
        super(username, password);
    }

    public void registerClient(String clientName) {
        System.out.println("Клиентът " + clientName + " е създаден.");
    }

    @Override
    public void displayRole() {
        System.out.println("Роля: 'Оператор'");
    }
}
