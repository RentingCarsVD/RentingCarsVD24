package org.example.carrentingproject.repositories;

import org.example.carrentingproject.database.DatabaseManager;
import org.example.carrentingproject.models.Car;

import java.util.List;

public class CarRepository extends GenericRepository<Car>{

    private final DatabaseManager databaseManager;

    public CarRepository(Class<Car> entityClass, DatabaseManager databaseManager) {
        super(entityClass);
        this.databaseManager = databaseManager;
    }

    // Метод за извличане на коли за дадена фирма
    public List<Car> getCarsByFirm(Long firmId) {
        return databaseManager.execute(session ->
                session.createQuery("FROM Car WHERE firm.id = :firmId", Car.class)
                        .setParameter("firmId", firmId)
                        .list()
        );
    }
}
