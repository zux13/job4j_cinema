package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.job4j.cinema.service.FilmSessionService;

@Controller
public class IndexController {

    private final FilmSessionService filmSessionService;

    public IndexController(FilmSessionService filmSessionService) {
        this.filmSessionService = filmSessionService;
    }

    @GetMapping({"/", "/index"})
    public String getIndex(Model model) {
        model.addAttribute("sessions", filmSessionService.findAll());
        return "index";
    }
}
