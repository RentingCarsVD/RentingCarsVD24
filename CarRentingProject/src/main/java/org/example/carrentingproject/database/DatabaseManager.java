package org.example.carrentingproject.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.function.Function;

public class DatabaseManager{
    //private static final Logger log = Logger.getLogger(DatabaseManager.class);
    private static DatabaseManager instance;
    private SessionFactory sessionFactory;

    private DatabaseManager() {
        try {
            Configuration configuration = new Configuration()
                    .configure("hibernate.cfg.xml");
            sessionFactory = configuration.buildSessionFactory();
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Failed to initialize Hibernate SessionFactory: " + e.getMessage());
        }
    }

//  public SessionFactory getSessionFactory() {
//        return sessionFactory;
//    }


    // Singletone подход
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }


    // Meтод за работа с базата данни
    public <R> R execute(Function<Session, R> action)
    {
        try (Session session = DatabaseManager.getInstance().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                R result = action.apply(session);
                transaction.commit();
                return result;
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }
    // Oтваряне на сесията за execute
    public Session openSession() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            throw new IllegalStateException("SessionFactory is not initialized or has been closed.");
        }
        return sessionFactory.openSession();
    }

    // Затваряне на сесия
    public void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}