package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.dto.FilmSessionListDto;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.service.FilmSessionService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FilmSessionControllerTest {

    private FilmSessionService filmSessionService;
    private FilmSessionController filmSessionController;

    @BeforeEach
    void setUp() {
        filmSessionService = mock(FilmSessionService.class);
        filmSessionController = new FilmSessionController(filmSessionService);
    }

    @Test
    void whenGetAllThenReturnsSessionsList() {
        Model model = new ConcurrentModel();

        FilmSessionListDto session1 = new FilmSessionListDto();
        session1.setId(1);
        session1.setFilmName("Film 1");
        session1.setStartTime(LocalDateTime.now());

        FilmSessionListDto session2 = new FilmSessionListDto();
        session2.setId(2);
        session2.setFilmName("Film 2");
        session2.setStartTime(LocalDateTime.now().plusDays(1));

        Collection<FilmSessionListDto> sessions = List.of(session1, session2);

        when(filmSessionService.findAll()).thenReturn(sessions);

        String viewName = filmSessionController.getAll(model);
        Object actualSessions = model.getAttribute("sessions");

        assertThat(viewName).isEqualTo("sessions/list");
        assertThat(actualSessions).isEqualTo(sessions);
    }

    @Test
    void whenGetByIdFoundThenReturnsSingleSession() {
        Model model = new ConcurrentModel();

        FilmSessionDto sessionDto = new FilmSessionDto();
        sessionDto.setId(1);
        sessionDto.setFilmName("Film");
        sessionDto.setStartTime(LocalDateTime.now());

        when(filmSessionService.findById(1)).thenReturn(Optional.of(sessionDto));

        String viewName = filmSessionController.getById(1, model);

        Object modelSessionDto = model.getAttribute("sessionDto");
        Object modelTicket = model.getAttribute("ticket");

        assertThat(viewName).isEqualTo("sessions/single");
        assertThat(modelSessionDto).isEqualTo(sessionDto);
        assertThat(modelTicket).isInstanceOf(Ticket.class);
    }

    @Test
    void whenGetByIdNotFoundThenReturns404() {
        Model model = new ConcurrentModel();

        when(filmSessionService.findById(1)).thenReturn(Optional.empty());

        String viewName = filmSessionController.getById(1, model);

        Object message = model.getAttribute("message");

        assertThat(viewName).isEqualTo("errors/404");
        assertThat(message).isEqualTo("Сеанс с указанным идентификатором не найден");
    }
}
