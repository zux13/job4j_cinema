package ru.job4j.cinema.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.cinema.model.File;

import java.util.Collection;
import java.util.Optional;

@Repository
public class Sql2oFileRepository implements FileRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(Sql2oFileRepository.class);
    private final Sql2o sql2o;

    Sql2oFileRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<File> save(File file) {
        try (Connection connection = sql2o.open()) {
            String sql = "INSERT INTO files(`name`, `path`) VALUES(:name, :path)";
            Query query = connection.createQuery(sql, true)
                    .addParameter("name", file.getName())
                    .addParameter("path", file.getPath());
            int generatedKey = query.executeUpdate().getKey(Integer.class);
            file.setId(generatedKey);
            return Optional.of(file);
        } catch (Sql2oException e) {
            LOGGER.error("Error saving file: {}", file, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<File> findById(int id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM files WHERE id=:id");
            File file = query.addParameter("id", id).executeAndFetchFirst(File.class);
            return Optional.ofNullable(file);
        }
    }

    @Override
    public void deleteById(int id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("DELETE FROM files WHERE id=:id");
            query.addParameter("id", id).executeUpdate();
        }
    }

    @Override
    public Collection<File> findAll() {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM files");
            return query.executeAndFetch(File.class);
        }
    }
}
