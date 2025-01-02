package org.example.carrentingproject.repositories;

import org.example.carrentingproject.database.DatabaseManager;
import org.example.carrentingproject.models.Client;
import org.example.carrentingproject.models.Operator;
import org.example.carrentingproject.models.User;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class UserRepository extends GenericRepository<User>{

    private final DatabaseManager databaseManager;

    public UserRepository(Class<User> entityClass, DatabaseManager databaseManager) {
        super(entityClass);
        this.databaseManager = databaseManager;
    }

    // Търсене в базата по име
    public Optional<User> findByUsername(String username) {
        return databaseManager.execute(session -> {
            Query<User> query = session.createQuery("FROM User WHERE name = :username", User.class);
            query.setParameter("username", username);
            return Optional.ofNullable(query.uniqueResult());
        });
    }

    // Дали съществува потребител с такова име
    public boolean isUsernameExists(String username) {
        return findByUsername(username).isPresent();
    }

    // Лист на всички оператори
    public List<Operator> getAllOperators() {
        return getAll()
                .stream()
                .filter(user -> user instanceof Operator)
                .map(user -> (Operator) user)
                .toList();
    }

    // Търсене на оператор в базата по име
    public Optional<Operator> findByName(String name) {
        return databaseManager.execute(session -> {
            Query<Operator> query = session.createQuery("FROM Operator WHERE name = :name", Operator.class);
            query.setParameter("name", name);
            return Optional.ofNullable(query.uniqueResult());
        });
    }

    // Лист на всички клиенти за дадена фирма
    public List<Client> getClientsByFirm(long firmId) {
        return databaseManager.execute(session -> {
            Query<Client> query = session.createQuery(
                    "FROM Client WHERE firm.id = :firmId", Client.class);
            query.setParameter("firmId", firmId);
            return query.list();
        });
    }
}