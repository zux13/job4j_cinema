package ru.job4j.cinema.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.cinema.model.Film;

import java.util.Collection;
import java.util.Optional;

@Repository
public class Sql2oFilmRepository implements FilmRepository {

    private final Sql2o sql2o;

    public Sql2oFilmRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<Film> save(Film film) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                    INSERT INTO films(name, description, release_year, genre_id, minimal_age, duration_in_minutes, file_id)
                    VALUES (:name, :description, :releaseYear, :genreId, :minimalAge, :durationInMinutes, :fileId)
                    """;
            Query query = connection.createQuery(sql, true)
                    .addParameter("name", film.getName())
                    .addParameter("description", film.getDescription())
                    .addParameter("releaseYear", film.getYear())
                    .addParameter("genreId", film.getGenreId())
                    .addParameter("minimalAge", film.getMinimalAge())
                    .addParameter("durationInMinutes", film.getDurationInMinutes())
                    .addParameter("fileId", film.getFileId());

            int generatedKey = query.executeUpdate().getKey(Integer.class);
            film.setId(generatedKey);

            return Optional.of(film);
        } catch (Sql2oException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Film> findById(int id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM films WHERE id=:id");
            Film film = query.addParameter("id", id)
                    .setColumnMappings(Film.COLUMN_MAPPING)
                    .executeAndFetchFirst(Film.class);
            return Optional.ofNullable(film);
        }
    }

    @Override
    public Collection<Film> findAll() {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM films");
            return query.setColumnMappings(Film.COLUMN_MAPPING)
                    .executeAndFetch(Film.class);
        }
    }

    @Override
    public void deleteById(int id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("DELETE FROM films WHERE id=:id");
            query.addParameter("id", id).executeUpdate();
        }
    }
}
