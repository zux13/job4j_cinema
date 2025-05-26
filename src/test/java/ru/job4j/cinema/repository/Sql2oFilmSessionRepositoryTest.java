package ru.job4j.cinema.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.job4j.cinema.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Sql2oFilmSessionRepositoryTest {

    @Autowired
    private Sql2oHallRepository sql2oHallRepository;

    @Autowired
    private Sql2oFilmRepository sql2oFilmRepository;

    @Autowired
    private Sql2oFileRepository sql2oFileRepository;

    @Autowired
    private Sql2oGenreRepository sql2oGenreRepository;

    @Autowired
    private Sql2oFilmSessionRepository sql2oFilmSessionRepository;

    private Genre genre;
    private File file;
    private Film film;
    private Hall hall;

    @BeforeAll
    void init() {
        genre = sql2oGenreRepository.save(new Genre("name")).orElseThrow();
        file = sql2oFileRepository.save(new File("name", "path")).orElseThrow();

        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setYear(2025);
        film.setGenreId(genre.getId());
        film.setMinimalAge(12);
        film.setDurationInMinutes(120);
        film.setFileId(file.getId());

        film = sql2oFilmRepository.save(film).orElseThrow();

        hall = new Hall();
        hall.setName("name");
        hall.setDescription("description");
        hall.setPlaceCount(10);
        hall.setRowCount(10);

        hall = sql2oHallRepository.save(hall).orElseThrow();
    }

    @AfterAll
    void clearAuxTables() {
        sql2oFilmRepository.deleteById(film.getId());
        sql2oGenreRepository.deleteById(genre.getId());
        sql2oFileRepository.deleteById(file.getId());
        sql2oHallRepository.deleteById(hall.getId());
    }

    @AfterEach
    void clearTable() {
        for (FilmSession filmSession : sql2oFilmSessionRepository.findAll()) {
            sql2oFilmSessionRepository.deleteById(filmSession.getId());
        }
    }

    @Test
    void whenSaveSessionWithCorrectForeignKeysThenReturnGeneratedKey() {
        FilmSession filmSession = new FilmSession();
        filmSession.setFilmId(film.getId());
        filmSession.setHallsId(hall.getId());
        filmSession.setStartTime(LocalDateTime.now());
        filmSession.setEndTime(LocalDateTime.now());
        filmSession.setPrice(new BigDecimal(500));

        Optional<FilmSession> filmSessionOptional = sql2oFilmSessionRepository.save(filmSession);

        assertThat(filmSessionOptional).isNotEmpty();
        assertThat(filmSessionOptional.get().getId()).isNotZero();
    }

    @Test
    void whenSaveSessionWithIncorrectForeignKeysThenReturnEmptyOptional() {
        FilmSession filmSession = new FilmSession();
        filmSession.setFilmId(Integer.MAX_VALUE);
        filmSession.setHallsId(Integer.MAX_VALUE);
        filmSession.setStartTime(LocalDateTime.now());
        filmSession.setEndTime(LocalDateTime.now());
        filmSession.setPrice(new BigDecimal(500));

        Optional<FilmSession> filmSessionOptional = sql2oFilmSessionRepository.save(filmSession);

        assertThat(filmSessionOptional).isEmpty();
    }

    @Test
    void whenSaveThenFindByIdReturnsSameSession() {
        FilmSession filmSession = new FilmSession();
        filmSession.setFilmId(film.getId());
        filmSession.setHallsId(hall.getId());
        filmSession.setStartTime(LocalDateTime.now());
        filmSession.setEndTime(LocalDateTime.now());
        filmSession.setPrice(new BigDecimal(500));

        sql2oFilmSessionRepository.save(filmSession);
        Optional<FilmSession> actualOptional = sql2oFilmSessionRepository.findById(filmSession.getId());

        assertThat(actualOptional).isNotEmpty();
        assertThat(actualOptional.orElseThrow())
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(LocalDateTime.class)
                .isEqualTo(filmSession);
    }

    @Test
    void whenFindBySomeInvalidIdThenGetEmptyOptional() {
        Optional<FilmSession> filmSessionOptional = sql2oFilmSessionRepository.findById(Integer.MAX_VALUE);
        assertThat(filmSessionOptional).isEmpty();
    }

    @Test
    void whenSaveSeveralSessionsThenFindAll() {
        FilmSession filmSession1 = new FilmSession();
        filmSession1.setFilmId(film.getId());
        filmSession1.setHallsId(hall.getId());
        filmSession1.setStartTime(LocalDateTime.now());
        filmSession1.setEndTime(LocalDateTime.now());
        filmSession1.setPrice(new BigDecimal(400));

        FilmSession filmSession2 = new FilmSession();
        filmSession2.setFilmId(film.getId());
        filmSession2.setHallsId(hall.getId());
        filmSession2.setStartTime(LocalDateTime.now());
        filmSession2.setEndTime(LocalDateTime.now());
        filmSession2.setPrice(new BigDecimal(500));

        sql2oFilmSessionRepository.save(filmSession1);
        sql2oFilmSessionRepository.save(filmSession2);

        Collection<FilmSession> expected = List.of(filmSession1, filmSession2);
        Collection<FilmSession> actual = sql2oFilmSessionRepository.findAll();

        assertThat(actual.isEmpty()).isFalse();
        assertThat(expected).usingRecursiveComparison().ignoringFieldsOfTypes(LocalDateTime.class).isEqualTo(actual);
    }

    @Test
    void whenSaveThenDeleteSuccessfully() {
        FilmSession filmSession1 = new FilmSession();
        filmSession1.setFilmId(film.getId());
        filmSession1.setHallsId(hall.getId());
        filmSession1.setStartTime(LocalDateTime.now());
        filmSession1.setEndTime(LocalDateTime.now());
        filmSession1.setPrice(new BigDecimal(400));

        FilmSession filmSession2 = new FilmSession();
        filmSession2.setFilmId(film.getId());
        filmSession2.setHallsId(hall.getId());
        filmSession2.setStartTime(LocalDateTime.now());
        filmSession2.setEndTime(LocalDateTime.now());
        filmSession2.setPrice(new BigDecimal(500));

        sql2oFilmSessionRepository.save(filmSession1);
        sql2oFilmSessionRepository.save(filmSession2);

        Collection<FilmSession> beforeDelete = sql2oFilmSessionRepository.findAll();
        sql2oFilmSessionRepository.deleteById(filmSession1.getId());
        Collection<FilmSession> afterDelete = sql2oFilmSessionRepository.findAll();

        assertThat(beforeDelete).usingRecursiveComparison().isNotEqualTo(afterDelete);
        assertThat(afterDelete.size()).isEqualTo(1);
    }

    @Test
    void whenDeleteByInvalidKeyDoesNothing() {
        FilmSession filmSession1 = new FilmSession();
        filmSession1.setFilmId(film.getId());
        filmSession1.setHallsId(hall.getId());
        filmSession1.setStartTime(LocalDateTime.now());
        filmSession1.setEndTime(LocalDateTime.now());
        filmSession1.setPrice(new BigDecimal(400));

        sql2oFilmSessionRepository.save(filmSession1);

        Collection<FilmSession> beforeDelete = sql2oFilmSessionRepository.findAll();
        sql2oFilmSessionRepository.deleteById(Integer.MAX_VALUE);
        Collection<FilmSession> afterDelete = sql2oFilmSessionRepository.findAll();

        assertThat(beforeDelete).usingRecursiveComparison().isEqualTo(afterDelete);
    }

}