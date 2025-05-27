package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.dto.FilmSessionListDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.FilmSessionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SimpleFilmSessionServiceTest {

    private FilmSessionRepository filmSessionRepository;
    private FilmService filmService;
    private HallService hallService;
    private SimpleFilmSessionService service;

    @BeforeEach
    void setUp() {
        filmSessionRepository = mock(FilmSessionRepository.class);
        filmService = mock(FilmService.class);
        hallService = mock(HallService.class);
        service = new SimpleFilmSessionService(filmSessionRepository, filmService, hallService);
    }

    @Test
    void whenFindAllThenReturnListDtos() {
        FilmSession session = new FilmSession();
        session.setId(1);
        session.setFilmId(100);
        session.setHallsId(10);
        session.setStartTime(LocalDateTime.now());
        session.setPrice(new BigDecimal(200));

        Film film = new Film();
        film.setId(100);
        film.setName("Test Film");
        film.setDescription("desc");
        film.setYear(2023);
        film.setGenreId(1);
        film.setMinimalAge(16);
        film.setDurationInMinutes(120);
        film.setFileId(8);

        Hall hall = new Hall();
        hall.setId(10);
        hall.setName("Red");
        hall.setDescription("Main hall");
        hall.setPlaceCount(10);
        hall.setRowCount(10);

        when(filmSessionRepository.findAll()).thenReturn(List.of(session));
        when(filmService.getFilmsMap()).thenReturn(Map.of(100, film));
        when(hallService.getHallsMap()).thenReturn(Map.of(10, hall));

        Collection<FilmSessionListDto> result = service.findAll();

        assertThat(result).hasSize(1);
        FilmSessionListDto dto = result.iterator().next();
        assertThat(dto.getFilmName()).isEqualTo("Test Film");
        assertThat(dto.getHallName()).isEqualTo("Red");
        assertThat(dto.getYear()).isEqualTo(2023);
    }

    @Test
    void whenFindByIdThenReturnFilmSessionDto() {
        FilmSession session = new FilmSession();
        session.setId(1);
        session.setFilmId(100);
        session.setHallsId(10);
        session.setStartTime(LocalDateTime.now());
        session.setPrice(new BigDecimal(200));

        FilmDto filmDto = new FilmDto("Movie", "desc", 2022, "Genre", 12, 90, 5);

        Hall hall = new Hall();
        hall.setId(10);
        hall.setName("Blue");
        hall.setDescription("Side hall");
        hall.setPlaceCount(12);
        hall.setRowCount(4);

        when(filmSessionRepository.findById(1)).thenReturn(Optional.of(session));
        when(filmService.getById(100)).thenReturn(Optional.of(filmDto));
        when(hallService.findById(10)).thenReturn(Optional.of(hall));

        Optional<FilmSessionDto> result = service.findById(1);

        assertThat(result).isPresent();
        FilmSessionDto dto = result.get();
        assertThat(dto.getFilmName()).isEqualTo("Movie");
        assertThat(dto.getHallName()).isEqualTo("Blue");
        assertThat(dto.getRowCount()).isEqualTo(4);
        assertThat(dto.getPlaceCount()).isEqualTo(12);
    }

    @Test
    void whenFindByIdAndFilmNotFoundThenReturnEmpty() {
        FilmSession session = new FilmSession();
        session.setId(1);
        session.setFilmId(100);
        session.setHallsId(10);
        session.setStartTime(LocalDateTime.now());
        session.setPrice(new BigDecimal(200));

        when(filmSessionRepository.findById(1)).thenReturn(Optional.of(session));
        when(filmService.getById(100)).thenReturn(Optional.empty());

        Optional<FilmSessionDto> result = service.findById(1);

        assertThat(result).isEmpty();
    }

    @Test
    void whenFindByIdAndHallNotFoundThenReturnEmpty() {
        FilmSession session = new FilmSession();
        session.setId(1);
        session.setFilmId(100);
        session.setHallsId(10);
        session.setStartTime(LocalDateTime.now());
        session.setPrice(new BigDecimal(200));

        FilmDto filmDto = new FilmDto("Movie", "desc", 2022, "Genre", 12, 90, 5);
        when(filmSessionRepository.findById(1)).thenReturn(Optional.of(session));
        when(filmService.getById(100)).thenReturn(Optional.of(filmDto));
        when(hallService.findById(10)).thenReturn(Optional.empty());

        Optional<FilmSessionDto> result = service.findById(1);

        assertThat(result).isEmpty();
    }
}
