package org.example.demo2.model;

public class Admin extends User{

    public Admin(String username, String password) {
        super(username, password);
    }
    public void createCompany(String companyName) {
        System.out.println("Фирмата " + companyName + " е създадена.");
    }

    public void createOperator(String operatorName, String operatorPassword) {
        System.out.println("Операторът " + operatorName + " е създаден.");
    }

    @Override
    public void displayRole() {
        System.out.println("Роля: 'Администратор'");
    }
}
