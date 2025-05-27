package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.repository.TicketRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SimpleTicketServiceTest {

    private TicketRepository ticketRepository;
    private SimpleTicketService ticketService;

    @BeforeEach
    void setUp() {
        ticketRepository = mock(TicketRepository.class);
        ticketService = new SimpleTicketService(ticketRepository);
    }

    @Test
    void whenSaveTicketThenReturnTicketOptional() {
        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setUserId(1);
        ticket.setRowNumber(1);
        ticket.setPlaceNumber(1);
        ticket.setSessionId(1);

        when(ticketRepository.save(ticket)).thenReturn(Optional.of(ticket));

        Optional<Ticket> result = ticketService.save(ticket);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(ticket);
        verify(ticketRepository).save(ticket);
    }

    @Test
    void whenSaveTicketFailsThenReturnEmptyOptional() {
        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setUserId(1);
        ticket.setRowNumber(1);
        ticket.setPlaceNumber(1);
        ticket.setSessionId(1);

        when(ticketRepository.save(ticket)).thenReturn(Optional.empty());

        Optional<Ticket> result = ticketService.save(ticket);

        assertThat(result).isEmpty();
        verify(ticketRepository).save(ticket);
    }
}
