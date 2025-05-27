package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.service.FilmService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FilmControllerTest {

    private FilmService filmService;
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmService = mock(FilmService.class);
        filmController = new FilmController(filmService);
    }

    @Test
    void whenGetAllThenReturnsListViewWithFilms() {
        var model = new ConcurrentModel();

        var film1 = new Film();
        film1.setId(1);
        film1.setName("Фильм 1");

        var film2 = new Film();
        film2.setId(2);
        film2.setName("Фильм 2");

        var films = List.of(film1, film2);
        when(filmService.findAll()).thenReturn(films);

        var viewName = filmController.getAll(model);
        var actualFilms = model.getAttribute("films");

        assertThat(viewName).isEqualTo("films/list");
        assertThat(actualFilms).isEqualTo(films);
    }

    @Test
    void whenGetByIdThenReturnsSingleViewWithFilmDto() {
        var model = new ConcurrentModel();

        var filmDto = new FilmDto("Фильм", "Описание", 2010, "Жанр", 16, 120, 4);

        when(filmService.getById(1)).thenReturn(Optional.of(filmDto));

        var viewName = filmController.getById(model, 1);
        var actualFilmDto = model.getAttribute("filmDto");

        assertThat(viewName).isEqualTo("films/single");
        assertThat(actualFilmDto).isEqualTo(filmDto);
    }

    @Test
    void whenGetByIdNotFoundThenReturnsErrorViewWithMessage() {
        var model = new ConcurrentModel();
        when(filmService.getById(1)).thenReturn(Optional.empty());

        var viewName = filmController.getById(model, 1);
        var message = model.getAttribute("message");

        assertThat(viewName).isEqualTo("errors/404");
        assertThat(message).isEqualTo("Фильм с указанным идентификатором не найден");
    }
}
