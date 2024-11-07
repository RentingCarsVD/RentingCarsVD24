package org.example.rentingcars;

import org.hibernate.Session;

public interface HibernateOperation<T> {
    void execute(Session session);
}