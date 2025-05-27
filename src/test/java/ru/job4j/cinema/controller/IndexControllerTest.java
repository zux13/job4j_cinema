package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import ru.job4j.cinema.dto.FilmSessionListDto;
import ru.job4j.cinema.service.FilmSessionService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class IndexControllerTest {

    private FilmSessionService filmSessionService;
    private IndexController indexController;

    @BeforeEach
    void setUp() {
        filmSessionService = mock(FilmSessionService.class);
        indexController = new IndexController(filmSessionService);
    }

    @Test
    void whenGetIndexThenReturnsIndexPageWithSessions() {
        Model model = new ConcurrentModel();

        FilmSessionListDto session1 = new FilmSessionListDto();
        session1.setId(1);
        session1.setFilmName("Film 1");
        session1.setStartTime(LocalDateTime.now());

        FilmSessionListDto session2 = new FilmSessionListDto();
        session2.setId(2);
        session2.setFilmName("Film 2");
        session2.setStartTime(LocalDateTime.now().plusDays(1));

        when(filmSessionService.findAll()).thenReturn(List.of(session1, session2));

        String view = indexController.getIndex(model);

        assertThat(view).isEqualTo("index");
        assertThat(model.getAttribute("sessions")).isEqualTo(List.of(session1, session2));

        verify(filmSessionService).findAll();
    }
}
