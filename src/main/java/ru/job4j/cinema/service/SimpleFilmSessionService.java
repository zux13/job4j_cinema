package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.dto.FilmSessionListDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.FilmSessionRepository;

import java.util.*;

@Service
public class SimpleFilmSessionService implements FilmSessionService {

    private final FilmSessionRepository filmSessionRepository;
    private final FilmService filmService;
    private final HallService hallService;

    public SimpleFilmSessionService(FilmSessionRepository filmSessionRepository, FilmService filmService, HallService hallService) {
        this.filmSessionRepository = filmSessionRepository;
        this.filmService = filmService;
        this.hallService = hallService;
    }

    @Override
    public Collection<FilmSessionListDto> findAll() {

        Collection<FilmSessionListDto> filmSessionListDtos = new ArrayList<>();

        Collection<FilmSession> filmSessions = filmSessionRepository.findAll();
        Map<Integer, Film> filmMap = filmService.getFilmsMap();
        Map<Integer, Hall> hallMap = hallService.getHallsMap();

        for (FilmSession filmSession : filmSessions) {
            Film film = filmMap.get(filmSession.getFilmId());
            Hall hall = hallMap.get(filmSession.getHallsId());
            FilmSessionListDto filmSessionListDto = getFilmSessionListDto(filmSession, film, hall);
            filmSessionListDtos.add(filmSessionListDto);
        }

        return filmSessionListDtos;
    }

    @Override
    public Optional<FilmSessionDto> findById(int id) {

        Optional<FilmSession> optionalFilmSession = filmSessionRepository.findById(id);
        if (optionalFilmSession.isEmpty()) {
            return Optional.empty();
        }

        FilmSession filmSession = optionalFilmSession.get();
        Optional<FilmDto> optionalFilmDto = filmService.getById(filmSession.getFilmId());
        Optional<Hall> optionalHall = hallService.findById(filmSession.getHallsId());

        if (optionalFilmDto.isEmpty() || optionalHall.isEmpty()) {
            return Optional.empty();
        }

        FilmDto filmDto = optionalFilmDto.get();
        Hall hall = optionalHall.get();

        FilmSessionDto filmSessionDto = getFilmSessionDto(filmDto, hall, filmSession);

        return Optional.of(filmSessionDto);
    }

    private FilmSessionDto getFilmSessionDto(FilmDto filmDto, Hall hall, FilmSession filmSession) {
        FilmSessionDto filmSessionDto = new FilmSessionDto();
        filmSessionDto.setId(filmSession.getId());
        filmSessionDto.setFilmId(filmSession.getFilmId());
        filmSessionDto.setFileId(filmDto.getFileId());
        filmSessionDto.setHallsId(filmSession.getHallsId());
        filmSessionDto.setRowCount(hall.getRowCount());
        filmSessionDto.setPlaceCount(hall.getPlaceCount());
        filmSessionDto.setFilmName(filmDto.getName());
        filmSessionDto.setHallName(hall.getName());
        filmSessionDto.setHallDescription(hall.getDescription());
        filmSessionDto.setStartTime(filmSession.getStartTime());
        filmSessionDto.setPrice(filmSession.getPrice());
        return filmSessionDto;
    }

    private FilmSessionListDto getFilmSessionListDto(FilmSession filmSession, Film film, Hall hall) {
        FilmSessionListDto filmSessionListDto = new FilmSessionListDto();
        filmSessionListDto.setId(filmSession.getId());
        filmSessionListDto.setFileId(film.getFileId());
        filmSessionListDto.setFilmName(film.getName());
        filmSessionListDto.setHallName(hall.getName());
        filmSessionListDto.setYear(film.getYear());
        filmSessionListDto.setMinimalAge(film.getMinimalAge());
        filmSessionListDto.setDurationInMinutes(film.getDurationInMinutes());
        filmSessionListDto.setStartTime(filmSession.getStartTime());
        filmSessionListDto.setPrice(filmSession.getPrice());
        return filmSessionListDto;
    }
}
