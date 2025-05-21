package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.GenreRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class SimpleFilmService implements FilmService {

    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;

    public SimpleFilmService(
            FilmRepository filmRepository,
            GenreRepository genreRepository
    ) {
        this.filmRepository = filmRepository;
        this.genreRepository = genreRepository;
    }

    @Override
    public Optional<FilmDto> getById(int id) {
        Optional<Film> optionalFilm = filmRepository.findById(id);
        if (optionalFilm.isEmpty()) {
            return Optional.empty();
        }
        Film film = optionalFilm.get();
        Optional<Genre> optionalGenre = genreRepository.findById(film.getGenreId());
        return optionalGenre.map(genre -> new FilmDto(
                film.getName(),
                film.getDescription(),
                film.getYear(),
                genre.getName(),
                film.getMinimalAge(),
                film.getDurationInMinutes(),
                film.getFileId()
        ));
    }

    @Override
    public Collection<Film> findAll() {
        return filmRepository.findAll();
    }
}
