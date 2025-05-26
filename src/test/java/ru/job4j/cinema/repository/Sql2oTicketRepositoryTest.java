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
class Sql2oTicketRepositoryTest {

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

    @Autowired
    private Sql2oUserRepository sql2oUserRepository;

    @Autowired
    private Sql2oTicketRepository sql2oTicketRepository;

    private Genre genre;
    private File file;
    private Film film;
    private Hall hall;
    private User user;
    private FilmSession filmSession;

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

        filmSession = new FilmSession();
        filmSession.setFilmId(film.getId());
        filmSession.setHallsId(hall.getId());
        filmSession.setStartTime(LocalDateTime.now());
        filmSession.setEndTime(LocalDateTime.now());
        filmSession.setPrice(new BigDecimal(500));

        filmSession = sql2oFilmSessionRepository.save(filmSession).orElseThrow();

        user = new User("email@email.ru", "username", "password");
        user = sql2oUserRepository.save(user).orElseThrow();
    }

    @AfterAll
    void clearAuxTables() {
        sql2oFilmSessionRepository.deleteById(filmSession.getId());
        sql2oFilmRepository.deleteById(film.getId());
        sql2oGenreRepository.deleteById(genre.getId());
        sql2oFileRepository.deleteById(file.getId());
        sql2oHallRepository.deleteById(hall.getId());
        sql2oUserRepository.deleteById(user.getId());
    }

    @AfterEach
    void clearTable() {
        for (Ticket ticket : sql2oTicketRepository.findAll()) {
            sql2oTicketRepository.deleteById(ticket.getId());
        }
    }

    @Test
    void whenSaveTicketWithCorrectForeignKeysThenSetGeneratedKey() {
        Ticket ticket = new Ticket();
        ticket.setSessionId(filmSession.getId());
        ticket.setUserId(user.getId());
        ticket.setPlaceNumber(1);
        ticket.setRowNumber(1);

        Optional<Ticket> ticketOptional = sql2oTicketRepository.save(ticket);

        assertThat(ticketOptional).isNotEmpty();
        assertThat(ticketOptional.orElseThrow().getId()).isNotZero();
    }

    @Test
    void whenSaveTicketWithIncorrectForeignKeysThenReturnEmptyOptional() {
        Ticket ticket = new Ticket();
        ticket.setSessionId(Integer.MAX_VALUE);
        ticket.setUserId(Integer.MAX_VALUE);
        ticket.setPlaceNumber(1);
        ticket.setRowNumber(1);

        Optional<Ticket> ticketOptional = sql2oTicketRepository.save(ticket);

        assertThat(ticketOptional).isEmpty();
    }

    @Test
    void whenSaveTicketThenFindByIdReturnsTheSameTicket() {
        Ticket ticket = new Ticket();
        ticket.setSessionId(filmSession.getId());
        ticket.setUserId(user.getId());
        ticket.setPlaceNumber(1);
        ticket.setRowNumber(1);

        sql2oTicketRepository.save(ticket);

        Optional<Ticket> foundTicketOptional = sql2oTicketRepository.findById(ticket.getId());

        assertThat(foundTicketOptional).isNotEmpty();
        assertThat(foundTicketOptional.orElseThrow()).isEqualTo(ticket);
    }

    @Test
    void whenFindByNonExistingIdThenReturnEmptyOptional() {
        Optional<Ticket> foundOptionalTicket = sql2oTicketRepository.findById(Integer.MAX_VALUE);
        assertThat(foundOptionalTicket).isEmpty();
    }

    @Test
    void whenSaveSeveralTicketsThenFindAll() {
        Ticket ticket = new Ticket();
        ticket.setSessionId(filmSession.getId());
        ticket.setUserId(user.getId());
        ticket.setPlaceNumber(1);
        ticket.setRowNumber(1);

        Ticket ticket2 = new Ticket();
        ticket2.setSessionId(filmSession.getId());
        ticket2.setUserId(user.getId());
        ticket2.setPlaceNumber(2);
        ticket2.setRowNumber(1);

        sql2oTicketRepository.save(ticket);
        sql2oTicketRepository.save(ticket2);

        Collection<Ticket> expected = List.of(ticket, ticket2);
        Collection<Ticket> actual = sql2oTicketRepository.findAll();

        assertThat(expected).usingRecursiveComparison().isEqualTo(actual);
    }

    @Test
    void whenFindAllOnEmptyDatabase() {
        Collection<Ticket> expected = List.of();
        Collection<Ticket> actual = sql2oTicketRepository.findAll();

        assertThat(expected).isEqualTo(actual);
    }

    @Test
    void whenSaveThenDeleteTicketById() {
        Ticket ticket = new Ticket();
        ticket.setSessionId(filmSession.getId());
        ticket.setUserId(user.getId());
        ticket.setPlaceNumber(1);
        ticket.setRowNumber(1);

        sql2oTicketRepository.save(ticket);

        Collection<Ticket> beforeDelete = sql2oTicketRepository.findAll();
        sql2oTicketRepository.deleteById(ticket.getId());
        Collection<Ticket> afterDelete = sql2oTicketRepository.findAll();

        assertThat(beforeDelete).usingRecursiveComparison().isNotEqualTo(afterDelete);
        assertThat(afterDelete).isEmpty();
    }

    @Test
    void whenDeleteByInvalidIdDoesNothing() {
        Ticket ticket = new Ticket();
        ticket.setSessionId(filmSession.getId());
        ticket.setUserId(user.getId());
        ticket.setPlaceNumber(1);
        ticket.setRowNumber(1);

        sql2oTicketRepository.save(ticket);

        Collection<Ticket> beforeDelete = sql2oTicketRepository.findAll();
        sql2oTicketRepository.deleteById(Integer.MAX_VALUE);
        Collection<Ticket> afterDelete = sql2oTicketRepository.findAll();

        assertThat(beforeDelete).usingRecursiveComparison().isEqualTo(afterDelete);
    }
}