package ru.job4j.cinema.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.cinema.model.Hall;

import java.util.Collection;
import java.util.Optional;

@Repository
public class Sql2oHallRepository implements HallRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(Sql2oHallRepository.class);
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

    @Override
    public Optional<Hall> save(Hall hall) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                    INSERT INTO halls(`name`, `row_count`, `place_count`, `description`)
                    VALUES (:name, :rowCount, :placeCount, :description);
                    """;
            Query query = connection.createQuery(sql, true)
                    .addParameter("name", hall.getName())
                    .addParameter("rowCount", hall.getRowCount())
                    .addParameter("placeCount", hall.getPlaceCount())
                    .addParameter("description", hall.getDescription());
            int generatedKey = query.executeUpdate().getKey(Integer.class);
            hall.setId(generatedKey);
            return Optional.of(hall);
        } catch (Sql2oException e) {
            LOGGER.error("Error saving hall: {}", hall, e);
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(int id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("DELETE FROM halls WHERE id=:id");
            query.addParameter("id", id).executeUpdate();
        }
    }
}
