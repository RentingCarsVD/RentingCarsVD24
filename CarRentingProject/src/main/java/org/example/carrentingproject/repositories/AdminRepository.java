package org.example.carrentingproject.repositories;

import org.apache.log4j.Logger;
import org.example.carrentingproject.database.DatabaseManager;
import org.example.carrentingproject.models.Client;
import org.example.carrentingproject.models.Firm;
import org.example.carrentingproject.models.Operator;
import org.example.carrentingproject.models.User;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AdminRepository extends GenericRepository<Firm> {
    private static final Logger log = Logger.getLogger(AdminRepository.class);


    private FirmRepository firmRepository;
    private UserRepository userRepository;

    public AdminRepository(Class<Firm> entityClass, FirmRepository firmRepository, UserRepository userRepository) {
        super(entityClass);
        this.firmRepository = firmRepository;
        this.userRepository = userRepository;
    }

    // Взимаме операторите от базата
    public List<User> getAllOperators() {
        return DatabaseManager.getInstance().execute(session ->
                session.createQuery("from User where accountType = :accountType", User.class)
                        .setParameter("accountType", "OPERATOR")
                        .list()
        );
    }

    public List<Operator> getOperatorsByFirm(long firmId) {
        return DatabaseManager.getInstance().execute(session -> {
            Query<Operator> query = session.createQuery(
                    "FROM Operator WHERE firm.id = :firmId", Operator.class);
            query.setParameter("firmId", firmId);
            return query.list();
        });
    }


    // Актуализираме operator_names в firms_table
    public void assignOperatorToFirm(Long firmId, Long operatorId)
    {
        Optional<Firm> firmOptional = firmRepository.getById(firmId);
        if (!firmOptional.isPresent()) {
            System.out.println("Firm not found with ID: " + firmId);
            return;
        }
        Firm firm = firmOptional.get();

        Optional<User> operatorOptional = userRepository.getById(operatorId);
        if (!operatorOptional.isPresent() || !operatorOptional.get().getAccountType().equals("OPERATOR")) {
            System.out.println("Operator not found or not valid.");
            return;
        }
        User operator = operatorOptional.get();

        operator.setFirm(firm);
        userRepository.update(operator);

        // Вземаме текущите оператори и добавяме новия
        String currentOperators = firm.getOperatorNames();
        if (currentOperators == null || currentOperators.isEmpty()) {
            firm.setOperatorNames(operator.getName());
        } else {
            // Преобразуваме имената в списък
            List<String> operatorList = new ArrayList<>(Arrays.asList(currentOperators.split(", ")));

            // Проверяваме дали новият оператор вече е в списъка
            if (!operatorList.contains(operator.getName())) {
                operatorList.add(operator.getName()); // Добавяме само ако не съществува
                firm.setOperatorNames(String.join(", ", operatorList)); // Обновяваме колоната
            } else {
                System.out.println("Operator is already assigned to this firm.");
            }
        }
        firmRepository.update(firm);
        log.info("Operator assigned successfully.");
    }

    // Изтриване на оператор
    public void removeOperator(Long operatorId){
        Optional<User> operatorOptional = userRepository.getById(operatorId);
        if (!operatorOptional.isPresent() || !operatorOptional.get().getAccountType().equals("OPERATOR")) {
            log.info("Operator not found or not valid.");
            return;
        }
        User operator = operatorOptional.get();

        // Ако операторът е свързан с фирма, актуализираме `operator_names`
        Firm firm = operator.getFirm();
        if (firm != null) {
            String currentOperators = firm.getOperatorNames();
            if (currentOperators != null && !currentOperators.isEmpty()) {
                List<String> operatorList = new ArrayList<>(Arrays.asList(currentOperators.split(", ")));
                operatorList.remove(operator.getName());
                firm.setOperatorNames(String.join(", ", operatorList));
                firmRepository.update(firm);
            }
        }
        // Премахваме оператора от базата
        userRepository.delete(operator);

        log.info("Operator removed successfully.");
    }
}