package ru.job4j.cinema.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Sql2oFilmRepositoryTest {

    @Autowired
    private Sql2oFilmRepository sql2oFilmRepository;

    @Autowired
    private Sql2oFileRepository sql2oFileRepository;

    @Autowired
    private Sql2oGenreRepository sql2oGenreRepository;

    private Genre genre;
    private File file;

    @BeforeAll
    void init() {
        genre = sql2oGenreRepository.save(new Genre("name")).orElseThrow();
        file = sql2oFileRepository.save(new File("name", "path")).orElseThrow();
    }

    @AfterEach
    void clearFilmsTable() {
        for (Film film : sql2oFilmRepository.findAll()) {
            sql2oFilmRepository.deleteById(film.getId());
        }
    }

    @AfterAll
    void clearAuxTables() {
        sql2oFileRepository.deleteById(file.getId());
        sql2oGenreRepository.deleteById(genre.getId());
    }

    @Test
    public void whenSaveFilmWithCorrectForeignKeysThenGetGeneratedKey() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setYear(2025);
        film.setGenreId(genre.getId());
        film.setMinimalAge(12);
        film.setDurationInMinutes(120);
        film.setFileId(file.getId());

        Optional<Film> optionalFilm = sql2oFilmRepository.save(film);

        assertThat(optionalFilm).isNotEmpty();
        assertThat(optionalFilm.get().getId()).isNotZero();
    }

    @Test
    public void whenSaveFilmWithIncorrectForeignKeysThenGetEmptyOptional() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setYear(2025);
        film.setGenreId(Integer.MAX_VALUE);
        film.setMinimalAge(12);
        film.setDurationInMinutes(120);
        film.setFileId(Integer.MAX_VALUE);

        Optional<Film> optionalFilm = sql2oFilmRepository.save(film);

        assertThat(optionalFilm).isEmpty();
    }

    @Test
    public void whenFindByIdSuccessfully() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setYear(2025);
        film.setGenreId(genre.getId());
        film.setMinimalAge(12);
        film.setDurationInMinutes(120);
        film.setFileId(file.getId());

        int id = sql2oFilmRepository.save(film).get().getId();

        Optional<Film> foundOptional = sql2oFilmRepository.findById(id);

        assertThat(foundOptional).isNotEmpty();
        assertThat(film).isEqualTo(foundOptional.get());
    }

    @Test
    public void whenFindByIdFails() {
        Optional<Film> foundOptional = sql2oFilmRepository.findById(Integer.MAX_VALUE);
        assertThat(foundOptional).isEmpty();
    }

    @Test
    public void whenSaveFilmsThenFindAllSuccessfully() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setYear(2025);
        film.setGenreId(genre.getId());
        film.setMinimalAge(12);
        film.setDurationInMinutes(120);
        film.setFileId(file.getId());

        Film film1 = new Film();
        film1.setName("name 1");
        film1.setDescription("description 1");
        film1.setYear(2025);
        film1.setGenreId(genre.getId());
        film1.setMinimalAge(12);
        film1.setDurationInMinutes(120);
        film1.setFileId(file.getId());

        sql2oFilmRepository.save(film);
        sql2oFilmRepository.save(film1);

        Collection<Film> expected = List.of(film, film1);
        Collection<Film> actual = sql2oFilmRepository.findAll();

        assertThat(actual.isEmpty()).isFalse();
        assertThat(expected).usingRecursiveComparison().isEqualTo(actual);
    }

    @Test
    public void whenFindAllReturnsEmptyCollection() {
        Collection<Film> expected = List.of();
        Collection<Film> actual = sql2oFilmRepository.findAll();

        assertThat(expected).usingRecursiveComparison().isEqualTo(actual);
    }

    @Test
    public void whenSaveThenDeleteSuccessfully() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setYear(2025);
        film.setGenreId(genre.getId());
        film.setMinimalAge(12);
        film.setDurationInMinutes(120);
        film.setFileId(file.getId());

        sql2oFilmRepository.save(film);
        Collection<Film> beforeDelete = sql2oFilmRepository.findAll();
        sql2oFilmRepository.deleteById(film.getId());
        Collection<Film> afterDelete = sql2oFilmRepository.findAll();

        assertThat(beforeDelete.isEmpty()).isFalse();
        assertThat(afterDelete.isEmpty()).isTrue();
    }

    @Test
    public void whenDeleteOnWrongIdFails() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setYear(2025);
        film.setGenreId(genre.getId());
        film.setMinimalAge(12);
        film.setDurationInMinutes(120);
        film.setFileId(file.getId());

        sql2oFilmRepository.save(film);
        Collection<Film> beforeDelete = sql2oFilmRepository.findAll();
        sql2oFilmRepository.deleteById(Integer.MAX_VALUE);
        Collection<Film> afterDelete = sql2oFilmRepository.findAll();

        assertThat(beforeDelete).usingRecursiveComparison().isEqualTo(afterDelete);
    }
}
