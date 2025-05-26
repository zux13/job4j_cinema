package ru.job4j.cinema.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.cinema.model.Genre;

import java.util.Collection;
import java.util.Optional;

@Repository
public class Sql2oGenreRepository implements GenreRepository {

    private final Sql2o sql2o;

    public Sql2oGenreRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<Genre> save(Genre genre) {
       try (Connection connection = sql2o.open()) {
           Query query = connection.createQuery("INSERT INTO genres(name) VALUES(:name)", true)
                   .addParameter("name", genre.getName());

           int generatedKey = query.executeUpdate().getKey(Integer.class);
           genre.setId(generatedKey);

           return Optional.of(genre);
       } catch (Sql2oException e) {
           return Optional.empty();
       }
    }

    @Override
    public Optional<Genre> findById(int id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM genres WHERE id=:id");
            Genre genre = query.addParameter("id", id).executeAndFetchFirst(Genre.class);
            return Optional.ofNullable(genre);
        }
    }

    @Override
    public Collection<Genre> findAll() {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM genres");
            return query.executeAndFetch(Genre.class);
        }
    }

    @Override
    public void deleteById(int id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("DELETE FROM genres WHERE id=:id");
            query.addParameter("id", id).executeUpdate();
        }
    }

}
