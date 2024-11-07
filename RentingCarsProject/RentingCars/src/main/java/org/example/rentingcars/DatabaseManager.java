package org.example.rentingcars;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class DatabaseManager {

    private static SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    // Метод за изпълнение на операция с Hibernate
    public static <T> void executeWithTransaction(HibernateOperation<T> operation) {
        Session session = getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            operation.execute(session); // Изпълняваме операцията
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e; // Повдигаме грешката, за да може да бъде обработена по-горе
        } finally {
            session.close(); // Затваряне на сесията
        }
    }
}