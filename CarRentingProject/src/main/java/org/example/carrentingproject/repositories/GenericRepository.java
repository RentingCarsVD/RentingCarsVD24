package org.example.carrentingproject.repositories;

import org.apache.log4j.Logger;
import org.example.carrentingproject.database.DatabaseManager;

import java.util.List;
import java.util.Optional;

public class GenericRepository<T> {
    private static final Logger log = Logger.getLogger(GenericRepository.class);

    private final DatabaseManager databaseManager;
    private final Class<T> entityClass;

//    // Конструктор за тестове (с инжектиране на зависимости)
//    public GenericRepository(Class<T> entityClass, DatabaseManager databaseManager) {
//        this.entityClass = entityClass;
//        this.databaseManager = databaseManager;
//    }
    // Основен конструктор (сингълтон подход)
    public GenericRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.databaseManager = DatabaseManager.getInstance();
    }

    public void save(T entity){
        databaseManager.execute(session -> {
            session.save(entity);// Hibernate `save` метод.
            log.info(entityClass.getSimpleName() + " saved successfully.");
            return null;
        });
    }

    public void update(T entity){
        databaseManager.execute(session -> {
            session.update(entity); // Hibernate `update` метод
            log.info(entityClass.getSimpleName() + " updated successfully.");
            return null;
        });

    }

    public void delete(T entity) {
        databaseManager.execute(session -> {
            session.delete(entity); // Hibernate `delete` метод
            log.info(entityClass.getSimpleName() + " deleted successfully.");
            return null;
        });

    }

    public Optional<T> getById(Long id) {
        return databaseManager.execute(session -> {
            T entity = session.get(entityClass, id); // Hibernate `get` метод
            log.info(entityClass.getSimpleName() + " fetched successfully.");
            return Optional.ofNullable(entity);
        });
    }


    public List<T> getAll() {
        return databaseManager.execute(session -> {
            List<T> entities = session.createQuery("FROM " + entityClass.getSimpleName(), entityClass).list();
            log.info("getAll() used: " + entities.size() + " " + entityClass.getSimpleName() + " entities found.");
            return entities;
        });
    }
}
