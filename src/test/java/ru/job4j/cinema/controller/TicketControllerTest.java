package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.TicketService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TicketControllerTest {

    private TicketService ticketService;
    private TicketController ticketController;
    private HttpSession httpSession;

    @BeforeEach
    void setUp() {
        ticketService = mock(TicketService.class);
        httpSession = mock(HttpSession.class);
        ticketController = new TicketController(ticketService);
    }

    @Test
    void whenBookTicketSuccess() {
        Model model = new ConcurrentModel();

        User user = new User();
        user.setId(10);

        Ticket ticket = new Ticket();
        ticket.setSessionId(5);
        ticket.setPlaceNumber(7);
        ticket.setRowNumber(3);

        when(httpSession.getAttribute("user")).thenReturn(user);
        when(ticketService.save(any(Ticket.class))).thenReturn(Optional.of(ticket));

        String view = ticketController.book(ticket, httpSession, model);

        assertThat(view).isEqualTo("tickets/result");
        assertThat(model.getAttribute("ticket")).isEqualTo(ticket);

        verify(httpSession).getAttribute("user");
        verify(ticketService).save(any(Ticket.class));
    }

    @Test
    void whenBookTicketFailsBecauseSeatTaken() {
        Model model = new ConcurrentModel();

        User user = new User();
        user.setId(10);

        Ticket ticket = new Ticket();
        ticket.setSessionId(5);
        ticket.setPlaceNumber(7);
        ticket.setRowNumber(3);

        when(httpSession.getAttribute("user")).thenReturn(user);
        when(ticketService.save(any(Ticket.class))).thenReturn(Optional.empty());

        String view = ticketController.book(ticket, httpSession, model);

        assertThat(view).isEqualTo("tickets/result");
        assertThat(model.getAttribute("message")).isEqualTo("К сожалению, место уже занято");

        verify(httpSession).getAttribute("user");
        verify(ticketService).save(any(Ticket.class));
    }
}
