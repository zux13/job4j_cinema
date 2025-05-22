package ru.job4j.cinema.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Hall;

import java.util.Collection;
import java.util.Optional;

@Repository
public class Sql2oHallRepository implements HallRepository {

    private final Sql2o sql2o;

    public Sql2oHallRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<Hall> findById(int id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM halls WHERE id=:id");
            Hall hall = query.addParameter("id", id)
                    .setColumnMappings(Hall.COLUMN_MAPPING)
                    .executeAndFetchFirst(Hall.class);
            return Optional.ofNullable(hall);
        }
    }

    @Override
    public Collection<Hall> findAll() {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM halls");
            return query.setColumnMappings(Hall.COLUMN_MAPPING)
                    .executeAndFetch(Hall.class);
        }
    }
}
