package org.example.carrentingproject.repositories;

import org.example.carrentingproject.database.DatabaseManager;
import org.example.carrentingproject.models.RentalRequest;
import org.hibernate.query.Query;

import java.util.List;

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

}
