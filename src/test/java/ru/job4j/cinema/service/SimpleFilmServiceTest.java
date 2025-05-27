package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.GenreRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SimpleFilmServiceTest {

    private FilmRepository filmRepository;
    private GenreRepository genreRepository;
    private FilmService filmService;

    @BeforeEach
    void setUp() {
        filmRepository = mock(FilmRepository.class);
        genreRepository = mock(GenreRepository.class);
        filmService = new SimpleFilmService(filmRepository, genreRepository);
    }

    @Test
    void whenGetByIdThenReturnFilmDto() {
        Film film = new Film();
        film.setId(1);
        film.setName("Inception");
        film.setDescription("Sci-fi movie");
        film.setYear(2010);
        film.setGenreId(2);
        film.setMinimalAge(16);
        film.setDurationInMinutes(148);
        film.setFileId(5);

        Genre genre = new Genre("Sci-fi");
        genre.setId(2);

        when(filmRepository.findById(1)).thenReturn(Optional.of(film));
        when(genreRepository.findById(2)).thenReturn(Optional.of(genre));

        Optional<FilmDto> result = filmService.getById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Inception");
        assertThat(result.get().getGenreName()).isEqualTo("Sci-fi");
    }

    @Test
    void whenGetByIdWithMissingFilmThenReturnEmpty() {
        when(filmRepository.findById(99)).thenReturn(Optional.empty());

        Optional<FilmDto> result = filmService.getById(99);

        assertThat(result).isEmpty();
    }

    @Test
    void whenGetAllThenReturnFilmList() {
        Film film1 = new Film();
        film1.setId(1);
        film1.setName("Film 1");
        film1.setDescription("Desc 1");
        film1.setYear(2010);
        film1.setGenreId(2);
        film1.setMinimalAge(16);
        film1.setDurationInMinutes(148);
        film1.setFileId(5);

        Film film2 = new Film();
        film2.setId(2);
        film2.setName("Film 2");
        film2.setDescription("Desc 2");
        film2.setYear(2010);
        film2.setGenreId(2);
        film2.setMinimalAge(16);
        film2.setDurationInMinutes(148);
        film2.setFileId(5);

        List<Film> films = List.of(film1, film2);
        when(filmRepository.findAll()).thenReturn(films);

        var result = filmService.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void whenGetFilmsMapThenReturnMapById() {
        Film film1 = new Film();
        film1.setId(1);
        film1.setName("Film 1");
        film1.setDescription("Desc 1");
        film1.setYear(2010);
        film1.setGenreId(2);
        film1.setMinimalAge(16);
        film1.setDurationInMinutes(148);
        film1.setFileId(5);

        Film film2 = new Film();
        film2.setId(2);
        film2.setName("Film 2");
        film2.setDescription("Desc 2");
        film2.setYear(2010);
        film2.setGenreId(2);
        film2.setMinimalAge(16);
        film2.setDurationInMinutes(148);
        film2.setFileId(5);

        when(filmRepository.findAll()).thenReturn(List.of(film1, film2));

        Map<Integer, Film> result = filmService.getFilmsMap();

        assertThat(result).hasSize(2);
        assertThat(result.get(1).getName()).isEqualTo("Film 1");
        assertThat(result.get(2).getName()).isEqualTo("Film 2");
    }
}
