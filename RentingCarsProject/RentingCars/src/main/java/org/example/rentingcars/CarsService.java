package org.example.rentingcars;

import org.example.rentingcars.Cars;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class CarsService {

    // Метод за създаване на нов запис (Create)
    public void addCar(Cars car) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(car);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Грешка при добавяне на колата: " + e.getMessage());
        }
    }

    // Метод за прочитане на всички записи (Read)
    public List<Cars> getAllCars() {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            Query<Cars> query = session.createQuery("FROM Cars", Cars.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Грешка при извличане на колите: " + e.getMessage());
            return null;
        }
    }

    // Метод за актуализиране на запис (Update)
    public void updateCar(Cars car) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(car);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Грешка при актуализиране на колата: " + e.getMessage());
        }
    }

    // Метод за изтриване на запис (Delete)
    public void deleteCar(Long carId) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Cars car = session.get(Cars.class, carId);
            if (car != null) {
                session.delete(car);
                transaction.commit();
            } else {
                System.out.println("Колата с ID " + carId + " не е намерена.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Грешка при изтриване на колата: " + e.getMessage());
        }
    }
    // Зареждане на наличните автомобили
    public List<Cars> getAvailableCars() {
        try (Session session = DatabaseManager.getSessionFactory().openSession())
        {
            Query<Cars> query = session.createQuery("FROM Cars WHERE isAvailable = true", Cars.class);
            return query.list(); // Връща списък с налични автомобили
        }
    }
}