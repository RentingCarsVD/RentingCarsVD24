package org.example.carrentingproject.repositories;

import org.example.carrentingproject.database.DatabaseManager;
import org.example.carrentingproject.models.Car;
import org.example.carrentingproject.models.RentalRequest;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    public List<RentalRequest> findOverlappingRequests(Car car, LocalDate startDate, LocalDate endDate) {
        return databaseManager.execute(session -> {
            String hql = "FROM RentalRequest r " +
                    "WHERE r.car = :car AND " +
                    "r.rentalStartDate <= :endDate AND r.rentalEndDate >= :startDate";
            Query<RentalRequest> query = session.createQuery(hql, RentalRequest.class);
            query.setParameter("car", car);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.list();
        });
    }

    public Optional<Period> getMergedBusyPeriod(Car car, LocalDate startDate, LocalDate endDate) {
        // Извиква съществуващия метод за намиране на припокриващи се заявки
        List<RentalRequest> overlappingRequests = findOverlappingRequests(car, startDate, endDate);

        if (overlappingRequests.isEmpty()) {
            return Optional.empty(); // Ако няма заявки, връщаме празно
        }

        // Намиране на най-ранната и най-късната дата
        LocalDate minStartDate = startDate;
        LocalDate maxEndDate = endDate;

        for (RentalRequest request : overlappingRequests) {
            LocalDate busyStart = request.getRentalStartDate();
            LocalDate busyEnd = request.getRentalEndDate();

            if (busyStart.isBefore(minStartDate)) {
                minStartDate = busyStart;
            }
            if (busyEnd.isAfter(maxEndDate)) {
                maxEndDate = busyEnd;
            }
        }

        // Връщаме обединения период
        return Optional.of(new Period(minStartDate, maxEndDate));
    }

    public class Period {
        private LocalDate start;
        private LocalDate end;

        public Period(LocalDate start, LocalDate end) {
            this.start = start;
            this.end = end;
        }

        public LocalDate getStart() {
            return start;
        }

        public LocalDate getEnd() {
            return end;
        }

        @Override
        public String toString() {
            return "Период: от " + start.toString() + " до " + end.toString();
        }
    }

}

