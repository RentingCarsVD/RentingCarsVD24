package org.example.carrentingproject.repositories;

import org.example.carrentingproject.controllers.RentalRequestController;
import org.example.carrentingproject.database.DatabaseManager;
import org.example.carrentingproject.models.Car;
import org.example.carrentingproject.models.Client;
import org.example.carrentingproject.models.RentalRequest;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RequestRepository extends GenericRepository<RentalRequest> {

    private final DatabaseManager databaseManager;

    public RequestRepository(Class<RentalRequest> entityClass, DatabaseManager databaseManager) {
        super(entityClass);
        this.databaseManager = databaseManager;
    }

    public List<RentalRequest> findRequestsByOperator(Long operatorId) {
        return databaseManager.execute(session -> {
            Query<RentalRequest> query = session.createQuery(
                    "FROM RentalRequest WHERE operatorName.id = :operatorId", RentalRequest.class);
            query.setParameter("operatorId", operatorId);
            return query.list();
        });
    }

    public RentalRequest findRequestByOperator(Long operatorId) {
        return databaseManager.execute(session -> {
            Query<RentalRequest> query = session.createQuery(
                    "FROM RentalRequest WHERE operatorName.id = :operatorId", RentalRequest.class);
            query.setParameter("operatorId", operatorId);
            query.setMaxResults(1); // Връща само първия резултат
            return query.uniqueResult(); // Връща единствен резултат или null, ако няма резултати
        });
    }

    public List<RentalRequest> findRequestsByClient(Long clientId) {
        return databaseManager.execute(session -> {
            Query<RentalRequest> query = session.createQuery(
                    "FROM RentalRequest WHERE clientName.id = :clientId AND status = :status", RentalRequest.class);
            query.setParameter("clientId", clientId);
            query.setParameter("status", RentalRequestController.RentalStatus.APPROVED); // Само одобрените заявки
            return query.list(); // Връща списък с резултати
        });
    }

    public List<RentalRequest> findAllCompletedRequests() {
        return databaseManager.execute(session -> {
            Query<RentalRequest> query = session.createQuery(
                    "FROM RentalRequest WHERE status = :status", RentalRequest.class);
            query.setParameter("status", RentalRequestController.RentalStatus.COMPLETED);
            return query.list();
        });
    }

    public List<Car> findAvailableCars(LocalDate startDate, LocalDate endDate) {
        return databaseManager.execute(session -> {
            Query<Car> query = session.createQuery(
                    "FROM Car c WHERE c.id NOT IN (" +
                            "SELECT r.car.id FROM RentalRequest r " +
                            "WHERE :startDate < r.rentalEndDate AND :endDate > r.rentalStartDate" +
                            ")", Car.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.list();
        });
    }

    public List<RentalRequest> getRentalRequestsForCar(Long carId) {
        return databaseManager.execute(session -> {
            Query<RentalRequest> query = session.createQuery(
                    "FROM RentalRequest r WHERE r.car.id = :carId", RentalRequest.class);
            query.setParameter("carId", carId);
            return query.list();
        });
    }
}