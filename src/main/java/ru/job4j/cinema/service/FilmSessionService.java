package ru.job4j.cinema.service;

import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.dto.FilmSessionListDto;

import java.util.Collection;
import java.util.Optional;

public interface FilmSessionService {

    Collection<FilmSessionListDto> findAll();

    Optional<FilmSessionDto> findById(int id);

}
