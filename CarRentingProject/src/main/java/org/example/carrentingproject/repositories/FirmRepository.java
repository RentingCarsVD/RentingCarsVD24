package org.example.carrentingproject.repositories;

import org.example.carrentingproject.database.DatabaseManager;
import org.example.carrentingproject.models.Firm;

import java.util.List;
import java.util.Optional;

public class FirmRepository extends GenericRepository<Firm> {

    //private final DatabaseManager databaseManager;

    public FirmRepository(Class<Firm> entityClass){//DatabaseManager databaseManager) {
        super(entityClass);
        //this.databaseManager = databaseManager;
    }

    public Optional<Firm> findByName(String name) {
        return getAll().stream()
                .filter(firm -> firm.getName().equalsIgnoreCase(name))
                .findFirst();
    }

}
