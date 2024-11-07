package org.example.rentingcars;

import javafx.scene.control.Alert;
import org.example.rentingcars.Users;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class UsersService {

    private static String currentUserName; // Променлива за съхраняване на името

    public void setCurrentUserName(String userName){
        currentUserName = userName;
    }
    public String getCurrentUserName(){
        return currentUserName;
    }

    // Метод за извличане на типа на акаунтa
    public String getUserAccountType(String username, String password) {
        try (Session session = DatabaseManager.getSessionFactory().openSession())
        {
            Query<Users> query = session.createQuery("FROM Users WHERE name = :username AND password = :password", Users.class);
            query.setParameter("username", username);
            query.setParameter("password", password);

            Users user = query.uniqueResult();
            return (user != null) ? user.getAccountType() : null; // Връща типа на акаунта или null
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Метод за добавяне на нов потребител
    public void addUser(Users user) {
        Transaction transaction = null;
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // Връща промените обратно при грешка
            }
            e.printStackTrace();
        }
    }

    // Проверка дали потребителското име вече е заето
    public boolean isUsernameTaken(String username) {
        try (Session session = DatabaseManager.getSessionFactory().openSession())
        {
            Query<Long> query = session.createQuery("SELECT COUNT(u.id) FROM Users u WHERE u.name = :username", Long.class);
            query.setParameter("username", username);
            Long count = query.uniqueResult();
            return count != null && count > 0; // Връща true, ако името е заето
        }
        catch (Exception e) {
            e.printStackTrace();
            return false; // В случай на грешка, връща false
        }
    }
}