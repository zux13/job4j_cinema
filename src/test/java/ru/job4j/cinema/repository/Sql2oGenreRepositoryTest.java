package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class Sql2oGenreRepositoryTest {

    private static Sql2oGenreRepository sql2oGenreRepository;

    @BeforeAll
    public static void init() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oFileRepository.class.getClassLoader().getResourceAsStream("application-test.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oGenreRepository = new Sql2oGenreRepository(sql2o);
    }

    @AfterEach
    public void clearTable() {
        for (Genre genre : sql2oGenreRepository.findAll()) {
            sql2oGenreRepository.deleteById(genre.getId());
        }
    }

    @Test
    public void whenSaveGenreSuccessfully() {
        Genre genre = new Genre("name");
        Optional<Genre> optionalGenre = sql2oGenreRepository.save(genre);

        assertThat(optionalGenre).isNotEmpty();
        assertThat(genre.getId()).isNotZero();
    }

    @Test
    public void whenSaveGenreThenFindByIdSuccessfully() {
        Genre genre = new Genre("name");
        sql2oGenreRepository.save(genre);

        Optional<Genre> optionalGenre = sql2oGenreRepository.findById(genre.getId());

        assertThat(optionalGenre).isNotEmpty();
        assertThat(genre).isEqualTo(optionalGenre.get());
    }

    @Test
    public void whenNotFoundById() {
        Optional<Genre> optionalGenre = sql2oGenreRepository.findById(1);
        assertThat(optionalGenre).isEmpty();
    }

    @Test
    public void whenFindAllSuccessfully() {
        Genre genre = new Genre("name");
        Genre genre1 = new Genre("name1");
        Genre genre2 = new Genre("name2");

        sql2oGenreRepository.save(genre);
        sql2oGenreRepository.save(genre1);
        sql2oGenreRepository.save(genre2);

        Collection<Genre> expected = List.of(genre, genre1, genre2);
        Collection<Genre> actual = sql2oGenreRepository.findAll();

        assertThat(expected).usingRecursiveComparison().isEqualTo(actual);
    }

    @Test
    public void whenDeleteSuccessfully() {
        Genre genre = new Genre("name");
        sql2oGenreRepository.save(genre);

        Collection<Genre> stateOne = sql2oGenreRepository.findAll();
        sql2oGenreRepository.deleteById(genre.getId());
        Collection<Genre> stateTwo = sql2oGenreRepository.findAll();

        assertThat(stateOne.isEmpty()).isFalse();
        assertThat(stateTwo.isEmpty()).isTrue();
    }

    @Test
    public void whenDeleteByWrongId() {
        Genre genre = new Genre("name");
        sql2oGenreRepository.save(genre);

        Collection<Genre> stateOne = sql2oGenreRepository.findAll();
        sql2oGenreRepository.deleteById(10);
        Collection<Genre> stateTwo = sql2oGenreRepository.findAll();

        assertThat(stateOne.isEmpty()).isFalse();
        assertThat(stateTwo.isEmpty()).isFalse();
        assertThat(stateOne).usingRecursiveComparison().isEqualTo(stateTwo);
    }

}