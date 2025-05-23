package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.TicketService;

import java.util.Optional;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/book")
    public String book(@ModelAttribute Ticket ticket, HttpSession httpSession, Model model) {

        User user = (User) httpSession.getAttribute("user");
        ticket.setUserId(user.getId());

        Optional<Ticket> savedTicket = ticketService.save(ticket);
        if (savedTicket.isEmpty()) {
            model.addAttribute("message", "К сожалению, место уже занято");
            return "tickets/result";
        }

        model.addAttribute("ticket", savedTicket.get());
        return "tickets/result";
    }
}
